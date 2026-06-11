package com.tyut.holiday.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tyut.holiday.entity.Student;
import com.tyut.holiday.module.student.dto.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** Student 数据访问 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /** 分页查询学生（联表学号/姓名/学院/班级，支持关键词与院系过滤） */
    @Select("""
            <script>
            SELECT s.id, s.user_id AS userId, u.login_name AS loginName, u.name, u.phone,
                   s.college_id AS collegeId, c.name AS collegeName,
                   s.class_id AS classId, cl.name AS className, cl.grade, cl.major,
                   s.dorm_address AS dormAddress
            FROM student s
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_college c ON c.id = s.college_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (u.name LIKE CONCAT('%', #{keyword}, '%')
                         OR u.login_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
                <if test="classId != null"> AND s.class_id = #{classId} </if>
            </where>
            ORDER BY s.id
            </script>
            """)
    IPage<StudentVO> pageStudents(IPage<StudentVO> page,
                                  @Param("keyword") String keyword,
                                  @Param("collegeId") Long collegeId,
                                  @Param("classId") Long classId);
}
