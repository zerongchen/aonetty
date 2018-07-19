package com.netty.aonet.http.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {


    public void run(int port){
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.TRACE))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel( SocketChannel socketChannel ) throws Exception {
                        socketChannel.pipeline()
                                .addLast("http-codec",new HttpServerCodec())
                                .addLast("aggregator",new HttpObjectAggregator(65536))
                                .addLast("http-chunked",new ChunkedWriteHandler())
                                .addLast("hander",new WebSocketServerHandler());
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("start on port "+port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args){
        new WebSocketServer().run(8111);
    }

}
