package com.fengxuechao.netty.example00;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author fengxuechao
 * @date 2021/5/8
 */
public class NIOFileChannel02 {
    public static void main(String[] args) throws IOException {
        //创建文件的输入流
        String file = System.getProperty("user.dir") + "/netty/file01.txt";
        FileInputStream fileInputStream = new FileInputStream(file);
        //通过 fileInputStream 获取对应的 FileChannel -> 实际类型
        FileChannel fileChannel = fileInputStream.getChannel();
        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        //将 通道的数据读入到 Buffer
        fileChannel.read(byteBuffer);
        //将 byteBuffer 的 字节数据 转成 String
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();

    }
}
