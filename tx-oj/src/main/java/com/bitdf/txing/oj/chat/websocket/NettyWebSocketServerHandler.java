package com.bitdf.txing.oj.chat.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.bitdf.txing.oj.chat.domain.dto.WsAuthorize;
import com.bitdf.txing.oj.chat.domain.vo.request.WsBaseRequest;
import com.bitdf.txing.oj.chat.enume.WsRequestTypeEnum;
import com.bitdf.txing.oj.chat.service.business.WebSocketService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Lizhiwei
 * @date 2024/1/2 19:11:47
 * 注释：
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        WsBaseRequest wsBaseRequest = JSONUtil.toBean(textWebSocketFrame.text(), WsBaseRequest.class);
        WsRequestTypeEnum wsRequestTypeEnum = WsRequestTypeEnum.of(wsBaseRequest.getType());
        switch (wsRequestTypeEnum) {
            case HEARTBEAT:
                log.info("接收到了心跳检测报文");
                break;
            default:
                log.info("接受到未知类型的ws消息");
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("触发channelInactive掉线 [{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = getWebSocketService();
    }

    private WebSocketService getWebSocketService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("触发channelInactive掉线[{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        this.webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 读空闲事件
                userOffLine(ctx);
            }
        } else if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // websocket握手完成
            this.webSocketService.connect(ctx.channel());
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            Long userId = NettyUtil.getAttr(ctx.channel(), NettyUtil.USERID);
            if (StringUtils.isNotBlank(token)) {
                this.webSocketService.authorize(ctx.channel(), new WsAuthorize(token,userId));
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("ws发生异常：{}", cause);
        ctx.channel().close();
    }


}
