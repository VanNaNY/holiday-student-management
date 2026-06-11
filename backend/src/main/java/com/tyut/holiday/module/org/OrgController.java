package com.tyut.holiday.module.org;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.entity.OrgClass;
import com.tyut.holiday.entity.OrgCollege;
import com.tyut.holiday.mapper.OrgClassMapper;
import com.tyut.holiday.mapper.OrgCollegeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织架构接口：学院与班级。
 */
@Tag(name = "组织架构", description = "学院 / 班级")
@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
public class OrgController {

    private final OrgCollegeMapper collegeMapper;
    private final OrgClassMapper classMapper;

    @Operation(summary = "学院列表")
    @GetMapping("/colleges")
    public Result<List<OrgCollege>> colleges() {
        return Result.ok(collegeMapper.selectList(
                Wrappers.<OrgCollege>lambdaQuery().orderByAsc(OrgCollege::getId)));
    }

    @Operation(summary = "新建学院")
    @PostMapping("/colleges")
    public Result<OrgCollege> createCollege(@RequestBody OrgCollege college) {
        college.setId(null);
        collegeMapper.insert(college);
        return Result.ok(college);
    }

    @Operation(summary = "班级列表（可按学院过滤）")
    @GetMapping("/classes")
    public Result<List<OrgClass>> classes(@RequestParam(required = false) Long collegeId) {
        return Result.ok(classMapper.selectList(
                Wrappers.<OrgClass>lambdaQuery()
                        .eq(collegeId != null, OrgClass::getCollegeId, collegeId)
                        .orderByAsc(OrgClass::getId)));
    }

    @Operation(summary = "新建班级")
    @PostMapping("/classes")
    public Result<OrgClass> createClass(@RequestBody OrgClass clazz) {
        if (clazz.getCollegeId() == null) {
            throw BizException.badRequest("请指定所属学院");
        }
        clazz.setId(null);
        classMapper.insert(clazz);
        return Result.ok(clazz);
    }

    @Operation(summary = "更新班级（含设置辅导员）")
    @PutMapping("/classes/{id}")
    public Result<OrgClass> updateClass(@PathVariable Long id, @RequestBody OrgClass clazz) {
        OrgClass exist = classMapper.selectById(id);
        if (exist == null) {
            throw BizException.notFound("班级不存在");
        }
        clazz.setId(id);
        classMapper.updateById(clazz);
        return Result.ok(classMapper.selectById(id));
    }
}
