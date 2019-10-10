package com.github.jerrymice.spring.boot.mvc.bean;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.common.entity.entity.Status;
import com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Type;


/**
 * @author tumingjian
 * 创建时间: 2019-10-08 13:27
 * 功能说明:一个统一全局JSON响应的类,统一JSON响应类为Result类.原方法的返回值将赋值给Result.body属性
 * @see Result
 * @see ResultInfo
 */
public class ResultWrapHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private HandlerMethodReturnValueHandler delegate;

    public ResultWrapHandlerMethodReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return this.delegate.supportsReturnType(returnType) || (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), WrapResponseBody.class) ||
                returnType.hasMethodAnnotation(WrapResponseBody.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        WrapResponseBody methodAnnotation = returnType.getMethodAnnotation(WrapResponseBody.class);
        Class<?> returnTypeClass = returnType.getMethod().getReturnType();

        //处理无注解或注解value为true的所有方法返回值
        if (methodAnnotation == null || methodAnnotation.value()) {
            ReturnWrapValue result;
            //处理void方法
            if (returnTypeClass.equals(void.class)) {
                result = new ReturnWrapValue(GlobalErrorCode.REQUEST_SUCCESS.getStatus(),null);
            } else if (returnTypeClass.isAssignableFrom(Result.class)) {
                result = new ReturnWrapValue((Result)returnValue);
            } else if (returnTypeClass.isAssignableFrom(Status.class)) {
                result = new ReturnWrapValue((Status)returnValue,null);
            } else {
                result = new ReturnWrapValue(GlobalErrorCode.REQUEST_SUCCESS.getStatus(),returnValue);
            }
            MethodParameter methodParameter = new MethodParameter(returnType){
                @Override
                public Type getGenericParameterType() {
                    if (HttpEntity.class.isAssignableFrom(this.getParameterType())) {
                        return super.getGenericParameterType();
                    }
                    else {
                        return ResolvableType.forType(ReturnWrapValue.class).getType();
                    }
                }
            };
            this.delegate.handleReturnValue(result, methodParameter, mavContainer, webRequest);
        }else{
            this.delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        }

    }


}
