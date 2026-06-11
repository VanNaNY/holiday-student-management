package com.tyut.holiday.module.ret.dto;

import com.tyut.holiday.module.trip.dto.TripDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 返校登记提交请求 */
@Data
public class ReturnSubmitReq {

    @NotNull(message = "请选择批次")
    private Long batchId;

    private LocalDateTime planDepartTime;

    private LocalDateTime planArriveTime;

    private String departProvince;

    private String departCity;

    private String departDistrict;

    private String departAddress;

    /** 多段行程（≤10） */
    private List<TripDTO> trips;
}
