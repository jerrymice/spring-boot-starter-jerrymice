package com.github.jerrymice.spring.boot.mvc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author tumingjian
 *         说明:
 */
@Data
@ConfigurationProperties(prefix = "jerrymice.spring.mvc")
@Configuration
public class SpringWebMvcProperties {
    /**
     * 存入user信息在session的KEY值
     */
    private String userSessionKey = "currentUser";
    /**
     * 是否添加默认的JSON Converter
     */
    private boolean jackJsonMessageConverter = true;
    /**
     * 启用增强的OrderRequestMappingHandlerMapping
     */
    private boolean orderMappingHandler = false;
    /**
     * 启用全局JSON格式统一响应,所有的响应内容都包装为Result类
     */
    private boolean unifyResponse=true;
    /**
     * 使用全局异常处理器
     */
    private boolean globalException=true;
    /**
     * 设置静态资源映射,每一个数组的相同下标对应一组资源
     */
    @Data
    @ConfigurationProperties(prefix = "jerrymice.spring.mvc.mapping-static-resource")
    @Configuration
    public class MappingStaticResource {
        /**
         * 设置映射路径
         */
        private String[] resourceHandler = new String[]{"/**"};
        /**
         * 本地路径
         */
        private String[] resourceLocation = new String[]{"/resource/"};
        /**
         * 是否缓存静态资源
         */
        private Boolean[] cacheResource = new Boolean[]{true};
        /**
         * 是否限制缓存配额
         */
        private Integer[] cachePeriod = new Integer[]{0};
    }

    /**
     * 是否加入当前用户的参数拦截.必须在登录之后将当前用户信息以userSessionKey的值为key存入session中才能生效
     */
    @Data
    @ConfigurationProperties(prefix = "jerrymice.spring.mvc.user-argument-resolver")
    @Configuration
    public class UserArgumentResolver {
        /**
         * 是否加入当前用户的参数拦截.必须在登录之后将当前用户信息以userSessionKey的值为key存入session中才能生效
         */
        private boolean enabled;
        /**
         * 当前user信息在session的KEY值
         */
        private String userSessionKey=SpringWebMvcProperties.this.userSessionKey;
        /**
         * 是否缓存用户对象的class类型,如果存在多种不同类型的用户登录同一系统,那么建议设置为false
         */
        private boolean enabledCacheUserClass = true;
        /**
         * 参数名,如果设置了参数名,那么将验证参数类型和参数名是否相同,只有方法的参数名与该配置值一致时才自动注入
         */
        private String methodParamName = null;
    }

    /**
     * 启用增强的sessionID生成和存取策略
     */
    @Data
    @ConfigurationProperties(prefix = "jerrymice.spring.mvc.session-strategy")
    @Configuration
    public class SessionStrategy {
        /**
         * 是否启用增强的SessionStrategy
         */
        private boolean enabled = true;
        /**
         * 验证sessionId的key名称
         */
        private String sessionAliasParamName = "access-token";
        /**
         * 启用在http header 中传入以sessionAliasParamName属性的值为Key,sessionId或token为Value的键值对来验证ID
         */
        private boolean supportHttpHeader = true;
        /**
         * 启用在HTTP GET URL 查询参数中传入以sessionAliasParamName属性的值为Key,sessionId或token为value的键值对来验证ID
         * 比如微信验证跳转
         */
        private boolean supportQueryString = true;
        /**
         * 是否启用cookie
         */
        private boolean supportCookie = true;
    }

    /**
     * 全局跨域
     */
    @Data
    @ConfigurationProperties(prefix = "jerrymice.spring.mvc.global-cors")
    @Configuration
    public class GlobalCors {
        /**
         * 是否使用全局跨域
         */
        private boolean enabled = true;
        /**
         * 允许跨域的组织机构
         */
        private String[] allowedOrigins = new String[]{"*"};
        /**
         * 允许跨域的HTTP的METHODS
         */
        private String[] allowedMethods = new String[]{"GET", "POST", "DELETE", "PUT"};
        /**
         * 跨域缓存检查时间
         */
        private int maxAge = 3600;
    }

    /**
     *
     * @author tumingjian
     * 说明:登录拦截器配置
     */
    @Data
    @ConfigurationProperties(prefix = "jerrymice.spring.mvc.login-interceptor")
    @Configuration
    public class LoginInterceptor {
        /**
         * 是否启用登录拦截器
         */
        private boolean enabled = true;
        /**
         * 当前user信息在session的KEY值
         */
        private String userSessionKey = SpringWebMvcProperties.this.userSessionKey;
        /**
         * 要拦截的URL Pattern列表
         */
        private String[] pathPatterns;
        /**
         * 要排除的URL Pattern列表
         */
        private String[] excludePathPatterns;
        /**
         * 拦截器顺序
         */
        private int order = 1;
    }
    /**
     * @author tumingjian
     * 说明:权限框架配置
     */
    @Data
    @ConfigurationProperties(prefix = "jerrymice.spring.mvc.security")
    @Configuration
    public class JerryMiceSecurityProperties {
        /**
         * 是否启用权限控制框架
         */
        private boolean enabled = false;
        /**
         * 用户的sessionKey名称
         */
        private String userSessionKey = SpringWebMvcProperties.this.userSessionKey;
        /**
         * 用户的所拥有相关资源的Key名称
         */
        private String resourceSessionKey = "currentResource";


    }

}
