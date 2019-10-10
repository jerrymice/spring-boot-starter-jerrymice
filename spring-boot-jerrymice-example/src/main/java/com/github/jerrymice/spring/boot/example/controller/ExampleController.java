package com.github.jerrymice.spring.boot.example.controller;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.common.entity.ex.ResultException;
import com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tumingjian
 * 创建时间: 2019-10-08 11:07
 * 功能说明:
 * @see com.github.jerrymice.common.entity.entity.Result
 * @see com.github.jerrymice.spring.boot.mvc.bean.ResultWrapHandlerMethodReturnValueHandler
 * @see com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler
 * */
@RestController
@RequestMapping("/api/example")
@Slf4j
public class ExampleController {
    /**
     * 返回Result类型,系统不会作额外处理,目前不推荐使用,推荐 /wrap
     * @return
     * {
     *     "code":"0000",
     *     "message":"成功",
     *     "body":{
     *         "name":"tumingjian",
     *         "age":12
     *     }
     * }
     */
    @RequestMapping("/old/body")
    public Result<Map<String,Object>> normal() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "tumingjian");
        body.put("age", 12);
        ResultInfo resultInfo = new ResultInfo(true).setBody(body);
        return resultInfo;
    }
    /**
     * 返回Result类型,和相关错误码,但目前不推荐再使用,这种方式,推荐/result/exception这种写法
     * @return
     * {
     *     "code":"0010",
     *     "message":"用户年龄必须大于19岁",
     * }
     */
    @RequestMapping("/old/errorCode")
    @Deprecated
    public Result<Map<String,Object>> errorCode(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", -19);
        if(Integer.valueOf(result.get("age").toString())<19){
            return new ResultInfo<>(GlobalErrorCode.INVALID_REQUEST_ARGUMENTS.getCode(),"用户年龄必须大于19岁");
        }else{
            return new ResultInfo<>(result);
        }
    }

    /**
     * 指定用Result包装,使用注解@WrapResponseBody
     *
     * @return
     * {
     *     "code":"0000",
     *     "message":"成功",
     *     "body":{
     *         "name":"tumingjian",
     *         "age":19
     *     }
     * }
     */
    @RequestMapping("/wrap")
    @WrapResponseBody
    public Map<String, Object> wrap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", 19);
        return result;
    }
    /**
     * 一些特殊情况下,我们并不需要包装为Result,我们只想返回原生对象,只需要添加注解@WrapResponseBody(value = false)
     * @return
     *     {
     *         "name":"tumingjian",
     *         "age":19
     *     }
     */
    @RequestMapping("/notWrap")
    @WrapResponseBody(value = false)
    public Map<String, Object> notWrap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", 19);
        return result;
    }
    /**
     * 如果是@RestController 就算没有WrapResponseBody注解,那么默认也会自动包装为Result
     * 但如果是@Controller 那么系统不会自动包装为Result,除非为方法加上@WrapResponseBody注解
     * @return
     * {
     *     "code":"0000",
     *     "message":"成功",
     *     "body":{
     *         "name":"tumingjian",
     *         "age":19
     *     }
     * }
     */
    @RequestMapping("/autoWrap")
    public Map<String, Object> autoWrap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", 19);
        return result;
    }
    /**
     * 这种情况并不会返回true,而是返回一个Result包装
     * @return
     * {
     *     "code":"0000",
     *     "message":"成功",
     *     "body": null
     * }
     */
    @RequestMapping("/save")
    public void save(){
        log.info("保存成功");
    }
    /**
     * 抛出ResultException异常,系统也会自动将异常包装为Result
     *
     * @return
     * @see ResultException
     * @see com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler
     * {
     *     "code":"0010",
     *     "message":"用户年龄必须大于20岁",
     * }
     */
    @RequestMapping("/result/exception")
    public Map<String, Object> resultException() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", 19);
        if(Integer.valueOf(result.get("age").toString())<20){
            throw new ResultException(GlobalErrorCode.INVALID_REQUEST_ARGUMENTS.getCode(),"用户年龄必须大于20岁");
        }
        return result;
    }
    /**
     * 抛出其他RuntimeException异常,系统也会自动将异常包装为Result,对于非ResultException的异常.code码统一为9999
     * @see com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler
     * @return
     * {
     *     "code":"9999",
     *     "message":"年龄必须大于0岁",
     * }
     */
    @RequestMapping("/runtime/exception")
    public Map<String, Object> runtimeException() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", -19);
        if(result!=null){
            throw new IllegalArgumentException("年龄必须大于0岁");
        }
        return result;
    }

    /**
     * 一个下载文件测试,不会被Result处理
     * @param fileName
     * @return
     * @throws IOException
     */
    @RequestMapping("/download")
    public HttpEntity<byte[]> download(String fileName)throws IOException {
        File file = new File("/tmp/" + fileName);
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] content = new String("这是一个测试文本文件").getBytes("utf-8");
        outputStream.write(content);
        outputStream.close();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes=new byte[content.length];
        fileInputStream.read(bytes);
        fileInputStream.close();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment;filename="+fileName);
        ResponseEntity<byte[]> httpEntity = new ResponseEntity(bytes,httpHeaders, HttpStatus.OK);
        return httpEntity;
    }

    @RequestMapping("/map")
    public Map<String, Object> map() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", -19);
        result.put("body",Collections.singletonMap("access-token","123"));
        return result;
    }


}
