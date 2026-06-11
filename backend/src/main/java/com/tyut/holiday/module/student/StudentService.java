package com.tyut.holiday.module.student;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.PageResult;
import com.tyut.holiday.common.Roles;
import com.tyut.holiday.entity.OrgClass;
import com.tyut.holiday.entity.OrgCollege;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.entity.SysRole;
import com.tyut.holiday.entity.SysUser;
import com.tyut.holiday.entity.UserRole;
import com.tyut.holiday.mapper.OrgClassMapper;
import com.tyut.holiday.mapper.OrgCollegeMapper;
import com.tyut.holiday.mapper.StudentMapper;
import com.tyut.holiday.mapper.SysRoleMapper;
import com.tyut.holiday.mapper.SysUserMapper;
import com.tyut.holiday.mapper.UserRoleMapper;
import com.tyut.holiday.module.student.dto.ImportResult;
import com.tyut.holiday.module.student.dto.StudentImportRow;
import com.tyut.holiday.module.student.dto.StudentSaveReq;
import com.tyut.holiday.module.student.dto.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生管理服务：分页查询、单个增改、Excel 批量导入。
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    /** 新账号默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    private final StudentMapper studentMapper;
    private final SysUserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final OrgCollegeMapper collegeMapper;
    private final OrgClassMapper classMapper;
    private final PasswordEncoder passwordEncoder;

    /** 分页查询 */
    public PageResult<StudentVO> page(long page, long size, String keyword, Long collegeId, Long classId) {
        IPage<StudentVO> p = studentMapper.pageStudents(new Page<>(page, size), keyword, collegeId, classId);
        return PageResult.of(p);
    }

    /** 单个新增/编辑（按学号 upsert 账号 + 学生档案） */
    @Transactional
    public void save(StudentSaveReq req) {
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
        ensureStudentRole(user.getId());

        Student stu = studentMapper.selectOne(
                Wrappers.<Student>lambdaQuery().eq(Student::getUserId, user.getId()));
        if (stu == null) {
            stu = new Student();
            stu.setUserId(user.getId());
        }
        stu.setCollegeId(req.getCollegeId());
        stu.setClassId(req.getClassId());
        stu.setDormAddress(req.getDormAddress());
        if (stu.getId() == null) {
            studentMapper.insert(stu);
        } else {
            studentMapper.updateById(stu);
        }
    }

    /** 删除学生档案（保留账号，可按需扩展） */
    public void delete(Long id) {
        Student stu = studentMapper.selectById(id);
        if (stu == null) {
            throw BizException.notFound("学生不存在");
        }
        studentMapper.deleteById(id);
    }

    /** Excel 批量导入 */
    @Transactional
    public ImportResult importExcel(MultipartFile file) {
        List<StudentImportRow> rows;
        try {
            rows = EasyExcel.read(file.getInputStream()).head(StudentImportRow.class).sheet().doReadSync();
        } catch (IOException e) {
            throw BizException.badRequest("读取 Excel 失败：" + e.getMessage());
        }
        ImportResult result = new ImportResult();
        result.setTotal(rows.size());
        int rowNo = 1;
        for (StudentImportRow row : rows) {
            rowNo++;
            try {
                if (isBlank(row.getLoginName()) || isBlank(row.getName())) {
                    result.addError(rowNo, "学号或姓名为空");
                    continue;
                }
                Long collegeId = resolveCollege(row.getCollegeName());
                Long classId = resolveClass(collegeId, row);
                StudentSaveReq req = new StudentSaveReq();
                req.setLoginName(row.getLoginName().trim());
                req.setName(row.getName().trim());
                req.setPhone(row.getPhone());
                req.setCollegeId(collegeId);
                req.setClassId(classId);
                req.setDormAddress(row.getDormAddress());
                save(req);
                result.setSuccess(result.getSuccess() + 1);
            } catch (Exception e) {
                result.addError(rowNo, e.getMessage());
            }
        }
        return result;
    }

    // ---------- 内部 ----------

    private void ensureStudentRole(Long userId) {
        SysRole role = roleMapper.selectOne(
                Wrappers.<SysRole>lambdaQuery().eq(SysRole::getCode, Roles.STUDENT));
        if (role == null) {
            return;
        }
        Long count = userRoleMapper.selectCount(
                Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getRoleId, role.getId()));
        if (count == 0) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(role.getId());
            userRoleMapper.insert(ur);
        }
    }

    private Long resolveCollege(String name) {
        if (isBlank(name)) {
            return null;
        }
        OrgCollege c = collegeMapper.selectOne(
                Wrappers.<OrgCollege>lambdaQuery().eq(OrgCollege::getName, name.trim()));
        if (c == null) {
            c = new OrgCollege();
            c.setName(name.trim());
            collegeMapper.insert(c);
        }
        return c.getId();
    }

    private Long resolveClass(Long collegeId, StudentImportRow row) {
        if (isBlank(row.getClassName()) || collegeId == null) {
            return null;
        }
        OrgClass cl = classMapper.selectOne(
                Wrappers.<OrgClass>lambdaQuery()
                        .eq(OrgClass::getCollegeId, collegeId)
                        .eq(OrgClass::getName, row.getClassName().trim()));
        if (cl == null) {
            cl = new OrgClass();
            cl.setCollegeId(collegeId);
            cl.setName(row.getClassName().trim());
            cl.setGrade(row.getGrade());
            cl.setMajor(row.getMajor());
            classMapper.insert(cl);
        }
        return cl.getId();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
