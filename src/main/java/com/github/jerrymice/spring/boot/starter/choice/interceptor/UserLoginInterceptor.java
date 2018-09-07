package com.github.jerrymice.spring.boot.starter.choice.interceptor;
import com.github.jerrymice.common.entity.code.ErrorCode;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author tumingjian
 * Spring JSON登录拦截器.
 */
public class UserLoginInterceptor implements HandlerInterceptor{
    private String userSessionKey;
    private HttpMessageConverter<Object> converter;

    /**
     *
     * @param userSessionKey  用户Key
     * @param converter spring web mvc JSON HttpMessageConverter
     */
    public UserLoginInterceptor(String userSessionKey, HttpMessageConverter<Object> converter) {
        this.userSessionKey = userSessionKey;
        this.converter = converter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute(userSessionKey) == null) {
            ResultInfo resultInfo = new ResultInfo(false);
            resultInfo.setCode(ErrorCode.USER_NO_LOGIN.getCode()).setMessage(ErrorCode.USER_NO_LOGIN.getMessage());
            converter.write(resultInfo, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
            return false;


        }
        return true;
    }
}
