package com.github.jerrymice.spring.boot.starter.auto.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tumingjian
 * @date 2018/8/6
 * 说明:
 */
@Data
@ConfigurationProperties(prefix = "jerrymice.spring.boot.config.sql")
@Configuration
public class SqlProperties {
    /**
     * 是否启用增强的sessionFactory
     */
    @Deprecated
    private boolean sessionFactory=true;


}
