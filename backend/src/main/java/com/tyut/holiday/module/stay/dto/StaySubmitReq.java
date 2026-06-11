package com.tyut.holiday.module.stay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/** 留校申请提交请求 */
@Data
public class StaySubmitReq {

    @NotNull(message = "请选择批次")
    private Long batchId;

    private LocalDate planStart;

    private LocalDate planEnd;

    /** 校区 A/B */
    private String campus;

    private String reason;

    private String emergencyName;

    private String emergencyPhone;

    private String originDorm;

    private Long centralDormId;

    private Long managerId;

    /** 附件图片 URL（1~5 张） */
    private List<String> attachments;
}
