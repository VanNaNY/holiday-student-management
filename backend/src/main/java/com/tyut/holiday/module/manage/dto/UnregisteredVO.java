package com.tyut.holiday.module.manage.dto;

import lombok.Data;

/** 未登记学生项（催办） */
@Data
public class UnregisteredVO {

    private Long studentId;

    private String studentName;

    private String studentNo;

    private String phone;

    private String className;
}
