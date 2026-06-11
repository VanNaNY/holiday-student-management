package com.tyut.holiday.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 角色切换请求 */
@Data
public class SwitchRoleReq {

    @NotBlank(message = "请选择要切换的角色")
    private String role;
}
