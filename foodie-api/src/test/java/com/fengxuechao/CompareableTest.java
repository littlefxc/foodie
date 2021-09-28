/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/8/12
 * 创建作者: 冯雪超
 * 文件名称: CompareableTest.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.fengxuechao;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author fengxuechao
 * @date 2021/8/12
 */
public class CompareableTest {

    @Test
    public void test() {
        ArrayList<Student> list = Lists.newArrayListWithCapacity(5);
        list.add(new Student(1));
        list.add(new Student(4));
        list.add(new Student(3));
        list.add(new Student(2));
        list.add(new Student());
        System.out.println(list);
    }

    static class Student implements Comparable<Student> {

        @Getter
        @Setter
        private Integer id;

        public Student() {
        }

        public Student(Integer id) {
            this.id = id;
        }

        @Override
        public int compareTo(Student o) {
            if (o == null || o.getId() == null) {
                return -1;
            }
            return this.id.compareTo(o.id);
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    '}';
        }
    }
}
