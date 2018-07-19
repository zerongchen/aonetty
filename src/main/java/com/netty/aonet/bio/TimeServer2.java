package com.netty.aonet.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞IO
 * @author chenzr
 * @since 20180604
 * @version 1.0
 */

/**
 *
 *     ServerSocket :
 */
public class TimeServer2 {

    public static void main(String[] args){

        int port=8080;
        try {
            if (args!=null && args.length>0){
                port=Integer.valueOf(args[0]);
            }
        }catch (Exception e){}

        bindServer(port);

    }

    /**
     * ServerSocket 负责绑定IP,启动监听port,Socket 负责发起链接操作。成功后双方通过输入和输出流进行同步阻塞式通信
     * @param port
     */
    public static void bindServer(int port){
        ServerSocket server = null;

        try {
            server=new ServerSocket(port);
            System.out.println("server is start ,port is "+port);

            Socket socket = null;
            TimeServerHandleExecutePool pool = new TimeServerHandleExecutePool(50,200);
            while (true){
                socket=server.accept();
                pool.execute(new TimeServerHandle(socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(server!=null){
                System.out.println("the time server close");
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server=null;
            }
        }
    }

}
