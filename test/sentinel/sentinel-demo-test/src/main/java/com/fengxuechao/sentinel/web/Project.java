package com.fengxuechao.sentinel.web;

import lombok.Data;

/**
 * @author fengxc
 * @date 2022/7/5
 */
@Data
public class Project {

    private Integer id;

    private String name;

    private Bug bug;
}
