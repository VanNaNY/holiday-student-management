package com.tyut.holiday.module.batch;

import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.HolidayBatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 假期批次接口。读取对所有登录用户开放；增删改建议后台/管理员使用。
 */
@Tag(name = "假期批次", description = "批次 CRUD / 当前生效批次 / 状态流转")
@Validated
@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @Operation(summary = "批次列表")
    @GetMapping
    public Result<List<HolidayBatch>> list() {
        return Result.ok(batchService.list());
    }

    @Operation(summary = "当前生效批次")
    @GetMapping("/current")
    public Result<HolidayBatch> current() {
        return Result.ok(batchService.current());
    }

    @Operation(summary = "批次详情")
    @GetMapping("/{id}")
    public Result<HolidayBatch> get(@PathVariable Long id) {
        return Result.ok(batchService.get(id));
    }

    @Operation(summary = "新建批次")
    @PostMapping
    public Result<HolidayBatch> create(@RequestBody HolidayBatch batch) {
        return Result.ok(batchService.create(batch));
    }

    @Operation(summary = "更新批次")
    @PutMapping("/{id}")
    public Result<HolidayBatch> update(@PathVariable Long id, @RequestBody HolidayBatch batch) {
        return Result.ok(batchService.update(id, batch));
    }

    @Operation(summary = "修改批次状态")
    @PutMapping("/{id}/status")
    public Result<HolidayBatch> changeStatus(@PathVariable Long id,
                                             @RequestParam @NotBlank String status) {
        return Result.ok(batchService.changeStatus(id, status));
    }

    @Operation(summary = "删除批次")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        batchService.delete(id);
        return Result.ok();
    }
}
