package com.tyut.holiday.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 登录请求 */
@Data
public class LoginReq {

    @NotBlank(message = "请输入学号/工号")
    private String loginName;

    @NotBlank(message = "请输入密码")
    private String password;
}
