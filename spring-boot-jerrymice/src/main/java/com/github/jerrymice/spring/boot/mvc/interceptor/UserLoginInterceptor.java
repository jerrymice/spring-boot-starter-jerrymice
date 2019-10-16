package com.github.jerrymice.spring.boot.mvc.interceptor;

import com.github.jerrymice.spring.boot.mvc.properties.SpringWebMvcProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author tumingjian
 * Spring JSON登录拦截器.
 */
public class UserLoginInterceptor implements HandlerInterceptor {
    private String userSessionKey;
    private HttpMessageConverter<Object> converter;
    private InterceptUserHandler handler;
    public UserLoginInterceptor(ApplicationContext applicationContext) {
        this.userSessionKey = applicationContext.getBean(SpringWebMvcProperties.LoginInterceptor.class).getUserSessionKey();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(HttpMessageConverter.class, Object.class));
        this.converter = (HttpMessageConverter) applicationContext.getBean(beanNamesForType[0]);
        this.handler = applicationContext.getBean(InterceptUserHandler.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute(userSessionKey) == null) {
            ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
            this.handler.forbidden(request,servletServerHttpResponse,converter);
            return false;
        }
        return true;
    }
}
