package com.netty.aonet.nty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeClientHandler extends ChannelDuplexHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //after
    private int counter;
    private byte[] req;

    TimeClientHandler(){
        req=("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
    }

    /**
     * client 链接成功后会调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        ByteBuf message = null;
        for (int i=0;i<100;i++){
            message=Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    /**
     * server 回应消息时调用该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        ByteBuf b = (ByteBuf) msg;
        byte[] bs = new byte[b.readableBytes()];
        b.readBytes(bs);
        String body = new String(bs,"utf-8");
        System.out.println("now is "+ body +" ; the counter is "+counter++);
    }

    /**
     * 异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        logger.error("exception cause by ",cause);
        ctx.close();
    }
}
