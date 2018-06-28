package com.netty.aonet.nty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel( SocketChannel socketChannel ) throws Exception {
        socketChannel.pipeline().addLast(new TimeServerHandler());

    }
}
