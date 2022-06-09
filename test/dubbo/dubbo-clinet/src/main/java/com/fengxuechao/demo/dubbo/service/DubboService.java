package com.fengxuechao.demo.dubbo.service;

import com.fengxuechao.demo.dubbo.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Service;

import java.math.BigDecimal;

/**
 * @author fengxuechao
 * @date 2022/6/7
 */
@Service
@Slf4j
public class DubboService implements IDubboService{
    @Override
    public Product publish(Product product) {
        log.info("Publishing product {}", product);
        product.setPrice(new BigDecimal(RandomUtils.nextInt(1,100)));
        return product;
    }
}
