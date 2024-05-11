package tq.cn.protocol;

import java.io.Serializable;

/**
 * @description: 消息
 */
public class RpcProtocol<T> implements Serializable {

    private tq.cn.protocol.MsgHeader header;
    private T body;

    public tq.cn.protocol.MsgHeader getHeader() {
        return header;
    }

    public void setHeader(tq.cn.protocol.MsgHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
