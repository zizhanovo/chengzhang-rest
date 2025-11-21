package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.dto.LoginRequest;
import com.chengzhang.dto.RegisterRequest;
import com.chengzhang.dto.UserDTO;
import com.chengzhang.service.AuthService;
import com.chengzhang.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDTO userDTO = authService.register(request);
            return ApiResponse.success("注册成功", userDTO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = authService.login(request);
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // 简化版：客户端直接删除Token即可
        return ApiResponse.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ApiResponse.error(401, "未登录");
            }

            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 验证Token
            if (!JwtUtil.validateToken(token)) {
                return ApiResponse.error(401, "Token无效或已过期");
            }

            // 获取用户ID
            Long userId = JwtUtil.getUserIdFromToken(token);
            UserDTO userDTO = authService.getCurrentUser(userId);

            return ApiResponse.success(userDTO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
