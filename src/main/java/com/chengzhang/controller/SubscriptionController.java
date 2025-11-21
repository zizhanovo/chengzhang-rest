package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.entity.Subscription;
import com.chengzhang.service.SubscriptionService;
import com.chengzhang.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订阅控制器
 */
@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * 获取用户的所有订阅
     */
    @GetMapping("")
    public ApiResponse<List<Subscription>> getSubscriptions(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);
            return ApiResponse.success(subscriptions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取有效订阅
     */
    @GetMapping("/active")
    public ApiResponse<Subscription> getActiveSubscription(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Subscription subscription = subscriptionService.getActiveSubscription(userId);
            return ApiResponse.success(subscription);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 购买会员（创建订阅）
     */
    @PostMapping("")
    public ApiResponse<Subscription> createSubscription(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            Long userId = getUserIdFromToken(token);
            String planType = request.get("planType");

            if (planType == null || planType.isEmpty()) {
                return ApiResponse.error("请选择会员类型");
            }

            Subscription subscription = subscriptionService.createSubscription(userId, planType);
            return ApiResponse.success("会员购买成功，已发放积分！", subscription);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查会员状态
     */
    @GetMapping("/status")
    public ApiResponse<Boolean> checkMemberStatus(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            boolean isMember = subscriptionService.isMember(userId);
            return ApiResponse.success(isMember);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 从Token获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.getUserIdFromToken(token);
    }
}
