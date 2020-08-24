package xyz.yuanjin.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import xyz.yuanjin.project.intercepter.AuthFileManagementFilter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanjin
 * @version v1.0 on 2020/4/23 22:36
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 解决controller返回字符串中文乱码问题
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }

    @Override
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(ContentNegotiationManager contentNegotiationManager, FormattingConversionService conversionService, Validator validator) {
        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();

        List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        MediaType textMedia = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        supportedMediaTypes.add(textMedia);
        MediaType jsonMedia = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        supportedMediaTypes.add(jsonMedia);
        jsonConverter.setSupportedMediaTypes(supportedMediaTypes);

        converters.add(jsonConverter);


        adapter.setMessageConverters(converters);

        return adapter;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthFileManagementFilter()).addPathPatterns("/**");
    }
}
