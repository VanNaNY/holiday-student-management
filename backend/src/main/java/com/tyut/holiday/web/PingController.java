package com.tyut.holiday.web;

import com.tyut.holiday.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查 / 联通性测试接口。
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("app", "holiday-student-management");
        data.put("status", "UP");
        data.put("time", System.currentTimeMillis());
        return Result.ok(data);
    }
}
