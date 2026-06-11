package com.tyut.holiday.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tyut.holiday.entity.StayApplication;
import com.tyut.holiday.module.approval.dto.ApplicationListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** StayApplication 数据访问 */
@Mapper
public interface StayApplicationMapper extends BaseMapper<StayApplication> {

    /**
     * 审批列表分页查询（联表学生/班级）。
     * 各过滤参数为空则忽略；counselorStaffId 限定辅导员管辖班级，collegeId 限定院部范围。
     */
    @Select("""
            <script>
            SELECT a.id, a.student_id AS studentId, u.name AS studentName, u.login_name AS studentNo,
                   u.phone AS studentPhone, cl.name AS className, a.batch_id AS batchId,
                   a.plan_start AS planStart, a.plan_end AS planEnd, a.campus, a.reason,
                   a.approval_status AS approvalStatus, a.current_node AS currentNode,
                   a.create_time AS createTime
            FROM stay_application a
            JOIN student s ON s.id = a.student_id
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            <where>
                <if test="batchId != null"> AND a.batch_id = #{batchId} </if>
                <if test="node != null"> AND a.current_node = #{node} </if>
                <if test="status != null"> AND a.approval_status = #{status} </if>
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
                <if test="keyword != null and keyword != ''">
                    AND (u.name LIKE CONCAT('%', #{keyword}, '%')
                         OR u.login_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
            </where>
            ORDER BY a.id DESC
            </script>
            """)
    IPage<ApplicationListVO> pageApplications(IPage<ApplicationListVO> page,
                                              @Param("batchId") Long batchId,
                                              @Param("node") String node,
                                              @Param("status") String status,
                                              @Param("counselorStaffId") Long counselorStaffId,
                                              @Param("collegeId") Long collegeId,
                                              @Param("keyword") String keyword);
}
