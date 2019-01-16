package com.github.jerrymice.spring.boot.starter.interceptor;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author tumingjian
 *         Spring JSON登录拦截器.
 */
public class UserLoginInterceptor implements HandlerInterceptor {
    private String userSessionKey;
    private HttpMessageConverter<Object> converter;
    private InterceptUserHandler handler;

    public UserLoginInterceptor(String userSessionKey, HttpMessageConverter<Object> converter, InterceptUserHandler handler) {
        this.userSessionKey = userSessionKey;
        this.converter = converter;
            this.handler = handler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute(userSessionKey) == null) {
            converter.write(this.handler.returnObject(request), MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
            return false;
        }
        return true;
    }


}
