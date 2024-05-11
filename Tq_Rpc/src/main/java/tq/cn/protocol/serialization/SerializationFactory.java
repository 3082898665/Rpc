package tq.cn.protocol.serialization;

import tq.cn.spi.ExtensionLoader;

/**
 * @description: 序列化工厂
 */
public class SerializationFactory {


    public static tq.cn.protocol.serialization.RpcSerialization get(String serialization) throws Exception {

        return ExtensionLoader.getInstance().get(serialization);

    }

    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(tq.cn.protocol.serialization.RpcSerialization.class);
    }
}
