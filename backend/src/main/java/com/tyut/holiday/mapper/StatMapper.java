package com.tyut.holiday.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tyut.holiday.common.StatusCount;
import com.tyut.holiday.module.manage.dto.UnregisteredVO;
import com.tyut.holiday.module.stat.dto.StudentExportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 统计与管理类查询。所有查询按管辖范围过滤：
 * counselorStaffId 限辅导员管辖班级、collegeId 限院部；均为 null 则不过滤（管理员）。
 */
@Mapper
public interface StatMapper {

    /** 范围内学生总数 */
    @Select("""
            <script>
            SELECT COUNT(*) FROM student s
            LEFT JOIN org_class cl ON cl.id = s.class_id
            <where>
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
            </where>
            </script>
            """)
    int countStudents(@Param("counselorStaffId") Long counselorStaffId,
                      @Param("collegeId") Long collegeId);

    /** 离校登记 按状态计数 */
    @Select("""
            <script>
            SELECT lr.status AS status, COUNT(*) AS cnt
            FROM leave_registration lr
            JOIN student s ON s.id = lr.student_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            WHERE lr.batch_id = #{batchId}
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
            GROUP BY lr.status
            </script>
            """)
    List<StatusCount> countLeaveByStatus(@Param("batchId") Long batchId,
                                         @Param("counselorStaffId") Long counselorStaffId,
                                         @Param("collegeId") Long collegeId);

    /** 返校登记 按状态计数 */
    @Select("""
            <script>
            SELECT rr.status AS status, COUNT(*) AS cnt
            FROM return_registration rr
            JOIN student s ON s.id = rr.student_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            WHERE rr.batch_id = #{batchId}
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
            GROUP BY rr.status
            </script>
            """)
    List<StatusCount> countReturnByStatus(@Param("batchId") Long batchId,
                                          @Param("counselorStaffId") Long counselorStaffId,
                                          @Param("collegeId") Long collegeId);

    /** 留校申请 按审批状态计数 */
    @Select("""
            <script>
            SELECT a.approval_status AS status, COUNT(*) AS cnt
            FROM stay_application a
            JOIN student s ON s.id = a.student_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            WHERE a.batch_id = #{batchId}
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
            GROUP BY a.approval_status
            </script>
            """)
    List<StatusCount> countStayByStatus(@Param("batchId") Long batchId,
                                        @Param("counselorStaffId") Long counselorStaffId,
                                        @Param("collegeId") Long collegeId);

    /** 导出明细 */
    @Select("""
            <script>
            SELECT u.login_name AS studentNo, u.name AS studentName, cl.name AS className,
                   lr.status AS leaveStatus, lr.dest_address AS dest,
                   (SELECT a.approval_status FROM stay_application a
                      WHERE a.student_id = s.id AND a.batch_id = #{batchId}
                      ORDER BY a.id DESC LIMIT 1) AS stayStatus,
                   rr.status AS returnStatus
            FROM student s
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            LEFT JOIN leave_registration lr ON lr.student_id = s.id AND lr.batch_id = #{batchId}
            LEFT JOIN return_registration rr ON rr.student_id = s.id AND rr.batch_id = #{batchId}
            <where>
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
            </where>
            ORDER BY s.id
            </script>
            """)
    List<StudentExportRow> exportRows(@Param("batchId") Long batchId,
                                      @Param("counselorStaffId") Long counselorStaffId,
                                      @Param("collegeId") Long collegeId);

    /** 离校未登记学生分页 */
    @Select("""
            <script>
            SELECT s.id AS studentId, u.name AS studentName, u.login_name AS studentNo,
                   u.phone AS phone, cl.name AS className
            FROM student s
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            <where>
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
                <if test="keyword != null and keyword != ''">
                    AND (u.name LIKE CONCAT('%', #{keyword}, '%')
                         OR u.login_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                AND NOT EXISTS (SELECT 1 FROM leave_registration lr
                    WHERE lr.student_id = s.id AND lr.batch_id = #{batchId})
            </where>
            ORDER BY s.id
            </script>
            """)
    IPage<UnregisteredVO> pageLeaveUnregistered(IPage<UnregisteredVO> page,
                                                @Param("batchId") Long batchId,
                                                @Param("counselorStaffId") Long counselorStaffId,
                                                @Param("collegeId") Long collegeId,
                                                @Param("keyword") String keyword);

    /** 返校未登记学生分页 */
    @Select("""
            <script>
            SELECT s.id AS studentId, u.name AS studentName, u.login_name AS studentNo,
                   u.phone AS phone, cl.name AS className
            FROM student s
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            <where>
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
                <if test="keyword != null and keyword != ''">
                    AND (u.name LIKE CONCAT('%', #{keyword}, '%')
                         OR u.login_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                AND NOT EXISTS (SELECT 1 FROM return_registration rr
                    WHERE rr.student_id = s.id AND rr.batch_id = #{batchId})
            </where>
            ORDER BY s.id
            </script>
            """)
    IPage<UnregisteredVO> pageReturnUnregistered(IPage<UnregisteredVO> page,
                                                 @Param("batchId") Long batchId,
                                                 @Param("counselorStaffId") Long counselorStaffId,
                                                 @Param("collegeId") Long collegeId,
                                                 @Param("keyword") String keyword);

    /** 已登记离校的学生分页（用于帮助重置/修改离校时间） */
    @Select("""
            <script>
            SELECT s.id AS studentId, u.name AS studentName, u.login_name AS studentNo,
                   u.phone AS phone, cl.name AS className
            FROM student s
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            JOIN leave_registration lr ON lr.student_id = s.id AND lr.batch_id = #{batchId}
            <where>
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
                <if test="keyword != null and keyword != ''">
                    AND (u.name LIKE CONCAT('%', #{keyword}, '%')
                         OR u.login_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
            </where>
            ORDER BY s.id
            </script>
            """)
    IPage<UnregisteredVO> pageLeaveRegistered(IPage<UnregisteredVO> page,
                                              @Param("batchId") Long batchId,
                                              @Param("counselorStaffId") Long counselorStaffId,
                                              @Param("collegeId") Long collegeId,
                                              @Param("keyword") String keyword);
}
