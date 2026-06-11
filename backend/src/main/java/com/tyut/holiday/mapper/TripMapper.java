package com.tyut.holiday.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tyut.holiday.entity.Trip;
import org.apache.ibatis.annotations.Mapper;

/** Trip 数据访问 */
@Mapper
public interface TripMapper extends BaseMapper<Trip> {
}
