package com.tyut.holiday.security;

import com.tyut.holiday.common.BizException;

/**
 * 基于 ThreadLocal 的当前登录用户上下文。
 * 由 {@link AuthInterceptor} 在请求开始时写入、结束时清理。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    /** 获取当前用户，未登录返回 null */
    public static LoginUser get() {
        return HOLDER.get();
    }

    /** 获取当前用户，未登录抛 401 */
    public static LoginUser require() {
        LoginUser u = HOLDER.get();
        if (u == null) {
            throw BizException.unauthorized("未登录或登录已失效");
        }
        return u;
    }

    /** 当前用户 id（未登录抛 401） */
    public static Long userId() {
        return require().getUserId();
    }

    /** 当前激活角色 */
    public static String activeRole() {
        return require().getActiveRole();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
