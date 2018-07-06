package com.netty.aonet.nty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel( SocketChannel socketChannel ) throws Exception {
        //LineBasedFrameDecoder && StringDecoder 主要用于TCP粘包问题
        /**
         * LineBasedFrameDecoder 一次遍历byteBuf中的/n /r/n 如果有这视为结束位置，否者超过最大长度后抛出异常，且忽略之前读到的字节
         * DelimiterBasedFrameDecoder 自定义字符分割TCP内容，防止粘包
         */
//        new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$".getBytes()));
        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new TimeServerHandler());

    }
}
