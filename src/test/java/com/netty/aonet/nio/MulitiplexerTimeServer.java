package com.netty.aonet.nio;

import org.springframework.expression.spel.ast.Selection;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>多路复用类</p>
 * @author chenzr
 *
 */
public class MulitiplexerTimeServer implements Runnable{

    //多路复用器
    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MulitiplexerTimeServer(int port){
        try {
            //起用多路复用器
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            //1024 允许最多同时TCP连接数
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            //监听accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("time server is start in port "+port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStop(){
        this.stop=true;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key=null;
                while (it.hasNext()){
                    key=it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    }catch (Exception e){
                        if(key!=null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //多路复用器关闭后，所有注册在上面的chanel和pipe等资源自动关闭
        if (selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput( SelectionKey key ) {
        //处理请求
        if(key.isValid()){
            if(key.isAcceptable()){
                //accept the new connection
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                try {
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    //add new connection to selector
                    serverSocketChannel.register(selector, Selection.ACC_OPEN);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if(key.isReadable()){
                //read data
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer bf = ByteBuffer.allocate(1024);
                try {
                    int readBytes = sc.read(bf);
                    if (readBytes>0){
                        //limit重置
                        bf.flip();
                        byte[] bytes = new byte[bf.remaining()];
                        //mark和position会重置移动
                        bf.get(bytes);
                        String body = new String(bytes,"utf-8");
                        System.out.println("the time server receive order is "+body);
                        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
                        doWrite(sc,currentTime);
                    }else if(readBytes<0) {
                        key.channel().close();
                        sc.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doWrite( SocketChannel sc, String currentTime ) {
        if(!StringUtils.isEmpty(currentTime)){
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            try {
                sc.write(writeBuffer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
