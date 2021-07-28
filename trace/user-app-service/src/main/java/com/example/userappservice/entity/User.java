/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/27
 * 创建作者: 冯雪超
 * 文件名称: User.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.example.userappservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxuechao
 * @date 2021/7/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;

    private String username;

    private String password;
}
