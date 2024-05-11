package tq.cn.protocol.handler.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tq.cn.common.RpcFuture;
import tq.cn.common.RpcRequestHolder;
import tq.cn.common.RpcResponse;
import tq.cn.protocol.RpcProtocol;

/**
 * @description: 响应
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) {
        long requestId = msg.getHeader().getRequestId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(msg.getBody());
    }

}
