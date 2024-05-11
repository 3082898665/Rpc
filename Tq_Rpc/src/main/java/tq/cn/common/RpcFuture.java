package tq.cn.common;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 */

public class RpcFuture<T> {

    private Promise<T> promise;
    private long timeout;

    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public RpcFuture() {
    }

    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}

