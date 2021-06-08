package com.fengxuechao.netty.example00;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户端连接: telnet localhost 6666
 *
 * @author fengxuechao
 * @date 2021/4/30
 */
public class BIOServer {

    public static void main(String[] args) throws IOException {
        //线程池机制
        //思路
        //1. 创建一个线程池
        //2. 如果有客户端连接，就创建一个线程，与之通讯(单独写一个方法)
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        //创建 ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了");
        while (true) {
            System.out.println(" 线 程 信 息 id =" + Thread.currentThread().getId() + " 名 字 =" + Thread.currentThread().getName());
            //监听，等待客户端连接 System.out.println("等待连接....");
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");
            //就创建一个线程，与之通讯(单独写一个方法) newCachedThreadPool.execute(new Runnable() {
            newCachedThreadPool.execute(() -> {
                // 我们重写
                // 可以和客户端通讯
                handler(socket);
            });
        }
    }

    //编写一个 handler 方法，和客户端通讯
    public static void handler(Socket socket) {
        //通过 socket 获取输入流
        try (InputStream inputStream = socket.getInputStream()) {
            byte[] bytes = new byte[1024];
            //循环的读取客户端发送的数据
            while (true) {
                System.out.println("线程信息id =" + Thread.currentThread().getId() + "名字=" + Thread.currentThread().getName());
                System.out.println("read....");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    //输出客户端发送的数据
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}