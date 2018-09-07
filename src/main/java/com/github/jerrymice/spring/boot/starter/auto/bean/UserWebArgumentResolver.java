package com.github.jerrymice.spring.boot.starter.auto.bean;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tumingjian
 * SpringMVC参数解析拦截器
 * 返回当前用户信息
 */
public class UserWebArgumentResolver implements WebArgumentResolver {
    /**
     * 当前用户在session中的key值
     */
    private String userSessionKey;

    public UserWebArgumentResolver(String userSessionKey) {
        this.userSessionKey = userSessionKey;
    }
    /**
     * 缓存当前用户类
     */
    private static Class<?> cacheCurrentUserClass;

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        if (cacheCurrentUserClass != null) {
            if (methodParameter.getParameterType() != null && cacheCurrentUserClass.isAssignableFrom(methodParameter.getParameterType())) {
                HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                Object currentUser = request.getSession().getAttribute(userSessionKey);
                return currentUser;
            }
        } else {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            Object currentUser = request.getSession().getAttribute(userSessionKey);
            if (currentUser != null) {
                cacheCurrentUserClass = currentUser.getClass();
                if (methodParameter.getParameterType() != null && cacheCurrentUserClass.isAssignableFrom(methodParameter.getParameterType())) {
                    return currentUser;
                }
            }
        }
        return UNRESOLVED;
    }

}
