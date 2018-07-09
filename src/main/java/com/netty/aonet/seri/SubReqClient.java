package com.netty.aonet.seri;

public class SubReqClient {

    public static void main(String[] args){

        int port=8010;
        if(args!=null&&args.length>0){
            port=Integer.parseInt(args[0]);
        }
        new SubReqClient().connect(port,"127.0.0.1");

    }

    private void connect(int port,String host) {
    }
}
