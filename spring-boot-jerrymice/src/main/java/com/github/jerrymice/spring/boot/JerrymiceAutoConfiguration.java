package com.github.jerrymice.spring.boot;

import com.github.jerrymice.spring.boot.mvc.config.*;
import com.github.jerrymice.spring.boot.mvc.properties.JerryMiceWebMvcTaskProperties;
import com.github.jerrymice.spring.boot.mvc.properties.SpringWebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author tumingjian
 * 创建时间: 2019-10-09 14:28
 * 功能说明:
 */
@EnableConfigurationProperties({
        JerryMiceWebMvcTaskProperties.class,
        SpringWebMvcProperties.class})

@Import({SecurityAutoConfiguration.class,
        TaskAutoConfiguration.class,
        WebMvcAutoRegistrations.class,
        WebAutoConfiguration.class,
        WebGlobalExceptionConfiguration.class})
public class JerrymiceAutoConfiguration {
}
