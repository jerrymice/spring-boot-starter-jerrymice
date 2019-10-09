package com.github.jerrymice.spring.boot.example.controller.login;

import com.github.jerrymice.common.entity.entity.Status;
import com.github.jerrymice.common.entity.ex.ResultException;
import com.github.jerrymice.spring.boot.mvc.annotation.WrapResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author tumingjian
 * 创建时间: 2019-10-09 16:08
 * 功能说明:
 */
@RestController
@RequestMapping("/api/example")
public class LoginController {
    /**
     * 登录用户
     * @param session
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/user/login")
    public Map<String,String> login(HttpSession session,String username, String password){
        if("admin".equals(username) && "admin".equals(password)){
            ManageUser manageUser = new ManageUser();
            manageUser.setId("1");
            manageUser.setUsername(username);
            manageUser.setPhone("131023212343");
            session.setAttribute("currentUser",manageUser);
            return Collections.singletonMap("access-token", session.getId());
        }else{
            throw new ResultException(Status.wrapped("9990","用户密码或密码错误"));
        }
    }

    /**
     * 排除url
     * @return
     */
    @RequestMapping("/exclude/test")
    @WrapResponseBody(value = false)
    public String test(){
        return "这是一个被排除的URL";
    }
}
