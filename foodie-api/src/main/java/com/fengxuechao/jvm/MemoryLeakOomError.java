package com.fengxuechao.jvm;

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;

/**
 * 只重写了 hashcode 方法，没有重写 equals 方法，或者两者都没有重写，造成内存泄漏
 *
 * @author fengxuechao
 * @date 2021/8/24
 */
public class MemoryLeakOomError {
    static class Key {
        Integer id;

        public Key(Integer id) {
            this.id = id;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equal(id, key.id);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
    public static void main(String[] args) {
        Map<Key, String> map = new HashMap<>();
        while (true) {
            for (int i = 0; i< 10000; i++) {
                if (!map.containsKey(new Key(i))) {
                    map.put(new Key(i), "Number:" + i);
                    System.out.println(i);
                }
            }
        }
    }
}
