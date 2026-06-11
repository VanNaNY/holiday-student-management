package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 离校登记 leave_registration */
@Data
@TableName("leave_registration")
public class LeaveRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long batchId;

    private LocalDateTime planLeaveTime;

    private LocalDateTime planArriveTime;

    private String destProvince;

    private String destCity;

    private String destDistrict;

    private String destAddress;

    private String emergencyName;

    private String emergencyPhone;

    private String remark;

    /** NOT_REG/REGISTERED/ON_THE_WAY/ARRIVED */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
