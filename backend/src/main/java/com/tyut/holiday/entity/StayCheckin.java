package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 留校签到记录 stay_checkin（按天） */
@Data
@TableName("stay_checkin")
public class StayCheckin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long batchId;

    private Long ruleId;

    private LocalDate checkinDate;

    private LocalDateTime checkinTime;

    private BigDecimal lat;

    private BigDecimal lng;

    private String address;

    /** DONE 已签到 / NOT_YET 未签到 */
    private String status;
}
