package com.github.jerrymice.spring.boot.starter.auto.config;

import com.github.jerrymice.spring.boot.starter.EnableJerryMiceSpringMvcConfiguration;
import com.github.jerrymice.spring.boot.starter.auto.properties.JerryMiceWebMvcTaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.open.code.base.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author tumingjian
 * 说明:
 */
@Slf4j
@ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_TASK, havingValue = "true")
public class TaskAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(TaskProvider.class)
    public TaskProvider taskProvider() {
        return new TaskProvider();
    }

    @Configuration
    @ConditionalOnMissingBean(TaskProcessor.class)
    public class DefaultTaskProcessor implements TaskProcessor {

    }

    @Configuration
    @ConditionalOnMissingBean(TaskAuthPassword.class)
    public class DefaultTaskAuthPassword implements TaskAuthPassword {
        @Autowired
        private JerryMiceWebMvcTaskProperties taskProperties;

        @Override
        public boolean verify(String username, String password) {
            if (taskProperties.isEnabledUserVerify()) {
                log.warn("无法通过身份认证,因为这是默认的TaskAuthPassword实现,生产环境必须手动实现TaskAuthPassword接口");
            }
            return !taskProperties.isEnabledUserVerify();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(TaskAuthToken.class)
    public class DefaultTaskAuthToken implements TaskAuthToken {
        @Autowired
        private JerryMiceWebMvcTaskProperties taskProperties;

        @Override
        public boolean verify(String token) {
            if (taskProperties.isEnabledUserVerify()) {
                log.warn("无法通过身份认证,因为这是默认的TaskAuthToken实现,生产环境必须手动实现TaskAuthToken接口");
            }
            return !taskProperties.isEnabledUserVerify();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(TaskService.class)
    public class DefaultTaskService implements TaskService {

    }

    @Configuration
    public class TaskWebMvcConfigurer implements WebMvcConfigurer {
        @Autowired
        private JerryMiceWebMvcTaskProperties taskProperties;
        @Autowired
        private TaskProvider taskProvider;
        @Autowired
        private HttpMessageConverter<Object> converter;
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new TaskInterceptor(taskProvider,converter)).order(taskProperties.getOrder()).addPathPatterns(taskProperties.getPath());
        }
    }
}
