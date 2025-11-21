package com.chengzhang.service;

import com.chengzhang.dto.LoginRequest;
import com.chengzhang.dto.RegisterRequest;
import com.chengzhang.dto.UserDTO;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    UserDTO register(RegisterRequest request);

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 验证Token
     */
    boolean validateToken(String token);

    /**
     * 获取当前用户信息
     */
    UserDTO getCurrentUser(Long userId);
}
