package com.github.jerrymice.spring.boot.starter.auto.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tumingjian
 * @date 2018/8/24
 * 说明:
 */
@Data
@ConfigurationProperties(prefix = "jerrymice.spring.boot.config.web.security")
public class SecurityProperties {
    boolean enabled = false;
    String userSessionKey = "currentUser";
    String resourceSessionKey = "currentResource";


}
