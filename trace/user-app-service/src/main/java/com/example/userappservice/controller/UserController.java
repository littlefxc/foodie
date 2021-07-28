package com.example.userappservice.controller;

import com.example.userappservice.entity.User;
import com.example.userappservice.mapper.UserMapper;
import com.example.userappservice.service.UserService;
import com.example.userappservice.utils.ResultBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author fengxuechao
 * @date 2021/7/27
 */
@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public ResultBean<?> getUserById(@PathVariable Integer id) throws JsonProcessingException {
        return ResultBean.ok(userService.getUserById(id));
    }

    @PostMapping("/users")
    public ResultBean<?> addUser(@RequestBody User user) {
        userMapper.addUser(user);
        if (mongoTemplate != null) {
            mongoTemplate.insert(user);
        }
        return ResultBean.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResultBean<?> deleteUser(@PathVariable Integer id) {
        User user = userMapper.getById(id);
        userMapper.deleteUser(id);
        return ResultBean.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResultBean<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        userMapper.updatePasswordById(user);
        return ResultBean.ok(userMapper.getById(id));
    }
}
