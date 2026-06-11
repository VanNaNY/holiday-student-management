package com.tyut.holiday.module.approval;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.entity.Staff;
import com.tyut.holiday.mapper.StaffMapper;
import com.tyut.holiday.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 教职工身份支撑：解析当前登录用户对应的 staff 档案。
 */
@Component
@RequiredArgsConstructor
public class StaffSupport {

    private final StaffMapper staffMapper;

    /** 当前登录用户的 staff 档案，非教职工抛错 */
    public Staff currentStaff() {
        Staff staff = staffMapper.selectOne(
                Wrappers.<Staff>lambdaQuery().eq(Staff::getUserId, UserContext.userId()));
        if (staff == null) {
            throw BizException.forbidden("当前账号不是教职工");
        }
        return staff;
    }
}
