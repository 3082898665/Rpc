package tq.cn.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tq.cn.common.RpcRequest;
import tq.cn.poll.ThreadPollFactory;
import tq.cn.protocol.RpcProtocol;

/**
 * @description: 处理消费方发送数据并且调用方法
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {


    public RpcRequestHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ThreadPollFactory.submitRequest(ctx,protocol);
    }

}

