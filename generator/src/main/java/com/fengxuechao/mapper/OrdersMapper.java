package com.fengxuechao.mapper;

import com.fengxuechao.pojo.Orders;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface OrdersMapper extends Mapper<Orders> {
}