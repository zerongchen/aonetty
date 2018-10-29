package com.netty.aonet.selfdefinded.MsgHandler;

import com.netty.aonet.selfdefinded.util.MessageType;
import com.netty.aonet.selfdefinded.model.Header;
import com.netty.aonet.selfdefinded.model.NettyMsg;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 握手成功后，由客户端主动发送心跳包，检测可用性
 */
public class HeaderBeatRespHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        NettyMsg nettyMsg = (NettyMsg)msg;
        //握手成功，主动发送心跳消息，进入无限发送心跳包模式

        if (nettyMsg.getHeader()!=null && nettyMsg.getHeader().getType()== MessageType.HEARBEAT_REQ.getValue()){
            System.out.println("Receive client  server heart beat message ->"+nettyMsg);
            NettyMsg bearBeat = buildLHearBeat();
            System.out.println("send hear beat to client "+bearBeat);
            ctx.writeAndFlush(bearBeat);
        }else {
            ctx.fireChannelRead(msg);
        }
    }


    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {

        ctx.fireExceptionCaught(cause);
    }

    /**
     * 构造心跳消息
     * @return
     */
    private NettyMsg buildLHearBeat() {
        NettyMsg message = new NettyMsg();
        Header header = new Header();
        header.setType((MessageType.HEARBEAT_RESP.getValue()));
        message.setHeader(header);
        return message;
    }
}
