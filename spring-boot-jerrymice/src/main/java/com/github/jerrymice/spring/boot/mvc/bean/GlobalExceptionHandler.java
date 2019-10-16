package com.github.jerrymice.spring.boot.mvc.bean;


import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.common.entity.entity.Status;
import com.github.jerrymice.common.entity.ex.RemoteResultException;
import com.github.jerrymice.common.entity.ex.ResultException;
import com.github.jerrymice.common.entity.ex.ResultIllegalArgumentException;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
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
     * 如果是ResultException异常,那么在日志中排除这些包的堆栈信息
     */
    private static String[] RESULT_EXCEPTION_PRINT_EXCLUDE_PACKAGE = new String[]{"java.", "javax.", "sun.", "org.apache", "org.springframework"};

    /**
     * 404-参数校验
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public Status handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) error;

                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            String message = e.getAllErrors().stream().map(i -> i.getObjectName() + ":" + i.getDefaultMessage()).collect(Collectors.joining(";"));
            log.error(MessageFormat.format("Rest Controller参数 异常,路径:{0},错误信息:{1}", e.getNestedPath(), message));
            return Status.wrapped(GlobalErrorCode.INVALID_REQUEST_ARGUMENTS.getCode(), map.values().stream().collect(Collectors.joining(";")));
        } else {
            return Status.wrapped(GlobalErrorCode.INVALID_REQUEST_ARGUMENTS.getCode(), e.getFieldError().getDefaultMessage());
        }
    }

    /**
     * 400
     *
     * @param e 异常
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResultIllegalArgumentException.class)
    public Status resultIllegalArgumentException(ResultIllegalArgumentException e) {
        if (log.isDebugEnabled()) {
            log.error("Rest Controller参数 异常", e);
        } else {
            log.warn("Rest Controller参数 异常,错误信息:{},异常方法:\n{}", e.getLocalizedMessage(), getResultExceptionMessage(e));
        }
        return Status.wrapped(e.getCode(), e.getLocalizedMessage());
    }
    /**
     * 404
     *
     * @param e 异常
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Status noHandlerFoundException(NoHandlerFoundException e) {
        String url = e.getRequestURL();
        url = url.indexOf("?") >= 1 ? url.substring(0, url.indexOf("?")) : url;
        log.error("Rest API调用 异常,找不到指定的API:{}",url);
        return Status.wrapped(GlobalErrorCode.INVALID_SERVICE_API.getCode(), url + GlobalErrorCode.INVALID_SERVICE_API.getMessage());
    }

    /**
     * 405 - Method Not Allowed
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Status handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e.getLocalizedMessage());
        return Status.wrapped(GlobalErrorCode.UNKNOWN_SYSTEM_ERROR.getCode(), "不支持当前请求方法");
    }

    /**
     * 415 - Unsupported Media Type
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Status handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("不支持当前媒体类型", e.getLocalizedMessage());
        return Status.wrapped(GlobalErrorCode.UNKNOWN_SYSTEM_ERROR.getCode(), "不支持当前媒体类型");
    }

    /**
     * 500
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public Status dealNullPointerException(NullPointerException e) {
        log.error("空指针异常:", e);
        return Status.wrapped(GlobalErrorCode.UNKNOWN_SYSTEM_ERROR.getCode(), "空指针错误：" + e.getLocalizedMessage());
    }
    /**
     * 500
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RemoteResultException.class)
    public Status remoteResultException(RemoteResultException e) {
        if (log.isDebugEnabled()) {
            log.error("Rest API调用 异常", e.toString(), e);
        } else {
            log.warn("Rest API调用 异常,错误信息:{},异常方法:{}", e.toString(), getResultExceptionMessage(e));
        }
        return Status.wrapped(e.getCode(), e.toString());
    }
    /**
     * 500
     *
     * @param e spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ResultException.class)
    public Status resultException(ResultException e) {
        if (log.isDebugEnabled()) {
            log.error("Rest Controller业务 异常", e);
        } else {
            log.warn("Rest Controller业务 异常,错误信息:{},异常方法:\n{}", e.getLocalizedMessage(), getResultExceptionMessage(e));
        }
        return Status.wrapped(e.getCode(), e.getLocalizedMessage());
    }

    /**
     * 500-通用异常
     *
     * @param request HttpServletRequest
     * @param e       spring 异常信息
     * @return 异常响应结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Status defaultExceptionHandler(HttpServletRequest request, Exception e) {
            log.error("Rest Controller 异常", e);
            return Status.wrapped(GlobalErrorCode.UNKNOWN_SYSTEM_ERROR.getCode(), e.getLocalizedMessage());
    }

    private String getResultExceptionMessage(Exception ex) {
        StringBuilder result = new StringBuilder();
        StackTraceElement[] stackTraceElement = ex.getStackTrace();
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (isAppend(stackTraceElement[i])) {
                result.append(stackTraceElement[i].toString() + "\n");
            }
        }
        return result.toString();
    }

    private boolean isAppend(StackTraceElement element) {
        for (int i = 0; i < RESULT_EXCEPTION_PRINT_EXCLUDE_PACKAGE.length; i++) {
            int index = element.getClassName().indexOf(RESULT_EXCEPTION_PRINT_EXCLUDE_PACKAGE[i]);
            if (index == 0) {
                return false;
            }
        }
        return true;
    }

}
