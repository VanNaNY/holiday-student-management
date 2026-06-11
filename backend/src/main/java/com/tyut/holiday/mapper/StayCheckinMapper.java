package com.tyut.holiday.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tyut.holiday.entity.StayCheckin;
import com.tyut.holiday.module.checkin.dto.CheckinStudentVO;
import com.tyut.holiday.module.checkin.dto.DateCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** StayCheckin 数据访问 */
@Mapper
public interface StayCheckinMapper extends BaseMapper<StayCheckin> {

    /** 留校已通过学生中，按日已签到人数（管辖范围内） */
    @Select("""
            <script>
            SELECT sc.checkin_date AS checkinDate, COUNT(DISTINCT sc.student_id) AS cnt
            FROM stay_checkin sc
            JOIN stay_application a ON a.student_id = sc.student_id AND a.batch_id = sc.batch_id
                 AND a.approval_status = 'APPROVED'
            JOIN student s ON s.id = sc.student_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            WHERE sc.batch_id = #{batchId} AND sc.status = 'DONE'
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
            GROUP BY sc.checkin_date
            </script>
            """)
    List<DateCount> signedCountByDate(@Param("batchId") Long batchId,
                                      @Param("counselorStaffId") Long counselorStaffId,
                                      @Param("collegeId") Long collegeId);

    /**
     * 某日签到学生分页（cohort=留校已通过学生），signed 过滤：true 仅已签 / false 仅未签 / null 全部。
     */
    @Select("""
            <script>
            SELECT s.id AS studentId, u.name AS studentName, u.login_name AS studentNo,
                   u.phone AS phone, cl.name AS className,
                   CASE WHEN sc.id IS NULL THEN 0 ELSE 1 END AS signed,
                   sc.checkin_time AS checkinTime
            FROM stay_application a
            JOIN student s ON s.id = a.student_id
            JOIN sys_user u ON u.id = s.user_id
            LEFT JOIN org_class cl ON cl.id = s.class_id
            LEFT JOIN stay_checkin sc ON sc.student_id = s.id AND sc.batch_id = a.batch_id
                 AND sc.checkin_date = #{date} AND sc.status = 'DONE'
            WHERE a.batch_id = #{batchId} AND a.approval_status = 'APPROVED'
                <if test="counselorStaffId != null"> AND cl.counselor_id = #{counselorStaffId} </if>
                <if test="collegeId != null"> AND s.college_id = #{collegeId} </if>
                <if test="signed == true"> AND sc.id IS NOT NULL </if>
                <if test="signed == false"> AND sc.id IS NULL </if>
            ORDER BY signed ASC, s.id ASC
            </script>
            """)
    IPage<CheckinStudentVO> pageCheckinStudents(IPage<CheckinStudentVO> page,
                                                @Param("batchId") Long batchId,
                                                @Param("date") String date,
                                                @Param("counselorStaffId") Long counselorStaffId,
                                                @Param("collegeId") Long collegeId,
                                                @Param("signed") Boolean signed);
}
