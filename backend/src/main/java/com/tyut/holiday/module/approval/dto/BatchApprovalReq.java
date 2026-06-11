package com.tyut.holiday.module.approval.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 批量审批请求 */
@Data
public class BatchApprovalReq {

    @NotEmpty(message = "请选择要审批的申请")
    private List<Long> ids;

    /** APPROVE / REJECT */
    @NotNull(message = "请指定审批动作")
    private String action;

    private String comment;
}
