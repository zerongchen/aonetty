package com.netty.aonet.selfdefinded.MsgHandler;

import com.netty.aonet.selfdefinded.util.MessageType;
import com.netty.aonet.selfdefinded.model.Header;
import com.netty.aonet.selfdefinded.model.NettyMsg;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespqHandler extends ChannelDuplexHandler {

    private Map<String,Boolean> nodeCheck=new ConcurrentHashMap<>();

    private String[] whileList={"192.168.3.163"};

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        NettyMsg nettyMsg = (NettyMsg)msg;
        if (nettyMsg.getHeader()!=null && nettyMsg.getHeader().getType()== MessageType.LOGIN_REQ.getValue()){

            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMsg loginResp = null;
            if (nodeCheck.containsKey(nodeIndex)){
                //重复登录
                loginResp=buildLoginResp(MessageType.MsgAction.REFUSE);
            }else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip =address.getHostName();
                boolean isok = false;
                for (String wIP:whileList) {
                    if (wIP.equals(ip))
                    {
                        isok=true;
                        break;
                    }
                }
                loginResp = isok?buildLoginResp(MessageType.MsgAction.AGREE):buildLoginResp(MessageType.MsgAction.REFUSE);
                if (isok) nodeCheck.put(nodeIndex,true);
            }
            System.out.println("the login response is "+loginResp+" body {"+loginResp.getBody()+"}");
            ctx.writeAndFlush(loginResp);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    /**
     * 构造应答消息
     * @param type
     * @return
     */
    private NettyMsg buildLoginResp( MessageType.MsgAction type) {
        NettyMsg message = new NettyMsg();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.getValue());
        message.setHeader(header);
        message.setBody(type.getValue());
        return message;
    }
}
