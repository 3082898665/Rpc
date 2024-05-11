package tq.cn.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import tq.cn.filter.FilterConfig;
import tq.cn.annotation.MyRpcService;
import tq.cn.common.RpcServiceNameBuilder;
import tq.cn.common.ServiceMeta;
import tq.cn.config.RpcProperties;
import tq.cn.poll.ThreadPollFactory;
import tq.cn.protocol.codec.RpcDecoder;
import tq.cn.protocol.codec.RpcEncoder;
import tq.cn.protocol.handler.service.ServiceAfterFilterHandler;
import tq.cn.protocol.handler.service.ServiceBeforeFilterHandler;
import tq.cn.protocol.handler.service.RpcRequestHandler;
import tq.cn.protocol.serialization.SerializationFactory;
import tq.cn.registry.RegistryFactory;
import tq.cn.registry.RegistryService;
import tq.cn.router.LoadBalancerFactory;
import tq.cn.utils.PropertiesUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * InitializingBean:
 * 这个接口定义了一个afterPropertiesSet方法，它在Spring的bean的所有属性被设置之后，但在bean被初始化方法（如@PostConstruct注解的方法）调用之前被调用。
 * ProviderPostProcessor实现这个接口可以在其afterPropertiesSet方法中执行一些初始化逻辑，比如设置内部状态，启动某些资源，
 * 或者执行一些只依赖于bean属性而不依赖于bean实际功能的操作。
 *
 * BeanPostProcessor:
 * BeanPostProcessor接口允许你在Spring容器实例化一个bean之后和bean被任何其他bean引用之前，对bean进行修改或后处理。
 * 这个接口定义了两个方法：postProcessBeforeInitialization和postProcessAfterInitialization。
 * ProviderPostProcessor可以通过实现这些方法，在bean的生命周期的不同阶段执行自定义操作，比如检查bean的属性，修改bean的状态，或者替换bean实例。
 *
 * EnvironmentAware:
 * EnvironmentAware接口使得ProviderPostProcessor能够访问Environment对象，该对象代表了Spring的环境，
 * 包含了所有的属性源，如系统属性、JVM参数、命令行参数以及配置文件等。
 * 实现EnvironmentAware接口的setEnvironment方法允许ProviderPostProcessor获取这些配置信息，并可能根据这些配置信息来动态调整其行为或状态。
 */

/**
 * @description: 服务提供方后置处理器
 */
public class ProviderPostProcessor implements InitializingBean,BeanPostProcessor, EnvironmentAware {


    private Logger logger = LoggerFactory.getLogger(ProviderPostProcessor.class);

    RpcProperties rpcProperties;

    // 此处在linux环境下改为0.0.0.0
    private static String serverAddress = "127.0.0.1";

    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {

        Thread t = new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                logger.error("start rpc server error.", e);
            }
        });
        t.setDaemon(true);
        t.start();
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initServiceFilter();
        ThreadPollFactory.setRpcServiceMap(rpcServiceMap);
    }

    private void startRpcServer() throws InterruptedException {
        int serverPort = rpcProperties.getPort();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new ServiceBeforeFilterHandler())
                                    .addLast(new RpcRequestHandler())
                                    .addLast(new ServiceAfterFilterHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, serverPort).sync();
            logger.info("server addr {} started on port {}", this.serverAddress, serverPort);
            channelFuture.channel().closeFuture().sync();
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                logger.info("ShutdownHook execute start...");
                logger.info("Netty NioEventLoopGroup shutdownGracefully...");
                logger.info("Netty NioEventLoopGroup shutdownGracefully2...");
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                logger.info("ShutdownHook execute end...");
            }, "Allen-thread"));
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 服务注册
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService 注解的类
        MyRpcService rpcService = beanClass.getAnnotation(MyRpcService.class);
        if (rpcService != null) {

            System.out.println(beanName);
            System.out.println(bean);

            // 可能会有多个接口,默认选择第一个接口
            String serviceName = beanClass.getInterfaces()[0].getName();
            if (!rpcService.serviceInterface().equals(void.class)){
                serviceName = rpcService.serviceInterface().getName();
            }
            String serviceVersion = rpcService.serviceVersion();
            try {
                // 服务注册
                int servicePort = rpcProperties.getPort();
                // 获取注册中心 ioc
                RegistryService registryService = RegistryFactory.get(rpcProperties.getRegisterType());
                ServiceMeta serviceMeta = new ServiceMeta();
                // 服务提供方地址
                serviceMeta.setServiceAddr("127.0.0.1");
                serviceMeta.setServicePort(servicePort);
                serviceMeta.setServiceVersion(serviceVersion);
                serviceMeta.setServiceName(serviceName);
                registryService.register(serviceMeta);
                // 缓存
                rpcServiceMap.put(RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getServiceVersion()), bean);
                logger.info("register server {} version {}",serviceName,serviceVersion);
            } catch (Exception e) {
                logger.error("failed to register service {}",  serviceVersion, e);
            }
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
        rpcProperties = properties;
        logger.info("读取配置文件成功");
    }
}

