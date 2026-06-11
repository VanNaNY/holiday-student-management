package com.tyut.holiday.module.stay.dto;

import com.tyut.holiday.entity.ApprovalRecord;
import com.tyut.holiday.entity.StayApplication;
import lombok.Data;

import java.util.List;

/** 留校申请详情（申请 + 附件 + 审批流 + 批次名 + 学生信息） */
@Data
public class StayDetailVO {

    private StayApplication application;

    /** 附件图片 URL 列表 */
    private List<String> attachments;

    /** 审批流水 */
    private List<ApprovalRecord> approvalRecords;

    private String batchName;

    /** 学生姓名（供辅导员审批查看） */
    private String studentName;

    /** 学号 */
    private String studentNo;

    /** 集中住宿地址 */
    private String centralDormAddress;

    /** 责任人姓名 */
    private String managerName;
}
