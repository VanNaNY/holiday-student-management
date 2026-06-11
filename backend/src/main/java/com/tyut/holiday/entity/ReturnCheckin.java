package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 返校报到确认 return_checkin */
@Data
@TableName("return_checkin")
public class ReturnCheckin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long batchId;

    private LocalDateTime checkinTime;

    private BigDecimal lat;

    private BigDecimal lng;

    private String address;

    /** DONE 已报到 / NOT_YET 未报到 */
    private String status;
}
