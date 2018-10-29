package com.netty.aonet.selfdefinded.MsgHandler;

import com.netty.aonet.selfdefinded.model.Header;
import com.netty.aonet.selfdefinded.model.NettyMsg;
import com.netty.aonet.selfdefinded.util.MarshallingCodecFactory;
import com.netty.aonet.selfdefinded.util.NettyMarshallingDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

public class NettyMsgDecoder extends LengthFieldBasedFrameDecoder{

    private NettyMarshallingDecoder marshallingDecoder;

    public NettyMsgDecoder( int maxFrameLength, int lengthFieldOffset, int lengthFieldLength ) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.marshallingDecoder= MarshallingCodecFactory.buildMarshallingDecoder();
    }

    @Override
    protected Object decode( ChannelHandlerContext ctx, ByteBuf in ) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (byteBuf==null){
            return null;
        }
        NettyMsg msg = new NettyMsg();
        Header header = new Header();
        header.setCrcCode(byteBuf.readInt());
        header.setLength(byteBuf.readInt());
        header.setSessionID(byteBuf.readLong());
        header.setType(byteBuf.readByte());
        header.setPriority(byteBuf.readByte());

        int size = byteBuf.readInt();
        if(size > 0){
            Map<String, Object> attach = new HashMap<String, Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for(int i=0; i<size; i++){
                keySize = byteBuf.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attach.put(key, marshallingDecoder.decode(ctx, byteBuf));
            }
            key = null;
            keyArray = null;
            header.setAttachment(attach);
        }
        if(byteBuf.readableBytes() > 0){
            msg.setBody(marshallingDecoder.decode(ctx, byteBuf));
        }
        msg.setHeader(header);
        return msg;
    }
}
