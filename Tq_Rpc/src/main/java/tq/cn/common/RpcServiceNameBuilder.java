package tq.cn.common;

/**
 * @description: 构建key
 */
public class RpcServiceNameBuilder {


    // key: 服务名 value: 服务提供方s
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }

}
