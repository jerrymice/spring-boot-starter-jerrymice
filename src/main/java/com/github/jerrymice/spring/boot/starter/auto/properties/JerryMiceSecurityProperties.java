package com.github.jerrymice.spring.boot.starter.auto.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tumingjian
 * 说明:
 */
@Data
@ConfigurationProperties(prefix = "jerrymice.spring.mvc.security")
public class JerryMiceSecurityProperties {
    boolean enabled = false;
    String userSessionKey = "currentUser";
    String resourceSessionKey = "currentResource";


}
