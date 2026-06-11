package com.tyut.holiday.module.stay;

import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.CentralDorm;
import com.tyut.holiday.entity.StayApplication;
import com.tyut.holiday.entity.StayManager;
import com.tyut.holiday.module.stay.dto.StayDetailVO;
import com.tyut.holiday.module.stay.dto.StaySubmitReq;
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

import java.util.List;

/**
 * 留校申请接口（学生侧）：提交/撤回/详情/记录。审批见 Phase 3。
 */
@Tag(name = "留校申请", description = "提交 / 撤回 / 详情 / 记录")
@RestController
@RequestMapping("/stay")
@RequiredArgsConstructor
public class StayController {

    private final StayService stayService;

    @Operation(summary = "提交留校申请（发起审批）")
    @PostMapping
    public Result<StayDetailVO> submit(@Valid @RequestBody StaySubmitReq req) {
        return Result.ok(stayService.submit(req));
    }

    @Operation(summary = "撤回留校申请")
    @PostMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id) {
        stayService.withdraw(id);
        return Result.ok();
    }

    @Operation(summary = "留校申请详情")
    @GetMapping("/{id}")
    public Result<StayDetailVO> detail(@PathVariable Long id) {
        return Result.ok(stayService.detail(id));
    }

    @Operation(summary = "我的留校申请记录（可按状态过滤）")
    @GetMapping("/records")
    public Result<List<StayApplication>> records(@RequestParam(required = false) String status) {
        return Result.ok(stayService.myRecords(status));
    }

    @Operation(summary = "集中住宿地址选项")
    @GetMapping("/central-dorms")
    public Result<List<CentralDorm>> centralDorms() {
        return Result.ok(stayService.listCentralDorms());
    }

    @Operation(summary = "留校责任人选项")
    @GetMapping("/managers")
    public Result<List<StayManager>> managers() {
        return Result.ok(stayService.listManagers());
    }
}
