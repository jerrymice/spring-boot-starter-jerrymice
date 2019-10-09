package com.github.jerrymice.spring.boot.example.task;

import com.github.jerrymice.common.task.Task;
import com.github.jerrymice.common.task.TaskService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tumingjian
 * 创建时间: 2019-10-09 09:19
 * 功能说明:定时任务测试类
 */
@Component
public class ExampleTaskService implements TaskService {
    /**
     * http://ip:port/example/task/execute?taskName="无参定时任务测试"&username=task&password=pwd
     * 定时任务测试,可以传入零个或多个参数.但必须用@RequestParam指定参数名称
     *
     * @return
     */
    @Task(value = "无参定时任务测试", timeout = 10, remark = "这是一个无参定时任务的测试")
    public Map<String, Object> test1() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "任务执行成功");
        return map;
    }

    /**
     * http://ip:port/example/task/execute?taskName="有参定时任务测试"&username=task&password=pwd&client=13&txid=00003
     * 定时任务测试,可以传入零个或多个参数.但必须用@RequestParam指定参数名称
     *
     * @param txid   表示一个唯一的序列号.用于任务id标识,这个参数不是必须的,方法签名里可以不用声明这个参数
     * @param client 一个扩展参数,如果没有传入该参数,方法签名里可以不用声明这个参数
     * @return
     */
    @Task(value = "有参定时任务测试", timeout = 10, remark = "这是一个有参定时任务的测试")
    public Map<String, Object> test2(@RequestParam(value = "txid", required = false) String txid,
                                     @RequestParam(value = "client", required = false) Integer client) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "任务执行成功");
        return map;
    }
}
