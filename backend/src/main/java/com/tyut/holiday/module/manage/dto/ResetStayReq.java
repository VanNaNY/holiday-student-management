package com.tyut.holiday.module.manage.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/** 帮助重置留校申请请求 */
@Data
public class ResetStayReq {

    @NotEmpty(message = "请选择申请")
    private List<Long> applicationIds;
}
