package com.tyut.holiday.module.student.dto;

import lombok.Data;

/** 学生列表展示对象（联表 user/college/class） */
@Data
public class StudentVO {

    private Long id;

    private Long userId;

    /** 学号 */
    private String loginName;

    private String name;

    private String phone;

    private Long collegeId;

    private String collegeName;

    private Long classId;

    private String className;

    private String grade;

    private String major;

    private String dormAddress;
}
