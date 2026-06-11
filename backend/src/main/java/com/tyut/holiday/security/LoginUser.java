package com.tyut.holiday.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当前登录用户上下文信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    /** sys_user.id */
    private Long userId;

    /** 登录名（学号/工号） */
    private String loginName;

    /** 姓名 */
    private String name;

    /** 拥有的全部角色码：STUDENT/COUNSELOR/SECRETARY/ADMIN */
    private List<String> roles;

    /** 当前激活角色（角色切换后变化），默认取 roles 第一个 */
    private String activeRole;
}
