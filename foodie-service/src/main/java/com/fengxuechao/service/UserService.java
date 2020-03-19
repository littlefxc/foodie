package com.fengxuechao.service;

import com.fengxuechao.pojo.Users;
import com.fengxuechao.pojo.bo.UserBO;

public interface UserService {

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 判断用户名是否存在
     *
     * @param userBO 用于创建用户的信息
     * @return 创建成功的用户信息
     */
    Users createUser(UserBO userBO);

    /**
     * 检索用户名和密码是否匹配，用于登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    Users queryUserForLogin(String username, String password);
}
