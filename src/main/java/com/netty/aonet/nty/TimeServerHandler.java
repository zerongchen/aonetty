package com.netty.aonet.nty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeServerHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        byte[] bs = new byte[buf.readableBytes()];
        buf.readBytes(bs);
        String body = new String (bs,"utf-8");
        System.out.println("the time server receive order is "+body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
        ByteBuf byteBuf = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(byteBuf);
    }

    @Override
    public void channelReadComplete( ChannelHandlerContext ctx ) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
