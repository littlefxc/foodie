package com.fengxuechao.io.demo01;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private int DEFAULT_PORT = 8888;
    /**
     * 创建一个Map存储在线用户的信息。这个map可以统计在线用户、针对这些用户可以转发其他用户发送的消息
     * 因为会有多个线程操作这个map，所以为了安全起见用ConcurrentHashMap
     * 在这里key就是客户端的端口号，但在实际中肯定不会用端口号区分用户，如果是web的话一般用session。
     * value是IO的Writer，用以存储客户端发送的消息
     */
    private Map<Integer, Writer> map=new ConcurrentHashMap<>();
    /**
     * 创建线程池，线程上限为10个，如果第11个客户端请求进来，服务器会接收但是不会去分配线程处理它。
     * 前10个客户端的聊天记录，它看不见。当有一个客户端下线时，这第11个客户端就会被分配线程，服务器显示在线
     * 大家可以把10再设置小一点，测试看看
     * */
    private ExecutorService executorService= Executors.newFixedThreadPool(10);
    //客户端连接时往map添加客户端
    public void addClient(Socket socket) throws IOException {
        if (socket != null) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            map.put(socket.getPort(), writer);
            System.out.println("Client["+socket.getPort()+"]:Online");
        }
    }

    //断开连接时map里移除客户端
    public void removeClient(Socket socket) throws Exception {
        if (socket != null) {
            if (map.containsKey(socket.getPort())) {
                map.get(socket.getPort()).close();
                map.remove(socket.getPort());
            }
            System.out.println("Client[" + socket.getPort() + "]Offline");
        }
    }

    //转发客户端消息，这个方法就是把消息发送给在线的其他的所有客户端
    public void sendMessage(Socket socket, String msg) throws IOException {
        //遍历在线客户端
        for (Integer port : map.keySet()) {
            //发送给在线的其他客户端
            if (port != socket.getPort()) {
                Writer writer = map.get(port);
                writer.write(msg);
                writer.flush();
            }
        }
    }

    //接收客户端请求，并分配Handler去处理请求
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            System.out.println("Server Start,The Port is:"+DEFAULT_PORT);
            while (true){
                //等待客户端连接
                Socket socket=serverSocket.accept();
                //为客户端分配一个ChatHandler线程
                executorService.execute(new ChatHandler(this,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server=new ChatServer();
        server.start();
    }
}