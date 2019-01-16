package com.github.jerrymice.spring.boot.starter.config;

import com.github.jerrymice.spring.boot.starter.EnableJerryMiceSpringMvcConfiguration;
import com.github.jerrymice.spring.boot.starter.bean.OrderRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class WebMvcAutoRegistrations {
    /**
     * @author tumingjian
     * 说明:启用OrderRequestMappingHandlerMapping
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(OrderRequestMappingHandlerMapping.class)
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_ORDER_MAPPING_ENABLED, havingValue = "true", matchIfMissing = true)
    public class OrderRequestMappingHandlerMappingWebMvcRegistrations implements WebMvcRegistrations {
        @Override
        public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
            return new OrderRequestMappingHandlerMapping();
        }
    }
}
