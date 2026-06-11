package com.tyut.holiday.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tyut.holiday.entity.Staff;
import org.apache.ibatis.annotations.Mapper;

/** Staff 数据访问 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
}
