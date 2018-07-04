package com.netty.aonet.nty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {

    public static void main(String[] args){

        int port=8010;
        try {
            if (args!=null && args.length>0){
                port=Integer.valueOf(args[0]);
            }
        }catch (Exception e){}

    new TimeClient().connect(port,"127.0.0.1");
    }

    public void connect(int port , String host){
        //配置客户端NIO 线程组
        EventLoopGroup loop = new NioEventLoopGroup();
        //创建辅助启动类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loop).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel( SocketChannel socketChannel ) throws Exception {
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new TimeClientHandler());
                    }
                });

        //绑定端口，同步等待成功

        try {
            ChannelFuture f = bootstrap.connect(host,port).sync();
            //等待服务端关闭端口
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //退出，释放线程池资源
            loop.shutdownGracefully();
        }
    }
}
