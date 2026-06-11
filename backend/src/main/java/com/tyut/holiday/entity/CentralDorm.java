package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 留校集中住宿地址 central_dorm */
@Data
@TableName("central_dorm")
public class CentralDorm {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 校区 A/B */
    private String campus;

    private String building;

    private String address;
}
