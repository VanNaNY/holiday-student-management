package com.tyut.holiday.module.leave.dto;

import com.tyut.holiday.module.trip.dto.TripDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 离校登记提交请求 */
@Data
public class LeaveSubmitReq {

    @NotNull(message = "请选择批次")
    private Long batchId;

    private LocalDateTime planLeaveTime;

    private LocalDateTime planArriveTime;

    private String destProvince;

    private String destCity;

    private String destDistrict;

    private String destAddress;

    private String emergencyName;

    private String emergencyPhone;

    private String remark;

    /** 多段行程（≤10） */
    private List<TripDTO> trips;
}
