package com.github.jerrymice.spring.boot.starter.auto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.jerrymice.common.entity.code.ErrorCode;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.spring.boot.starter.auto.bean.OrderRequestMappingHandlerMapping;
import com.github.jerrymice.spring.boot.starter.auto.bean.SuperHeaderHttpSessionStrategy;
import com.github.jerrymice.spring.boot.starter.auto.bean.UserWebArgumentResolver;
import com.github.jerrymice.spring.boot.starter.auto.interceptor.InterceptUserHandler;
import com.github.jerrymice.spring.boot.starter.auto.interceptor.UserLoginInterceptor;
import com.github.jerrymice.spring.boot.starter.auto.properties.SpringWebMvcProperties;
import com.github.jerrymice.spring.boot.starter.EnableJerryMiceSpringMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tumingjian
 * 说明:
 */

public class WebAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_LOGIN_INTERCEPTOR_ENABLE, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(InterceptUserHandler.class)
    public class DefaultInterceptUserHandler implements InterceptUserHandler {

    }

    /**
     * @author tumingjian
     * 说明:将解析当前用户参数的resolver添加到web配置中
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_USER_ARGUMENT_RESOLVER_ENABLE, havingValue = "true", matchIfMissing = true)
    public class UserWebArgumentResolverConfigurer implements WebMvcConfigurer {
        @Autowired
        private SpringWebMvcProperties.UserArgumentResolver config;

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new ServletWebArgumentResolverAdapter(new UserWebArgumentResolver(config.getUserSessionKey(), config.isEnabledCacheUserClass(), config.getMethodParamName())));
        }
    }


    /**
     * 自动初始化登录拦截器
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_LOGIN_INTERCEPTOR_ENABLE, havingValue = "true", matchIfMissing = true)
    public class UserLoginInterceptorConfigurer implements WebMvcConfigurer {
        @Autowired
        private SpringWebMvcProperties.LoginInterceptor loginConfig;
        @Autowired
        private HttpMessageConverter<Object> converter;
        @Autowired
        private InterceptUserHandler interceptUserHandler;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            InterceptorRegistration registration = registry.addInterceptor(new UserLoginInterceptor(loginConfig.getUserSessionKey(), converter, interceptUserHandler))
                    .order(loginConfig.getOrder());
            String[] pathPatterns = loginConfig.getPathPatterns();
            String[] excludePathPatterns = loginConfig.getExcludePathPatterns();
            if(pathPatterns!=null){
                registration.addPathPatterns(pathPatterns);
            }
            if(excludePathPatterns!=null){
                registration.excludePathPatterns(excludePathPatterns);
            }
        }
    }

    /**
     * @author tumingjian
     * 说明:允许跨域
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_GLOBAL_CORS_ENABLE, havingValue = "true", matchIfMissing = true)
    public class CorsMappingsWebMvcConfigurer implements WebMvcConfigurer {
        @Autowired
        private SpringWebMvcProperties.GlobalCors config;

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins(config.getAllowedOrigins())
                    .allowCredentials(true)
                    .allowedMethods(config.getAllowedMethods())
                    .maxAge(config.getMaxAge());
        }
    }

    /**
     * @author kexl
     * 转换long为string, 防止前台JS丢失精度
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_MESSAGE_CONVERTERS_ENABLE, havingValue = "true", matchIfMissing = true)
    public class MessageConvertersWebMvcConfigurer implements WebMvcConfigurer {

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
            jackson2HttpMessageConverter.setObjectMapper(objectMapper);
            converters.add(0, jackson2HttpMessageConverter);
            converters.add(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        }
    }

    /**
     * @author tumingjian
     * 说明: 映射项目下webapp/resource/目录为静态资源/**
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_RESOURCE_HANDLER_ENABLE, havingValue = "true", matchIfMissing = true)
    public class MappingStaticResourceWebMvcConfigurer implements WebMvcConfigurer {
        @Autowired
        private SpringWebMvcProperties.MappingStaticResource config;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (config.getResourceHandler().length == config.getResourceLocation().length) {
                for (int i = 0; i < config.getResourceHandler().length; i++) {
                    ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(config.getResourceHandler()[i]).addResourceLocations(config.getResourceLocation()[i]);
                    if (config.getCachePeriod() != null && config.getCachePeriod().length > i) {
                        resourceHandlerRegistration
                                .setCachePeriod(config.getCachePeriod()[i]);
                    }
                    if (config.getCacheResource() != null && config.getCacheResource().length > i) {
                        resourceHandlerRegistration
                                .resourceChain(config.getCacheResource()[i]);
                    }

                }
            } else {
                throw new ResourceAccessException("application config property value jerrymice.spring.mvc.resource-handler length need equals jerrymice.spring.mvc.resource-location length");
            }
        }
    }

    /**
     * @author tumingjian
     * 说明:先启用spring session.再根据配置启用SuperHeaderHttpSessionStrategy
     */
    @Configuration
    @ConditionalOnWebApplication
    public class SuperHttpSessionAutoConfiguration {
        @Autowired
        private SpringWebMvcProperties.SessionStrategy config;

        /**
         * 默认启用SpringSessionMapRepository
         *
         * @return SessionRepository
         */
        @Bean
        @ConditionalOnWebApplication
        @ConditionalOnProperty(name = "spring.session.store-type", havingValue = "none", matchIfMissing = true)
        public SessionRepository<ExpiringSession> sessionRegistry() {
            MapSessionRepository mapSessionRepository = new MapSessionRepository(new ConcurrentHashMap<>(50));
            return mapSessionRepository;
        }

        /**
         * 如果未启用SpringSession,那么启用SpringSession
         *
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
         *
         * @return HttpSessionStrategy
         */
        @Bean
        @ConditionalOnWebApplication
        @ConditionalOnBean(SpringHttpSessionConfiguration.class)
        @ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_SESSION_STRATEGY_ENABLE, havingValue = "true", matchIfMissing = true)
        public HttpSessionStrategy httpSessionStrategy() {
            return new SuperHeaderHttpSessionStrategy(config.getSessionAliasParamName(), config.isSupportHttpHeader(), config.isSupportQueryString(), config.isSupportCookie());
        }

    }


}
