package com.fengxuechao.netty.example01;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     *  channelActive
     *  客户端通道激活
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.err.println("client channel active..");
    }
    
    /**
     *  channelRead
     *  真正的数据最终会走到这个方法进行处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	// 固定模式的 try .. finally  
    	// 在try代码片段处理逻辑, finally进行释放缓存资源, 也就是 Object msg (buffer)
        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);

            String body = new String(req, "utf-8");
            System.out.println("Client :" + body );
            String response = "收到服务器端的返回信息：" + body;
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     *  exceptionCaught
     *  异常捕获方法
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}


