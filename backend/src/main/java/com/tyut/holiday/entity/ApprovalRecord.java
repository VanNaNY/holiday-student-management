package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 审批流水 approval_record（学生发起→辅导员→副书记） */
@Data
@TableName("approval_record")
public class ApprovalRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long applicationId;

    /** 步骤序号 */
    private Integer seq;

    /** 操作人 user.id */
    private Long approverId;

    /** STUDENT/COUNSELOR/SECRETARY */
    private String approverRole;

    /** SUBMIT/APPROVE/REJECT/WITHDRAW */
    private String action;

    private String comment;

    private LocalDateTime createTime;
}
