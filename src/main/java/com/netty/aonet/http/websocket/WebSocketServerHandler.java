package com.netty.aonet.http.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private WebSocketServerHandshaker socketServerHandshaker;

    @Override
    protected void channelRead0( ChannelHandlerContext channelHandlerContext, Object o ) throws Exception {

        if (o instanceof FullHttpRequest){
            //http
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        }
        else if(o instanceof WebSocketFrame){
            //webSocket
            handleWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    @Override
    public void channelReadComplete( ChannelHandlerContext ctx ) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx , FullHttpRequest request){
        //http 解码失败
        if(!request.decoderResult().isSuccess()
                || !"websocket".equalsIgnoreCase(request.headers().get("Upgrade"))){

            sendHttpResponse(ctx,request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //构造握手响应返回
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8111/websocket",null,false);
        socketServerHandshaker =factory.newHandshaker(request);
        if (socketServerHandshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            socketServerHandshaker.handshake(ctx.channel(),request);
        }

    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx , WebSocketFrame frame){
        //关闭链路指令
        if (frame instanceof CloseWebSocketFrame){
            socketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        //PING 消息
        if (frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //此处仅支持文本
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame type not support ",frame.getClass().getName()));
        }
        String context =  ((TextWebSocketFrame) frame).text();

        if(logger.isInfoEnabled()){
            logger.info("%s receive %s",ctx.channel(),context);
        }

        ctx.write(new TextWebSocketFrame(context+"wellcome to netty webSocket"+ new java.util.Date().toString()));



    }

    private void sendHttpResponse( ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse defaultFullHttpResponse ) {

        if (defaultFullHttpResponse.status().code()!=200){
            ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.status().toString(), CharsetUtil.UTF_8);
            defaultFullHttpResponse.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(defaultFullHttpResponse,defaultFullHttpResponse.content().readableBytes());
        }

        //非 keep alive 关闭链接
        ChannelFuture ch = ctx.channel().writeAndFlush(defaultFullHttpResponse);
        if(!HttpUtil.isKeepAlive(request) || defaultFullHttpResponse.status().code()!=200){
            ch.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
