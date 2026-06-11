package com.tyut.holiday.module.checkin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 签到请求（学生定位上报） */
@Data
public class CheckinReq {

    @NotNull(message = "请选择批次")
    private Long batchId;

    @NotNull(message = "缺少定位纬度")
    private Double lat;

    @NotNull(message = "缺少定位经度")
    private Double lng;

    /** 逆地理地址（可选） */
    private String address;
}
