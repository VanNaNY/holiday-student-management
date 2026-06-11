package com.tyut.holiday.module.stat.dto;

import lombok.Data;

import java.util.Map;

/** 批次统计概览 */
@Data
public class StatOverviewVO {

    /** 范围内学生总数 */
    private int totalStudents;

    /** 离校：NOT_REG/REGISTERED/ON_THE_WAY/ARRIVED → 人数 */
    private Map<String, Integer> leave;

    /** 留校：PENDING/APPROVED/REJECTED/WITHDRAWN → 人数 */
    private Map<String, Integer> stay;

    /** 返校：NOT_REG/REGISTERED/ON_THE_WAY/ARRIVED → 人数 */
    private Map<String, Integer> ret;
}
