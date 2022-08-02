package com.fengxuechao.converter;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * GET 请求的 Query 参数下划线转驼峰
 *
 * @author fengxuechao
 * @date 2022/7/13
 */
public class UnderlineToCamelArgumentResolver extends AbstractCustomizeResolver {

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return HttpMethod.GET.matches(request.getMethod().toUpperCase()) && parameter.hasParameterAnnotation(ParameterConvert.class);
    }

    /**
     * 装载参数
     *
     * @param methodParameter       方法参数
     * @param modelAndViewContainer 返回视图容器
     * @param nativeWebRequest      本次请求对象
     * @param webDataBinderFactory  数据绑定工厂
     * @return the resolved argument value, or {@code null}
     * @throws Exception in case of errors with the preparation of argument values
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return createObject(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
    }
}