package tq.cn.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tq.cn.filter.FilterConfig;
import tq.cn.filter.client.ClientLogFilter;
import tq.cn.annotation.MyRpcReference;
import tq.cn.config.RpcProperties;
import tq.cn.protocol.serialization.SerializationFactory;
import tq.cn.registry.RegistryFactory;
import tq.cn.router.LoadBalancerFactory;
import tq.cn.utils.PropertiesUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @description: 消费方后置处理器
 */
@Configuration
public class ConsumerPostProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    RpcProperties rpcProperties;

    /**
     * 从配置文件中读取配置
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
        rpcProperties = properties;
        logger.info("读取配置文件成功");

    }

    /**
     * 初始化一些bean
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initClientFilter();
    }

    /**
     * 代理层注入
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有字段
        final Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历所有字段找到 RpcReference 注解的字段
        for (Field field : fields) {
            if(field.isAnnotationPresent(MyRpcReference.class)){
                final MyRpcReference rpcReference = field.getAnnotation(MyRpcReference.class);
                final Class<?> aClass = field.getType();
                field.setAccessible(true);
                Object object = null;
                try {
                    // 创建代理对象
                    object = Proxy.newProxyInstance(
                            aClass.getClassLoader(),
                            new Class<?>[]{aClass},
                            new RpcInvokerProxy(rpcReference.serviceVersion(),rpcReference.timeout(),rpcReference.faultTolerant(),
                                    rpcReference.loadBalancer(),rpcReference.retryCount()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // 将代理对象设置给字段
                    field.set(bean,object);
                    field.setAccessible(false);
                    logger.info(beanName + " field:" + field.getName() + "注入成功");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    logger.info(beanName + " field:" + field.getName() + "注入失败");
                }
            }
        }
        return bean;
    }
}
