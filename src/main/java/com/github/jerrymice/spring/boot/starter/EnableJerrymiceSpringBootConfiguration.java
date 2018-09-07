package com.github.jerrymice.spring.boot.starter;

import com.github.jerrymice.spring.boot.starter.auto.config.*;
import com.github.jerrymice.spring.boot.starter.auto.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tumingjian
 * @date 2018/8/2
 * 说明:启用Spring boot配置的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({
        WebTaskProperties.class,
        SecurityProperties.class,
        WebProperties.class,
        SqlProperties.class,
        ProxyProperties.class})

@Import({HttpSessionAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        TaskAutoConfiguration.class,
        WebAutoConfiguration.class,
        ProxyAutoConfiguration.class,
        SqlSessionFactoryConfiguration.class})
public @interface EnableJerrymiceSpringBootConfiguration {

    /**
     * 是否启用扩展SessionStrategy,默认true
     */
    String WEB_SESSION_STRATEGY_ENABLE = "jerrymice.spring.boot.config.web.session-strategy";
    /**
     * 是否启用跨域,默认true
     */
    String WEB_CORS_MAPPINGS_ENABLE = "jerrymice.spring.boot.config.web.cors-mappings";
    /**
     * 是否启用默认的静态资源路径,默认true
     */
    String WEB_RESOURCE_HANDLER_ENABLE = "jerrymice.spring.boot.config.web.resource-handler";
    /**
     * 是否启用默认的SqlSessionFactory,默认true
     */
    String WEB_SQL_SESSIONF_FACTORY_ENABLE = "jerrymice.spring.boot.config.sql.session-factory";
    /**
     * 是否启用默认的MessageConverters,默认true
     */
    String WEB_MESSAGE_CONVERTERS_ENABLE = "jerrymice.spring.boot.config.web.message-converters";
    /**
     * 是否自动注入UserWebArgumentResolver,可以直接在Controller中获取当前用户
     */
    String WEB_USER_ARGUMENT_RESOLVER_ENABLE = "jerrymice.spring.boot.config.web.user-argument-resolver";
    /**
     * 是否启用security权限控制
     */
    String WEB_SECURITY = "jerrymice.spring.boot.config.web.security.enabled";
    /**
     * 是否启用定时任务接口
     */
    String WEB_TASK="jerrymice.spring.boot.config.web.task.enabled";
}
