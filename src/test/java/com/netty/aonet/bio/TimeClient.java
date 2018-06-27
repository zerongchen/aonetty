package com.netty.aonet.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeClient {

    public static void main(String[] args){


        int port=8090;
        try {
            if (args!=null && args.length>0){
                port=Integer.valueOf(args[0]);
            }
        }catch (Exception e){}

        Socket socket = null;
        BufferedReader in =null;

        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1",port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out= new PrintWriter(socket.getOutputStream(),true);

            out.print("QUERY TIME ORDER");
            out.flush();

            System.out.println("send order to server success");
            String resp= in.readLine();
            System.out.println("now is "+resp);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(in!=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out!=null){
                out.close();
            }
            if (socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
