package tq.cn.filter.service;

import tq.cn.config.RpcProperties;
import tq.cn.filter.FilterData;
import tq.cn.filter.ServiceBeforeFilter;

import java.util.Map;

/**
 * @description: token拦截器
 */
public class ServiceTokenFilter implements ServiceBeforeFilter {

    @Override
    public void doFilter(FilterData filterData) {
        final Map<String, Object> attachments = filterData.getClientAttachments();
        final Map<String, Object> serviceAttachments = RpcProperties.getInstance().getServiceAttachments();
        if (!attachments.getOrDefault("token","").equals(serviceAttachments.getOrDefault("token",""))){
            throw new IllegalArgumentException("token不正确");
        }
    }

}
