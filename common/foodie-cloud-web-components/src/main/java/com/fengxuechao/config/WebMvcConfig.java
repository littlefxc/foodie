package com.fengxuechao.config;

import com.fengxuechao.converter.FormAndJsonArgumentResolver;
import com.fengxuechao.converter.StringToDateConverter;
import com.fengxuechao.converter.UnderlineToCamelArgumentResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 实现静态资源的映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")  // 映射swagger2
                .addResourceLocations("file:/workspaces/images/");  // 映射本地静态资源
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UnderlineToCamelArgumentResolver());
        argumentResolvers.add(new FormAndJsonArgumentResolver());
    }

    /**
     * Add {@link Converter Converters} and {@link Formatter Formatters} in addition to the ones
     * registered by default.
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }

    @Bean
    public BeanPostProcessor underlineToCamelArgumentResolver() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof RequestMappingHandlerAdapter) {
                    FormAndJsonArgumentResolver formAndJsonArgumentResolver = null;
                    ModelAttributeMethodProcessor modelAttributeMethodProcessor = null;
                    RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = null;
                    for (HandlerMethodArgumentResolver argumentResolver : ((RequestMappingHandlerAdapter) bean).getArgumentResolvers()) {
                        if (argumentResolver instanceof FormAndJsonArgumentResolver) {
                            formAndJsonArgumentResolver = (FormAndJsonArgumentResolver) argumentResolver;
                            continue;
                        }
                        if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                            requestResponseBodyMethodProcessor = (RequestResponseBodyMethodProcessor) argumentResolver;
                            continue;
                        }
                        if (argumentResolver instanceof ModelAttributeMethodProcessor) {
                            modelAttributeMethodProcessor = (ModelAttributeMethodProcessor) argumentResolver;
                        }
                    }
                    if (formAndJsonArgumentResolver != null) {
                        formAndJsonArgumentResolver.setModelAttributeMethodProcessor(modelAttributeMethodProcessor);
                        formAndJsonArgumentResolver.setRequestResponseBodyMethodProcessor(requestResponseBodyMethodProcessor);
                    }
                }

                return bean;
            }
        };
    }

}
