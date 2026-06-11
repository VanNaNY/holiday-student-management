package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 签到规则 checkin_rule（时间段 + 地理围栏） */
@Data
@TableName("checkin_rule")
public class CheckinRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long batchId;

    /** 为空表示全校通用 */
    private Long collegeId;

    /** STAY 留校签到 / RETURN 返校报到 */
    private String type;

    private LocalDateTime timeStart;

    private LocalDateTime timeEnd;

    private BigDecimal fenceLat;

    private BigDecimal fenceLng;

    /** 围栏半径(米) */
    private Integer fenceRadius;
}
