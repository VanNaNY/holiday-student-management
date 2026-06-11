package com.tyut.holiday.module.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** 签到详情中的学生项（已签/未签） */
@Data
public class CheckinStudentVO {

    private Long studentId;

    private String studentName;

    private String studentNo;

    private String phone;

    private String className;

    /** 1 已签到 / 0 未签到 */
    private Integer signed;

    private LocalDateTime checkinTime;
}
