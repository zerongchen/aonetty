package com.netty.aonet.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Client {
        private static Logger logger = LoggerFactory.getLogger(Client.class);

        public static final int MessageReceived = 0x99;
        private int scanPort;

        public Client(int scanPort) {
            this.scanPort = scanPort;
        }

        private static class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

            @Override
            protected void channelRead0( ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                String response = msg.content().toString(CharsetUtil.UTF_8);

                if(response.startsWith("结果：")){
                    System.out.println(response);
                    ctx.close();
                }
            }
        }

        public static void sendPackage(int port) {
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioDatagramChannel.class)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(new ClientHandler());

                Channel ch = b.bind(0).sync().channel();

                //先网段内所有机器广播UDP信息
                ch.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer("hello!!!", CharsetUtil.UTF_8),
                        new InetSocketAddress("255.255.255.255", port))).sync();

                logger.info("Search, sendPackage()");
                // QuoteOfTheMomentClientHandler will close the DatagramChannel when a
                // response is received.  If the channel is not closed within 5 seconds,
                // print an error message and quit.
                // 等待15秒钟
                if (!ch.closeFuture().await(15000)) {
                    logger.info("Search request timed out.");
                }
            }catch (Exception e){
                e.printStackTrace();
                logger.info("Search, An Error Occur ==>" + e);
            }finally {
                group.shutdownGracefully();
            }
        }

        public static void main(String[] args){
            sendPackage(8888);
        }
    }