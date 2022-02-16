package com.fengxuechao.order.jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author fengxuechao
 * @date 2022/2/15
 */
public class InlineTest {

    private final static Logger log = LoggerFactory.getLogger(InlineTest.class);

    public static void main(String[] args) {
        long cost = compute();
        log.info("执行花费 {} ms", cost);
    }

    private static long compute() {
        long start = System.currentTimeMillis();
        int result;
        Random random = new Random();
        for (int i = 0; i< 10000000; i++) {
            int a = random.nextInt();
            int b = random.nextInt();
            int c = random.nextInt();
            int d = random.nextInt();
            result = add1(a, b, c, d);
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static int add1(int n1, int n2, int n3, int n4) {
        return add2(n1, n2) + add2(n3, n4);
    }

    private static int add2(int n1, int n2) {
        return n1 + n2;
    }
}
