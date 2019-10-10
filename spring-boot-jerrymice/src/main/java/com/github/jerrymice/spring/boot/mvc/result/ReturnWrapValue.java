package com.github.jerrymice.spring.boot.mvc.result;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.Status;

import java.util.HashMap;

/**
 * @author tumingjian
 * 创建时间: 2019-10-10 14:21
 * 功能说明:一个spring mvc返回值包装类
 */
public class ReturnWrapValue extends HashMap<String,Object> {
    private final  String codeKey ="code";
    private final  String messageKey ="message";
    private final  String bodyKey ="body";
    public ReturnWrapValue() {
    }
    public ReturnWrapValue(Status status, Object body){
        this(status.getCode(),status.getMessage(),body);
    }
    public ReturnWrapValue(Result result){
        this(result.getCode(),result.getMessage(),result.getBody());
    }
    public ReturnWrapValue(String code,String message,Object body) {
        this.put(codeKey,code);
        this.put(messageKey,message);
        //请求成功,始终有body字段,如果是请求失败,且没有body,那么去掉body字段
        if(body!=null || GlobalErrorCode.REQUEST_SUCCESS_ONLY_CODE.getCode().equals(code)){
            this.put(bodyKey,body);
        }
    }
}
