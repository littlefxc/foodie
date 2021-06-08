package com.fengxuechao.io.demo00;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        //这是服务端的IP和端口
        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        final int DEFAULT_SERVER_PORT = 8888;
        //创建Socket
        try (Socket socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT)) {
            //接收消息
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            //发送消息
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            //获取用户输入的消息
            BufferedReader userReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            String msg = null;
            //循环的话客户端就可以一直输入消息，不然执行完try catch会自动释放资源，也就是断开连接
            while (true) {
                String input = userReader.readLine();
                //写入客户端要发送的消息。因为服务端用readLine获取消息，其以\n为终点，所以要在消息最后加上\n
                writer.write(input + "\n");
                writer.flush();
                msg = reader.readLine();
                System.out.println(msg);
                //如果客户端输入quit就可以跳出循环、断开连接了
                if(input.equals("quit")){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

