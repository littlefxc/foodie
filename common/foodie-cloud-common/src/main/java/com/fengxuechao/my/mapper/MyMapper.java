package com.fengxuechao.my.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author fengxuechao
 * @date 2020/3/5
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
