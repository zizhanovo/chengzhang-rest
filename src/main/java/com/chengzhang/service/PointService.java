package com.chengzhang.service;

import com.chengzhang.entity.PointAccount;
import com.chengzhang.entity.PointTransaction;

import java.util.List;
import java.util.Map;

/**
 * 积分服务接口
 */
public interface PointService {

    /**
     * 获取积分账户信息
     */
    PointAccount getAccount(Long userId);

    /**
     * 获取积分余额
     */
    Long getBalance(Long userId);

    /**
     * 赠送积分
     */
    void grantPoints(Long userId, Long points, String source, String description);

    /**
     * 消费积分
     */
    void spendPoints(Long userId, Long points, String source, String description);

    /**
     * 获取积分交易记录
     */
    List<PointTransaction> getTransactions(Long userId, int page, int size);

    /**
     * 每日签到
     */
    Map<String, Object> dailyCheckin(Long userId);
}
