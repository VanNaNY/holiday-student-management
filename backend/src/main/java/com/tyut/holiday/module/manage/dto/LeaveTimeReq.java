package com.tyut.holiday.module.manage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/** 修改学生离校时间请求 */
@Data
public class LeaveTimeReq {

    @NotNull(message = "请指定学生")
    private Long studentId;

    @NotNull(message = "请选择批次")
    private Long batchId;

    @NotNull(message = "请填写计划离校时间")
    private LocalDateTime planLeaveTime;
}
