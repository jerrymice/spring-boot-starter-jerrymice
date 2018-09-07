package com.github.jerrymice.spring.boot.starter.auto.config;

import com.github.jerrymice.spring.boot.starter.EnableJerrymiceSpringBootConfiguration;
import com.github.jerrymice.spring.boot.starter.auto.bean.SupperHeaderHttpSessionStrategy;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.web.http.HttpSessionStrategy;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tumingjian
 * 说明:
 */


public class HttpSessionAutoConfiguration {
    /**
     * 默认启用SpringSessionMapRepository
     * @return SessionRepository
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "spring.session.store-type",havingValue = "none",matchIfMissing = true)
    public SessionRepository<ExpiringSession> sessionRegistry() {
        MapSessionRepository mapSessionRepository = new MapSessionRepository(new ConcurrentHashMap<>(50));
        return mapSessionRepository;
    }

    /**
     * 如果未启用SpringSession,那么启用SpringSession
     * @return SpringHttpSessionConfiguration
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(SpringHttpSessionConfiguration.class)
    public SpringHttpSessionConfiguration springHttpSessionConfiguration() {
        return new SpringHttpSessionConfiguration();
    }

    /**
     * 启用扩展的SessionStrategy
     * @return HttpSessionStrategy
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnBean(SpringHttpSessionConfiguration.class)
    @ConditionalOnProperty(name = EnableJerrymiceSpringBootConfiguration.WEB_SESSION_STRATEGY_ENABLE,havingValue = "true",matchIfMissing = true)
    public HttpSessionStrategy httpSessionStrategy() {
        return new SupperHeaderHttpSessionStrategy();
    }
}
