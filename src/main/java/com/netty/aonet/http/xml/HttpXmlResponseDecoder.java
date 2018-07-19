package com.netty.aonet.http.xml;

import com.google.gson.Gson;
import com.netty.aonet.http.xml.model.HttpXmlRequest;
import com.netty.aonet.http.xml.model.HttpXmlResponse;
import com.netty.aonet.http.xml.model.Order;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

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
        FullHttpResponse msg = (FullHttpResponse)o;

        String content =  msg.content().toString(CharsetUtil.UTF_8);
        Gson gson = new Gson();
        Order order = gson.fromJson(content, Order.class);
        HttpXmlResponse resHttpXmlResponse = new HttpXmlResponse(msg, order);
        out.add(resHttpXmlResponse);
    }
}