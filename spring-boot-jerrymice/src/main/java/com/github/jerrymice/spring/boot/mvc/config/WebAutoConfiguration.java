package com.github.jerrymice.spring.boot.mvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.jerrymice.spring.boot.EnableJerryMice;
import com.github.jerrymice.spring.boot.mvc.bean.*;
import com.github.jerrymice.spring.boot.mvc.interceptor.InterceptUserHandler;
import com.github.jerrymice.spring.boot.mvc.interceptor.UserLoginInterceptor;
import com.github.jerrymice.spring.boot.mvc.properties.SpringWebMvcProperties;
import com.github.jerrymice.spring.boot.mvc.result.DelegateRequestResponseBodyMethodProcessor;
import com.github.jerrymice.spring.boot.mvc.result.ResultWrapHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
@Configuration
public class WebAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(name = EnableJerryMice.WEB_LOGIN_INTERCEPTOR_ENABLE, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(InterceptUserHandler.class)
    public class DefaultInterceptUserHandler implements InterceptUserHandler {
        @Autowired
        private SpringWebMvcProperties springWebMvcProperties;
        /**
         * 登录请求拦截后的响应内容返回
         *
         * @param request 请求
         * @param response 响应
         * @param converter json转换器
         * @throws IOException
         */
        @Override
        public void forbidden(HttpServletRequest request, ServletServerHttpResponse response, HttpMessageConverter converter) throws IOException {
            /**
             * 如果没有x-http-response-wrap头,
             */
            if (HttpStatus.OK.value() == response.getServletResponse().getStatus()
                    && springWebMvcProperties.isUnifyResponse()
                    && "true".equalsIgnoreCase(request.getHeader("x-http-response-wrap"))) {
                converter.write(jsonBody(request), MediaType.APPLICATION_JSON, response);
            } else {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                converter.write(jsonBody(request), MediaType.APPLICATION_JSON, response);
            }
        }
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
        private ApplicationContext applicationContext;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            InterceptorRegistration registration = registry.addInterceptor(new UserLoginInterceptor(applicationContext))
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
            } else {
                log.warn("您启用了登录拦截器,至少应该排除登录地址");
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
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
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
     * 创建时间: 2019-10-10 16:22
     * 功能说明: 默认统一的ControllerJSON返值值结果处理类
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(ResultWrapHandler.class)
    @ConditionalOnProperty(name = EnableJerryMice.WEB_UNIFY_RESPONSE_ENABLED, havingValue = "true", matchIfMissing = true)
    public class DefaultResultWrapHandler implements ResultWrapHandler {

    }
    /**
     * @author tumingjian
     * 创建时间: 2019-10-10 16:22
     * 功能说明: 统一的ControllerJSON返值值结果处理配置类
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = EnableJerryMice.WEB_UNIFY_RESPONSE_ENABLED, havingValue = "true", matchIfMissing = true)
    @AutoConfigureAfter(BeanConfiguration.class)
    public class ResultWrapConfiguration implements InitializingBean {
        @Autowired
        private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
        @Autowired
        private SpringWebMvcProperties springWebMvcProperties;
        @Autowired(required = false)
        private ResultWrapHandler resultWrapHandler;

        /**
         * 启用统一的Result JSON响应值时,需要替换掉原始的RequestResponseBodyMethodProcessor
         * * @see com.github.jerrymice.common.entity.entity.Result
         *
         * @throws Exception
         * @see HandlerMethodReturnValueHandler
         */
        @Override
        public void afterPropertiesSet() throws Exception {
            if (springWebMvcProperties.isUnifyResponse()) {
                List<HandlerMethodReturnValueHandler> unmodifiableList = requestMappingHandlerAdapter.getReturnValueHandlers();
                List<HandlerMethodReturnValueHandler> list = new ArrayList<>(unmodifiableList.size());
                for (HandlerMethodReturnValueHandler returnValueHandler : unmodifiableList) {
                    if (returnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                        list.add(new DelegateRequestResponseBodyMethodProcessor(returnValueHandler, resultWrapHandler));
                    } else {
                        list.add(returnValueHandler);
                    }
                }
                requestMappingHandlerAdapter.setReturnValueHandlers(list);
            }
        }
    }
    /**
     * 这是一个启用了统一JSON返回值时,全局统一异常HttpStatus为200的配置类.
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = {EnableJerryMice.WEB_UNIFY_RESPONSE_ENABLED,EnableJerryMice.WEB_GLOBAL_EXCEPTION_ENABLED}, havingValue = "true", matchIfMissing = true)
    public class ExceptionResolversWebMvcConfigurer implements WebMvcConfigurer {
        @Autowired(required = false)
        private ResultWrapHandler resultWrapHandler;
        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
            if(resultWrapHandler!=null){
                HandlerExceptionResolver handlerExceptionResolver = resolvers.get(0);
                if(handlerExceptionResolver instanceof ExceptionHandlerExceptionResolver){
                    ExceptionHandlerExceptionResolver resolver=(ExceptionHandlerExceptionResolver)  handlerExceptionResolver;
                    List<HandlerMethodReturnValueHandler> handlers = resolver.getReturnValueHandlers().getHandlers();
                    ArrayList<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
                    for(HandlerMethodReturnValueHandler handler :handlers){
                        if(handler instanceof RequestResponseBodyMethodProcessor){
                            newHandlers.add(new DelegateRequestResponseBodyMethodProcessor(handler,resultWrapHandler));
                        }
                        newHandlers.add(handler);
                    }
                    resolver.setReturnValueHandlers(newHandlers);
                }
            }
        }
    }

    /**
     * @author tumingjian
     * 说明:先启用spring session.再根据配置启用SuperHeaderHttpSessionStrategy
     */
    @Configuration
    @ConditionalOnWebApplication
    public class BeanConfiguration {
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
         *
         * 启用扩展的SessionStrategy,适合于spring-session 2.0以下的版本
         *
         * @return HttpSessionStrategy
         */
        @Deprecated
        @Bean
        @ConditionalOnWebApplication
        @ConditionalOnBean(SpringHttpSessionConfiguration.class)
        @ConditionalOnClass(HttpSessionStrategy.class)
        @ConditionalOnMissingClass("org.springframework.session.web.http.HttpSessionIdResolver")
        @ConditionalOnProperty(name = EnableJerryMice.WEB_SESSION_STRATEGY_ENABLE, havingValue = "true", matchIfMissing = true)
        public SuperHeaderHttpSessionStrategy httpSessionStrategy() {
            return new SuperHeaderHttpSessionStrategy(config.getSessionAliasParamName(), config.isSupportHttpHeader(), config.isSupportQueryString(), config.isSupportCookie());
        }

        /**
         * 启用扩展的HttpSessionIdResolver,适合于spring-session 2.0以上的版本
         *
         * @return HttpSessionIdResolver
         */
        @Bean
        @ConditionalOnWebApplication
        @ConditionalOnBean(SpringHttpSessionConfiguration.class)
        @ConditionalOnClass(HttpSessionIdResolver.class)
        @ConditionalOnProperty(name = EnableJerryMice.WEB_SESSION_STRATEGY_ENABLE, havingValue = "true", matchIfMissing = true)
        public SuperHeaderHttpSessionIdResolver httpSessionIdResolver() {
            return new SuperHeaderHttpSessionIdResolver(config.getSessionAliasParamName(), config.isSupportHttpHeader(), config.isSupportQueryString(), config.isSupportCookie());
        }
        /**
         * 支持在 ConstraintValidator接口中直接用Autowired注解引用spring中的bean,用于自定义的注解验证器.
         *
         * @return LocalValidatorFactoryBean
         */
        @Bean
        @ConditionalOnMissingBean(LocalValidatorFactoryBean.class)
        public LocalValidatorFactoryBean localValidatorFactoryBean() {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            return localValidatorFactoryBean;
        }

    }


}
