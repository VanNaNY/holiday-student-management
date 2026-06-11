package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 留校责任人 stay_manager */
@Data
@TableName("stay_manager")
public class StayManager {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long collegeId;

    private String name;

    private String phone;

    /** 是否在校 1是 0否 */
    private Integer onCampus;
}
