package com.netty.aonet.http.xml;

import com.netty.aonet.http.xml.model.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder {

    public HttpXmlResponseDecoder(Class clazz) {
        this(clazz, false);
    }
    public HttpXmlResponseDecoder(Class clazz, boolean isPrintlog) {
        super(clazz, isPrintlog);
    }

    @Override
    protected void decode( ChannelHandlerContext ctx, Object o, List out) throws Exception {
        DefaultFullHttpResponse msg = (DefaultFullHttpResponse)o;
        HttpXmlResponse resHttpXmlResponse = new HttpXmlResponse(msg, decode0(
                ctx, msg.content()));
        out.add(resHttpXmlResponse);
    }
}