package com.netty.aonet.selfdefinded.MsgHandler;

import com.netty.aonet.selfdefinded.util.MessageType;
import com.netty.aonet.selfdefinded.model.Header;
import com.netty.aonet.selfdefinded.model.NettyMsg;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 握手成功后，由客户端主动发送心跳包，检测可用性
 */
public class HeaderBeatReqHandler extends ChannelDuplexHandler {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        NettyMsg nettyMsg = (NettyMsg)msg;
        //握手成功，主动发送心跳消息，进入无限发送心跳包模式

        if (nettyMsg.getHeader()!=null && nettyMsg.getHeader().getType()== MessageType.LOGIN_RESP.getValue()){
            heartBeat=ctx.executor().scheduleWithFixedDelay(new HeaderBeatReqHandler.HeartBeatTasK(ctx),0,5000, TimeUnit.SECONDS);
        }else if (nettyMsg.getHeader()!=null && nettyMsg.getHeader().getType()== MessageType.HEARBEAT_RESP.getValue()){
            //接受服务端心跳应答消息
            System.out.println("Client receive server heart beat message ->"+nettyMsg);
        }else {
            ctx.fireChannelRead(msg);
        }
    }


   private class HeartBeatTasK implements Runnable{
       private ChannelHandlerContext ctx;

       public HeartBeatTasK(ChannelHandlerContext ctx){
           this.ctx=ctx;
       }

       @Override
       public void run() {
            NettyMsg msg = buildLHearBeat();
            System.out.println("Client send hear beat message top server "+msg);
            ctx.writeAndFlush(msg);
       }
   }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        if (heartBeat!=null){
            heartBeat.cancel(true);
            heartBeat=null;
        }
        ctx.fireExceptionCaught(cause);
    }

    /**
     * 构造心跳消息
     * @return
     */
    private NettyMsg buildLHearBeat() {
        NettyMsg message = new NettyMsg();
        Header header = new Header();
        header.setType((MessageType.HEARBEAT_REQ.getValue()));
        message.setHeader(header);
        return message;
    }
}
