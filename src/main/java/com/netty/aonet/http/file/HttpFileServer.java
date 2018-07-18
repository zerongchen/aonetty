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
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

    private static String path="/otherProject";

    public void run(final String url ,int post) throws InterruptedException {

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.TRACE))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel( SocketChannel socketChannel ) throws Exception {
                            socketChannel.pipeline()
                                    //它负责把字节解码成Http请求
                                    .addLast("http-decode",new HttpRequestDecoder())
                                    //它负责把多个HttpMessage组装成一个完整的Http请求或者响应。
                                    // 到底是组装成请求还是响应，则取决于它所处理的内容是请求的内容，还是响应的内容。
                                    // 这其实可以通过Inbound和Outbound来判断，对于Server端而言，在Inbound 端接收请求，在Outbound端返回响应。
                                    .addLast("http-aggregator",new HttpObjectAggregator(65536))
                                    //当Server处理完消息后，需要向Client发送响应。那么需要把响应编码成字节，再发送出去。故添加HttpResponseEncoder处理器。
                                    .addLast("http-encoder",new HttpResponseEncoder())
                                    .addLast("http-chunked",new ChunkedWriteHandler())
                                    .addLast("fileServerHandler",new HttpFileServerHandler(url));

                        }
                    });

            ChannelFuture future = bootstrap.bind(post).sync();
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
