package com.fengxuechao.example.rocketmq;

import lombok.Data;

/**
 * @author fengxuechao
 * @date 2022/4/7
 */
@Data
public class Order {

    private Integer id;

    private Integer userId;

    private String goodsName;

    private Integer total;

}
