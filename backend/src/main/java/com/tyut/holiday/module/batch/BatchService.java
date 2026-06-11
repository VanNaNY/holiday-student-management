package com.tyut.holiday.module.batch;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.entity.HolidayBatch;
import com.tyut.holiday.mapper.HolidayBatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 假期批次服务：CRUD、当前生效批次、状态流转。
 */
@Service
@RequiredArgsConstructor
public class BatchService {

    private final HolidayBatchMapper batchMapper;

    /** 全部批次（按创建时间倒序） */
    public List<HolidayBatch> list() {
        return batchMapper.selectList(
                Wrappers.<HolidayBatch>lambdaQuery().orderByDesc(HolidayBatch::getId));
    }

    /** 详情 */
    public HolidayBatch get(Long id) {
        HolidayBatch b = batchMapper.selectById(id);
        if (b == null) {
            throw BizException.notFound("批次不存在");
        }
        return b;
    }

    /** 当前生效批次（ACTIVE，取最新一个）；无则返回 null */
    public HolidayBatch current() {
        return batchMapper.selectOne(
                Wrappers.<HolidayBatch>lambdaQuery()
                        .eq(HolidayBatch::getStatus, "ACTIVE")
                        .orderByDesc(HolidayBatch::getId)
                        .last("limit 1"));
    }

    /** 新建 */
    public HolidayBatch create(HolidayBatch b) {
        if (b.getStatus() == null || b.getStatus().isBlank()) {
            b.setStatus("DRAFT");
        }
        b.setId(null);
        b.setCreateTime(LocalDateTime.now());
        batchMapper.insert(b);
        return b;
    }

    /** 更新 */
    public HolidayBatch update(Long id, HolidayBatch b) {
        HolidayBatch exist = get(id);
        b.setId(exist.getId());
        b.setCreateTime(exist.getCreateTime());
        batchMapper.updateById(b);
        return get(id);
    }

    /** 修改状态：DRAFT/ACTIVE/CLOSED */
    public HolidayBatch changeStatus(Long id, String status) {
        if (!List.of("DRAFT", "ACTIVE", "CLOSED").contains(status)) {
            throw BizException.badRequest("非法状态：" + status);
        }
        HolidayBatch b = get(id);
        b.setStatus(status);
        batchMapper.updateById(b);
        return b;
    }

    /** 删除 */
    public void delete(Long id) {
        get(id);
        batchMapper.deleteById(id);
    }
}
