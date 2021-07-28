package com.example.userappservice.service;

import com.example.userappservice.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author fengxuechao
 * @date 2021/7/28
 */
public interface UserService {

    User getUserById(Integer id) throws JsonProcessingException;
}
