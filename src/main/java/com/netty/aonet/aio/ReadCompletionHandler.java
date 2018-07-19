package com.netty.aonet.aio;

import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {

    private AsynchronousSocketChannel socketChannel;

    public ReadCompletionHandler( AsynchronousSocketChannel result ) {

        if(this.socketChannel==null){
            this.socketChannel = result;
        }
    }


    @Override
    public void completed( Integer result, ByteBuffer attachment ) {
        //mark position 重置 limit大小t为当前长度
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String (body,"utf-8");

            System.out.println("the time server receive order is "+req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
            doWrite(currentTime);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doWrite( String currentTime ) {
        if(!StringUtil.isNullOrEmpty(currentTime)){
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            socketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed( Integer result, ByteBuffer attachment ) {
                    if(attachment.hasRemaining()){
                        socketChannel.write(attachment,attachment,this);
                    }
                }

                @Override
                public void failed( Throwable exc, ByteBuffer attachment ) {
                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

    @Override
    public void failed( Throwable exc, ByteBuffer attachment ) {
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
