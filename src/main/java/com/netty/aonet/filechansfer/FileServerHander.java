package com.netty.aonet.filechansfer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

public class FileServerHander extends SimpleChannelInboundHandler {

    private static final String CR= System.getProperty("line.separator");

    @Override
    protected void channelRead0( ChannelHandlerContext channelHandlerContext, Object o ) throws Exception {
        File file = new File(o.toString());
        if (file.exists()){
            if (!file.isFile()){
                channelHandlerContext.writeAndFlush("not a file "+file+CR);
                return;
            }

            channelHandlerContext.write(file+":"+file.length()+CR);
            RandomAccessFile accessFile = new RandomAccessFile(file,"r");
            FileRegion fileRegion = new DefaultFileRegion(accessFile.getChannel(),0,file.length());
            channelHandlerContext.write(fileRegion);
            channelHandlerContext.writeAndFlush(CR);
        }else {
            channelHandlerContext.writeAndFlush("file not find "+file+CR);
            return;
        }
    }
}
