package com.tyut.holiday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 留校申请附件 stay_attachment */
@Data
@TableName("stay_attachment")
public class StayAttachment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long applicationId;

    /** 附件图片 URL */
    private String fileUrl;
}
