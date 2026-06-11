package com.tyut.holiday.module.auth.dto;

import lombok.Data;

import java.util.List;

/** 当前用户信息（返回给前端） */
@Data
public class UserInfo {

    private Long userId;

    private String loginName;

    private String name;

    private String phone;

    private String avatar;

    /** 拥有的全部角色码 */
    private List<String> roles;

    /** 当前激活角色 */
    private String activeRole;

    /** 学生档案 id（学生角色时有值） */
    private Long studentId;

    /** 教职工档案 id（辅导员/副书记角色时有值） */
    private Long staffId;
}
