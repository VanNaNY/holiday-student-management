package com.tyut.holiday.common;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理：将各类异常统一转为 {@link Result}。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException e, HttpServletResponse resp) {
        // 401/403 同步设置 HTTP 状态，便于前端拦截
        if (e.getCode() == ResultCode.UNAUTHORIZED) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else if (e.getCode() == ResultCode.FORBIDDEN) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** @Valid 校验失败（请求体） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.fail(ResultCode.BAD_REQUEST, msg.isEmpty() ? "参数校验失败" : msg);
    }

    /** 表单绑定校验失败 */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.fail(ResultCode.BAD_REQUEST, msg.isEmpty() ? "参数校验失败" : msg);
    }

    /** 兜底 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        log.error("未处理异常", e);
        return Result.fail(ResultCode.BIZ_ERROR, "服务器内部错误：" + e.getMessage());
    }
}
