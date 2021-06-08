package com.fengxuechao.io.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    //设置缓冲区字节大小
    private static final int BUFFER = 1024;

    //声明AsynchronousServerSocketChannel和AsynchronousChannelGroup
    private AsynchronousServerSocketChannel serverSocketChannel;
    private AsynchronousChannelGroup channelGroup;

    //在线用户列表。为了并发下的线程安全，所以使用CopyOnWriteArrayList
    //CopyOnWriteArrayList在写时加锁，读时不加锁，而本项目正好在转发消息时需要频繁读取.
    //ClientHandler包含每个客户端的通道，类型选择为ClientHandler是为了在write的时候调用每个客户端的handler
    private CopyOnWriteArrayList<ClientHandler> clientHandlerList;
    //字符和字符串互转需要用到，规定编码方式，避免中文乱码
    private Charset charset = Charset.forName("UTF-8");

    //通过构造函数设置监听端口
    private int port;
    public ChatServer(int port) {
        this.port = port;
        clientHandlerList=new CopyOnWriteArrayList<>();
    }

    public void start() {
        try {
            /**
             *创建一个线程池并把线程池和AsynchronousChannelGroup绑定，前面提到了AsynchronousChannelGroup包括一些系统资源，而线程就是其中一种。
             *为了方便理解我们就暂且把它当作线程池，实际上并不止包含线程池。如果你需要自己选定线程池类型和数量，就可以如下操作
             *如果不需要自定义线程池类型和数量，可以不用写下面两行代码。
             * */
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            channelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
            serverSocketChannel=AsynchronousServerSocketChannel.open(channelGroup);
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",port));
            System.out.println("服务器启动：端口【"+port+"】");
            /**
             * AIO中accept可以异步调用，就用上面说到的CompletionHandler方式
             * 第一个参数是辅助参数，回调函数中可能会用上的，如果没有就填null;第二个参数为CompletionHandler接口的实现
             * 这里使用while和System.in.read()的原因：
             * while是为了让服务器保持运行状态，前面的NIO，BIO都有用到while无限循环来保持服务器运行，但是它们用的地方可能更好理解
             * System.in.read()是阻塞式的调用，只是单纯的避免无限循环而让accept频繁被调用，无实际业务功能。
             */
            while (true) {
                serverSocketChannel.accept(null, new AcceptHandler());
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocketChannel!=null){
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //AsynchronousSocketChannel为accept返回的类型，Object为辅助参数类型，没有就填Object
    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Object> {
        //如果成功，执行的回调方法
        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
            //如果服务器没关闭，在接收完当前客户端的请求后，再次调用,以接着接收其他客户端的请求
            if(serverSocketChannel.isOpen()){
                serverSocketChannel.accept(null,this);
            }
            //如果客户端的channel没有关闭
            if(clientChannel!=null&&clientChannel.isOpen()){
                //这个就是异步read和write要用到的handler,并传入当前客户端的channel
                ClientHandler handler=new ClientHandler(clientChannel);
                //把新用户添加到在线用户列表里
                clientHandlerList.add(handler);
                System.out.println(getPort(clientChannel)+"上线啦！");
                ByteBuffer buffer=ByteBuffer.allocate(BUFFER);
                //异步调用read,第一个buffer是存放读到数据的容器，第二个是辅助参数。
                //因为真正的处理是在handler里的回调函数进行的，辅助参数会直接传进回调函数，所以为了方便使用，buffer就当作辅助参数
                clientChannel.read(buffer,buffer,handler);
            }
        }
        //如果失败，执行的回调方法
        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("连接失败"+exc);
        }
    }

    private class ClientHandler implements CompletionHandler<Integer, ByteBuffer>{
        private AsynchronousSocketChannel clientChannel;
        public ClientHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }
        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            if(buffer!=null){
                //如果read返回的结果小于等于0，而buffer不为空，说明客户端通道出现异常，做下线操作
                if(result<=0){
                    removeClient(this);
                }else {
                    //转换buffer读写模式并获取消息
                    buffer.flip();
                    String msg=String.valueOf(charset.decode(buffer));
                    //在服务器上打印客户端发来的消息
                    System.out.println(getPort(clientChannel)+msg);
                    //把消息转发给其他客户端
                    sendMessage(clientChannel,getPort(clientChannel)+msg);
                    buffer=ByteBuffer.allocate(BUFFER);

                    //如果用户输入的是退出，就从在线列表里移除。否则接着监听这个用户发送消息
                    if(msg.equals("quit"))
                        removeClient(this);
                    else
                        clientChannel.read(buffer, buffer, this);
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("客户端读写异常："+exc);
        }
    }

    //转发消息的方法
    private void sendMessage(AsynchronousSocketChannel clientChannel,String msg){
        for(ClientHandler handler:clientHandlerList){
            if(!handler.clientChannel.equals(clientChannel)){
                ByteBuffer buffer=charset.encode(msg);
                //write不需要buffer当辅助参数，因为写到客户端的通道就完事了，而读还需要回调函数转发给其他客户端。
                handler.clientChannel.write(buffer,null,handler);
            }
        }
    }
    //根据客户端channel获取对应端口号的方法
    private String getPort(AsynchronousSocketChannel clientChannel){
        try {
            InetSocketAddress address=(InetSocketAddress)clientChannel.getRemoteAddress();
            return "客户端["+address.getPort()+"]:";
        } catch (IOException e) {
            e.printStackTrace();
            return "客户端[Undefined]:";
        }
    }
    //移除客户端
    private void removeClient(ClientHandler handler){
        clientHandlerList.remove(handler);
        System.out.println(getPort(handler.clientChannel)+"断开连接...");
        if(handler.clientChannel!=null){
            try {
                handler.clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer(8888).start();
    }
}