package com.fengxuechao.converter;

import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * 参数下划线转驼峰
 * <ul>
 *     <li>支持 multipart/form-data </>
 *     <li>支持 application/x-form-urlencoded </>
 *     <li>支持 application/json </>
 * </ul>
 *
 * @author fengxuechao
 * @date 2022/7/13
 */
public class FormAndJsonArgumentResolver extends AbstractCustomizeResolver {

    @Setter
    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    @Setter
    private ModelAttributeMethodProcessor modelAttributeMethodProcessor;

    public FormAndJsonArgumentResolver() {
    }

    public FormAndJsonArgumentResolver(RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor,
                                       ModelAttributeMethodProcessor modelAttributeMethodProcessor) {
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
        this.modelAttributeMethodProcessor = modelAttributeMethodProcessor;
    }

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
        return modelAttributeMethodProcessor.supportsParameter(parameter)
                || requestResponseBodyMethodProcessor.supportsParameter(parameter);
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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request.getContentType().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            return createObject(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }
        if (request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return createObject(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }
        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return requestResponseBodyMethodProcessor.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }
        return null;
    }
}