package com.tyut.holiday.module.auth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.Staff;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.entity.SysRole;
import com.tyut.holiday.entity.SysUser;
import com.tyut.holiday.entity.UserRole;
import com.tyut.holiday.mapper.StaffMapper;
import com.tyut.holiday.mapper.StudentMapper;
import com.tyut.holiday.mapper.SysRoleMapper;
import com.tyut.holiday.mapper.SysUserMapper;
import com.tyut.holiday.mapper.UserRoleMapper;
import com.tyut.holiday.module.auth.dto.LoginReq;
import com.tyut.holiday.module.auth.dto.LoginResp;
import com.tyut.holiday.module.auth.dto.UserInfo;
import com.tyut.holiday.security.JwtUtil;
import com.tyut.holiday.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * 鉴权服务：登录、加载登录用户、角色切换。
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final StudentMapper studentMapper;
    private final StaffMapper staffMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /** 登录：校验账号密码，签发 token */
    public LoginResp login(LoginReq req) {
        SysUser user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getLoginName, req.getLoginName()));
        if (user == null) {
            throw BizException.badRequest("账号或密码错误");
        }
        if (user.getEnabled() != null && user.getEnabled() == 0) {
            throw BizException.forbidden("账号已被禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw BizException.badRequest("账号或密码错误");
        }
        List<String> roles = loadRoleCodes(user.getId());
        if (roles.isEmpty()) {
            throw BizException.forbidden("账号未分配角色");
        }
        String activeRole = defaultActiveRole(roles);
        String token = jwtUtil.generate(user.getId(), user.getLoginName(), activeRole);
        return new LoginResp(token, buildUserInfo(user, roles, activeRole));
    }

    /** 角色切换：校验目标角色属于该用户，重新签发 token */
    public LoginResp switchRole(Long userId, String targetRole) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw BizException.unauthorized("用户不存在");
        }
        List<String> roles = loadRoleCodes(userId);
        if (!roles.contains(targetRole)) {
            throw BizException.forbidden("无该角色权限：" + targetRole);
        }
        String token = jwtUtil.generate(user.getId(), user.getLoginName(), targetRole);
        return new LoginResp(token, buildUserInfo(user, roles, targetRole));
    }

    /** 供拦截器使用：根据 userId + activeRole 构建 LoginUser */
    public LoginUser loadLoginUser(Long userId, String activeRole) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || (user.getEnabled() != null && user.getEnabled() == 0)) {
            throw BizException.unauthorized("用户不存在或已禁用");
        }
        List<String> roles = loadRoleCodes(userId);
        String active = (activeRole != null && roles.contains(activeRole))
                ? activeRole : defaultActiveRole(roles);
        return new LoginUser(user.getId(), user.getLoginName(), user.getName(), roles, active);
    }

    /** 当前用户信息（含档案 id） */
    public UserInfo currentUserInfo(LoginUser lu) {
        SysUser user = userMapper.selectById(lu.getUserId());
        return buildUserInfo(user, lu.getRoles(), lu.getActiveRole());
    }

    // ---------- 内部 ----------

    private List<String> loadRoleCodes(Long userId) {
        List<UserRole> urs = userRoleMapper.selectList(
                Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId));
        if (urs.isEmpty()) {
            return List.of();
        }
        List<Long> roleIds = urs.stream().map(UserRole::getRoleId).toList();
        return roleMapper.selectBatchIds(roleIds).stream().map(SysRole::getCode).toList();
    }

    private String defaultActiveRole(List<String> roles) {
        return roles.stream()
                .min(Comparator.comparingInt(r -> {
                    int idx = Roles.DEFAULT_PRIORITY.indexOf(r);
                    return idx < 0 ? Integer.MAX_VALUE : idx;
                }))
                .orElse(roles.get(0));
    }

    private UserInfo buildUserInfo(SysUser user, List<String> roles, String activeRole) {
        UserInfo info = new UserInfo();
        info.setUserId(user.getId());
        info.setLoginName(user.getLoginName());
        info.setName(user.getName());
        info.setPhone(user.getPhone());
        info.setAvatar(user.getAvatar());
        info.setRoles(roles);
        info.setActiveRole(activeRole);
        // 关联档案 id
        Student stu = studentMapper.selectOne(
                Wrappers.<Student>lambdaQuery().eq(Student::getUserId, user.getId()));
        if (stu != null) {
            info.setStudentId(stu.getId());
        }
        Staff staff = staffMapper.selectOne(
                Wrappers.<Staff>lambdaQuery().eq(Staff::getUserId, user.getId()));
        if (staff != null) {
            info.setStaffId(staff.getId());
        }
        return info;
    }
}
