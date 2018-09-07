package com.github.jerrymice.spring.boot.starter.auto.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tumingjian
 * @date 2018/8/6
 * 说明:
 */
@Data
@ConfigurationProperties(prefix = "jerrymice.spring.boot.config.proxy")
@Configuration
public class ProxyProperties{
    /**
     * 代理主机地址
     */
    private String httpHost;
    /**
     * 代理端口
     */
    private Integer httpPort;
    /**
     * 测试代理的测试网址
     */
    private String heart="www.baidu.com";

}
