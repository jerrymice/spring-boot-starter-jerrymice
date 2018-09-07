package com.github.jerrymice.spring.boot.starter.auto.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tumingjian
 * @date 2018/8/27
 * 说明:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jerrymice.spring.boot.config.web.task")
public class WebTaskProperties {
    /**
     * 是否启用远程定时任务调度功能.
     */
    private boolean enabled = false;
    /**
     * task任务统一执行的URL映射地址
     */
    private String requestMappingPath = "/task/execute";
    /**
     * 拦截器顺序,一般使用默认值
     */
    private int interceptorOrder = 0;
    /**
     * 是否启用用户身份验证
     */
    private boolean enabledUserVerify=true;
}
