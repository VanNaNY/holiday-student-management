package com.tyut.holiday.module.leave;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.entity.HolidayBatch;
import com.tyut.holiday.entity.LeaveRegistration;
import com.tyut.holiday.mapper.HolidayBatchMapper;
import com.tyut.holiday.mapper.LeaveRegistrationMapper;
import com.tyut.holiday.module.leave.dto.LeaveDetailVO;
import com.tyut.holiday.module.leave.dto.LeaveSubmitReq;
import com.tyut.holiday.module.registration.RegistrationSupport;
import com.tyut.holiday.module.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 离校登记服务：提交/修改、查询、状态流转、多段行程。
 */
@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRegistrationMapper leaveMapper;
    private final HolidayBatchMapper batchMapper;
    private final TripService tripService;
    private final RegistrationSupport support;

    /** 提交/修改离校登记（按 学生+批次 唯一，存在则更新） */
    @Transactional
    public LeaveDetailVO submit(LeaveSubmitReq req) {
        Long studentId = support.currentStudentId();
        HolidayBatch batch = support.getBatch(req.getBatchId());
        support.assertWindowOpen(batch, Constants.TripType.LEAVE);

        LeaveRegistration reg = leaveMapper.selectOne(Wrappers.<LeaveRegistration>lambdaQuery()
                .eq(LeaveRegistration::getStudentId, studentId)
                .eq(LeaveRegistration::getBatchId, req.getBatchId()));
        boolean isNew = reg == null;
        if (isNew) {
            reg = new LeaveRegistration();
            reg.setStudentId(studentId);
            reg.setBatchId(req.getBatchId());
            reg.setCreateTime(LocalDateTime.now());
        }
        reg.setPlanLeaveTime(req.getPlanLeaveTime());
        reg.setPlanArriveTime(req.getPlanArriveTime());
        reg.setDestProvince(req.getDestProvince());
        reg.setDestCity(req.getDestCity());
        reg.setDestDistrict(req.getDestDistrict());
        reg.setDestAddress(req.getDestAddress());
        reg.setEmergencyName(req.getEmergencyName());
        reg.setEmergencyPhone(req.getEmergencyPhone());
        reg.setRemark(req.getRemark());
        // 已登记；若此前更靠后状态（途中/到达）则保留
        if (reg.getStatus() == null || Constants.RegStatus.NOT_REG.equals(reg.getStatus())) {
            reg.setStatus(Constants.RegStatus.REGISTERED);
        }
        reg.setUpdateTime(LocalDateTime.now());
        if (isNew) {
            leaveMapper.insert(reg);
        } else {
            leaveMapper.updateById(reg);
        }
        tripService.replace(reg.getId(), Constants.TripType.LEAVE, req.getTrips());
        return detail(reg.getId());
    }

    /** 当前学生在某批次的离校登记（无则返回 null 的详情包装） */
    public LeaveDetailVO myByBatch(Long batchId) {
        Long studentId = support.currentStudentId();
        LeaveRegistration reg = leaveMapper.selectOne(Wrappers.<LeaveRegistration>lambdaQuery()
                .eq(LeaveRegistration::getStudentId, studentId)
                .eq(LeaveRegistration::getBatchId, batchId));
        if (reg == null) {
            return null;
        }
        return detail(reg.getId());
    }

    /** 详情（校验归属在调用处保证；records/my 已限定本人） */
    public LeaveDetailVO detail(Long id) {
        LeaveRegistration reg = leaveMapper.selectById(id);
        if (reg == null) {
            throw BizException.notFound("离校登记不存在");
        }
        LeaveDetailVO vo = new LeaveDetailVO();
        vo.setRegistration(reg);
        vo.setTrips(tripService.list(id, Constants.TripType.LEAVE));
        HolidayBatch b = batchMapper.selectById(reg.getBatchId());
        vo.setBatchName(b == null ? null : b.getName());
        return vo;
    }

    /** 当前学生的全部离校登记记录 */
    public List<LeaveRegistration> myRecords() {
        Long studentId = support.currentStudentId();
        return leaveMapper.selectList(Wrappers.<LeaveRegistration>lambdaQuery()
                .eq(LeaveRegistration::getStudentId, studentId)
                .orderByDesc(LeaveRegistration::getId));
    }

    /** 学生更新自己的离校状态（离校途中/已到目的地） */
    public void updateStatus(Long id, String status) {
        if (!Set.of(Constants.RegStatus.ON_THE_WAY, Constants.RegStatus.ARRIVED).contains(status)) {
            throw BizException.badRequest("非法状态");
        }
        Long studentId = support.currentStudentId();
        LeaveRegistration reg = leaveMapper.selectById(id);
        if (reg == null || !reg.getStudentId().equals(studentId)) {
            throw BizException.forbidden("无权操作该登记");
        }
        reg.setStatus(status);
        reg.setUpdateTime(LocalDateTime.now());
        leaveMapper.updateById(reg);
    }
}
