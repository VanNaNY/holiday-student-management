package com.tyut.holiday.module.ret.dto;

import com.tyut.holiday.entity.ReturnRegistration;
import com.tyut.holiday.entity.Trip;
import lombok.Data;

import java.util.List;

/** 返校登记详情（登记 + 行程 + 批次名） */
@Data
public class ReturnDetailVO {

    private ReturnRegistration registration;

    private List<Trip> trips;

    private String batchName;
}
