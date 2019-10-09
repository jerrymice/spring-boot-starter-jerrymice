package com.github.jerrymice.spring.boot;


import com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler;
import com.github.jerrymice.spring.boot.mvc.bean.OrderRequestMappingHandlerMapping;
import com.github.jerrymice.spring.boot.mvc.bean.SuperHeaderHttpSessionStrategy;
import com.github.jerrymice.spring.boot.mvc.bean.UserWebArgumentResolver;
import com.github.jerrymice.spring.boot.mvc.config.*;
import com.github.jerrymice.spring.boot.mvc.interceptor.UserLoginInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tumingjian
 * 说明:启用Spring boot配置的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JerrymiceAutoConfiguration.class)
public @interface EnableJerryMice {

    /**
     * 是否启用扩展SessionStrategy,默认true
     * @see SuperHeaderHttpSessionStrategy
     */
    String WEB_SESSION_STRATEGY_ENABLE = "jerrymice.spring.mvc.session-strategy.enabled";
    /**
     * 是否启用跨域,默认true
     */
    String WEB_GLOBAL_CORS_ENABLE = "jerrymice.spring.mvc.global-cors.enabled";
    /**
     * 是否自动注入UserWebArgumentResolver,可以直接在Controller中获取当前用户
     * @see UserWebArgumentResolver
     */
    String WEB_USER_ARGUMENT_RESOLVER_ENABLE = "jerrymice.spring.mvc.user-argument-resolver.enabled";
    /**
     * 是否启用登录拦截器
     * @see UserLoginInterceptor
     * @see WebAutoConfiguration.UserLoginInterceptorConfigurer
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
     * @see OrderRequestMappingHandlerMapping
     */
    String WEB_ORDER_MAPPING_ENABLED="jerrymice.spring.mvc.order-mapping-handler";
    /**
     * 全局异常处理,默认true,所有异常包装为Result类
     * @see GlobalExceptionHandler
     * @see com.github.jerrymice.common.entity.entity.Result
     */
    String WEB_GLOBAL_EXCEPTION_ENABLED="jerrymice.spring.mvc.global-exception";
    /**
     *
     * 所有Response JSON都统一包装为Result接口形式的返回值,默认true
     * 接口的正常返回值将在result的body中.正常返回时result的code属性值为0000
     * @see com.github.jerrymice.common.entity.entity.Result
     * @see com.github.jerrymice.common.entity.code.GlobalErrorCode
     * @see com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody
     *
     */
    String WEB_UNIFY_RESPONSE_ENABLED ="jerrymice.spring.mvc.unify-response";
}
