package com.tyut.holiday.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用自定义配置，绑定 app.* 。
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Upload upload = new Upload();

    @Data
    public static class Upload {
        /** 本地磁盘存储目录 */
        private String dir = "./uploads";
        /** 单文件最大字节数 */
        private long maxSize = 31457280L;
        /** 访问前缀 */
        private String urlPrefix = "/files";
    }
}
