package com.github.jerrymice.spring.boot.example.controller;

import com.github.jerrymice.common.entity.code.GlobalErrorCode;
import com.github.jerrymice.common.entity.entity.Result;
import com.github.jerrymice.common.entity.entity.ResultInfo;
import com.github.jerrymice.common.entity.ex.ResultException;
import com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
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
public class ExampleController {
    /**
     * Result类型返回,系统不会作额外处理
     *
     * @return
     */
    @RequestMapping("/normal")
    public Result<Map<String,Object>> normal() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "tumingjian");
        body.put("age", 12);
        ResultInfo resultInfo = new ResultInfo(true).setBody(body);
        return resultInfo;
    }
    /**
     * 无Result,也不包装为Result.相当于原生的ResponseBody
     *
     * @return
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
     * 指定用Result包装
     *
     * @return
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
     * 如果是@RestController 就算没有WrapResponseBody注解,那么默认也会自动包装为Result
     * 但如果是@Controller 那么系统不会自动包装为Result,除非为方法加上@WrapResponseBody注解
     * @return
     */
    @RequestMapping("/body")
    public Map<String, Object> body() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", 19);
        return result;
    }
    /**
     * 抛出ResultException异常,系统也会自动包装为Result
     *
     * @return
     * @see ResultException
     * @see com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler
     */
    @RequestMapping("/result/exception")
    public Map<String, Object> resultException() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", "tumingjian");
        result.put("age", 19);
        if(result!=null){
            throw new ResultException(GlobalErrorCode.INVALID_SERVICE_API);
        }
        return result;
    }
    /**
     * 抛出其他RuntimeException异常,系统也会自动包装为Result
     * @see com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler
     * @return
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
}
