package com.github.jerrymice.spring.boot.mvc.bean;

import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author tumingjian
 * 创建时间: 2019-10-04 14:32
 * 功能说明:
 */
public class ResultMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(type instanceof Result){
            super.writeInternal(object,type,outputMessage);
        }else{
            ResultInfo result = new ResultInfo(true);
            result.setBody(object);
            super.writeInternal(object,Result.class,outputMessage);
        }
    }

}
