package com.fengxuechao.netty.example01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	public static void main(String[] args) throws InterruptedException {
		
		//1. 创建两个线程组: 只需要一个线程组用于我们的实际处理（网络通信的读写）
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		//2. 通过辅助类去构造client,然后进行配置响应的配置参数
		Bootstrap b = new Bootstrap();
		b.group(workGroup)
		 .channel(NioSocketChannel.class)
		 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
		 .option(ChannelOption.SO_RCVBUF, 1024 * 32)
		 .option(ChannelOption.SO_SNDBUF, 1024 * 32)
		 //3. 初始化ChannelInitializer
		 .handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				//3.1  添加客户端业务处理类
				ch.pipeline().addLast(new ClientHandler());	
			}
		});
		//4. 服务器端绑定端口并启动服务; 使用channel级别的监听close端口 阻塞的方式
		ChannelFuture cf = b.connect("127.0.0.1", 8765).syncUninterruptibly();

		//5. 发送一条数据到服务器端
		cf.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty!".getBytes()));
		
		//6. 休眠一秒钟后再发送一条数据到服务端
		Thread.sleep(1000);
		cf.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty again!".getBytes()));

		//7. 同步阻塞关闭监听并释放资源
		cf.channel().closeFuture().sync();
		workGroup.shutdownGracefully();
		
	}
}

