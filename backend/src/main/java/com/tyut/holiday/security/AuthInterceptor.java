package com.tyut.holiday.security;

import com.tyut.holiday.common.BizException;
import com.tyut.holiday.module.auth.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器：解析 token → 装载当前用户到 {@link UserContext}。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProperties props;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行非控制器处理器（静态资源等）与预检请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(props.getHeader());
        if (header == null || header.isBlank()) {
            throw BizException.unauthorized("未登录或缺少凭证");
        }
        String token = header.startsWith(props.getPrefix())
                ? header.substring(props.getPrefix().length()) : header;

        try {
            Claims claims = jwtUtil.parse(token);
            Long userId = Long.valueOf(claims.getSubject());
            String activeRole = claims.get("activeRole", String.class);
            LoginUser loginUser = authService.loadLoginUser(userId, activeRole);
            UserContext.set(loginUser);
            return true;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw BizException.unauthorized("登录已失效，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
