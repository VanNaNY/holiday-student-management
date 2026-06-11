package com.tyut.holiday.module.student.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/** Excel 导入行模型，列顺序与表头对应 */
@Data
public class StudentImportRow {

    @ExcelProperty("学号")
    private String loginName;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("学院")
    private String collegeName;

    @ExcelProperty("年级")
    private String grade;

    @ExcelProperty("专业")
    private String major;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("宿舍")
    private String dormAddress;
}
