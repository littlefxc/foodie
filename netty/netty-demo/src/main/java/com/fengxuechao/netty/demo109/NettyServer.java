/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/2
 * 创建作者: 冯雪超
 * 文件名称: NettyServer.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.fengxuechao.netty.demo109;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author fengxuechao
 * @date 2021/7/2
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

    /**
     * 自定义编码器
     */
    private static class MyDecoder extends ByteToMessageDecoder {

        private final int BASE_LENGTH = 4;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            //基础长度不足，我们设定基础长度为4
            if (in.readableBytes() < BASE_LENGTH) {
                return;
            }

            int beginIdx; //记录包头位置

            while (true) {
                // 获取包头开始的index
                beginIdx = in.readerIndex();
                // 标记包头开始的index
                in.markReaderIndex();
                // 读到了协议的开始标志，结束while循环
                if (in.readByte() == 0x02) {
                    break;
                }
                // 未读到包头，略过一个字节
                // 每次略过，一个字节，去读取，包头信息的开始标记
                in.resetReaderIndex();
                in.readByte();
                // 当略过，一个字节之后，
                // 数据包的长度，又变得不满足
                // 此时，应该结束。等待后面的数据到达
                if (in.readableBytes() < BASE_LENGTH) {
                    return;
                }

            }

            //剩余长度不足可读取数量[没有内容长度位]
            int readableCount = in.readableBytes();
            if (readableCount <= 1) {
                in.readerIndex(beginIdx);
                return;
            }

            //长度域占4字节，读取int
            ByteBuf byteBuf = in.readBytes(1);
            String msgLengthStr = byteBuf.toString(Charset.forName("GBK"));
            int msgLength = Integer.parseInt(msgLengthStr);

            //剩余长度不足可读取数量[没有结尾标识]
            readableCount = in.readableBytes();
            if (readableCount < msgLength + 1) {
                in.readerIndex(beginIdx);
                return;
            }

            ByteBuf msgContent = in.readBytes(msgLength);

            //如果没有结尾标识，还原指针位置[其他标识结尾]
            byte end = in.readByte();
            if (end != 0x03) {
                in.readerIndex(beginIdx);
                return;
            }

            out.add(msgContent.toString(Charset.forName("GBK")));

        }
    }

    private static class MyEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
            String msg = in.toString();
            byte[] bytes = msg.getBytes();

            byte[] send = new byte[bytes.length + 2];
            System.arraycopy(bytes, 0, send, 1, bytes.length);
            send[0] = 0x02;
            send[send.length - 1] = 0x03;

            out.writeInt(send.length);
            out.writeBytes(send);
        }
    }

    private class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) {
            //自定义解码器
            channel.pipeline().addLast(new MyDecoder());
            //自定义编码器
            channel.pipeline().addLast(new MyEncoder());
            //在管道中添加我们自己的接收数据实现方法
            channel.pipeline().addLast(new MyServerHandler());
        }
    }

    private class MyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            SocketChannel channel = (SocketChannel) ctx.channel();
            System.out.println("链接报告开始");
            System.out.println("链接报告信息：有一客户端链接到本服务端");
            System.out.println("链接报告IP:" + channel.localAddress().getHostString());
            System.out.println("链接报告Port:" + channel.localAddress().getPort());
            System.out.println("链接报告完毕");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            //接收msg消息{与上一章节相比，此处已经不需要自己进行解码}
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息：" + msg);

            ctx.writeAndFlush("hi I'm ok");
        }

    }
}
