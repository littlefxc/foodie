//package com.fengxuechao.netty.example;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//
///**
// * @author fengxuechao
// * @date 2021/5/8
// */
//public class NIOFileChannel03 {
//
//    public static void main(String[] args) throws IOException {
//        String file1 = System.getProperty("user.dir") + "/netty/file01.txt";
//        String file2 = System.getProperty("user.dir") + "/netty/file02.txt";
//        FileInputStream fileInputStream = new FileInputStream(file1);
//        FileChannel fileChannel01 = fileInputStream.getChannel();
//        FileOutputStream fileOutputStream = new FileOutputStream("file2");
//        FileChannel fileChannel02 = fileOutputStream.getChannel();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
//        while (true) {
//            //循环读取
//
//            //这里有一个重要的操作，一定不要忘了
//            /*
//            public final Buffer clear() { position = 0;
//                limit = capacity; mark = -1; return this;
//            } */
//        }
//        byteBuffer.clear(); //清空 buffer
//        int read = fileChannel01.read(byteBuffer);
//        System.out.println("read =" + read);
//        if (read == -1) {
//            //表示读完
//            break;
//        }
//        //将 buffer 中的数据写入到 fileChannel02 -- 2.txt
//        byteBuffer.flip();
//        fileChannel02.write(byteBuffer);
//        //关闭相关的流
//        fileInputStream.close(); fileOutputStream.close();
//    }
//
//}
