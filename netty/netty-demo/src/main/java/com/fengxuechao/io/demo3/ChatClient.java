package com.fengxuechao.io.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatClient {
    private static final int BUFFER = 1024;
    private AsynchronousSocketChannel clientChannel;
    private Charset charset = Charset.forName("UTF-8");

    private String host;
    private int port;
    //设置服务器IP和端口
    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            clientChannel = AsynchronousSocketChannel.open();
            //连接服务器
            Future<Void> future = clientChannel.connect(new InetSocketAddress(host, port));
            future.get();
            //新建一个线程去等待用户输入
            new Thread(new UserInputHandler(this)).start();
            ByteBuffer buffer=ByteBuffer.allocate(BUFFER);
            //无限循环让客户端保持运行状态
            while (true){
                //获取服务器发来的消息并存入到buffer
                Future<Integer> read=clientChannel.read(buffer);
                if(read.get()>0){
                    buffer.flip();
                    String msg=String.valueOf(charset.decode(buffer));
                    System.out.println(msg);
                    buffer.clear();
                }else {
                    //如果read的结果小于等于0说明和服务器连接出现异常
                    System.out.println("服务器断开连接");
                    if(clientChannel!=null){
                        clientChannel.close();
                    }
                    System.exit(-1);
                }
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        ByteBuffer buffer = charset.encode(msg);
        Future<Integer> write=clientChannel.write(buffer);
        try {
            //获取发送结果，如果get方法发生异常说明发送失败
            write.get();
        } catch (ExecutionException|InterruptedException e) {
            System.out.println("消息发送失败");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatClient("127.0.0.1",8888).start();
    }
}