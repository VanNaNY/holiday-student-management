package com.tyut.holiday.module.checkin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.common.GeoUtil;
import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.CheckinRule;
import com.tyut.holiday.entity.ReturnCheckin;
import com.tyut.holiday.entity.Staff;
import com.tyut.holiday.entity.StayCheckin;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.mapper.CheckinRuleMapper;
import com.tyut.holiday.mapper.ReturnCheckinMapper;
import com.tyut.holiday.mapper.StayApplicationMapper;
import com.tyut.holiday.mapper.StayCheckinMapper;
import com.tyut.holiday.module.approval.StaffSupport;
import com.tyut.holiday.module.checkin.dto.CheckinReq;
import com.tyut.holiday.module.checkin.dto.CheckinResultVO;
import com.tyut.holiday.module.checkin.dto.CheckinStudentVO;
import com.tyut.holiday.module.checkin.dto.CheckinSummaryVO;
import com.tyut.holiday.module.checkin.dto.DateCount;
import com.tyut.holiday.module.registration.RegistrationSupport;
import com.tyut.holiday.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 签到服务：留校签到 / 返校报到（地理围栏 + 时间段），辅导员签到汇总/详情。
 */
@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinRuleMapper ruleMapper;
    private final StayCheckinMapper stayCheckinMapper;
    private final ReturnCheckinMapper returnCheckinMapper;
    private final StayApplicationMapper applicationMapper;
    private final RegistrationSupport regSupport;
    private final StaffSupport staffSupport;

    /** 签到规则（优先本院，否则全校通用）；供地图展示与时段提示 */
    public CheckinRule rule(Long batchId, String type, Long collegeId) {
        List<CheckinRule> rules = ruleMapper.selectList(Wrappers.<CheckinRule>lambdaQuery()
                .eq(CheckinRule::getBatchId, batchId).eq(CheckinRule::getType, type));
        if (rules.isEmpty()) {
            return null;
        }
        // 本院专属优先
        return rules.stream()
                .filter(r -> collegeId != null && collegeId.equals(r.getCollegeId()))
                .findFirst()
                .orElseGet(() -> rules.stream()
                        .filter(r -> r.getCollegeId() == null)
                        .findFirst()
                        .orElse(rules.get(0)));
    }

    /** 留校签到 */
    public CheckinResultVO stayCheckin(CheckinReq req) {
        Student stu = regSupport.currentStudent();
        CheckinRule r = requireRule(req.getBatchId(), Constants.CheckinType.STAY, stu.getCollegeId());
        long dist = validate(r, req);
        if (dist < 0) {
            return new CheckinResultVO(false, -dist, "不在签到范围内，距中心约 " + (-dist) + " 米");
        }
        LocalDate today = LocalDate.now();
        StayCheckin c = stayCheckinMapper.selectOne(Wrappers.<StayCheckin>lambdaQuery()
                .eq(StayCheckin::getStudentId, stu.getId())
                .eq(StayCheckin::getBatchId, req.getBatchId())
                .eq(StayCheckin::getCheckinDate, today));
        boolean isNew = c == null;
        if (isNew) {
            c = new StayCheckin();
            c.setStudentId(stu.getId());
            c.setBatchId(req.getBatchId());
            c.setCheckinDate(today);
        }
        c.setRuleId(r.getId());
        c.setCheckinTime(LocalDateTime.now());
        c.setLat(java.math.BigDecimal.valueOf(req.getLat()));
        c.setLng(java.math.BigDecimal.valueOf(req.getLng()));
        c.setAddress(req.getAddress());
        c.setStatus(Constants.CheckinStatus.DONE);
        if (isNew) {
            stayCheckinMapper.insert(c);
        } else {
            stayCheckinMapper.updateById(c);
        }
        return new CheckinResultVO(true, dist, "签到成功");
    }

    /** 返校报到 */
    public CheckinResultVO returnCheckin(CheckinReq req) {
        Student stu = regSupport.currentStudent();
        CheckinRule r = requireRule(req.getBatchId(), Constants.CheckinType.RETURN, stu.getCollegeId());
        long dist = validate(r, req);
        if (dist < 0) {
            return new CheckinResultVO(false, -dist, "不在报到范围内，距中心约 " + (-dist) + " 米");
        }
        ReturnCheckin c = returnCheckinMapper.selectOne(Wrappers.<ReturnCheckin>lambdaQuery()
                .eq(ReturnCheckin::getStudentId, stu.getId())
                .eq(ReturnCheckin::getBatchId, req.getBatchId()));
        boolean isNew = c == null;
        if (isNew) {
            c = new ReturnCheckin();
            c.setStudentId(stu.getId());
            c.setBatchId(req.getBatchId());
        }
        c.setCheckinTime(LocalDateTime.now());
        c.setLat(java.math.BigDecimal.valueOf(req.getLat()));
        c.setLng(java.math.BigDecimal.valueOf(req.getLng()));
        c.setAddress(req.getAddress());
        c.setStatus(Constants.CheckinStatus.DONE);
        if (isNew) {
            returnCheckinMapper.insert(c);
        } else {
            returnCheckinMapper.updateById(c);
        }
        return new CheckinResultVO(true, dist, "报到成功");
    }

    /** 我的留校签到记录 */
    public List<StayCheckin> myStayCheckins(Long batchId) {
        Long studentId = regSupport.currentStudentId();
        return stayCheckinMapper.selectList(Wrappers.<StayCheckin>lambdaQuery()
                .eq(StayCheckin::getStudentId, studentId)
                .eq(StayCheckin::getBatchId, batchId)
                .orderByDesc(StayCheckin::getCheckinDate));
    }

    /** 我的返校报到记录 */
    public ReturnCheckin myReturnCheckin(Long batchId) {
        Long studentId = regSupport.currentStudentId();
        return returnCheckinMapper.selectOne(Wrappers.<ReturnCheckin>lambdaQuery()
                .eq(ReturnCheckin::getStudentId, studentId)
                .eq(ReturnCheckin::getBatchId, batchId));
    }

    // ---------- 辅导员/副书记 ----------

    /** 留校签到按日汇总（0/N） */
    public List<CheckinSummaryVO> summary(Long batchId) {
        Scope sc = staffScope();
        int total = applicationMapper.countApprovedStudents(batchId, sc.counselorStaffId, sc.collegeId);
        List<DateCount> counts = stayCheckinMapper.signedCountByDate(batchId, sc.counselorStaffId, sc.collegeId);
        Map<LocalDate, Integer> map = new HashMap<>();
        for (DateCount dc : counts) {
            map.put(dc.getCheckinDate(), dc.getCnt());
        }
        List<CheckinSummaryVO> list = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> e : map.entrySet()) {
            list.add(new CheckinSummaryVO(e.getKey(), total, e.getValue()));
        }
        // 若今日尚无签到记录，也补一条今日 0/total，便于查看
        LocalDate today = LocalDate.now();
        if (!map.containsKey(today)) {
            list.add(new CheckinSummaryVO(today, total, 0));
        }
        list.sort(Comparator.comparing(CheckinSummaryVO::getDate).reversed());
        return list;
    }

    /** 某日签到详情（signed=true 已签 / false 未签 / null 全部） */
    public PageResult<CheckinStudentVO> detail(Long batchId, LocalDate date, Boolean signed,
                                               long page, long size) {
        Scope sc = staffScope();
        IPage<CheckinStudentVO> p = stayCheckinMapper.pageCheckinStudents(
                new Page<>(page, size), batchId, date.toString(), sc.counselorStaffId, sc.collegeId, signed);
        return PageResult.of(p);
    }

    // ---------- 内部 ----------

    private CheckinRule requireRule(Long batchId, String type, Long collegeId) {
        CheckinRule r = rule(batchId, type, collegeId);
        if (r == null) {
            throw BizException.badRequest("尚未配置签到规则");
        }
        return r;
    }

    /**
     * 校验时段与围栏。返回值：≥0 表示通过（为距离米）；&lt;0 表示超出围栏（绝对值为距离米）。
     * 时段不符直接抛错。
     */
    private long validate(CheckinRule r, CheckinReq req) {
        LocalDateTime now = LocalDateTime.now();
        if (r.getTimeStart() != null && now.isBefore(r.getTimeStart())) {
            throw BizException.badRequest("当前不在签到时段内（未开始）");
        }
        if (r.getTimeEnd() != null && now.isAfter(r.getTimeEnd())) {
            throw BizException.badRequest("当前不在签到时段内（已结束）");
        }
        if (r.getFenceLat() == null || r.getFenceLng() == null || r.getFenceRadius() == null) {
            return 0;
        }
        double dist = GeoUtil.distance(req.getLat(), req.getLng(),
                r.getFenceLat().doubleValue(), r.getFenceLng().doubleValue());
        long meters = Math.round(dist);
        return meters <= r.getFenceRadius() ? meters : -meters;
    }

    private record Scope(Long counselorStaffId, Long collegeId) {
    }

    private Scope staffScope() {
        String role = UserContext.activeRole();
        if (Roles.ADMIN.equals(role)) {
            // 管理员查看全部，无需 staff 档案
            return new Scope(null, null);
        }
        Staff staff = staffSupport.currentStaff();
        if (Roles.COUNSELOR.equals(role)) {
            return new Scope(staff.getId(), null);
        }
        if (Roles.SECRETARY.equals(role)) {
            return new Scope(null, staff.getCollegeId());
        }
        throw BizException.forbidden("当前角色无权查看签到汇总");
    }
}
