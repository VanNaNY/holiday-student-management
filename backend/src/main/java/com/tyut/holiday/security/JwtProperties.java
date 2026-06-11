package com.tyut.holiday.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项，绑定 application.yml 中 jwt.* 。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥（生产经环境变量注入） */
    private String secret;

    /** token 有效期（分钟） */
    private long expireMinutes = 720;

    /** 请求头名称 */
    private String header = "Authorization";

    /** token 前缀 */
    private String prefix = "Bearer ";
}
