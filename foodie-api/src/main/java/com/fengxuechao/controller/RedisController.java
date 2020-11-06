package com.fengxuechao.controller;

import com.fengxuechao.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

    private final Logger log = LoggerFactory.getLogger(getClass());

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public String set(String key, String value) {
        redisOperator.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(String key) {
        return redisOperator.get(key);
    }

    @GetMapping("/del")
    public Boolean del(String key) {
        redisOperator.del(key);
        return true;
    }

}
