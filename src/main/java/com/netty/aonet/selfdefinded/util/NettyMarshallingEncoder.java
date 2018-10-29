package com.netty.aonet.selfdefinded.util;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

public class NettyMarshallingEncoder extends MarshallingEncoder{


    /**
     * Creates a new encoder.
     *
     * @param provider the {@link MarshallerProvider} to use
     */
    public NettyMarshallingEncoder( MarshallerProvider provider ) {
        super(provider);
    }

    public void encode( ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
       super.encode(ctx,msg,out);
    }
}
