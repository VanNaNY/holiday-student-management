package com.tyut.holiday.module.staff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/** 教职工新增/编辑请求 */
@Data
public class StaffSaveReq {

    /** 为空表示新增 */
    private Long id;

    @NotBlank(message = "工号不能为空")
    private String loginName;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    private Long collegeId;

    private String title;

    /** 角色码（仅 COUNSELOR/SECRETARY 生效） */
    private List<String> roles;
}
