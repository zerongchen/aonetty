package com.netty.aonet.http.xml;

import com.google.gson.Gson;
import com.netty.aonet.http.xml.model.HttpXmlResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;

import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder {

    protected void encode( ChannelHandlerContext ctx, Object o, List out) throws Exception {
        HttpXmlResponse msg = (HttpXmlResponse) o;
//        ByteBuf body = encode0(ctx, msg.getResult());
        Gson gson = new Gson();
        ByteBuf body = Unpooled.copiedBuffer(gson.toJson(msg.getResult()).getBytes());
        FullHttpResponse response = msg.getHttpResponse();
        if (response == null) {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, body);
        } else {
            response = new DefaultFullHttpResponse(msg.getHttpResponse()
                    .getProtocolVersion(), msg.getHttpResponse().getStatus(),
                    body);
        }
        response.headers().set(CONTENT_TYPE, "text/xml");
        HttpUtil.setContentLength(response, body.readableBytes());
        out.add(response);
    }
}