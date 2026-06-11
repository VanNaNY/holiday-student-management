package com.tyut.holiday.module.approval;

import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.module.approval.dto.ApplicationListVO;
import com.tyut.holiday.module.approval.dto.ApprovalActionReq;
import com.tyut.holiday.module.approval.dto.BatchApprovalReq;
import com.tyut.holiday.module.stay.dto.StayDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 留校审批接口（辅导员/副书记）。按当前激活角色决定审批节点与管辖范围。
 */
@Tag(name = "留校审批", description = "待审批列表 / 详情 / 通过 / 驳回 / 批量 / 审批记录")
@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalWorkflowService workflowService;

    @Operation(summary = "待审批列表（按当前角色节点）")
    @GetMapping("/pending")
    public Result<PageResult<ApplicationListVO>> pending(
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(workflowService.pendingPage(batchId, keyword, page, size));
    }

    @Operation(summary = "审批记录（可按状态过滤）")
    @GetMapping("/records")
    public Result<PageResult<ApplicationListVO>> records(
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(workflowService.recordsPage(batchId, status, keyword, page, size));
    }

    @Operation(summary = "审批详情")
    @GetMapping("/{id}")
    public Result<StayDetailVO> detail(@PathVariable Long id) {
        return Result.ok(workflowService.detail(id));
    }

    @Operation(summary = "通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody(required = false) ApprovalActionReq req) {
        workflowService.approve(id, req == null ? null : req.getComment());
        return Result.ok();
    }

    @Operation(summary = "驳回")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody(required = false) ApprovalActionReq req) {
        workflowService.reject(id, req == null ? null : req.getComment());
        return Result.ok();
    }

    @Operation(summary = "批量审批")
    @PostMapping("/batch")
    public Result<Map<String, Object>> batch(@Valid @RequestBody BatchApprovalReq req) {
        return Result.ok(workflowService.batch(req));
    }
}
