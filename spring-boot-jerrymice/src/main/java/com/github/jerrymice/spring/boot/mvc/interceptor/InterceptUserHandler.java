package com.github.jerrymice.spring.boot.mvc.interceptor;


import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.ResultInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tumingjian
 * 说明:自定义的登录验证拦截后结果处理者
 */
public interface InterceptUserHandler {
    /**
     * 登录拦截器拦截后的处理结果
     * @param request HttpServletRequest
     * @return 返回要JSON序列化的对象.
     */
    default Object returnObject(HttpServletRequest request){
        return  GlobalErrorCode.INVALID_USER_SESSION.getStatus();
    }
}
