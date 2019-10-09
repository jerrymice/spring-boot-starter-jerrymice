package com.github.jerrymice.spring.boot.mvc.annotation;

import com.github.jerrymice.spring.boot.mvc.bean.ResultWrapHandlerMethodReturnValueHandler;

import java.lang.annotation.*;

/**
 * @author tumingjian
 * 创建时间: 2019-10-08 13:28
 * 功能说明: 与ResponseBody相同,可以为JSON响应输出统一的格式.
 * @see com.github.jerrymice.common.entity.entity.Result
 * @see ResultWrapHandlerMethodReturnValueHandler
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WrapResponseBody {
    /**
     * 是否用Result类包装,默认为true
     * @return
     */
    boolean value() default true;
}
