package com.tyut.holiday.module.leave.dto;

import com.tyut.holiday.entity.LeaveRegistration;
import com.tyut.holiday.entity.Trip;
import lombok.Data;

import java.util.List;

/** 离校登记详情（登记 + 行程 + 批次名） */
@Data
public class LeaveDetailVO {

    private LeaveRegistration registration;

    private List<Trip> trips;

    private String batchName;
}
