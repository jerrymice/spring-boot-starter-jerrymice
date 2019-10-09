package com.github.jerrymice.spring.boot.example.controller.login;

import lombok.Data;

/**
 * @author tumingjian
 * 创建时间: 2019-10-09 16:10
 * 功能说明:
 */
@Data
public class ManageUser {
    private String id;
    private String username;
    private String phone;
    private int sex;
}
