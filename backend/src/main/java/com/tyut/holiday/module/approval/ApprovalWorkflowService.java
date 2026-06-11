package com.tyut.holiday.module.approval;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.OrgClass;
import com.tyut.holiday.entity.Staff;
import com.tyut.holiday.entity.StayApplication;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.mapper.OrgClassMapper;
import com.tyut.holiday.mapper.StayApplicationMapper;
import com.tyut.holiday.mapper.StudentMapper;
import com.tyut.holiday.module.approval.dto.ApplicationListVO;
import com.tyut.holiday.module.approval.dto.BatchApprovalReq;
import com.tyut.holiday.module.stay.StayService;
import com.tyut.holiday.module.stay.dto.StayDetailVO;
import com.tyut.holiday.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 留校审批工作流：辅导员 → 副书记两级审批，管辖范围校验，单个/批量。
 */
@Service
@RequiredArgsConstructor
public class ApprovalWorkflowService {

    private final StayApplicationMapper applicationMapper;
    private final StudentMapper studentMapper;
    private final OrgClassMapper classMapper;
    private final StaffSupport staffSupport;
    private final ApprovalService approvalService;
    private final StayService stayService;

    /** 当前角色的待审批列表 */
    public PageResult<ApplicationListVO> pendingPage(Long batchId, String keyword, long page, long size) {
        Ctx c = ctx();
        IPage<ApplicationListVO> p = applicationMapper.pageApplications(
                new Page<>(page, size), batchId, c.node, Constants.ApprovalStatus.PENDING,
                c.counselorStaffId, c.collegeId, keyword);
        return PageResult.of(p);
    }

    /** 当前角色的审批记录（按状态过滤，跨节点） */
    public PageResult<ApplicationListVO> recordsPage(Long batchId, String status, String keyword,
                                                     long page, long size) {
        Ctx c = ctx();
        IPage<ApplicationListVO> p = applicationMapper.pageApplications(
                new Page<>(page, size), batchId, null, status,
                c.counselorStaffId, c.collegeId, keyword);
        return PageResult.of(p);
    }

    /** 审批详情（带管辖校验） */
    public StayDetailVO detail(Long id) {
        StayApplication app = applicationMapper.selectById(id);
        if (app == null) {
            throw BizException.notFound("留校申请不存在");
        }
        assertInScope(app);
        return stayService.detail(id);
    }

    /** 单个通过 */
    @Transactional
    public void approve(Long id, String comment) {
        StayApplication app = loadForAction(id);
        String role = UserContext.activeRole();
        if (Roles.COUNSELOR.equals(role)) {
            // 辅导员通过 → 流转到副书记
            app.setCurrentNode(Constants.ApprovalNode.SECRETARY);
        } else {
            // 副书记通过 → 终审通过
            app.setApprovalStatus(Constants.ApprovalStatus.APPROVED);
            app.setCurrentNode(Constants.ApprovalNode.DONE);
        }
        app.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(app);
        approvalService.record(id, UserContext.userId(), role,
                Constants.ApprovalAction.APPROVE, comment);
    }

    /** 单个驳回 */
    @Transactional
    public void reject(Long id, String comment) {
        StayApplication app = loadForAction(id);
        String role = UserContext.activeRole();
        app.setApprovalStatus(Constants.ApprovalStatus.REJECTED);
        app.setCurrentNode(Constants.ApprovalNode.DONE);
        app.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(app);
        approvalService.record(id, UserContext.userId(), role,
                Constants.ApprovalAction.REJECT, comment);
    }

    /** 批量审批 */
    @Transactional
    public Map<String, Object> batch(BatchApprovalReq req) {
        boolean approve = Constants.ApprovalAction.APPROVE.equalsIgnoreCase(req.getAction());
        boolean reject = Constants.ApprovalAction.REJECT.equalsIgnoreCase(req.getAction());
        if (!approve && !reject) {
            throw BizException.badRequest("审批动作非法");
        }
        int success = 0;
        List<String> errors = new ArrayList<>();
        for (Long id : req.getIds()) {
            try {
                if (approve) {
                    approve(id, req.getComment());
                } else {
                    reject(id, req.getComment());
                }
                success++;
            } catch (Exception e) {
                errors.add("申请#" + id + "：" + e.getMessage());
            }
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", req.getIds().size());
        r.put("success", success);
        r.put("failed", req.getIds().size() - success);
        r.put("errors", errors);
        return r;
    }

    // ---------- 内部 ----------

    /** 当前审批人上下文：节点 + 管辖维度 */
    private record Ctx(String node, Long counselorStaffId, Long collegeId) {
    }

    private Ctx ctx() {
        String role = UserContext.activeRole();
        Staff staff = staffSupport.currentStaff();
        if (Roles.COUNSELOR.equals(role)) {
            return new Ctx(Constants.ApprovalNode.COUNSELOR, staff.getId(), null);
        }
        if (Roles.SECRETARY.equals(role)) {
            return new Ctx(Constants.ApprovalNode.SECRETARY, null, staff.getCollegeId());
        }
        throw BizException.forbidden("当前角色无审批权限，请切换为辅导员/副书记");
    }

    /** 加载并校验：状态待审、节点匹配当前角色、在管辖范围内 */
    private StayApplication loadForAction(Long id) {
        StayApplication app = applicationMapper.selectById(id);
        if (app == null) {
            throw BizException.notFound("留校申请不存在");
        }
        if (!Constants.ApprovalStatus.PENDING.equals(app.getApprovalStatus())) {
            throw BizException.badRequest("申请不在待审批状态");
        }
        String role = UserContext.activeRole();
        if (Roles.COUNSELOR.equals(role)
                && !Constants.ApprovalNode.COUNSELOR.equals(app.getCurrentNode())) {
            throw BizException.badRequest("该申请当前不在辅导员审批节点");
        }
        if (Roles.SECRETARY.equals(role)
                && !Constants.ApprovalNode.SECRETARY.equals(app.getCurrentNode())) {
            throw BizException.badRequest("该申请当前不在副书记审批节点");
        }
        assertInScope(app);
        return app;
    }

    /** 管辖范围校验：辅导员限本班、副书记限本院 */
    private void assertInScope(StayApplication app) {
        String role = UserContext.activeRole();
        Staff staff = staffSupport.currentStaff();
        Student stu = studentMapper.selectById(app.getStudentId());
        if (stu == null) {
            throw BizException.notFound("学生不存在");
        }
        if (Roles.COUNSELOR.equals(role)) {
            OrgClass cl = stu.getClassId() == null ? null : classMapper.selectById(stu.getClassId());
            if (cl == null || !staff.getId().equals(cl.getCounselorId())) {
                throw BizException.forbidden("非你管辖班级的学生");
            }
        } else if (Roles.SECRETARY.equals(role)) {
            if (!staff.getCollegeId().equals(stu.getCollegeId())) {
                throw BizException.forbidden("非你管辖院部的学生");
            }
        } else {
            throw BizException.forbidden("当前角色无审批权限");
        }
    }
}
