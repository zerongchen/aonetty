package com.netty.aonet.aio;

import com.netty.aonet.util.CallStack;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements CompletionHandler<Void,AsyncTimeClientHandler>, Runnable {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host,int port){
        this.host=host;
        this.port=port;
        try {
            client=AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch=new CountDownLatch(1);
        client.connect(new InetSocketAddress(host,port),this,this);
        try {
            //等待latch.countDown执行
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed( Void result, AsyncTimeClientHandler attachment ) {
        CallStack.printCallStatck();
        byte[] bytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer byteBuffer =  ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed( Integer result, ByteBuffer attachment ) {
                if (attachment.hasRemaining()){
                    //类似递归
                    client.write(attachment,attachment,this);
                }else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed( Integer result, ByteBuffer attachment ) {
                            attachment.flip();
                            byte[] bs = new byte[attachment.remaining()];
                            attachment.get(bs);
                            try {
                                String body = new String (bs,"utf-8");

                                System.out.println("Now is "+body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed( Throwable exc, ByteBuffer attachment ) {
                            try {
                                client.close();
                                latch.countDown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed( Throwable exc, ByteBuffer attachment ) {

            }
        });
        if (!byteBuffer.hasRemaining()){
            System.out.println("send to server success");
        }
    }

    @Override
    public void failed( Throwable exc, AsyncTimeClientHandler attachment ) {
        exc.printStackTrace();
        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
