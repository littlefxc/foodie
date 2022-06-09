package com.fengxuechao;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author fengxuechao
 * @date 2021/9/1
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new EchoServer(8088).start();
    }

    public void start() {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            sc.pipeline().addLast(echoServerHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }

    /**
     * @author fengxuechao
     * @date 2021/9/1
     * @see ChannelHandler.Sharable 标示一个ChannelHandler 可以被多个 Channel 安全地共享
     * @see ChannelInboundHandlerAdapter 用来定义响应入站事件的方法
     */
    @ChannelHandler.Sharable
    static class EchoServerHandler extends ChannelInboundHandlerAdapter {

        /**
         * 每次消息传入都会调用该防范
         *
         * @param ctx
         * @param msg
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf in = (ByteBuf) msg;
            System.out.println("Netty 服务端: " + in.toString(CharsetUtil.UTF_8));
            // 将接收到的消息写给发送者,而不冲刷出站消息
            ctx.write(Unpooled.copiedBuffer("2. 收到！", CharsetUtil.UTF_8));
        }

        /**
         * 通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息
         *
         * @param ctx
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            // 将未决消息冲刷到远程节点,并且关闭该 Channel
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
        }

        /**
         * 在读取操作期间,有异常抛出时会调用
         *
         * @param ctx
         * @param cause
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}
