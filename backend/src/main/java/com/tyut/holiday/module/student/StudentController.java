package com.tyut.holiday.module.student;

import com.alibaba.excel.EasyExcel;
import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.module.student.dto.ImportResult;
import com.tyut.holiday.module.student.dto.StudentImportRow;
import com.tyut.holiday.module.student.dto.StudentSaveReq;
import com.tyut.holiday.module.student.dto.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 学生管理接口：分页、增改删、Excel 导入与模板下载。
 */
@Tag(name = "学生管理", description = "分页查询 / 增改删 / Excel 导入")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "学生分页列表")
    @GetMapping
    public Result<PageResult<StudentVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long classId) {
        return Result.ok(studentService.page(page, size, keyword, collegeId, classId));
    }

    @Operation(summary = "新增/编辑学生")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody StudentSaveReq req) {
        studentService.save(req);
        return Result.ok();
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "Excel 批量导入")
    @PostMapping("/import")
    public Result<ImportResult> importExcel(@RequestPart("file") MultipartFile file) {
        return Result.ok(studentService.importExcel(file));
    }

    @Operation(summary = "下载导入模板")
    @GetMapping("/import/template")
    public void template(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生导入模板", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        // 写入一行示例数据
        StudentImportRow demo = new StudentImportRow();
        demo.setLoginName("2023001");
        demo.setName("张三");
        demo.setPhone("13800000001");
        demo.setCollegeName("计算机科学与技术学院");
        demo.setGrade("2023");
        demo.setMajor("计算机科学与技术");
        demo.setClassName("计科2301");
        demo.setDormAddress("紫荆公寓 5#-301");
        EasyExcel.write(response.getOutputStream(), StudentImportRow.class)
                .sheet("学生")
                .doWrite(List.of(demo));
    }
}
