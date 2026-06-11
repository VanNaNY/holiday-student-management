package com.tyut.holiday.module.registration;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.entity.HolidayBatch;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.mapper.HolidayBatchMapper;
import com.tyut.holiday.mapper.StudentMapper;
import com.tyut.holiday.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 登记类业务公共支撑：解析当前学生、批次与开放时间窗校验。
 */
@Component
@RequiredArgsConstructor
public class RegistrationSupport {

    private final StudentMapper studentMapper;
    private final HolidayBatchMapper batchMapper;

    /** 当前登录用户对应的学生档案 id，非学生抛错 */
    public Long currentStudentId() {
        Student stu = studentMapper.selectOne(
                Wrappers.<Student>lambdaQuery().eq(Student::getUserId, UserContext.userId()));
        if (stu == null) {
            throw BizException.forbidden("当前账号不是学生，无法进行登记");
        }
        return stu.getId();
    }

    /** 取批次，不存在抛错 */
    public HolidayBatch getBatch(Long batchId) {
        HolidayBatch b = batchMapper.selectById(batchId);
        if (b == null) {
            throw BizException.notFound("批次不存在");
        }
        return b;
    }

    /**
     * 校验某类登记的开放时间窗。
     *
     * @param type LEAVE / STAY / RETURN
     */
    public void assertWindowOpen(HolidayBatch b, String type) {
        LocalDateTime start;
        LocalDateTime end;
        switch (type) {
            case Constants.TripType.LEAVE -> {
                start = b.getLeaveOpenStart();
                end = b.getLeaveOpenEnd();
            }
            case "STAY" -> {
                start = b.getStayOpenStart();
                end = b.getStayOpenEnd();
            }
            case Constants.TripType.RETURN -> {
                start = b.getReturnOpenStart();
                end = b.getReturnOpenEnd();
            }
            default -> {
                return;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (start != null && now.isBefore(start)) {
            throw BizException.badRequest("当前不在登记开放时间内（尚未开放）");
        }
        if (end != null && now.isAfter(end)) {
            throw BizException.badRequest("当前不在登记开放时间内（已截止）");
        }
    }
}
