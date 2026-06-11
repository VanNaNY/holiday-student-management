package com.tyut.holiday.module.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 签到结果 */
@Data
@AllArgsConstructor
public class CheckinResultVO {

    /** 是否签到成功 */
    private boolean success;

    /** 距围栏中心距离（米，取整） */
    private long distance;

    private String message;
}
