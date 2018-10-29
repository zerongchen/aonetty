package com.netty.aonet.selfdefinded.MsgHandler;

import com.netty.aonet.selfdefinded.model.NettyMsg;
import com.netty.aonet.selfdefinded.util.MarshallingCodecFactory;
import com.netty.aonet.selfdefinded.util.NettyMarshallingEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

public class NettyMsgEncoder extends MessageToMessageEncoder<NettyMsg> {

    private static NettyMarshallingEncoder marshallingEncoder;

    public NettyMsgEncoder(){
        this.marshallingEncoder = MarshallingCodecFactory.buildMarshallingEncoder();
    }

    @Override
    protected void encode( ChannelHandlerContext channelHandlerContext, NettyMsg nettyMsg, List<Object> list ) throws Exception {
        if (nettyMsg==null || nettyMsg.getHeader()==null){
            throw new Exception("msg is null");
        }
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(nettyMsg.getHeader().getCrcCode());
        byteBuf.writeInt(nettyMsg.getHeader().getLength());
        byteBuf.writeLong(nettyMsg.getHeader().getSessionID());
        byteBuf.writeByte(nettyMsg.getHeader().getType());
        byteBuf.writeByte(nettyMsg.getHeader().getPriority());
        byteBuf.writeInt(nettyMsg.getHeader().getAttachment().size());

        String key;
        byte[] keyArray;
        Object value;
        for (Map.Entry<String,Object> param:nettyMsg.getHeader().getAttachment().entrySet()){
            key=param.getKey();
            keyArray=key.getBytes(CharsetUtil.UTF_8);
            byteBuf.writeInt(keyArray.length);
            byteBuf.writeBytes(keyArray);
            value=param.getValue();
            marshallingEncoder.encode(channelHandlerContext,value,byteBuf);
        }
        key = null;
        keyArray = null;
        value = null;
        if(nettyMsg.getBody() != null){
            marshallingEncoder.encode(channelHandlerContext, nettyMsg.getBody(), byteBuf);
        }else {

        }
        // 在第4个字节出写入Buffer的长度
        int readableBytes = byteBuf.readableBytes();
        byteBuf.setInt(4, readableBytes);

        // 把Message添加到List传递到下一个Handler
        list.add(byteBuf);
    }
}
