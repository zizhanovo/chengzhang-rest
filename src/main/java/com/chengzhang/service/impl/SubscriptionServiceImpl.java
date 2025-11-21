package com.chengzhang.service.impl;

import com.chengzhang.entity.PointAccount;
import com.chengzhang.entity.PointTransaction;
import com.chengzhang.entity.Subscription;
import com.chengzhang.repository.PointAccountRepository;
import com.chengzhang.repository.PointTransactionRepository;
import com.chengzhang.repository.SubscriptionRepository;
import com.chengzhang.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订阅服务实现
 */
@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PointAccountRepository pointAccountRepository;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Override
    public Subscription createSubscription(Long userId, String planType) {
        // 检查是否已有有效订阅
        Optional<Subscription> existing = subscriptionRepository.findActiveSubscription(
                userId, LocalDateTime.now());
        if (existing.isPresent()) {
            throw new RuntimeException("您已经是会员了");
        }

        // 根据会员类型设置参数
        String planName;
        BigDecimal price;
        LocalDateTime endDate;
        Long totalPoints;  // 总积分（简化版：一次性发放）

        if ("happy_island_6y".equals(planType)) {
            planName = "幸福岛6年会员";
            price = new BigDecimal("3999.00");
            endDate = LocalDateTime.now().plusYears(6);
            totalPoints = 46000L;  // 10000首次 + 500*72月 = 46000
        } else {
            throw new RuntimeException("不支持的会员类型");
        }

        // 创建订阅
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setPlanType(planType);
        subscription.setPlanName(planName);
        subscription.setPrice(price);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(endDate);
        subscription.setStatus("active");
        subscription.setAutoRenew(0);

        subscription = subscriptionRepository.save(subscription);

        // 发放积分（简化版：一次性发放全部积分）
        grantPoints(userId, totalPoints, subscription.getId());

        return subscription;
    }

    @Override
    public Subscription getActiveSubscription(Long userId) {
        return subscriptionRepository.findActiveSubscription(userId, LocalDateTime.now())
                .orElse(null);
    }

    @Override
    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public boolean isMember(Long userId) {
        return subscriptionRepository.findActiveSubscription(userId, LocalDateTime.now())
                .isPresent();
    }

    /**
     * 发放积分
     */
    private void grantPoints(Long userId, Long points, Long subscriptionId) {
        // 查询或创建积分账户
        PointAccount account = pointAccountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PointAccount newAccount = new PointAccount();
                    newAccount.setUserId(userId);
                    newAccount.setBalance(0L);
                    newAccount.setTotalEarned(0L);
                    newAccount.setTotalSpent(0L);
                    newAccount.setLevel(1);
                    return pointAccountRepository.save(newAccount);
                });

        // 更新积分余额
        account.setBalance(account.getBalance() + points);
        account.setTotalEarned(account.getTotalEarned() + points);

        // 根据累计积分更新等级
        if (account.getTotalEarned() >= 200000) {
            account.setLevel(5);
        } else if (account.getTotalEarned() >= 100000) {
            account.setLevel(4);
        } else if (account.getTotalEarned() >= 50000) {
            account.setLevel(3);
        } else if (account.getTotalEarned() >= 10000) {
            account.setLevel(2);
        } else {
            account.setLevel(1);
        }

        pointAccountRepository.save(account);

        // 记录交易
        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setType("earn");
        transaction.setAmount(points);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setSource("subscription");
        transaction.setSourceId(subscriptionId.toString());
        transaction.setDescription("购买" + (subscriptionId != null ? "会员" : "") + "赠送积分");

        pointTransactionRepository.save(transaction);
    }
}
