package com.github.jerrymice.spring.boot.mvc.config;

import com.github.jerrymice.spring.boot.EnableJerryMice;
import com.github.jerrymice.spring.boot.mvc.bean.OrderRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author tumingjian
 * 说明: 注入一个 支持排序的RequestMappingHandlerMapping
 * @see OrderRequestMappingHandlerMapping
 * @see WebMvcRegistrations
 */
@Configuration
public class WebMvcAutoRegistrations {
    /**
     * @author tumingjian
     * 说明:启用OrderRequestMappingHandlerMapping
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(OrderRequestMappingHandlerMapping.class)
    @ConditionalOnProperty(name = EnableJerryMice.WEB_ORDER_MAPPING_ENABLED, havingValue = "true", matchIfMissing = true)
    public class OrderRequestMappingHandlerMappingWebMvcRegistrations implements WebMvcRegistrations {
        @Override
        public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
            return new OrderRequestMappingHandlerMapping();
        }
    }
}
