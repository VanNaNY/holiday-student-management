package com.tyut.holiday.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 登录响应：token + 用户信息 */
@Data
@AllArgsConstructor
public class LoginResp {

    private String token;

    private UserInfo user;
}
