package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 假期批次 holiday_batch（含各登记开放时间窗） */
@Data
@TableName("holiday_batch")
public class HolidayBatch {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDate holidayStart;

    private LocalDate holidayEnd;

    private LocalDateTime leaveOpenStart;

    private LocalDateTime leaveOpenEnd;

    private LocalDateTime stayOpenStart;

    private LocalDateTime stayOpenEnd;

    private LocalDateTime returnOpenStart;

    private LocalDateTime returnOpenEnd;

    /** DRAFT/ACTIVE/CLOSED */
    private String status;

    private LocalDateTime createTime;
}
