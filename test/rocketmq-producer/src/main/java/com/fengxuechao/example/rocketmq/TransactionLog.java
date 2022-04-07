package com.fengxuechao.example.rocketmq;

import lombok.Data;

/**
 * @author fengxuechao
 * @date 2022/4/7
 */
@Data
public class TransactionLog {

    private Integer id;

    private String business;

    private String foreignKey;
}
