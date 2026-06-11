package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 返校登记 return_registration */
@Data
@TableName("return_registration")
public class ReturnRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long batchId;

    private LocalDateTime planDepartTime;

    private LocalDateTime planArriveTime;

    private String departProvince;

    private String departCity;

    private String departDistrict;

    private String departAddress;

    /** NOT_REG/REGISTERED/ON_THE_WAY/ARRIVED */
    private String status;

    /** 可修改截止时间 */
    private LocalDateTime editableBefore;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
