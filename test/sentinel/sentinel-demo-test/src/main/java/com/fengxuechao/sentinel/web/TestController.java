package com.fengxuechao.sentinel.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fengxc
 * @date 2022/7/5
 */
@RequestMapping("/test")
@RestController
public class TestController {

    @PostMapping("/characters")
    public Project publishProject(Project project) {
        return project;
    }

}
