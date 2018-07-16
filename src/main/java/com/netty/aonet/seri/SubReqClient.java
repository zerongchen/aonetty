package com.netty.aonet.seri;

import com.netty.aonet.seri.model.SubcribeReq;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class SubReqClient {

    public static void main(String[] args){

        int port=8010;
        if(args!=null&&args.length>0){
            port=Integer.parseInt(args[0]);
        }
        new SubReqClient().connect(port,"127.0.0.1");

    }

    private void connect(int port,String host) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel( SocketChannel socketChannel ) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new ObjectDecoder(1024*1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                                .addLast(new ObjectEncoder())
                                .addLast(new ChannelDuplexHandler(){
                                    @Override
                                    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
                                        super.channelActive(ctx);
                                        for (int i = 0;i<10;i++){
                                            ctx.write(req(i));
                                        }
                                        ctx.flush();
                                    }

                                    @Override
                                    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
//                                        super.channelRead(ctx, msg);
                                        System.out.println("response msg is "+msg);
                                    }

                                    @Override
                                    public void channelReadComplete( ChannelHandlerContext ctx ) throws Exception {
//                                        super.channelReadComplete(ctx);
                                        ctx.flush();

                                    }

                                    @Override
                                    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
//                                        super.exceptionCaught(ctx, cause);
                                        cause.printStackTrace();
                                    }

                                    private SubcribeReq req( int id){
                                        SubcribeReq req = new SubcribeReq();
                                        req.setReqId(id);
                                        req.setAddress("beijing");
                                        req.setName("czr");
                                        req.setPhoneNo("1592****5465");
                                        req.setProductName("netty");
                                        return req;
                                    }
                                });
                    }
                });


        try {
            ChannelFuture f = b.connect(host,port).sync();
            //等待服务端关闭端口
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //退出，释放线程池资源
            group.shutdownGracefully();
        }


    }
}
