package com.fengxuechao.netty.example01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author fengxuechao
 * @date 2021/5/11
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {

        //1. 创建两个线程组: 一个用于进行网络连接接受的 另一个用于我们的实际处理（网络通信的读写）

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        //2. 通过辅助类去构造server/client
        ServerBootstrap b = new ServerBootstrap();

        //3. 进行Nio Server的基础配置

        //3.1 绑定两个线程组
        b.group(bossGroup, workGroup)
                //3.2 因为是server端，所以需要配置NioServerSocketChannel
                .channel(NioServerSocketChannel.class)
                //3.3 设置链接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                //3.4 设置TCP backlog参数 = sync队列 + accept队列
                .option(ChannelOption.SO_BACKLOG, 1024)
                //3.5 设置配置项 通信不延迟
                .childOption(ChannelOption.TCP_NODELAY, true)
                //3.6 设置配置项 接收与发送缓存区大小
                .childOption(ChannelOption.SO_RCVBUF, 1024 * 32)
                .childOption(ChannelOption.SO_SNDBUF, 1024 * 32)
                //3.7 进行初始化 ChannelInitializer , 用于构建双向链表 "pipeline" 添加业务handler处理
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //3.8 这里仅仅只是添加一个业务处理器：ServerHandler（后面我们要针对他进行编码）
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });

        //4. 服务器端绑定端口并启动服务;使用channel级别的监听close端口 阻塞的方式
        ChannelFuture cf = b.bind(8765).sync();
        cf.channel().closeFuture().sync();

        //5. 释放资源
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();

    }
}
