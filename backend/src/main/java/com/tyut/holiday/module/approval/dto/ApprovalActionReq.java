package com.tyut.holiday.module.approval.dto;

import lombok.Data;

/** 单个审批动作请求 */
@Data
public class ApprovalActionReq {

    /** 审批意见（可选） */
    private String comment;
}
