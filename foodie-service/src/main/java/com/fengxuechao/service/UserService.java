package com.fengxuechao.service;

import com.fengxuechao.pojo.Users;
import com.fengxuechao.pojo.bo.UserBO;

public interface UserService {

    /**
     * 判断用户名是否存在
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 判断用户名是否存在
     */
    Users createUser(UserBO userBO);

    /**
     * 检索用户名和密码是否匹配，用于登录
     */
    Users queryUserForLogin(String username, String password);
}
