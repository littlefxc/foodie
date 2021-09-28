package com.fengxuechao;

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
 * @date 2021/9/1
 */
public class EchoClient {

    private final int port;

    private final String host;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoClient("127.0.0.1", 8088).start();
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final EchoClientHandler echoClientHandler = new EchoClientHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(echoClientHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    /**
     * @see Sharable 标示一个ChannelHandler 可以被多个 Channel 安全地共享
     * @see ChannelInboundHandlerAdapter 用来定义响应入站事件的方法
     */
    @ChannelHandler.Sharable
    static class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        /**
         * 在到服务器的连接已经建立之后将被调用
         *
         * @param ctx
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("1. 收到请回答！", CharsetUtil.UTF_8));
        }

        /**
         * 当从服务器接收到一条消息时被调用
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("Netty 客户端: " + msg.toString(CharsetUtil.UTF_8));
        }

        /**
         * 在处理过程中引发异常时被调用。
         *
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            cause.printStackTrace();
            ctx.close();
        }
    }
}
