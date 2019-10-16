package com.github.jerrymice.spring.boot.mvc.result;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Status;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;


/**
 * @author tumingjian
 * 创建时间: 2019-10-08 13:27
 * 功能说明:一个统一全局JSON响应的类处理类,这个类替换了spring本身的DelegateRequestResponseBodyMethodProcessor;
 */
public class DelegateRequestResponseBodyMethodProcessor implements HandlerMethodReturnValueHandler {
    /**
     * @see DelegateRequestResponseBodyMethodProcessor
     */
    private HandlerMethodReturnValueHandler delegate;
    private ResultWrapHandler wrapDelegate;

    public DelegateRequestResponseBodyMethodProcessor(HandlerMethodReturnValueHandler delegate, ResultWrapHandler wrapDelegate) {
        this.delegate = delegate;
        this.wrapDelegate = wrapDelegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return this.delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        //包装类
        if (wrapDelegate.supportsWrap(returnValue, returnType, mavContainer, webRequest)) {
            final ReturnWrapValue returnWrapValue = wrapDelegate.wrapReturnValue(returnValue, returnType, mavContainer, webRequest);
            MethodParameter methodParameter = new MethodParameter(returnType) {
                @Override
                public Type getGenericParameterType() {
                    if (HttpEntity.class.isAssignableFrom(this.getParameterType())) {
                        return super.getGenericParameterType();
                    } else {
                        if (returnWrapValue != null) {
                            return ResolvableType.forInstance(returnWrapValue).getType();
                        } else {
                            return ResolvableType.forType(ReturnWrapValue.class).getType();
                        }
                    }
                }
            };
            /**
             * 当启用统一返回值包装时,所有HttpStatus都强制返回值为200
             */
            HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
            nativeResponse.setStatus(HttpStatus.OK.value());
            this.delegate.handleReturnValue(returnWrapValue, methodParameter, mavContainer, webRequest);
        } else {
            this.delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        }
    }
}
