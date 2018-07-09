package com.netty.aonet.seri;

import com.netty.aonet.nty.TimeServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SubReqServer {

    public static void main(String[] args){

        int port=8010;
        if(args!=null&&args.length>0){
            port=Integer.parseInt(args[0]);
        }
        new SubReqServer().bind(port);

    }

    /**
     * weakCachingResolver 创建线程安全的weakReferenceMap 对类加载器进行缓存
     * 它支持多线程并发访问，当虚拟机内存不足的时候，或释放内存中的内存,防止内存泄漏
     * @param port
     */
    public void bind(int port){
        //配置NIO线程组
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel( SocketChannel channel ) throws Exception {
                        channel.pipeline()
                                .addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingResolver(this.getClass().getClassLoader())))
                                .addLast(new ObjectEncoder())
                                .addLast(new ChannelDuplexHandler(){
                                    @Override
                                    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
                                        super.channelRead(ctx, msg);
                                    }
                                });
                    }
                });
        //绑定端口，同步等待成功

        try {
            ChannelFuture f = b.bind(port).sync();

            //等待服务端关闭端口
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //退出，释放线程池资源
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
