package com.tyut.holiday.module.manage;

import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.CentralDorm;
import com.tyut.holiday.entity.StayManager;
import com.tyut.holiday.module.manage.dto.LeaveTimeReq;
import com.tyut.holiday.module.manage.dto.ResetReq;
import com.tyut.holiday.module.manage.dto.ResetStayReq;
import com.tyut.holiday.module.manage.dto.UnregisteredVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理接口（辅导员/副书记/管理员）：催办、帮助重置、责任人/集中住宿维护、修改离校时间。
 */
@Tag(name = "假期管理", description = "未登记催办 / 帮助重置 / 责任人 / 集中住宿 / 修改离校时间")
@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManageController {

    private final ManageService manageService;

    @Operation(summary = "未登记学生分页（type=LEAVE/RETURN）")
    @GetMapping("/unregistered")
    public Result<PageResult<UnregisteredVO>> unregistered(
            @RequestParam Long batchId,
            @RequestParam(defaultValue = "LEAVE") String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        return Result.ok(manageService.unregistered(batchId, type, keyword, page, size));
    }

    @Operation(summary = "已登记离校学生分页（用于重置/改时间）")
    @GetMapping("/leave-registered")
    public Result<PageResult<UnregisteredVO>> leaveRegistered(
            @RequestParam Long batchId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        return Result.ok(manageService.leaveRegistered(batchId, keyword, page, size));
    }

    @Operation(summary = "帮助重置离校登记")
    @PostMapping("/reset-leave")
    public Result<Map<String, Object>> resetLeave(@Valid @RequestBody ResetReq req) {
        return Result.ok(manageService.resetLeave(req.getBatchId(), req.getStudentIds()));
    }

    @Operation(summary = "帮助重置返校登记")
    @PostMapping("/reset-return")
    public Result<Map<String, Object>> resetReturn(@Valid @RequestBody ResetReq req) {
        return Result.ok(manageService.resetReturn(req.getBatchId(), req.getStudentIds()));
    }

    @Operation(summary = "帮助重置留校申请")
    @PostMapping("/reset-stay")
    public Result<Map<String, Object>> resetStay(@Valid @RequestBody ResetStayReq req) {
        return Result.ok(manageService.resetStay(req.getApplicationIds()));
    }

    @Operation(summary = "修改学生离校时间")
    @PutMapping("/leave-time")
    public Result<Void> updateLeaveTime(@Valid @RequestBody LeaveTimeReq req) {
        manageService.updateLeaveTime(req);
        return Result.ok();
    }

    @Operation(summary = "留校责任人列表")
    @GetMapping("/managers")
    public Result<List<StayManager>> managers() {
        return Result.ok(manageService.listManagers());
    }

    @Operation(summary = "添加留校责任人")
    @PostMapping("/managers")
    public Result<StayManager> addManager(@RequestBody StayManager m) {
        return Result.ok(manageService.addManager(m));
    }

    @Operation(summary = "集中住宿地址列表")
    @GetMapping("/central-dorms")
    public Result<List<CentralDorm>> centralDorms() {
        return Result.ok(manageService.listCentralDorms());
    }

    @Operation(summary = "添加集中住宿地址")
    @PostMapping("/central-dorms")
    public Result<CentralDorm> addCentralDorm(@RequestBody CentralDorm d) {
        return Result.ok(manageService.addCentralDorm(d));
    }
}
