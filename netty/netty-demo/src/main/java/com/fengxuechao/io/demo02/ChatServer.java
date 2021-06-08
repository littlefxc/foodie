package com.fengxuechao.io.demo02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class ChatServer {
    //设置缓冲区的大小，这里设置为1024个字节
    private static final int BUFFER = 1024;

    //Channel都要配合缓冲区进行读写，所以这里创建一个读缓冲区和一个写缓冲区
    //allocate()静态方法就是设置缓存区大小的方法
    private ByteBuffer read_buffer = ByteBuffer.allocate(BUFFER);
    private ByteBuffer write_buffer = ByteBuffer.allocate(BUFFER);

    //为了监听端口更灵活，再不写死了，用一个构造函数设置需要监听的端口号
    private int port;
    //编码方式设置为utf-8，下面字符和字符串互转时用得到
    private Charset charset = Charset.forName("UTF-8");

    public ChatServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new ChatServer(8888).start();
    }

    private void start() {
        //创建ServerSocketChannel和Selector并打开
        try (ServerSocketChannel server = ServerSocketChannel.open(); Selector selector = Selector.open()) {
            //【重点,实现NIO编程模型的关键】configureBlocking设置ServerSocketChannel为非阻塞式调用,Channel默认的是阻塞的调用方式
            server.configureBlocking(false);
            //绑定监听端口,这里不是给ServerSocketChannel绑定，而是给ServerSocket绑定，socket()就是获取通道原生的ServerSocket或Socket
            server.socket().bind(new InetSocketAddress(port));

            //把server注册到Selector并监听Accept事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口:" + port);


            while (true) {
                //select()会返回此时触发了多少个Selector监听的事件
                if (selector.select() > 0) {
                    //获取这些已经触发的事件,selectedKeys()返回的是触发事件的所有信息
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    //循环处理这些事件
                    for (SelectionKey key : selectionKeys) {
                        handles(key, selector);
                    }
                    //处理完后清空selectedKeys，避免重复处理
                    selectionKeys.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理事件的方法
    private void handles(SelectionKey key, Selector selector) throws IOException {
        //当触发了Accept事件，也就是有客户端请求进来
        if (key.isAcceptable()) {
            //获取ServerSocketChannel
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            //然后通过accept()方法接收客户端的请求，这个方法会返回客户端的SocketChannel，这一步和原生的ServerSocket类似
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            //把客户端的SocketChannel注册到Selector，并监听Read事件
            client.register(selector, SelectionKey.OP_READ);
            System.out.println("客户端[" + client.socket().getPort() + "]上线啦！");
        }
        //当触发了Read事件，也就是客户端发来了消息
        if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            //获取消息
            String msg = receive(client);
            System.out.println("客户端[" + client.socket().getPort() + "]:" + msg);
            //把消息转发给其他客户端
            sendMessage(client, msg, selector);
            //判断用户是否退出
            if (msg.equals("quit")) {
                //解除该事件的监听
                key.cancel();
                //更新Selector
                selector.wakeup();
                System.out.println("客户端[" + client.socket().getPort() + "]下线了！");
            }
        }
    }

    //接收消息的方法
    private String receive(SocketChannel client) throws IOException {
        //用缓冲区之前先清空一下,避免之前的信息残留
        read_buffer.clear();
        //把通道里的信息读取到缓冲区，用while循环一直读取，直到读完所有消息。因为没有明确的类似\n这样的结尾，所以要一直读到没有字节为止
        while (client.read(read_buffer) > 0) {
        }
        //把消息读取到缓冲区后，需要转换buffer的读写状态，不明白的看看前面的Buffer的讲解
        read_buffer.flip();
        return String.valueOf(charset.decode(read_buffer));
    }

    //转发消息的方法
    private void sendMessage(SocketChannel client, String msg, Selector selector) throws IOException {
        msg = "客户端[" + client.socket().getPort() + "]:" + msg;
        //获取所有客户端,keys()与前面的selectedKeys不同，这个是获取所有已经注册的信息，而selectedKeys获取的是触发了的事件的信息
        for (SelectionKey key : selector.keys()) {
            //排除服务器和本客户端并且保证key是有效的，isValid()会判断Selector监听是否正常、对应的通道是保持连接的状态等
            if (!(key.channel() instanceof ServerSocketChannel) && !client.equals(key.channel()) && key.isValid()) {
                SocketChannel otherClient = (SocketChannel) key.channel();
                write_buffer.clear();
                write_buffer.put(charset.encode(msg));
                write_buffer.flip();
                //把消息写入到缓冲区后，再把缓冲区的内容写到客户端对应的通道中
                while (write_buffer.hasRemaining()) {
                    otherClient.write(write_buffer);
                }
            }
        }
    }
}