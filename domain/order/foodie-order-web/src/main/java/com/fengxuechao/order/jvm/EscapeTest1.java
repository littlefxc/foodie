package com.fengxuechao.order.jvm;

/**
 * @author fengxuechao
 * @date 2022/2/15
 */
public class EscapeTest1 {

    public static SomeClass someClass;

    /**
     * 全局变量赋值逃逸
     */
    public void globalVariablePointerEscape() {
        someClass = new SomeClass();
    }

    /**
     * 方法返回值逃逸
     * someMethod() {
     *     SomeClass someClass = methodPointerEscape();
     * }
     */
    public SomeClass methodPointerEscape() {
        return new SomeClass();
    }

    /**
     * 实例引用传递逃逸
     */
    public void instancePassPointerEscape() {
        this.methodPointerEscape()
                .printClassName(this);
    }

    public static void main(String[] args) {
        Person person = new Person();
        person.id = 1;
        person.age = 18;

        // 上面这段代码，标量替换后，就会优化成如下所示：
        int id = 1;
        int age = 18;
    }

}

class SomeClass {
    public void printClassName(EscapeTest1 escapeTest) {
        System.out.println(escapeTest.getClass().getName());
    }
}

class Person {
    int id;
    int age;
}
