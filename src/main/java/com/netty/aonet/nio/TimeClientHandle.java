package com.netty.aonet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {

    private volatile boolean stop=false;
    private static Selector selector;
    private String host;
    private int port;

    private static SocketChannel cilentChannel;

    public TimeClientHandle(String host,int port){
        this.host = host==null?"127.0.0.1":host;
        this.port = port;
        try {
            selector = Selector.open();
            cilentChannel = SocketChannel.open();
            cilentChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void stop(){
       this.stop=true;
    }

    @Override
    public void run() {
        try {
            doConnect();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> its = keys.iterator();
                SelectionKey key=null;
                while (its.hasNext()){
                    key = its.next();
                    its.remove();
                    try {
                        handleKey(key);
                    }catch (Exception e){
                        e.printStackTrace();
                        if (key!=null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleKey( SelectionKey key ) throws IOException {
        if (key.isValid()){
            //是否链接成功
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if (sc.finishConnect()){
                    sc.register(selector,SelectionKey.OP_READ);
                    doWrite(sc);
                }else {
                    //链接失败,进程退出
                    System.exit(1);
                }
            }
            if (key.isReadable()){
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBuffer = sc.read(byteBuffer);
                if(readBuffer >0){
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    String body = new String(bytes,"utf-8");
                    System.out.println("Now is " + body);
                    stop();
                }else if (readBuffer<0){
                    key.channel();
                    sc.close();
                }
            }
        }
    }

    private void doConnect() throws IOException {
        //如果链接成功，则注册到多路复用上，发送信息请求，读取应答
        if(cilentChannel.connect(new InetSocketAddress(host,port))){
            cilentChannel.register(selector, SelectionKey.OP_READ);
            doWrite(cilentChannel);
        }else {
            cilentChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }
    private void doWrite( SocketChannel cilentChannel ) throws IOException {

        byte[] bytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer byteBuffer =  ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        cilentChannel.write(byteBuffer);
        if (!byteBuffer.hasRemaining()){
            System.out.println("send to server success");
        }
    }
}
