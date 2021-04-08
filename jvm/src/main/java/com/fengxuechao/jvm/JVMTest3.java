package com.fengxuechao.jvm;

/**
 * @author fengxuechao
 * @date 2021/2/24
 */
public class JVMTest3 {

    static {
        System.out.println("JVMTest3 静态块");
    }

    {
        System.out.println("JVMTest3 构造块");
    }

    public JVMTest3() {
        System.out.println("JVMTest3 构造方法");
    }

    public static void main(String[] args) {
        System.out.println("JVMTest3 main() 方法");
        new Sub();
    }
}

class Super {
    static {
        System.out.println("Super 静态块");
    }

    {
        System.out.println("Super 构造块");
    }

    public Super() {
        System.out.println("Super 构造方法");
    }
}

class Sub extends Super {
    static {
        System.out.println("Sub 静态块");
    }

    {
        System.out.println("Sub 构造块");
    }

    public Sub() {
        System.out.println("Sub 构造方法");
    }
}
