/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/6/11
 * 创建作者: 冯雪超
 * 文件名称: EchoClient.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.fengxuechao.netty.action1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author fengxuechao
 * @date 2021/6/11
 */
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]); new EchoClient(host, port).start();
    }

    public void start() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    // 在创建 Channel 时向 ChannelPipeline 中添加一个 EchoClientHandler 实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // 连接到远程节点，阻塞等待直到连接完成
            ChannelFuture future = bootstrap.connect().sync();
            // 阻塞，直到 Channel 关闭
            future.channel().closeFuture().sync();
        } finally {
            // 关闭线程池并且释放所有资源
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    /**
     * 客户端的 ChannelHandler
     * {@code @Sharable 标记该类的实例可以被多个 Channel 共享}
     */
    @ChannelHandler.Sharable
    private static class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        /**
         * 在到服务器的连接已经建立之后将被调用
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 当被通知 Channel 是活跃等待时候，发送一条消息
            ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
        }

        /**
         * 当从服务器接收到一条消息是被调用
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("Client reveived: " + msg.toString(CharsetUtil.UTF_8));
        }

        /**
         * 在处理过程中引发异常是被调用
         *
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
           cause.printStackTrace();
           ctx.close();
        }
    }
}
