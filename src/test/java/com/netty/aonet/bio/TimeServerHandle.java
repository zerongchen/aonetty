package com.netty.aonet.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandle implements Runnable {

    private Socket socket;

    public TimeServerHandle(Socket socket){
        this.socket=socket;
    }

    /**
     * (non-Javac)
     * @see Runnable#run()
     */
    @Override
    public void run() {
        BufferedReader in=null;
        PrintWriter out=null;

        try {
            in=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            out=new PrintWriter(socket.getOutputStream(),true);

            String currentTime = null;
            String body=null;
            while (true){
                body=in.readLine();
                if(body==null)
                    break;
                System.out.println("the TimeServer receive order is "+body);

                currentTime="QUERY TIME ORDER".equalsIgnoreCase(body)? new Date(System.currentTimeMillis()).toString():"BAD ORDER";
                out.println(currentTime);
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(out!=null){
                out.close();
                out=null;
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                socket=null;
            }
        }
    }
}
