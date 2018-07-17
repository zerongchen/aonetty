package com.netty.aonet.http.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

    private static String path=System.getProperty("user.dir");

    public void run(final String url ,int post) throws InterruptedException {

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel( SocketChannel socketChannel ) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("http-decode",new HttpRequestDecoder())
                                    .addLast("http-aggregator",new HttpObjectAggregator(65536))
                                    .addLast("http-encoder",new HttpRequestEncoder())
                                    .addLast("http-chunked",new ChunkedWriteHandler())
                                    .addLast("fileServerHander",new HttpFileServerHandler(url));

                        }
                    });

            ChannelFuture future = bootstrap.bind(url,post).sync();
            System.out.println("HTTP 文件目录启动在 port"+post+"   url:"+url);
            future.channel().closeFuture().sync();

        }catch (Exception e){
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args){
        try {
            new HttpFileServer().run(path,8010);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
