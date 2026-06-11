package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 班级 org_class */
@Data
@TableName("org_class")
public class OrgClass {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long collegeId;

    private String grade;

    private String major;

    private String name;

    /** 辅导员 staff.id */
    private Long counselorId;
}
