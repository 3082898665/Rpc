package tq.cn.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tq.cn.filter.FilterConfig;
import tq.cn.filter.FilterData;
import tq.cn.filter.client.ClientLogFilter;
import tq.cn.common.RpcResponse;
import tq.cn.common.constants.MsgStatus;
import tq.cn.protocol.MsgHeader;
import tq.cn.protocol.RpcProtocol;

/**
 * @description:
 */
public class ServiceAfterFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) {
        final FilterData filterData = new FilterData();
        filterData.setData(protocol.getBody());
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();
        try {
            FilterConfig.getServiceAfterFilterChain().doFilter(filterData);
        } catch (Exception e) {
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setException(e);
            logger.error("after process request {} error", header.getRequestId(), e);
        }
        ctx.writeAndFlush(protocol);
    }
}
