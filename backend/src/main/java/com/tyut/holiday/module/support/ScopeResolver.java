package com.tyut.holiday.module.support;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.Staff;
import com.tyut.holiday.mapper.StaffMapper;
import com.tyut.holiday.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 管辖范围解析：辅导员限本班、副书记限本院、管理员全部。
 * 解析结果中 counselorStaffId/collegeId 为 null 即对该维度不过滤。
 */
@Component
@RequiredArgsConstructor
public class ScopeResolver {

    private final StaffMapper staffMapper;

    /** 管辖维度 */
    public record Scope(Long counselorStaffId, Long collegeId) {
    }

    /** 按当前激活角色解析管辖范围 */
    public Scope resolve() {
        String role = UserContext.activeRole();
        if (Roles.ADMIN.equals(role)) {
            return new Scope(null, null);
        }
        if (Roles.COUNSELOR.equals(role)) {
            return new Scope(currentStaff().getId(), null);
        }
        if (Roles.SECRETARY.equals(role)) {
            return new Scope(null, currentStaff().getCollegeId());
        }
        throw BizException.forbidden("当前角色无管理权限，请切换为辅导员/副书记/管理员");
    }

    private Staff currentStaff() {
        Staff staff = staffMapper.selectOne(
                Wrappers.<Staff>lambdaQuery().eq(Staff::getUserId, UserContext.userId()));
        if (staff == null) {
            throw BizException.forbidden("当前账号不是教职工");
        }
        return staff;
    }
}
