package com.tyut.holiday.module.ret;

import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.ReturnRegistration;
import com.tyut.holiday.module.ret.dto.ReturnDetailVO;
import com.tyut.holiday.module.ret.dto.ReturnSubmitReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
 * 返校登记接口（学生）。
 */
@Tag(name = "返校登记", description = "返校登记提交/修改 / 查询 / 状态流转")
@Validated
@RestController
@RequestMapping("/return")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @Operation(summary = "提交/修改返校登记")
    @PostMapping
    public Result<ReturnDetailVO> submit(@Valid @RequestBody ReturnSubmitReq req) {
        return Result.ok(returnService.submit(req));
    }

    @Operation(summary = "我在某批次的返校登记")
    @GetMapping("/my")
    public Result<ReturnDetailVO> my(@RequestParam Long batchId) {
        return Result.ok(returnService.myByBatch(batchId));
    }

    @Operation(summary = "返校登记详情")
    @GetMapping("/{id}")
    public Result<ReturnDetailVO> detail(@PathVariable Long id) {
        return Result.ok(returnService.detail(id));
    }

    @Operation(summary = "我的返校登记记录")
    @GetMapping("/records")
    public Result<List<ReturnRegistration>> records() {
        return Result.ok(returnService.myRecords());
    }

    @Operation(summary = "更新返校状态（返校途中/已返校）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam @NotBlank String status) {
        returnService.updateStatus(id, status);
        return Result.ok();
    }
}
