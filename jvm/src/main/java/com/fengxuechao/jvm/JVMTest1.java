package com.fengxuechao.jvm;

/**
 * @author fengxuechao
 * @date 2021/2/24
 */
public class JVMTest1 {
    public static void main(String[] args) {
        Demo demo = new Demo("aaa");
        demo.printName();
    }
}

class Demo {
    private String name;
    public Demo(String name) {
        this.name = name;
    }

    public void printName() {
        System.out.println(this.name);
    }
}