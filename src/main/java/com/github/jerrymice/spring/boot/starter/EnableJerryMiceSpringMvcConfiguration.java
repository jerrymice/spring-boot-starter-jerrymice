package com.github.jerrymice.spring.boot.starter;


import com.github.jerrymice.spring.boot.starter.config.*;
import com.github.jerrymice.spring.boot.starter.properties.JerryMiceSecurityProperties;
import com.github.jerrymice.spring.boot.starter.properties.JerryMiceWebMvcTaskProperties;
import com.github.jerrymice.spring.boot.starter.properties.SpringWebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tumingjian
 * 说明:启用Spring boot配置的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({
        JerryMiceWebMvcTaskProperties.class,
        JerryMiceSecurityProperties.class,
        SpringWebMvcProperties.class})

@Import({SecurityAutoConfiguration.class,
        TaskAutoConfiguration.class,
        WebMvcAutoRegistrations.class,
        WebAutoConfiguration.class,
        WebGlobalExceptionConfiguration.class})
public @interface EnableJerryMiceSpringMvcConfiguration {

    /**
     * 是否启用扩展SessionStrategy,默认true
     * @see com.github.jerrymice.spring.boot.starter.bean.SuperHeaderHttpSessionStrategy
     */
    String WEB_SESSION_STRATEGY_ENABLE = "jerrymice.spring.mvc.session-strategy.enabled";
    /**
     * 是否启用跨域,默认true
     */
    String WEB_GLOBAL_CORS_ENABLE = "jerrymice.spring.mvc.global-cors.enabled";
    /**
     * 是否自动注入UserWebArgumentResolver,可以直接在Controller中获取当前用户
     * @see com.github.jerrymice.spring.boot.starter.bean.UserWebArgumentResolver
     */
    String WEB_USER_ARGUMENT_RESOLVER_ENABLE = "jerrymice.spring.mvc.user-argument-resolver.enabled";
    /**
     * 是否启用登录拦截器
     * @see com.github.jerrymice.spring.boot.starter.interceptor.UserLoginInterceptor
     * @see com.github.jerrymice.spring.boot.starter.config.WebAutoConfiguration.UserLoginInterceptorConfigurer
     */
    String WEB_LOGIN_INTERCEPTOR_ENABLE="jerrymice.spring.mvc.login-interceptor.enabled";
    /**
     * 是否启用默认的静态资源路径,默认true
     */
    String WEB_RESOURCE_HANDLER_ENABLE = "jerrymice.spring.mvc.mapping-static-resource";
    /**
     * 是否启用默认的MessageConverters,默认true
     */
    String WEB_MESSAGE_CONVERTERS_ENABLE = "jerrymice.spring.mvc.jack-json-message-converter";
    /**
     * 是否启用permission(一个轻量级的权限控制框架)权限控制
     * @see com.github.jerrymice.common.permission.Permission
     */
    String WEB_SECURITY = "jerrymice.spring.mvc.security.enabled";
    /**
     * 是否启用定时任务接口
     * @see TaskAutoConfiguration
     */
    String WEB_TASK="jerrymice.spring.mvc.task.enabled";
    /**
     * 一个可以支持@Order注解排序的Controller @RequestMapping方法.
     * @see com.github.jerrymice.spring.boot.starter.bean.OrderRequestMappingHandlerMapping
     */
    String WEB_ORDER_MAPPING_ENABLED="jerrymice.spring.mvc.order-mapping-handler";
    /**
     * 全局异常处理,默认true
     * @see com.github.jerrymice.spring.boot.starter.bean.GlobalExceptionHandler
     */
    String WEB_GLOBAL_EXCEPTION_ENABLED="jerrymice.spring.mvc.global-exception.enabled";
}
