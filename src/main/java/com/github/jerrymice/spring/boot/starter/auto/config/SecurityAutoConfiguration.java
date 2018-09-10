package com.github.jerrymice.spring.boot.starter.auto.config;

import com.github.jerrymice.common.permission.GlobalPermissionFailure;
import com.github.jerrymice.common.permission.PermissionAspect;
import com.github.jerrymice.common.permission.PermissionResource;
import com.github.jerrymice.common.permission.PermissionUserHandler;
import com.github.jerrymice.spring.boot.starter.auto.properties.JerryMiceSecurityProperties;
import com.github.jerrymice.spring.boot.starter.EnableJerryMiceSpringMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * @author tumingjian
 * 说明:
 */
@ConditionalOnProperty(name = EnableJerryMiceSpringMvcConfiguration.WEB_SECURITY, havingValue = "true")
public class SecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
    @Bean
    @ConditionalOnMissingBean(PermissionAspect.class)
    public PermissionAspect permissionAspect(GlobalPermissionFailure globalPermissionFailure, PermissionResource permissionResource, PermissionUserHandler userHandler) {
        PermissionAspect permissionAspect = new PermissionAspect(globalPermissionFailure,permissionResource,userHandler);
        return permissionAspect;
    }
    @Configuration
    @ConditionalOnMissingBean(GlobalPermissionFailure.class)
    public class DefaultGlobalPermissionFailure implements GlobalPermissionFailure {

    }
    @Configuration
    @ConditionalOnMissingBean(SessionKeyPermissionResource.class)
    public class SessionKeyPermissionResource implements PermissionResource {
        @Autowired
        private HttpSession httpSession;
        @Autowired
        private JerryMiceSecurityProperties securityProperties;

        @Override
        public Set<String> currentUserResources(Object user) {
            return (Set<String>) httpSession.getAttribute(securityProperties.getResourceSessionKey());
        }
    }
    @Configuration
    @ConditionalOnMissingBean(SessionKeyPermissionUserHandler.class)
    public class SessionKeyPermissionUserHandler implements PermissionUserHandler{
        @Autowired
        private HttpSession httpSession;
        @Autowired
        private JerryMiceSecurityProperties securityProperties;
        @Override
        public Object getCurrentUser() {
            return httpSession.getAttribute(securityProperties.getUserSessionKey());
        }
    }
}
