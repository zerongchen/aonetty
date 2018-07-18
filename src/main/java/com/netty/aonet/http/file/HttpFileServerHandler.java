package com.netty.aonet.http.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private String url;

    public HttpFileServerHandler( String url ) {
        this.url=url;
    }


    @Override
    protected void channelRead0( ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest ) throws Exception {
        if(!fullHttpRequest.decoderResult().isSuccess()){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (fullHttpRequest.method()!= HttpMethod.GET){
            sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        final String uri = fullHttpRequest.uri();
        final String path = sanitizeUri(uri);
        if (path==null){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        File file = new File(path);
        if (file.isHidden()|| !file.exists()){
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        if(file.isDirectory()){
            if (uri.endsWith("/")){
                sendList(ctx,file);
            }else {
                sendRedirct(ctx,uri+"/");
            }
            return;
        }
        if (!file.isFile()){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile = null;

        try {

            randomAccessFile = new RandomAccessFile(file,"r");
        }catch (Exception e){
            e.printStackTrace();
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        long filelength = randomAccessFile.length();
        //不能用DefaultFullHttpResponse
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        setContentLength(response,filelength);
        setContentTypeHeader(response,file);
        if ( isKeepAlive(fullHttpRequest)){
            response.headers().set(CONNECTION,KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture future ;
        future = ctx.write(new ChunkedFile(randomAccessFile,0,filelength,8192),ctx.newProgressivePromise());
        future.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed( ChannelProgressiveFuture channelProgressiveFuture, long l, long l1 ) throws Exception {
                if(l1<0){
                    System.err.println("transfer progress "+l);
                }else {
                    System.out.println("transfer progress "+l+"/"+l1);
                }
            }

            @Override
            public void operationComplete( ChannelProgressiveFuture channelProgressiveFuture ) throws Exception {
                System.out.println("transfer progress complete");
            }
        });
        ChannelFuture lastfuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!isKeepAlive(fullHttpRequest)){
            lastfuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static final Pattern INSECURE_URL = Pattern.compile(".*[<>&\"].*");
    private static final Pattern ALLOWFILENAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private boolean isKeepAlive( FullHttpRequest request ) {
        return HttpUtil.isKeepAlive(request);
//        return request.headers().contains(KEEP_ALIVE);
    }

    private void  setContentTypeHeader( HttpResponse response, File file ) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,mimetypesFileTypeMap.getContentType(file.getPath()));
    }

    private void setContentLength( HttpResponse response, long filelength ) {
//        response.headers().set(CONTENT_LENGTH,filelength);
        HttpUtil.setContentLength(response, filelength);
    }

    private void sendRedirct( ChannelHandlerContext ctx, String s ) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FOUND);
        response.headers().set(LOCATION,s);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendList( ChannelHandlerContext ctx, File dir ) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("目录:");
        buf.append("</title></head>\r\n");

        buf.append("<h3>");
        buf.append(dirPath).append(" 目录：");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li><a href=' ../'</a>上一级</li>\r\n");
        for (File f : dir.listFiles()){
            if(f.isHidden() || !f.canRead()){
                continue;
            }
            String name = f.getName();
            if(!ALLOWFILENAME.matcher(name).matches()){
                continue;
            }
            buf.append("<li>   <a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    /**
     * 校验路径
     * @param uri
     * @return
     */
    private String sanitizeUri( String uri ) {

        try {
            uri= URLDecoder.decode(uri,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            try {
                uri = URLDecoder.decode(uri,"ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        if(!uri.startsWith(url)){
            return null;
        }
        if(!uri.startsWith("/")){
            return null;
        }
        uri.replace('/',File.separatorChar);

        if (uri.contains(File.separator+".")
                || uri.contains("."+File.separator)
                || uri.endsWith(".")
                || uri.startsWith(".")
                || INSECURE_URL.matcher(uri).matches()
                ){
            return null;
        }
        return "E:"+uri;
    }

    private void sendError( ChannelHandlerContext ctx, HttpResponseStatus status ) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,Unpooled.copiedBuffer("Failure :"+status.toString()+"\r\n",CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE,"text/plain;charset=utf-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
