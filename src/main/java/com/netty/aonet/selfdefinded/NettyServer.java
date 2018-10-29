package com.netty.aonet.selfdefinded;

import com.netty.aonet.selfdefinded.MsgHandler.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 利用netty的ChannelPipeline 和channelHandler 机制，实现谢解耦和业务产品的定制（类似APO但是比AOP性能更高）
 */
public class NettyServer {

    public void bind() {
    //配置客户端NIO线程组
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b =new ServerBootstrap();
            b.group(loopGroup,workGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel( SocketChannel ch ) throws Exception {
                            //防止单条信息过大，导致内存溢出或者畸形码流导致解码错误引起内存分配失败
                            ch.pipeline().addLast(new NettyMsgDecoder(1024*1024,4,4))
                                    .addLast(new NettyMsgEncoder())
                                    .addLast("readTimeoutHandler",new ReadTimeoutHandler(50))
                                    .addLast("loginAuthHandler",new LoginAuthRespqHandler())
                                    .addLast("HeartBeatHandler",new HeaderBeatRespHandler());

                        }
                    });
            b.bind("192.168.3.163",8888).sync();
            System.out.println("server start ok : 192.168.3.163:8888");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new NettyServer().bind();
    }
}
