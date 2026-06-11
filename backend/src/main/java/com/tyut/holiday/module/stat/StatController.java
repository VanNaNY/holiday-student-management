package com.tyut.holiday.module.stat;

import com.alibaba.excel.EasyExcel;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.module.stat.dto.StatOverviewVO;
import com.tyut.holiday.module.stat.dto.StudentExportRow;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 批次统计接口（辅导员/副书记/管理员，按管辖范围）。
 */
@Tag(name = "批次统计", description = "统计概览 / 数据导出")
@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @Operation(summary = "批次统计概览")
    @GetMapping("/overview")
    public Result<StatOverviewVO> overview(@RequestParam Long batchId) {
        return Result.ok(statService.overview(batchId));
    }

    @Operation(summary = "导出统计 Excel")
    @GetMapping("/export")
    public void export(@RequestParam Long batchId, HttpServletResponse response) throws IOException {
        List<StudentExportRow> rows = statService.exportRows(batchId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("假期统计", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StudentExportRow.class)
                .sheet("统计")
                .doWrite(rows);
    }
}
