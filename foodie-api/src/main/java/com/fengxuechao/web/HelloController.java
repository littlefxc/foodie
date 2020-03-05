package com.fengxuechao.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fengxuechao
 * @date 2020/2/26
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        return "Hello, World!";
    }
}
