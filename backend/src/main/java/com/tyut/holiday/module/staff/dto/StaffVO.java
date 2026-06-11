package com.tyut.holiday.module.staff.dto;

import lombok.Data;

import java.util.List;

/** 教职工列表视图 */
@Data
public class StaffVO {

    /** staff 档案 id */
    private Long id;

    private Long userId;

    /** 工号（登录名） */
    private String loginName;

    private String name;

    private String phone;

    private Long collegeId;

    private String collegeName;

    /** 职务 */
    private String title;

    /** 角色码列表（COUNSELOR/SECRETARY 等） */
    private List<String> roles;
}
