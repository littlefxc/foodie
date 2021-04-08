package com.fengxuechao.jvm;

/**
 * @author fengxuechao
 * @date 2021/2/24
 */
public class JVMTest2 {

    static {
        System.out.println("JVMTest2 静态块");
    }

    {
        System.out.println("JVMTest2 构造块");
    }

    public JVMTest2() {
        System.out.println("JVMTest2 构造方法");
    }

    public static void main(String[] args) {
        System.out.println("JVMTest2 main() 方法");
        new JVMTest2();
    }
}
