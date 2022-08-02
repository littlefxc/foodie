package com.fengxuechao.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.util.Iterator;

/**
 * 参数校验
 *
 * @author fengxuechao
 * @date 2022/7/13
 */
public abstract class AbstractCustomizeResolver implements HandlerMethodArgumentResolver {

    protected Object createObject(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Object org = BeanUtils.instantiateClass(methodParameter.getParameterType());
        String name = Conventions.getVariableNameForParameter(methodParameter);
        WebDataBinder binder = webDataBinderFactory.createBinder(nativeWebRequest, org, name);
        // 赋值对象
        assignObject(nativeWebRequest, org, binder);
        // 校验对象
        valid(methodParameter, modelAndViewContainer, org, name, binder);
        return org;
    }

    /**
     * 赋值
     *
     * @param nativeWebRequest
     * @param org
     * @param binder
     */
    private void assignObject(NativeWebRequest nativeWebRequest, Object org, WebDataBinder binder) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(org);
        Iterator<String> paramNames = nativeWebRequest.getParameterNames();
        while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            Object sourceValue = nativeWebRequest.getParameter(paramName);
            String camelParamName = com.awifi.rainbow.common.converter.StringHelpers.underLineToCamel(paramName);
            Class<?> targetClass = wrapper.getPropertyType(camelParamName);
            if (targetClass != null) {
                Object targetValue = null;
                ConversionService conversionService = binder.getConversionService();
                if (conversionService.canConvert(String.class, targetClass)) {
                    targetValue = conversionService.convert(sourceValue, targetClass);
                }
                wrapper.setPropertyValue(camelParamName, targetValue == null ? sourceValue : targetValue);
            }
        }
    }

    /**
     * @param parameter
     * @param mavContainer
     * @param binder
     * @param arg
     * @throws Exception
     */
    private void valid(MethodParameter parameter, ModelAndViewContainer mavContainer, Object arg, String name, WebDataBinder binder) throws MethodArgumentNotValidException {
        if (arg != null) {
            validateIfApplicable(binder, parameter);
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
            }
        }
        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
    }

    /**
     * Validate the binding target if applicable.
     * <p>The default implementation checks for {@code @javax.validation.Valid},
     * Spring's {@link Validated},
     * and custom annotations whose name starts with "Valid".
     *
     * @param binder    the DataBinder to be used
     * @param parameter the method parameter descriptor
     * @see #isBindExceptionRequired
     * @since 4.1.5
     */
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                binder.validate(validationHints);
                break;
            }
        }
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
        return !hasBindingResult;
    }
}