package tq.cn.filter;


import lombok.SneakyThrows;
import tq.cn.spi.ExtensionLoader;

import java.io.IOException;

/**
 * @description: 拦截器配置类，用于统一管理拦截器
 */
public class FilterConfig {


    private static tq.cn.filter.FilterChain serviceBeforeFilterChain = new tq.cn.filter.FilterChain();
    private static tq.cn.filter.FilterChain serviceAfterFilterChain = new tq.cn.filter.FilterChain();
    private static tq.cn.filter.FilterChain clientBeforeFilterChain = new tq.cn.filter.FilterChain();
    private static tq.cn.filter.FilterChain clientAfterFilterChain = new tq.cn.filter.FilterChain();

    @SneakyThrows
    public static void initServiceFilter(){
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(tq.cn.filter.ServiceAfterFilter.class);
        extensionLoader.loadExtension(tq.cn.filter.ServiceBeforeFilter.class);
        serviceBeforeFilterChain.addFilter(extensionLoader.gets(tq.cn.filter.ServiceBeforeFilter.class));
        serviceAfterFilterChain.addFilter(extensionLoader.gets(tq.cn.filter.ServiceAfterFilter.class));
    }
    public static void initClientFilter() throws IOException, ClassNotFoundException {
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(tq.cn.filter.ClientAfterFilter.class);
        extensionLoader.loadExtension(tq.cn.filter.ClientBeforeFilter.class);
        clientBeforeFilterChain.addFilter(extensionLoader.gets(tq.cn.filter.ClientBeforeFilter.class));
        clientAfterFilterChain.addFilter(extensionLoader.gets(tq.cn.filter.ClientAfterFilter.class));
    }

    public static tq.cn.filter.FilterChain getServiceBeforeFilterChain(){
        return serviceBeforeFilterChain;
    }
    public static tq.cn.filter.FilterChain getServiceAfterFilterChain(){
        return serviceAfterFilterChain;
    }
    public static tq.cn.filter.FilterChain getClientBeforeFilterChain(){
        return clientBeforeFilterChain;
    }
    public static tq.cn.filter.FilterChain getClientAfterFilterChain(){
        return clientAfterFilterChain;
    }
}
