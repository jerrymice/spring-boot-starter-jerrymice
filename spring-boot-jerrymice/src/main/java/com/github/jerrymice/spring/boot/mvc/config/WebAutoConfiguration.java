package com.github.jerrymice.spring.boot.mvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.jerrymice.spring.boot.EnableJerryMice;
import com.github.jerrymice.spring.boot.mvc.bean.*;
import com.github.jerrymice.spring.boot.mvc.interceptor.InterceptUserHandler;
import com.github.jerrymice.spring.boot.mvc.interceptor.UserLoginInterceptor;
import com.github.jerrymice.spring.boot.mvc.properties.SpringWebMvcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tumingjian
 * 说明:
 */
@Slf4j
public class WebAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(name = EnableJerryMice.WEB_LOGIN_INTERCEPTOR_ENABLE, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(InterceptUserHandler.class)
    public class DefaultInterceptUserHandler implements InterceptUserHandler {

    }

    /**
     * @author tumingjian
     * 说明:将解析当前用户参数的resolver添加到web配置中
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMice.WEB_USER_ARGUMENT_RESOLVER_ENABLE, havingValue = "true", matchIfMissing = true)
    public class UserWebArgumentResolverConfigurer implements WebMvcConfigurer {
        @Autowired
        private SpringWebMvcProperties.UserArgumentResolver config;
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new ServletWebArgumentResolverAdapter(new UserWebArgumentResolver(config.getUserSessionKey(), config.isEnabledCacheUserClass(), config.getMethodParamName())));
        }
    }


    /**
     * 自动初始化登录拦截器GlobalExceptionHandler.
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMice.WEB_LOGIN_INTERCEPTOR_ENABLE, havingValue = "true", matchIfMissing = true)
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
            if (pathPatterns != null) {
                registration.addPathPatterns(pathPatterns);
            } else {
                log.warn("您启用了登录拦截器,但却没有设置登录拦截器要拦截的地址");
            }
            if (excludePathPatterns != null) {
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
    @ConditionalOnProperty(name = EnableJerryMice.WEB_GLOBAL_CORS_ENABLE, havingValue = "true", matchIfMissing = true)
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
    @ConditionalOnProperty(name = EnableJerryMice.WEB_MESSAGE_CONVERTERS_ENABLE, havingValue = "true", matchIfMissing = true)
    public class MessageConvertersWebMvcConfigurer implements WebMvcConfigurer {

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
            converter.setObjectMapper(objectMapper);
            converters.add(0, converter);
            converters.add(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        }
    }
    /**
     * @author tumingjian
     * 说明:先启用spring session.再根据配置启用SuperHeaderHttpSessionStrategy
     */
    @Configuration
    @ConditionalOnWebApplication
    public class BeanConfiguration implements InitializingBean {
        @Autowired
        private SpringWebMvcProperties.SessionStrategy config;
        @Autowired
        private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
        @Autowired
        private SpringWebMvcProperties springWebMvcProperties;

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
        @ConditionalOnProperty(name = EnableJerryMice.WEB_SESSION_STRATEGY_ENABLE, havingValue = "true", matchIfMissing = true)
        public HttpSessionStrategy httpSessionStrategy() {
            return new SuperHeaderHttpSessionStrategy(config.getSessionAliasParamName(), config.isSupportHttpHeader(), config.isSupportQueryString(), config.isSupportCookie());
        }

        /**
         * 支持在 ConstraintValidator接口中直接用Autowired流解引用spring中的bean,用于自定义的注解验证器.
         *
         * @return LocalValidatorFactoryBean
         */
        @Bean
        @ConditionalOnMissingBean(LocalValidatorFactoryBean.class)
        public LocalValidatorFactoryBean localValidatorFactoryBean() {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            return localValidatorFactoryBean;
        }

        /**
         * 启用统一的Result JSON响应值时,需要替换掉原始的RequestResponseBodyMethodProcessor
         * @see com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody
         * @see com.github.jerrymice.common.entity.entity.Result
         * @see HandlerMethodReturnValueHandler
         * @throws Exception
         */
        @Override
        public void afterPropertiesSet() throws Exception {
            if(springWebMvcProperties.isUnifyResponse()){
                List<HandlerMethodReturnValueHandler> unmodifiableList = requestMappingHandlerAdapter.getReturnValueHandlers();
                List<HandlerMethodReturnValueHandler> list = new ArrayList<>(unmodifiableList.size());
                for (HandlerMethodReturnValueHandler returnValueHandler : unmodifiableList) {
                    if (returnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                        list.add(new ResultWrapHandlerMethodReturnValueHandler(returnValueHandler));
                    } else {
                        list.add(returnValueHandler);
                    }
                }
                requestMappingHandlerAdapter.setReturnValueHandlers(list);
            }
        }
    }


}
