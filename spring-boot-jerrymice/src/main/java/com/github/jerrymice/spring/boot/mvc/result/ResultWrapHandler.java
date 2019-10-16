package com.github.jerrymice.spring.boot.mvc.result;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.Status;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tumingjian
 * 创建时间: 2019-10-10 15:33
 * 功能说明:包装和统一Controller返回结果,仅支持对JSON格式的返回数据进行包装和统一.
 */
public interface ResultWrapHandler {
    /**
     * 是否支持包装结果,如果返回true,将会调用wrapReturnValue方法
     * @param returnValue 原Controller方法返回值
     * @param returnType  原Controller方法返回类型
     * @param mavContainer  view管理器
     * @param webRequest 当前请求request
     * @return  true或false
     * @throws Exception
     */
     default boolean supportsWrap(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest){
         String wrap = webRequest.getHeader("x-http-response-wrap");
         return wrap != null && "true".equalsIgnoreCase(wrap);
     }
    /**
     * 包装结果
     * @param returnValue 原Controller方法返回值
     * @param returnType  原Controller方法返回类型
     * @param mavContainer  view管理器
     * @param webRequest 当前请求request
     * @return  包装后的结果
     * @throws Exception
     */
    default ReturnWrapValue wrapReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest){
        Class<?> returnTypeClass = returnType.getMethod().getReturnType();
        ReturnWrapValue result;
        //处理void方法
        if (returnTypeClass.equals(void.class)) {
            result = new ReturnWrapValue(GlobalErrorCode.REQUEST_SUCCESS.getStatus(), null);
        } else if (returnTypeClass.isAssignableFrom(Result.class)) {
            result = new ReturnWrapValue((Result) returnValue);
        } else if (returnTypeClass.isAssignableFrom(Status.class)) {
            result = new ReturnWrapValue((Status) returnValue, null);
        } else {
            result = new ReturnWrapValue(GlobalErrorCode.REQUEST_SUCCESS.getStatus(), returnValue);
        }
        return result;

    }
}