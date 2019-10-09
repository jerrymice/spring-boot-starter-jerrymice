package com.github.jerrymice.spring.boot.example.task;

import com.github.jerrymice.common.task.TaskAuthPassword;
import org.springframework.stereotype.Component;

/**
 * @author tumingjian
 * 创建时间: 2019-10-09 10:59
 * 功能说明:定时任务用户名和密码验证
 */
@Component
public class ExampleTaskAuthPassword implements TaskAuthPassword {
    @Override
    public boolean verify(String username, String password) {
        return "task".equals(username) && "pwd".equals(password);
    }
}
