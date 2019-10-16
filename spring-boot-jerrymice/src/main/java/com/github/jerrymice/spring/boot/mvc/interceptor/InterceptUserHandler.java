package com.github.jerrymice.spring.boot.mvc.interceptor;


import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author tumingjian
 * 说明:自定义的登录验证拦截后结果处理者
 */
public interface InterceptUserHandler {
    /**
     * 登录拦截器拦截后的处理结果值
     *
     * @param request HttpServletRequest
     * @return 返回要JSON序列化的对象.
     */
    default Object jsonBody(HttpServletRequest request) {
        return GlobalErrorCode.INVALID_USER_SESSION.getStatus();
    }

    /**
     * 登录请求拦截后的响应内容返回
     *
     * @param request 请求
     * @param response 响应
     * @param converter json转换器
     * @throws IOException
     */
    default void forbidden(HttpServletRequest request, ServletServerHttpResponse response, HttpMessageConverter converter) throws IOException {
        converter.write(jsonBody(request), MediaType.APPLICATION_JSON, response);
    }
}
