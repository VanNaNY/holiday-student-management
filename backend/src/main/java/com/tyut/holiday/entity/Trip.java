package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 多段行程 trip（离校/返校共用） */
@Data
@TableName("trip")
public class Trip {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联登记 id */
    private Long refId;

    /** LEAVE 离校 / RETURN 返校 */
    private String refType;

    /** 行程序号 1..10 */
    private Integer seq;

    /** 交通方式：火车/飞机/汽车/自驾 等 */
    private String transport;

    /** 车次/航班号等 */
    private String transportInfo;

    private String fromStation;

    private String destProvince;

    private String destCity;

    private String destDistrict;

    private String destStation;

    private LocalDateTime departTime;

    private LocalDateTime arriveTime;
}
