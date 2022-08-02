package com.fengxuechao.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GET 请求的 Query 参数下划线转驼峰
 *
 * @author fengxuechao
 * @date 2022/7/13
 */
@Target(value = ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterConvert {
}
