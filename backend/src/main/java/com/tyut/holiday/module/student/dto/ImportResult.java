package com.tyut.holiday.module.student.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Excel 导入结果统计 */
@Data
public class ImportResult {

    /** 总行数 */
    private int total;

    /** 成功数 */
    private int success;

    /** 失败数 */
    private int failed;

    /** 失败明细：行号 + 原因 */
    private List<String> errors = new ArrayList<>();

    public void addError(int rowNo, String reason) {
        failed++;
        errors.add("第 " + rowNo + " 行：" + reason);
    }
}
