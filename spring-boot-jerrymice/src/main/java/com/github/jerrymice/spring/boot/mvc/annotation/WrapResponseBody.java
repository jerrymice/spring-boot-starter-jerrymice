package com.github.jerrymice.spring.boot.mvc.annotation;

import com.github.jerrymice.spring.boot.mvc.bean.ResultWrapHandlerMethodReturnValueHandler;

import java.lang.annotation.*;

/**
 * @author tumingjian
 * 创建时间: 2019-10-08 13:28
 * 功能说明: 与ResponseBody相同,可以为JSON响应输出统一的格式.
 * 该注解可以替代ResponseBody,将相关的controller返回值先包装为Result的形式,最后输出JSON
 * @see com.github.jerrymice.common.entity.entity.Result
 * @see ResultWrapHandlerMethodReturnValueHandler
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WrapResponseBody {
    /**
     * 是否用Result类包装
     * 如果为true,表示将结果先包装为Result再返回JSON字符串
     * 如果为false,表示原样输出结果的JSON字符串
     * @return
     */
    boolean value() default true;
}
