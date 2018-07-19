package com.netty.aonet.nio;

public class TimeServer {

    public static void main(String[] args){

        int port=8090;
        if(args!=null&&args.length>0){
            port=Integer.parseInt(args[0]);
        }


        MulitiplexerTimeServer mulitiplexerTimeServer = new MulitiplexerTimeServer(port);

        new Thread(mulitiplexerTimeServer,"NIO-MulitiplexerTimeServer-001").start();

    }
}
