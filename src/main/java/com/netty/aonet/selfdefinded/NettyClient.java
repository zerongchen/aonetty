package com.netty.aonet.selfdefinded;

import com.netty.aonet.selfdefinded.MsgHandler.HeaderBeatReqHandler;
import com.netty.aonet.selfdefinded.MsgHandler.LoginAuthReqHandler;
import com.netty.aonet.selfdefinded.MsgHandler.NettyMsgDecoder;
import com.netty.aonet.selfdefinded.MsgHandler.NettyMsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 利用netty的ChannelPipeline 和channelHandler 机制，实现谢解耦和业务产品的定制（类似APO但是比AOP性能更高）
 */
public class NettyClient {

    private ScheduledExecutorService executor =  new ScheduledThreadPoolExecutor(1);

    public void connect(int port,String host) {
    //配置客户端NIO线程组
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            Bootstrap b =new Bootstrap();
            b.group(loopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel( SocketChannel ch ) throws Exception {
                            //防止单条信息过大，导致内存溢出或者畸形码流导致解码错误引起内存分配失败
                            ch.pipeline().addLast(new NettyMsgDecoder(1024*1024,4,4))
                                    .addLast("MessageEncoder",new NettyMsgEncoder())
                                    .addLast("readTimeoutHandler",new ReadTimeoutHandler(50))
                                    .addLast("loginAuthHandler",new LoginAuthReqHandler())
                                    .addLast("HeartBeatHandler",new HeaderBeatReqHandler());

                        }
                    });
            //绑定本地端口1,用于服务端重复登录保护，2，一般情况下不允许随机端口
            ChannelFuture future = b.connect(new InetSocketAddress(host,port),new InetSocketAddress("192.168.3.163",8880)).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            //所有资源释放完成之后，清空资源，再次发起重连操作
            executor.execute(this::run);
        }
    }

    private void run() {
        try {
            TimeUnit.SECONDS.sleep(5);
            try {
                connect(8888, "192.168.3.163");
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args){
        new NettyClient().connect(8888, "192.168.3.163");
    }
}
