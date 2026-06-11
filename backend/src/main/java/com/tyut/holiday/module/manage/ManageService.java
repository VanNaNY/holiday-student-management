package com.tyut.holiday.module.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.CentralDorm;
import com.tyut.holiday.entity.LeaveRegistration;
import com.tyut.holiday.entity.OrgClass;
import com.tyut.holiday.entity.ReturnRegistration;
import com.tyut.holiday.entity.StayApplication;
import com.tyut.holiday.entity.StayManager;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.entity.Trip;
import com.tyut.holiday.mapper.ApprovalRecordMapper;
import com.tyut.holiday.mapper.CentralDormMapper;
import com.tyut.holiday.mapper.LeaveRegistrationMapper;
import com.tyut.holiday.mapper.OrgClassMapper;
import com.tyut.holiday.mapper.ReturnRegistrationMapper;
import com.tyut.holiday.mapper.StatMapper;
import com.tyut.holiday.mapper.StayApplicationMapper;
import com.tyut.holiday.mapper.StayAttachmentMapper;
import com.tyut.holiday.mapper.StayManagerMapper;
import com.tyut.holiday.mapper.StudentMapper;
import com.tyut.holiday.mapper.TripMapper;
import com.tyut.holiday.entity.ApprovalRecord;
import com.tyut.holiday.entity.StayAttachment;
import com.tyut.holiday.module.manage.dto.LeaveTimeReq;
import com.tyut.holiday.module.manage.dto.UnregisteredVO;
import com.tyut.holiday.module.support.ScopeResolver;
import com.tyut.holiday.module.support.ScopeResolver.Scope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理类服务：未登记催办、帮助重置登记/申请、责任人/集中住宿地址维护、修改离校时间。
 */
@Service
@RequiredArgsConstructor
public class ManageService {

    private final StatMapper statMapper;
    private final StudentMapper studentMapper;
    private final OrgClassMapper classMapper;
    private final LeaveRegistrationMapper leaveMapper;
    private final ReturnRegistrationMapper returnMapper;
    private final TripMapper tripMapper;
    private final StayApplicationMapper applicationMapper;
    private final StayAttachmentMapper attachmentMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final StayManagerMapper managerMapper;
    private final CentralDormMapper centralDormMapper;
    private final ScopeResolver scopeResolver;

    /** 未登记学生分页（type: LEAVE/RETURN） */
    public PageResult<UnregisteredVO> unregistered(Long batchId, String type, String keyword,
                                                   long page, long size) {
        Scope sc = scopeResolver.resolve();
        IPage<UnregisteredVO> p;
        if (Constants.TripType.RETURN.equalsIgnoreCase(type)) {
            p = statMapper.pageReturnUnregistered(new Page<>(page, size), batchId,
                    sc.counselorStaffId(), sc.collegeId(), keyword);
        } else {
            p = statMapper.pageLeaveUnregistered(new Page<>(page, size), batchId,
                    sc.counselorStaffId(), sc.collegeId(), keyword);
        }
        return PageResult.of(p);
    }

    /** 已登记离校的学生分页（用于帮助重置/修改离校时间） */
    public PageResult<UnregisteredVO> leaveRegistered(Long batchId, String keyword, long page, long size) {
        Scope sc = scopeResolver.resolve();
        IPage<UnregisteredVO> p = statMapper.pageLeaveRegistered(new Page<>(page, size), batchId,
                sc.counselorStaffId(), sc.collegeId(), keyword);
        return PageResult.of(p);
    }

    /** 帮助重置离校登记（删除登记+行程，允许学生重填） */
    @Transactional
    public Map<String, Object> resetLeave(Long batchId, List<Long> studentIds) {
        int success = 0;
        for (Long sid : studentIds) {
            assertStudentInScope(sid);
            LeaveRegistration reg = leaveMapper.selectOne(Wrappers.<LeaveRegistration>lambdaQuery()
                    .eq(LeaveRegistration::getStudentId, sid).eq(LeaveRegistration::getBatchId, batchId));
            if (reg != null) {
                tripMapper.delete(Wrappers.<Trip>lambdaQuery()
                        .eq(Trip::getRefId, reg.getId()).eq(Trip::getRefType, Constants.TripType.LEAVE));
                leaveMapper.deleteById(reg.getId());
            }
            success++;
        }
        return result(studentIds.size(), success);
    }

    /** 帮助重置返校登记 */
    @Transactional
    public Map<String, Object> resetReturn(Long batchId, List<Long> studentIds) {
        int success = 0;
        for (Long sid : studentIds) {
            assertStudentInScope(sid);
            ReturnRegistration reg = returnMapper.selectOne(Wrappers.<ReturnRegistration>lambdaQuery()
                    .eq(ReturnRegistration::getStudentId, sid).eq(ReturnRegistration::getBatchId, batchId));
            if (reg != null) {
                tripMapper.delete(Wrappers.<Trip>lambdaQuery()
                        .eq(Trip::getRefId, reg.getId()).eq(Trip::getRefType, Constants.TripType.RETURN));
                returnMapper.deleteById(reg.getId());
            }
            success++;
        }
        return result(studentIds.size(), success);
    }

    /** 帮助重置留校申请（删除申请+附件+审批流，允许重新提交） */
    @Transactional
    public Map<String, Object> resetStay(List<Long> applicationIds) {
        int success = 0;
        for (Long id : applicationIds) {
            StayApplication app = applicationMapper.selectById(id);
            if (app != null) {
                assertStudentInScope(app.getStudentId());
                attachmentMapper.delete(Wrappers.<StayAttachment>lambdaQuery()
                        .eq(StayAttachment::getApplicationId, id));
                approvalRecordMapper.delete(Wrappers.<ApprovalRecord>lambdaQuery()
                        .eq(ApprovalRecord::getApplicationId, id));
                applicationMapper.deleteById(id);
            }
            success++;
        }
        return result(applicationIds.size(), success);
    }

    /** 修改学生离校时间 */
    @Transactional
    public void updateLeaveTime(LeaveTimeReq req) {
        assertStudentInScope(req.getStudentId());
        LeaveRegistration reg = leaveMapper.selectOne(Wrappers.<LeaveRegistration>lambdaQuery()
                .eq(LeaveRegistration::getStudentId, req.getStudentId())
                .eq(LeaveRegistration::getBatchId, req.getBatchId()));
        if (reg == null) {
            throw BizException.notFound("该学生尚无离校登记");
        }
        reg.setPlanLeaveTime(req.getPlanLeaveTime());
        reg.setUpdateTime(LocalDateTime.now());
        leaveMapper.updateById(reg);
    }

    // ---------- 留校责任人 / 集中住宿地址 ----------

    public List<StayManager> listManagers() {
        return managerMapper.selectList(Wrappers.<StayManager>lambdaQuery().orderByAsc(StayManager::getId));
    }

    public StayManager addManager(StayManager m) {
        m.setId(null);
        if (m.getOnCampus() == null) {
            m.setOnCampus(1);
        }
        managerMapper.insert(m);
        return m;
    }

    public List<CentralDorm> listCentralDorms() {
        return centralDormMapper.selectList(Wrappers.<CentralDorm>lambdaQuery().orderByAsc(CentralDorm::getId));
    }

    public CentralDorm addCentralDorm(CentralDorm d) {
        d.setId(null);
        centralDormMapper.insert(d);
        return d;
    }

    // ---------- 内部 ----------

    private void assertStudentInScope(Long studentId) {
        Scope sc = scopeResolver.resolve();
        // 管理员不限制
        if (sc.counselorStaffId() == null && sc.collegeId() == null
                && Roles.ADMIN.equals(com.tyut.holiday.security.UserContext.activeRole())) {
            return;
        }
        Student stu = studentMapper.selectById(studentId);
        if (stu == null) {
            throw BizException.notFound("学生不存在");
        }
        if (sc.counselorStaffId() != null) {
            OrgClass cl = stu.getClassId() == null ? null : classMapper.selectById(stu.getClassId());
            if (cl == null || !sc.counselorStaffId().equals(cl.getCounselorId())) {
                throw BizException.forbidden("非你管辖班级的学生");
            }
        } else if (sc.collegeId() != null) {
            if (!sc.collegeId().equals(stu.getCollegeId())) {
                throw BizException.forbidden("非你管辖院部的学生");
            }
        }
    }

    private Map<String, Object> result(int total, int success) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", total);
        r.put("success", success);
        r.put("failed", total - success);
        return r;
    }
}
