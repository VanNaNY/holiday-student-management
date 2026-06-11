package com.tyut.holiday.module.ret;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.entity.HolidayBatch;
import com.tyut.holiday.entity.ReturnRegistration;
import com.tyut.holiday.mapper.HolidayBatchMapper;
import com.tyut.holiday.mapper.ReturnRegistrationMapper;
import com.tyut.holiday.module.registration.RegistrationSupport;
import com.tyut.holiday.module.ret.dto.ReturnDetailVO;
import com.tyut.holiday.module.ret.dto.ReturnSubmitReq;
import com.tyut.holiday.module.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 返校登记服务：免审批，提交/修改（开放窗内），查询，状态流转，多段行程。
 */
@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRegistrationMapper returnMapper;
    private final HolidayBatchMapper batchMapper;
    private final TripService tripService;
    private final RegistrationSupport support;

    @Transactional
    public ReturnDetailVO submit(ReturnSubmitReq req) {
        Long studentId = support.currentStudentId();
        HolidayBatch batch = support.getBatch(req.getBatchId());
        support.assertWindowOpen(batch, Constants.TripType.RETURN);

        ReturnRegistration reg = returnMapper.selectOne(Wrappers.<ReturnRegistration>lambdaQuery()
                .eq(ReturnRegistration::getStudentId, studentId)
                .eq(ReturnRegistration::getBatchId, req.getBatchId()));
        boolean isNew = reg == null;
        if (isNew) {
            reg = new ReturnRegistration();
            reg.setStudentId(studentId);
            reg.setBatchId(req.getBatchId());
            reg.setCreateTime(LocalDateTime.now());
        }
        reg.setPlanDepartTime(req.getPlanDepartTime());
        reg.setPlanArriveTime(req.getPlanArriveTime());
        reg.setDepartProvince(req.getDepartProvince());
        reg.setDepartCity(req.getDepartCity());
        reg.setDepartDistrict(req.getDepartDistrict());
        reg.setDepartAddress(req.getDepartAddress());
        if (reg.getStatus() == null || Constants.RegStatus.NOT_REG.equals(reg.getStatus())) {
            reg.setStatus(Constants.RegStatus.REGISTERED);
        }
        reg.setEditableBefore(batch.getReturnOpenEnd());
        reg.setUpdateTime(LocalDateTime.now());
        if (isNew) {
            returnMapper.insert(reg);
        } else {
            returnMapper.updateById(reg);
        }
        tripService.replace(reg.getId(), Constants.TripType.RETURN, req.getTrips());
        return detail(reg.getId());
    }

    public ReturnDetailVO myByBatch(Long batchId) {
        Long studentId = support.currentStudentId();
        ReturnRegistration reg = returnMapper.selectOne(Wrappers.<ReturnRegistration>lambdaQuery()
                .eq(ReturnRegistration::getStudentId, studentId)
                .eq(ReturnRegistration::getBatchId, batchId));
        return reg == null ? null : detail(reg.getId());
    }

    public ReturnDetailVO detail(Long id) {
        ReturnRegistration reg = returnMapper.selectById(id);
        if (reg == null) {
            throw BizException.notFound("返校登记不存在");
        }
        ReturnDetailVO vo = new ReturnDetailVO();
        vo.setRegistration(reg);
        vo.setTrips(tripService.list(id, Constants.TripType.RETURN));
        HolidayBatch b = batchMapper.selectById(reg.getBatchId());
        vo.setBatchName(b == null ? null : b.getName());
        return vo;
    }

    public List<ReturnRegistration> myRecords() {
        Long studentId = support.currentStudentId();
        return returnMapper.selectList(Wrappers.<ReturnRegistration>lambdaQuery()
                .eq(ReturnRegistration::getStudentId, studentId)
                .orderByDesc(ReturnRegistration::getId));
    }

    public void updateStatus(Long id, String status) {
        if (!Set.of(Constants.RegStatus.ON_THE_WAY, Constants.RegStatus.ARRIVED).contains(status)) {
            throw BizException.badRequest("非法状态");
        }
        Long studentId = support.currentStudentId();
        ReturnRegistration reg = returnMapper.selectById(id);
        if (reg == null || !reg.getStudentId().equals(studentId)) {
            throw BizException.forbidden("无权操作该登记");
        }
        reg.setStatus(status);
        reg.setUpdateTime(LocalDateTime.now());
        returnMapper.updateById(reg);
    }
}
