package com.netty.aonet.selfdefinded.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

public class NettyMarshallingDecoder extends MarshallingDecoder {

    public NettyMarshallingDecoder( UnmarshallerProvider provider, int i ) {
        super(provider,i);
    }
    public Object decode( ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx,in);
    }
}
