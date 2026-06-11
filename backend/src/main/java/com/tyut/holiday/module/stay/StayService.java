package com.tyut.holiday.module.stay;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.CentralDorm;
import com.tyut.holiday.entity.HolidayBatch;
import com.tyut.holiday.entity.StayApplication;
import com.tyut.holiday.entity.StayAttachment;
import com.tyut.holiday.entity.StayManager;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.entity.SysUser;
import com.tyut.holiday.mapper.CentralDormMapper;
import com.tyut.holiday.mapper.HolidayBatchMapper;
import com.tyut.holiday.mapper.StayApplicationMapper;
import com.tyut.holiday.mapper.StayAttachmentMapper;
import com.tyut.holiday.mapper.StayManagerMapper;
import com.tyut.holiday.mapper.StudentMapper;
import com.tyut.holiday.mapper.SysUserMapper;
import com.tyut.holiday.module.approval.ApprovalService;
import com.tyut.holiday.module.registration.RegistrationSupport;
import com.tyut.holiday.module.stay.dto.StayDetailVO;
import com.tyut.holiday.module.stay.dto.StaySubmitReq;
import com.tyut.holiday.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 留校申请服务：提交（发起审批）、撤回、详情、记录。审批处理见 Phase 3。
 */
@Service
@RequiredArgsConstructor
public class StayService {

    private final StayApplicationMapper applicationMapper;
    private final StayAttachmentMapper attachmentMapper;
    private final StudentMapper studentMapper;
    private final SysUserMapper userMapper;
    private final HolidayBatchMapper batchMapper;
    private final CentralDormMapper centralDormMapper;
    private final StayManagerMapper managerMapper;
    private final ApprovalService approvalService;
    private final RegistrationSupport support;

    /** 提交留校申请并发起审批流（学生→辅导员→副书记） */
    @Transactional
    public StayDetailVO submit(StaySubmitReq req) {
        Long studentId = support.currentStudentId();
        HolidayBatch batch = support.getBatch(req.getBatchId());
        support.assertWindowOpen(batch, "STAY");

        // 同批次仅允许一条待审批申请
        Long pending = applicationMapper.selectCount(Wrappers.<StayApplication>lambdaQuery()
                .eq(StayApplication::getStudentId, studentId)
                .eq(StayApplication::getBatchId, req.getBatchId())
                .eq(StayApplication::getApprovalStatus, Constants.ApprovalStatus.PENDING));
        if (pending != null && pending > 0) {
            throw BizException.badRequest("已有待审批的留校申请，请先撤回再重新提交");
        }
        if (req.getAttachments() != null && req.getAttachments().size() > Constants.MAX_STAY_ATTACHMENTS) {
            throw BizException.badRequest("附件最多 " + Constants.MAX_STAY_ATTACHMENTS + " 张");
        }

        StayApplication app = new StayApplication();
        app.setStudentId(studentId);
        app.setBatchId(req.getBatchId());
        app.setPlanStart(req.getPlanStart());
        app.setPlanEnd(req.getPlanEnd());
        app.setCampus(req.getCampus());
        app.setReason(req.getReason());
        app.setEmergencyName(req.getEmergencyName());
        app.setEmergencyPhone(req.getEmergencyPhone());
        app.setOriginDorm(req.getOriginDorm());
        app.setCentralDormId(req.getCentralDormId());
        app.setManagerId(req.getManagerId());
        app.setApprovalStatus(Constants.ApprovalStatus.PENDING);
        app.setCurrentNode(Constants.ApprovalNode.COUNSELOR);
        app.setCreateTime(LocalDateTime.now());
        app.setUpdateTime(LocalDateTime.now());
        applicationMapper.insert(app);

        saveAttachments(app.getId(), req.getAttachments());
        // 发起：写学生 SUBMIT 流水
        approvalService.record(app.getId(), UserContext.userId(), Roles.STUDENT,
                Constants.ApprovalAction.SUBMIT, "提交留校申请");
        return detail(app.getId());
    }

    /** 撤回（仅本人、仅待审批） */
    @Transactional
    public void withdraw(Long id) {
        Long studentId = support.currentStudentId();
        StayApplication app = applicationMapper.selectById(id);
        if (app == null || !app.getStudentId().equals(studentId)) {
            throw BizException.forbidden("无权操作该申请");
        }
        if (!Constants.ApprovalStatus.PENDING.equals(app.getApprovalStatus())) {
            throw BizException.badRequest("当前状态不可撤回");
        }
        app.setApprovalStatus(Constants.ApprovalStatus.WITHDRAWN);
        app.setCurrentNode(Constants.ApprovalNode.DONE);
        app.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(app);
        approvalService.record(id, UserContext.userId(), Roles.STUDENT,
                Constants.ApprovalAction.WITHDRAW, "学生撤回申请");
    }

    /** 详情 */
    public StayDetailVO detail(Long id) {
        StayApplication app = applicationMapper.selectById(id);
        if (app == null) {
            throw BizException.notFound("留校申请不存在");
        }
        StayDetailVO vo = new StayDetailVO();
        vo.setApplication(app);
        vo.setAttachments(attachmentMapper.selectList(Wrappers.<StayAttachment>lambdaQuery()
                        .eq(StayAttachment::getApplicationId, id))
                .stream().map(StayAttachment::getFileUrl).toList());
        vo.setApprovalRecords(approvalService.records(id));

        HolidayBatch b = batchMapper.selectById(app.getBatchId());
        vo.setBatchName(b == null ? null : b.getName());

        Student stu = studentMapper.selectById(app.getStudentId());
        if (stu != null) {
            SysUser u = userMapper.selectById(stu.getUserId());
            if (u != null) {
                vo.setStudentName(u.getName());
                vo.setStudentNo(u.getLoginName());
            }
        }
        if (app.getCentralDormId() != null) {
            CentralDorm cd = centralDormMapper.selectById(app.getCentralDormId());
            vo.setCentralDormAddress(cd == null ? null : cd.getAddress());
        }
        if (app.getManagerId() != null) {
            StayManager m = managerMapper.selectById(app.getManagerId());
            vo.setManagerName(m == null ? null : m.getName());
        }
        return vo;
    }

    /** 集中住宿地址选项 */
    public List<CentralDorm> listCentralDorms() {
        return centralDormMapper.selectList(Wrappers.<CentralDorm>lambdaQuery()
                .orderByAsc(CentralDorm::getId));
    }

    /** 留校责任人选项 */
    public List<StayManager> listManagers() {
        return managerMapper.selectList(Wrappers.<StayManager>lambdaQuery()
                .orderByAsc(StayManager::getId));
    }

    /** 我的留校申请记录（可按状态过滤） */
    public List<StayApplication> myRecords(String status) {
        Long studentId = support.currentStudentId();
        return applicationMapper.selectList(Wrappers.<StayApplication>lambdaQuery()
                .eq(StayApplication::getStudentId, studentId)
                .eq(status != null && !status.isBlank(), StayApplication::getApprovalStatus, status)
                .orderByDesc(StayApplication::getId));
    }

    // ---------- 内部 ----------

    private void saveAttachments(Long applicationId, List<String> urls) {
        if (urls == null) {
            return;
        }
        for (String url : urls) {
            if (url == null || url.isBlank()) {
                continue;
            }
            StayAttachment a = new StayAttachment();
            a.setApplicationId(applicationId);
            a.setFileUrl(url);
            attachmentMapper.insert(a);
        }
    }
}
