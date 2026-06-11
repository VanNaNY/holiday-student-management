package com.tyut.holiday.module.stat;

import com.tyut.holiday.common.Constants;
import com.tyut.holiday.common.StatusCount;
import com.tyut.holiday.mapper.StatMapper;
import com.tyut.holiday.module.stat.dto.StatOverviewVO;
import com.tyut.holiday.module.stat.dto.StudentExportRow;
import com.tyut.holiday.module.support.ScopeResolver;
import com.tyut.holiday.module.support.ScopeResolver.Scope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 批次统计服务：离校/留校/返校按状态计数、导出明细。
 */
@Service
@RequiredArgsConstructor
public class StatService {

    private final StatMapper statMapper;
    private final ScopeResolver scopeResolver;

    /** 统计概览 */
    public StatOverviewVO overview(Long batchId) {
        Scope sc = scopeResolver.resolve();
        int total = statMapper.countStudents(sc.counselorStaffId(), sc.collegeId());

        Map<String, Integer> leave = regMap(
                statMapper.countLeaveByStatus(batchId, sc.counselorStaffId(), sc.collegeId()), total);
        Map<String, Integer> ret = regMap(
                statMapper.countReturnByStatus(batchId, sc.counselorStaffId(), sc.collegeId()), total);
        Map<String, Integer> stay = stayMap(
                statMapper.countStayByStatus(batchId, sc.counselorStaffId(), sc.collegeId()));

        StatOverviewVO vo = new StatOverviewVO();
        vo.setTotalStudents(total);
        vo.setLeave(leave);
        vo.setRet(ret);
        vo.setStay(stay);
        return vo;
    }

    /** 导出明细行 */
    public List<StudentExportRow> exportRows(Long batchId) {
        Scope sc = scopeResolver.resolve();
        return statMapper.exportRows(batchId, sc.counselorStaffId(), sc.collegeId());
    }

    // ---------- 内部 ----------

    /** 离校/返校：补齐 4 个状态，NOT_REG = 总数 - 已登记行数 */
    private Map<String, Integer> regMap(List<StatusCount> counts, int total) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(Constants.RegStatus.NOT_REG, 0);
        m.put(Constants.RegStatus.REGISTERED, 0);
        m.put(Constants.RegStatus.ON_THE_WAY, 0);
        m.put(Constants.RegStatus.ARRIVED, 0);
        int registered = 0;
        for (StatusCount c : counts) {
            if (c.getStatus() == null) {
                continue;
            }
            m.put(c.getStatus(), c.getCnt());
            if (!Constants.RegStatus.NOT_REG.equals(c.getStatus())) {
                registered += c.getCnt();
            }
        }
        m.put(Constants.RegStatus.NOT_REG, Math.max(0, total - registered));
        return m;
    }

    /** 留校：补齐 4 个审批状态 */
    private Map<String, Integer> stayMap(List<StatusCount> counts) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(Constants.ApprovalStatus.PENDING, 0);
        m.put(Constants.ApprovalStatus.APPROVED, 0);
        m.put(Constants.ApprovalStatus.REJECTED, 0);
        m.put(Constants.ApprovalStatus.WITHDRAWN, 0);
        for (StatusCount c : counts) {
            if (c.getStatus() != null) {
                m.put(c.getStatus(), c.getCnt());
            }
        }
        return m;
    }
}
