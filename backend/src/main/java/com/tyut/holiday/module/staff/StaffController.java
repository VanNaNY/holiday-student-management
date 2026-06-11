package com.tyut.holiday.module.staff;

import com.tyut.holiday.common.Result;
import com.tyut.holiday.module.staff.dto.StaffSaveReq;
import com.tyut.holiday.module.staff.dto.StaffVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 教职工管理接口（后台）：辅导员/副书记增改删。班级辅导员指派复用 /org/classes/{id}。
 */
@Tag(name = "教职工管理", description = "教职工列表 / 新增 / 编辑 / 删除")
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffAdminService staffService;

    @Operation(summary = "教职工列表")
    @GetMapping
    public Result<List<StaffVO>> list() {
        return Result.ok(staffService.list());
    }

    @Operation(summary = "新增/编辑教职工")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody StaffSaveReq req) {
        staffService.save(req);
        return Result.ok();
    }

    @Operation(summary = "删除教职工")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return Result.ok();
    }
}
