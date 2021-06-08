package com.fengxuechao.netty.demo001;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Netty Server 入门示例
 *
 * @author fengxuechao
 * @date 2021/5/26
 */
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer().start(8888);
    }

    /**
     * 启动 Netty 服务
     * @param port
     */
    void start(int port) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup)
                    // 非阻塞模式
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new MyChannelInitializer());
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }

    private class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) {
            System.out.println("链接报告开始");
            System.out.println("链接报告信息：有一客户端链接到本服务端");
            System.out.println("链接报告IP:" + channel.localAddress().getHostString());
            System.out.println("链接报告Port:" + channel.localAddress().getPort());
            System.out.println("链接报告完毕");

            //在管道中添加我们自己的接收数据实现方法
            channel.pipeline().addLast(new MyServerHandler());
        }
    }

    private class MyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] msgByte = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgByte);
            System.out.println(new Date() + "接口到消息；");
            System.out.println(new String(msgByte, Charset.forName("GBK")));
        }
    }

}
