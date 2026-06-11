package com.tyut.holiday.module.approval;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.entity.ApprovalRecord;
import com.tyut.holiday.mapper.ApprovalRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批流水服务：写入与查询某申请的审批记录。
 */
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRecordMapper recordMapper;

    /** 追加一条审批流水，seq 自增 */
    public ApprovalRecord record(Long applicationId, Long approverId, String role,
                                 String action, String comment) {
        Long count = recordMapper.selectCount(Wrappers.<ApprovalRecord>lambdaQuery()
                .eq(ApprovalRecord::getApplicationId, applicationId));
        ApprovalRecord r = new ApprovalRecord();
        r.setApplicationId(applicationId);
        r.setSeq((count == null ? 0 : count.intValue()) + 1);
        r.setApproverId(approverId);
        r.setApproverRole(role);
        r.setAction(action);
        r.setComment(comment);
        r.setCreateTime(LocalDateTime.now());
        recordMapper.insert(r);
        return r;
    }

    /** 某申请的审批流水（按 seq 升序） */
    public List<ApprovalRecord> records(Long applicationId) {
        return recordMapper.selectList(Wrappers.<ApprovalRecord>lambdaQuery()
                .eq(ApprovalRecord::getApplicationId, applicationId)
                .orderByAsc(ApprovalRecord::getSeq));
    }
}
