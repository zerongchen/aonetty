package com.netty.aonet.aio;

public class TimeServer {

    public static void main(String[] args){

        int port=8090;
        try {
            if (args!=null && args.length>0){
                port=Integer.valueOf(args[0]);
            }
        }catch (Exception e){}

        new Thread(new AsyncTimeServerHandle(port),"AIO-server").start();

    }
}
