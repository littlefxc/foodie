package com.fengxuechao.user.service;

import com.fengxuechao.user.pojo.Users;
import com.fengxuechao.user.pojo.bo.UserBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("foodie-user-service")
@RequestMapping("user-api")
public interface UserService {

    /**
     * 判断用户名是否存在
     */
    @GetMapping("user/exists")
    public boolean queryUsernameIsExist(@RequestParam("username") String username);

    /**
     * 判断用户名是否存在
     */
    @PostMapping("user")
    public Users createUser(@RequestBody UserBO userBO);

    /**
     * 检索用户名和密码是否匹配，用于登录
     */
    @GetMapping("verify")
    public Users queryUserForLogin(@RequestParam("username") String username,
                                   @RequestParam("password") String password);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    Users findByUsername(String username);
}
