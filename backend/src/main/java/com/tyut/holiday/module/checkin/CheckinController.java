package com.tyut.holiday.module.checkin;

import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.CheckinRule;
import com.tyut.holiday.entity.ReturnCheckin;
import com.tyut.holiday.entity.StayCheckin;
import com.tyut.holiday.module.checkin.dto.CheckinReq;
import com.tyut.holiday.module.checkin.dto.CheckinResultVO;
import com.tyut.holiday.module.checkin.dto.CheckinStudentVO;
import com.tyut.holiday.module.checkin.dto.CheckinSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 签到接口：学生留校签到/返校报到；辅导员/副书记签到汇总与详情。
 */
@Tag(name = "签到", description = "留校签到 / 返校报到 / 签到汇总 / 签到详情")
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "签到规则（围栏+时段，供地图展示）")
    @GetMapping("/rule")
    public Result<CheckinRule> rule(@RequestParam Long batchId,
                                    @RequestParam String type,
                                    @RequestParam(required = false) Long collegeId) {
        return Result.ok(checkinService.rule(batchId, type, collegeId));
    }

    @Operation(summary = "留校签到")
    @PostMapping("/stay")
    public Result<CheckinResultVO> stay(@Valid @RequestBody CheckinReq req) {
        return Result.ok(checkinService.stayCheckin(req));
    }

    @Operation(summary = "返校报到")
    @PostMapping("/return")
    public Result<CheckinResultVO> ret(@Valid @RequestBody CheckinReq req) {
        return Result.ok(checkinService.returnCheckin(req));
    }

    @Operation(summary = "我的留校签到记录")
    @GetMapping("/stay/my")
    public Result<List<StayCheckin>> myStay(@RequestParam Long batchId) {
        return Result.ok(checkinService.myStayCheckins(batchId));
    }

    @Operation(summary = "我的返校报到记录")
    @GetMapping("/return/my")
    public Result<ReturnCheckin> myReturn(@RequestParam Long batchId) {
        return Result.ok(checkinService.myReturnCheckin(batchId));
    }

    @Operation(summary = "留校签到按日汇总（0/N）")
    @GetMapping("/summary")
    public Result<List<CheckinSummaryVO>> summary(@RequestParam Long batchId) {
        return Result.ok(checkinService.summary(batchId));
    }

    @Operation(summary = "某日签到详情（已签/未签分页）")
    @GetMapping("/detail")
    public Result<PageResult<CheckinStudentVO>> detail(
            @RequestParam Long batchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Boolean signed,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        return Result.ok(checkinService.detail(batchId, date, signed, page, size));
    }
}
