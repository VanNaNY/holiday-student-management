package com.tyut.holiday.module.manage.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 帮助重置登记请求（离校/返校） */
@Data
public class ResetReq {

    @NotNull(message = "请选择批次")
    private Long batchId;

    @NotEmpty(message = "请选择学生")
    private List<Long> studentIds;
}
