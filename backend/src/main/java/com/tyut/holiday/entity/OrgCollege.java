package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 学院/院部 org_college */
@Data
@TableName("org_college")
public class OrgCollege {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
}
