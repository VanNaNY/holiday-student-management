package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 教职工档案（辅导员/副书记） staff */
@Data
@TableName("staff")
public class Staff {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long collegeId;

    /** 职务：辅导员/副书记 等 */
    private String title;
}
