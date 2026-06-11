package com.tyut.holiday.module.auth;

import com.tyut.holiday.common.Result;
import com.tyut.holiday.module.auth.dto.LoginReq;
import com.tyut.holiday.module.auth.dto.LoginResp;
import com.tyut.holiday.module.auth.dto.SwitchRoleReq;
import com.tyut.holiday.module.auth.dto.UserInfo;
import com.tyut.holiday.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鉴权接口：登录、当前用户、角色切换。
 */
@Tag(name = "认证", description = "登录 / 当前用户 / 角色切换")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req) {
        return Result.ok(authService.login(req));
    }

    @Operation(summary = "当前登录用户信息")
    @GetMapping("/me")
    public Result<UserInfo> me() {
        return Result.ok(authService.currentUserInfo(UserContext.require()));
    }

    @Operation(summary = "角色切换（返回新 token）")
    @PostMapping("/switch-role")
    public Result<LoginResp> switchRole(@Valid @RequestBody SwitchRoleReq req) {
        return Result.ok(authService.switchRole(UserContext.userId(), req.getRole()));
    }
}
