package com.github.jerrymice.spring.boot.mvc.bean;


import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.common.entity.ex.ResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author kexl
 * 异常处理
 */

@RestControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 参数校验
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public Result handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) error;

                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            String message = e.getAllErrors().stream().map(i -> i.getObjectName() + ":" + i.getDefaultMessage()).collect(Collectors.joining(";"));
            log.error(MessageFormat.format("请求参数出错,path:{0},errormessage:{1}", e.getNestedPath(), message));
            return new ResultInfo(GlobalErrorCode.INVALID_REQUEST_ARGUMENTS).setMessage(map.values().stream().collect(Collectors.joining(";")));
        } else {
            return new ResultInfo(GlobalErrorCode.INVALID_REQUEST_ARGUMENTS.getCode(), e.getFieldError().getDefaultMessage());
        }
    }

    @ExceptionHandler(NullPointerException.class)
    public Result dealNullPointerException(NullPointerException e) {
        log.error("空指针异常:", e);
        return new ResultInfo(false).setCode(GlobalErrorCode.SYSTEM_UNKNOWN_ERROR.getCode()).setMessage("空指针错误：" + e.getLocalizedMessage());
    }

    /**
     * 405 - Method Not Allowed
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e.getLocalizedMessage());
        return new ResultInfo(false).setCode(GlobalErrorCode.SYSTEM_UNKNOWN_ERROR.getCode()).setMessage("不支持当前请求方法");
    }

    /**
     * 415 - Unsupported Media Type
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("不支持当前媒体类型", e.getLocalizedMessage());
        return new ResultInfo(false).setCode(GlobalErrorCode.SYSTEM_UNKNOWN_ERROR.getCode()).setMessage("不支持当前媒体类型");
    }

    /**
     * 通用异常
     *
     * @param request HttpServletRequest
     * @param e       spring 异常信息
     * @return 异常响应结果
     */
    @ExceptionHandler(Exception.class)
    public Result defaultExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Rest Controller 异常", e);
        if (e instanceof ResultException) {
            return new ResultInfo(((ResultException) e).getCode(), e.getLocalizedMessage());
        } else {
            return new ResultInfo(false).setCode(GlobalErrorCode.SYSTEM_UNKNOWN_ERROR.getCode()).setMessage("错误信息：" + e.getMessage());
        }
    }

}
