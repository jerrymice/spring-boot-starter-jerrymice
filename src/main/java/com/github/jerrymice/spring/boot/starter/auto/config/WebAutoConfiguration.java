package com.github.jerrymice.spring.boot.starter.auto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.jerrymice.spring.boot.starter.auto.bean.UserWebArgumentResolver;
import com.github.jerrymice.spring.boot.starter.auto.properties.WebProperties;
import com.github.jerrymice.spring.boot.starter.EnableJerrymiceSpringBootConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author tumingjian
 * 说明:
 */

public class WebAutoConfiguration {
    @Autowired
    private WebProperties webProperties;

    /**
     * @author tumingjian
     * 说明:允许跨域
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerrymiceSpringBootConfiguration.WEB_CORS_MAPPINGS_ENABLE, havingValue = "true", matchIfMissing = true)
    public class CorsMappingsWebMvcConfigurer implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowCredentials(true)
                    .allowedMethods("GET", "POST", "DELETE", "PUT")
                    .maxAge(3600);
        }
    }

    /**
     * @author kexl
     * 转换long为string, 防止前台JS丢失精度
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerrymiceSpringBootConfiguration.WEB_MESSAGE_CONVERTERS_ENABLE, havingValue = "true", matchIfMissing = true)
    public class MessageConvertersWebMvcConfigurer implements WebMvcConfigurer {

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
            jackson2HttpMessageConverter.setObjectMapper(objectMapper);
            converters.add(0, jackson2HttpMessageConverter);
            converters.add(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        }
    }

    /**
     * @author tumingjian
     * 说明: 映射项目下webapp/resource/目录为静态资源/**
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerrymiceSpringBootConfiguration.WEB_RESOURCE_HANDLER_ENABLE, havingValue = "true", matchIfMissing = true)
    public class ResourceHandlerWebMvcConfigurer implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**").addResourceLocations("/resource/");
        }
    }

    /**
     * @author tumingjian
     * 说明:将解析当前用户参数的resolver添加到web配置中
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerrymiceSpringBootConfiguration.WEB_USER_ARGUMENT_RESOLVER_ENABLE, havingValue = "true", matchIfMissing = true)
    public class WebArgumentResolverConfigurer implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new ServletWebArgumentResolverAdapter(new UserWebArgumentResolver(webProperties.getUserSessionKey())));
        }
    }

}
