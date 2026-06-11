package com.tyut.holiday.common;

import lombok.Getter;

/**
 * 业务异常。由全局异常处理器统一转为 {@link Result}。
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(ResultCode.BIZ_ERROR, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    /** 参数/请求错误 */
    public static BizException badRequest(String msg) {
        return new BizException(ResultCode.BAD_REQUEST, msg);
    }

    /** 未授权 */
    public static BizException unauthorized(String msg) {
        return new BizException(ResultCode.UNAUTHORIZED, msg);
    }

    /** 无权限 */
    public static BizException forbidden(String msg) {
        return new BizException(ResultCode.FORBIDDEN, msg);
    }

    /** 资源不存在 */
    public static BizException notFound(String msg) {
        return new BizException(ResultCode.NOT_FOUND, msg);
    }
}
