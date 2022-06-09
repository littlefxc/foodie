package com.fengxuechao.demo.dubbo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author fengxuechao
 * @date 2022/6/7
 */
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 5357580917757333488L;

    private String name;

    private BigDecimal price;

}
