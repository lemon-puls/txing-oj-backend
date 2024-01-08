package com.bitdf.txing.oj.chat.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;


/**
 * @author Lizhiwei
 * @date 2024/1/3 11:09:16
 * 注释：
 */
public class NettyUtil {
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<Long> USERID = AttributeKey.valueOf("userId");

    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> attributeKey) {
        Attribute<T> attr = channel.attr(attributeKey);
        return attr.get();
    }

}
