package com.tyut.holiday.module.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 单个新增/编辑学生请求 */
@Data
public class StudentSaveReq {

    @NotBlank(message = "请输入学号")
    private String loginName;

    @NotBlank(message = "请输入姓名")
    private String name;

    private String phone;

    private Long collegeId;

    private Long classId;

    private String dormAddress;
}
