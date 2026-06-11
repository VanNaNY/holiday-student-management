package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 留校申请 stay_application */
@Data
@TableName("stay_application")
public class StayApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

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

    /** PENDING/APPROVED/REJECTED/WITHDRAWN */
    private String approvalStatus;

    /** 当前审批节点 COUNSELOR/SECRETARY/DONE */
    private String currentNode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
