package com.tyut.holiday.common;

import lombok.Data;

/** 通用「状态 → 计数」投影 */
@Data
public class StatusCount {

    private String status;

    private Integer cnt;
}
