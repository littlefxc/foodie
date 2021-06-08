package com.fengxuechao.netty.example00;

import java.nio.IntBuffer;

/**
 * @author fengxuechao
 * @date 2021/4/30
 */
public class BasicBuffer {
    public static void main(String[] args) {
        //
        //举例说明 Buffer 的使用 (简单说明)
        //创建一个 Buffer, 大小为 5, 即可以存放 5 个 int
        IntBuffer intBuffer = IntBuffer.allocate(5);
        //向 buffer 存放数据
        // intBuffer.put(10);
        // intBuffer.put(11);
        // intBuffer.put(12);
        // intBuffer.put(13);
        // intBuffer.put(14);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }
        //如何从 buffer 读取数据
        //将 buffer 转换，读写切换(!!!)
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
