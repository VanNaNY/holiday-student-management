package com.tyut.holiday.module.staff;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.OrgClass;
import com.tyut.holiday.entity.OrgCollege;
import com.tyut.holiday.entity.Staff;
import com.tyut.holiday.entity.SysRole;
import com.tyut.holiday.entity.SysUser;
import com.tyut.holiday.entity.UserRole;
import com.tyut.holiday.mapper.OrgClassMapper;
import com.tyut.holiday.mapper.OrgCollegeMapper;
import com.tyut.holiday.mapper.StaffMapper;
import com.tyut.holiday.mapper.SysRoleMapper;
import com.tyut.holiday.mapper.SysUserMapper;
import com.tyut.holiday.mapper.UserRoleMapper;
import com.tyut.holiday.module.staff.dto.StaffSaveReq;
import com.tyut.holiday.module.staff.dto.StaffVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 教职工管理服务：辅导员/副书记的增改删、角色绑定、班级辅导员引用清理。
 * 仅维护 COUNSELOR/SECRETARY 两个角色，不触碰 STUDENT/ADMIN。
 */
@Service
@RequiredArgsConstructor
public class StaffAdminService {

    /** 新账号默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    /** 本模块负责维护的角色码 */
    private static final List<String> STAFF_ROLES = List.of(Roles.COUNSELOR, Roles.SECRETARY);

    private final StaffMapper staffMapper;
    private final SysUserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final OrgCollegeMapper collegeMapper;
    private final OrgClassMapper classMapper;
    private final PasswordEncoder passwordEncoder;

    /** 教职工列表（含账号、学院名、角色） */
    public List<StaffVO> list() {
        List<Staff> staffList = staffMapper.selectList(
                Wrappers.<Staff>lambdaQuery().orderByAsc(Staff::getId));
        Map<Long, String> collegeName = collegeMapper.selectList(null).stream()
                .collect(Collectors.toMap(OrgCollege::getId, OrgCollege::getName));
        List<StaffVO> result = new ArrayList<>();
        for (Staff s : staffList) {
            SysUser u = userMapper.selectById(s.getUserId());
            StaffVO vo = new StaffVO();
            vo.setId(s.getId());
            vo.setUserId(s.getUserId());
            vo.setCollegeId(s.getCollegeId());
            vo.setCollegeName(s.getCollegeId() == null ? null : collegeName.get(s.getCollegeId()));
            vo.setTitle(s.getTitle());
            if (u != null) {
                vo.setLoginName(u.getLoginName());
                vo.setName(u.getName());
                vo.setPhone(u.getPhone());
            }
            vo.setRoles(rolesOf(s.getUserId()));
            result.add(vo);
        }
        return result;
    }

    /** 新增/编辑教职工（按工号 upsert 账号 + staff 档案 + 角色） */
    @Transactional
    public void save(StaffSaveReq req) {
        SysUser user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getLoginName, req.getLoginName()));
        if (user == null) {
            user = new SysUser();
            user.setLoginName(req.getLoginName());
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setName(req.getName());
            user.setPhone(req.getPhone());
            user.setEnabled(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
        } else {
            user.setName(req.getName());
            user.setPhone(req.getPhone());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }

        Staff staff = staffMapper.selectOne(
                Wrappers.<Staff>lambdaQuery().eq(Staff::getUserId, user.getId()));
        if (staff == null) {
            staff = new Staff();
            staff.setUserId(user.getId());
        }
        staff.setCollegeId(req.getCollegeId());
        staff.setTitle(req.getTitle());
        if (staff.getId() == null) {
            staffMapper.insert(staff);
        } else {
            staffMapper.updateById(staff);
        }

        syncRoles(user.getId(), req.getRoles());
    }

    /** 删除教职工：清理班级辅导员引用 + 解绑教职工角色（保留账号） */
    @Transactional
    public void delete(Long id) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw BizException.notFound("教职工不存在");
        }
        List<OrgClass> classes = classMapper.selectList(
                Wrappers.<OrgClass>lambdaQuery().eq(OrgClass::getCounselorId, id));
        for (OrgClass cl : classes) {
            cl.setCounselorId(null);
            classMapper.updateById(cl);
        }
        syncRoles(staff.getUserId(), List.of());
        staffMapper.deleteById(id);
    }

    // ---------- 内部 ----------

    private List<String> rolesOf(Long userId) {
        List<UserRole> urs = userRoleMapper.selectList(
                Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId));
        if (urs.isEmpty()) {
            return List.of();
        }
        Set<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return roleMapper.selectList(Wrappers.<SysRole>lambdaQuery().in(SysRole::getId, roleIds))
                .stream().map(SysRole::getCode).collect(Collectors.toList());
    }

    /** 同步教职工角色：仅增删 COUNSELOR/SECRETARY，保留其它角色 */
    private void syncRoles(Long userId, List<String> wantCodes) {
        Set<String> want = new HashSet<>();
        if (wantCodes != null) {
            for (String c : wantCodes) {
                if (STAFF_ROLES.contains(c)) {
                    want.add(c);
                }
            }
        }
        for (String code : STAFF_ROLES) {
            SysRole role = roleMapper.selectOne(
                    Wrappers.<SysRole>lambdaQuery().eq(SysRole::getCode, code));
            if (role == null) {
                continue;
            }
            Long cnt = userRoleMapper.selectCount(
                    Wrappers.<UserRole>lambdaQuery()
                            .eq(UserRole::getUserId, userId)
                            .eq(UserRole::getRoleId, role.getId()));
            boolean has = cnt != null && cnt > 0;
            if (want.contains(code) && !has) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(role.getId());
                userRoleMapper.insert(ur);
            } else if (!want.contains(code) && has) {
                userRoleMapper.delete(
                        Wrappers.<UserRole>lambdaQuery()
                                .eq(UserRole::getUserId, userId)
                                .eq(UserRole::getRoleId, role.getId()));
            }
        }
    }
}
