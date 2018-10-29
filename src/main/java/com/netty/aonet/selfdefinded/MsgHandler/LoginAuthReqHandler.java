package com.netty.aonet.selfdefinded.MsgHandler;

import com.netty.aonet.selfdefinded.util.MessageType;
import com.netty.aonet.selfdefinded.model.Header;
import com.netty.aonet.selfdefinded.model.NettyMsg;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LoginAuthReqHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }


    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        NettyMsg nettyMsg = (NettyMsg)msg;
        if (nettyMsg.getHeader()!=null && nettyMsg.getHeader().getType()== MessageType.LOGIN_RESP.getValue()){
            byte loginResult = (byte)(nettyMsg.getBody());
            if (loginResult!=(byte) 0){
                //握手失败，关闭链接
                ctx.close();
            }else {
                System.out.println("login "+nettyMsg);
                ctx.fireChannelRead(msg);
            }
        }else {
            ctx.fireChannelRead(msg);
        }


    }

    /**
     * 构造请求消息
     * @return
     */
    private NettyMsg buildLoginReq() {
        NettyMsg message = new NettyMsg();
        Header header = new Header();
        header.setType((MessageType.LOGIN_REQ.getValue()));
        message.setHeader(header);
        message.setBody("It is request");
        return message;
    }
}
