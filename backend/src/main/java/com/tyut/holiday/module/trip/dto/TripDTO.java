package com.tyut.holiday.module.trip.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** 行程 DTO（提交与展示共用） */
@Data
public class TripDTO {

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
