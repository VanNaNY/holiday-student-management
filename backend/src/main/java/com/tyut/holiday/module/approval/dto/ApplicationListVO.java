package com.tyut.holiday.module.approval.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 审批列表项（留校申请 + 学生信息） */
@Data
public class ApplicationListVO {

    private Long id;

    private Long studentId;

    private String studentName;

    private String studentNo;

    private String studentPhone;

    private String className;

    private Long batchId;

    private LocalDate planStart;

    private LocalDate planEnd;

    private String campus;

    private String reason;

    /** PENDING/APPROVED/REJECTED/WITHDRAWN */
    private String approvalStatus;

    /** COUNSELOR/SECRETARY/DONE */
    private String currentNode;

    private LocalDateTime createTime;
}
