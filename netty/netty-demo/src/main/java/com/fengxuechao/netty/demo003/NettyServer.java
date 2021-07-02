package com.fengxuechao.netty.demo003;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fengxuechao
 * @date 2021/6/8
 */
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer().bing(7397);
    }

    private void bing(int port) {
        // 配置服务端线程
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        //NioEventLoopGroup extends MultithreadEventLoopGroup Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
        EventLoopGroup childGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new MyChannelInitializer());
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }

    private static class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) {
            /* 解码器 */
            // 基于换行符号
            channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            // 基于指定字符串【换行符，这样功能等同于LineBasedFrameDecoder】
            // e.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, false, Delimiters.lineDelimiter()));
            // 基于最大长度
            // e.pipeline().addLast(new FixedLengthFrameDecoder(4));
            // 解码转String，注意调整自己的编码格式GBK、UTF-8
            channel.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));
            channel.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
            //在管道中添加我们自己的接收数据实现方法
            channel.pipeline().addLast(new MyServerHandler());
        }
    }

    public static class MyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ChannelHandler.channelGroup.add(ctx.channel());
            SocketChannel channel = (SocketChannel) ctx.channel();
            System.out.println("链接报告开始");
            System.out.println("链接报告信息：有一客户端链接到本服务端");
            System.out.println("链接报告IP:" + channel.localAddress().getHostString());
            System.out.println("链接报告Port:" + channel.localAddress().getPort());
            System.out.println("链接报告完毕");
            // 通知客户端连接建立成功
            String strString = "通知客户端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
            ByteBuf buf = Unpooled.buffer(strString.getBytes().length);
            buf.writeBytes(strString.getBytes("GBK"));
            ctx.writeAndFlush(buf);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ChannelHandler.channelGroup.remove(ctx.channel());
            System.out.println("客户端断开连接" + ctx.channel().localAddress().toString());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
            //接收msg消息{与上一章节相比，此处已经不需要自己进行解码}
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息：" + msg);
            if ("quit".equals(msg)) {
                ctx.close();
            }
            // 通知客户端链消息发送成功
            String str = "服务端收到：" + new Date() + " " + msg + "\r\n";
            ChannelHandler.channelGroup.writeAndFlush(str);
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

    public static class ChannelHandler {

        //用于存放用户Channel信息，也可以建立map结构模拟不同的消息群
        public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    }
}
