package com.tyut.holiday.module.stat.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/** 统计导出行 */
@Data
public class StudentExportRow {

    @ExcelProperty("学号")
    private String studentNo;

    @ExcelProperty("姓名")
    private String studentName;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("离校状态")
    private String leaveStatus;

    @ExcelProperty("目的地")
    private String dest;

    @ExcelProperty("留校状态")
    private String stayStatus;

    @ExcelProperty("返校状态")
    private String returnStatus;
}
