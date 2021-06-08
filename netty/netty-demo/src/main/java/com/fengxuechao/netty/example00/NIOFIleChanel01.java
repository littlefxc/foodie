package com.fengxuechao.netty.example00;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author fengxuechao
 * @date 2021/5/8
 */
public class NIOFIleChanel01 {

    public static void main(String[] args) throws IOException {
        String str = "hello,尚硅谷";
        //创建一个输出流->channel
        String file = System.getProperty("user.dir") + "/netty/file01.txt";
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        //通过 fileOutputStream 获取 对应的 FileChannel //这个 fileChannel 真实 类型是 FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();
        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将 str 放入 byteBuffer
        byteBuffer.put(str.getBytes());
        //对 byteBuffer 进行 flip
        byteBuffer.flip();
        //将 byteBuffer 数据写入到 fileChannel
        fileChannel.write(byteBuffer);
        fileOutputStream.close();

    }
}
