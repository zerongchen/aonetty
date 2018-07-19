package com.netty.aonet.http.xml;

import com.netty.aonet.http.xml.model.Order;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

public class HttpXmlClient {

    public void connect(int port) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer() {

                        @Override
                        public void initChannel(Channel ch)
                                throws Exception {
                            ch.pipeline().addLast("http-decoder",new HttpResponseDecoder());
                            ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            // XML解码器
                            ch.pipeline().addLast("xml-decoder",new HttpXmlResponseDecoder(Order.class,true));
                            ch.pipeline().addLast("http-encoder",new HttpRequestEncoder());
                            ch.pipeline().addLast("xml-encoder",new HttpXmlRequestEncoder());
                            ch.pipeline().addLast("xmlClientHandler",new HttpXmlClientHandle());
                        }
                    });

            // 发起异步连接操作
            ChannelFuture f = b.connect(new InetSocketAddress(port)).sync();

            // 等待客户端链路关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new HttpXmlClient().connect(port);
    }
}