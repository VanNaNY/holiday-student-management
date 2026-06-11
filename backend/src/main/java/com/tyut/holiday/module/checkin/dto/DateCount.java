package com.tyut.holiday.module.checkin.dto;

import lombok.Data;

import java.time.LocalDate;

/** 按日计数投影 */
@Data
public class DateCount {

    private LocalDate checkinDate;

    private Integer cnt;
}
