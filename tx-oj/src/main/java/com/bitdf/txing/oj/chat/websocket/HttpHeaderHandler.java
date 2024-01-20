package com.bitdf.txing.oj.chat.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Optional;

/**
 * @author Lizhiwei
 * @date 2024/1/18 20:48:31
 * 注释：
 */
public class HttpHeaderHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            // 获取Token
            String token = Optional.ofNullable(urlBuilder.getQuery())
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString)
                    .orElse("");
            // 获取userId
            String userId = Optional.ofNullable(urlBuilder.getQuery())
                    .map(k -> k.get("userId"))
                    .map(CharSequence::toString)
                    .orElse("");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            NettyUtil.setAttr(ctx.channel(), NettyUtil.USERID, Long.valueOf(userId));

            request.setUri(urlBuilder.getPath().toString());

            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
