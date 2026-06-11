package com.tyut.holiday.module.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/** 某日签到汇总（0/N） */
@Data
@AllArgsConstructor
public class CheckinSummaryVO {

    private LocalDate date;

    /** 应签到人数（留校已通过的学生） */
    private int total;

    /** 已签到人数 */
    private int signed;
}
