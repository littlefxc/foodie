/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/2
 * 创建作者: 冯雪超
 * 文件名称: NettyClient.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.fengxuechao.netty.demo003;

import com.fengxuechao.netty.demo001.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fengxuechao
 * @date 2021/7/2
 */
public class NettyClient {
    public static void main(String[] args) {
        new NettyClient().connect("127.0.0.1", 7397);
    }

    private void connect(String inetHost, int inetPort) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new MyChannelInitializer());
            ChannelFuture f = b.connect(inetHost, inetPort).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            System.out.println("链接报告开始");
            System.out.println("链接报告信息：本客户端链接到服务端。channelId：" + channel.id());
            System.out.println("链接报告完毕");
            channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            channel.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));
            channel.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
            channel.pipeline().addLast(new MyClientHandler());
        }

    }

    public class MyClientHandler extends ChannelInboundHandlerAdapter {

        /**
         * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            SocketChannel channel = (SocketChannel) ctx.channel();
            System.out.println("链接报告开始");
            System.out.println("链接报告信息：本客户端链接到服务端。channelId：" + channel.id());
            System.out.println("链接报告IP:" + channel.localAddress().getHostString());
            System.out.println("链接报告Port:" + channel.localAddress().getPort());
            System.out.println("链接报告完毕");
            //通知客户端链接建立成功
            String str = "通知服务端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
            ctx.writeAndFlush(str);
        }

        /**
         * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("断开链接" + ctx.channel().localAddress().toString());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //接收msg消息{与上一章节相比，此处已经不需要自己进行解码}
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息：" + msg);
            //通知客户端链消息发送成功
            String str = "客户端收到：" + new Date() + " " + msg + "\r\n";
            ctx.writeAndFlush(str);
        }

        /**
         * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            System.out.println("异常信息：\r\n" + cause.getMessage());
        }

    }
}
