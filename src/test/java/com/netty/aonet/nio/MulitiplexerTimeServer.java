package com.netty.aonet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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




            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
