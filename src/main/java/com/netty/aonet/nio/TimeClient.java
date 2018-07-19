package com.netty.aonet.nio;

public class TimeClient {

    public static void main(String[] args){

        int port=8090;
        try {
            if (args!=null && args.length>0){
                port=Integer.valueOf(args[0]);
            }
        }catch (Exception e){}

        new Thread(new TimeClientHandle("127.0.0.1",8090),"TimeClient-Server").start();
    }
}
