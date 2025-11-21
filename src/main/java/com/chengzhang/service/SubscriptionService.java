package com.chengzhang.service;

import com.chengzhang.entity.Subscription;

import java.util.List;

/**
 * 订阅服务接口
 */
public interface SubscriptionService {

    /**
     * 创建订阅（购买会员）
     * @param userId 用户ID
     * @param planType 会员类型：happy_island_6y
     * @return 订阅信息
     */
    Subscription createSubscription(Long userId, String planType);

    /**
     * 获取用户的有效订阅
     */
    Subscription getActiveSubscription(Long userId);

    /**
     * 获取用户的所有订阅
     */
    List<Subscription> getUserSubscriptions(Long userId);

    /**
     * 检查用户是否是会员
     */
    boolean isMember(Long userId);
}
