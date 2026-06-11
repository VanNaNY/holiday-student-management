package com.tyut.holiday.module.leave;

import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.LeaveRegistration;
import com.tyut.holiday.module.leave.dto.LeaveDetailVO;
import com.tyut.holiday.module.leave.dto.LeaveSubmitReq;
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
 * 离校登记接口（学生）。
 */
@Tag(name = "离校登记", description = "离校登记提交/修改 / 查询 / 状态流转")
@Validated
@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @Operation(summary = "提交/修改离校登记")
    @PostMapping
    public Result<LeaveDetailVO> submit(@Valid @RequestBody LeaveSubmitReq req) {
        return Result.ok(leaveService.submit(req));
    }

    @Operation(summary = "我在某批次的离校登记")
    @GetMapping("/my")
    public Result<LeaveDetailVO> my(@RequestParam Long batchId) {
        return Result.ok(leaveService.myByBatch(batchId));
    }

    @Operation(summary = "离校登记详情")
    @GetMapping("/{id}")
    public Result<LeaveDetailVO> detail(@PathVariable Long id) {
        return Result.ok(leaveService.detail(id));
    }

    @Operation(summary = "我的离校登记记录")
    @GetMapping("/records")
    public Result<List<LeaveRegistration>> records() {
        return Result.ok(leaveService.myRecords());
    }

    @Operation(summary = "更新离校状态（离校途中/已到目的地）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam @NotBlank String status) {
        leaveService.updateStatus(id, status);
        return Result.ok();
    }
}
