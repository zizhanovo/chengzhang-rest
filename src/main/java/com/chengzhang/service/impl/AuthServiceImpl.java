package com.chengzhang.service.impl;

import com.chengzhang.dto.LoginRequest;
import com.chengzhang.dto.RegisterRequest;
import com.chengzhang.dto.UserDTO;
import com.chengzhang.entity.PointAccount;
import com.chengzhang.entity.Subscription;
import com.chengzhang.entity.User;
import com.chengzhang.repository.PointAccountRepository;
import com.chengzhang.repository.SubscriptionRepository;
import com.chengzhang.repository.UserRepository;
import com.chengzhang.service.AuthService;
import com.chengzhang.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证服务实现
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PointAccountRepository pointAccountRepository;

    @Override
    public UserDTO register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 简化版：明文存储，生产环境需要加密
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(1);

        user = userRepository.save(user);

        // 创建积分账户（初始为0）
        PointAccount pointAccount = new PointAccount();
        pointAccount.setUserId(user.getId());
        pointAccount.setBalance(0L);
        pointAccount.setTotalEarned(0L);
        pointAccount.setTotalSpent(0L);
        pointAccount.setLevel(1);
        pointAccountRepository.save(pointAccount);

        return convertToDTO(user, null, pointAccount);
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("邮箱或密码错误");
        }

        User user = userOpt.get();

        // 验证密码（简化版：明文比较）
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }

        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getEmail(), request.getRemember());

        // 查询会员信息
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findActiveSubscription(
                user.getId(), LocalDateTime.now());

        // 查询积分信息
        Optional<PointAccount> pointAccountOpt = pointAccountRepository.findByUserId(user.getId());

        // 构造响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userInfo", convertToDTO(user, subscriptionOpt.orElse(null), 
                                              pointAccountOpt.orElse(null)));

        return response;
    }

    @Override
    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    @Override
    public UserDTO getCurrentUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findActiveSubscription(
                userId, LocalDateTime.now());
        Optional<PointAccount> pointAccountOpt = pointAccountRepository.findByUserId(userId);

        return convertToDTO(user, subscriptionOpt.orElse(null), pointAccountOpt.orElse(null));
    }

    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user, Subscription subscription, PointAccount pointAccount) {
        UserDTO.MembershipInfo membershipInfo = UserDTO.MembershipInfo.builder()
                .isMember(subscription != null && "active".equals(subscription.getStatus()))
                .planType(subscription != null ? subscription.getPlanType() : null)
                .planName(subscription != null ? subscription.getPlanName() : null)
                .endDate(subscription != null ? subscription.getEndDate().toString() : null)
                .daysRemaining(subscription != null ? 
                        (int) ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getEndDate()) : 0)
                .build();

        UserDTO.PointInfo pointInfo = UserDTO.PointInfo.builder()
                .balance(pointAccount != null ? pointAccount.getBalance() : 0L)
                .totalEarned(pointAccount != null ? pointAccount.getTotalEarned() : 0L)
                .totalSpent(pointAccount != null ? pointAccount.getTotalSpent() : 0L)
                .level(pointAccount != null ? pointAccount.getLevel() : 1)
                .build();

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .status(user.getStatus())
                .membership(membershipInfo)
                .points(pointInfo)
                .build();
    }
}
