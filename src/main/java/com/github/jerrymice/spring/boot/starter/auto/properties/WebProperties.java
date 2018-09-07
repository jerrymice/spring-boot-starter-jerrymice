package com.github.jerrymice.spring.boot.starter.auto.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tumingjian
 * 说明:
 */
@Data
@ConfigurationProperties(prefix = "jerrymice.spring.boot.config.web")
@Configuration
public class WebProperties {
    /**
     * 是否启用增强的SessionStrategy
     */
    boolean sessionStrategy = true;
    /**
     * 是否允许跨域
     */
    boolean corsMappings = true;
    /**
     * 是否映射将webapp下的静态资源/resource/目录映射为/**
     */
    boolean resourceHandler = true;
    /**
     * 是否添加默认的JSON Converters
     */
    boolean messageConverters = true;
    /**
     * 是否加入当前用户的参数拦截.必须在登录之后将当前用户信息以userSessionKey的值为key存入session中才能生效
     */
    boolean userArgumentResolver = true;
    /**
     * 存入user信息在session的KEY值
     */
    String userSessionKey = "currentUser";

}
