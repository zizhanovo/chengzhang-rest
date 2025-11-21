package com.chengzhang.service.impl;

import com.chengzhang.entity.PointAccount;
import com.chengzhang.entity.PointTransaction;
import com.chengzhang.repository.PointAccountRepository;
import com.chengzhang.repository.PointTransactionRepository;
import com.chengzhang.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分服务实现
 */
@Service
@Transactional
public class PointServiceImpl implements PointService {

    @Autowired
    private PointAccountRepository pointAccountRepository;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Override
    public PointAccount getAccount(Long userId) {
        return pointAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("积分账户不存在"));
    }

    @Override
    public Long getBalance(Long userId) {
        return pointAccountRepository.findByUserId(userId)
                .map(PointAccount::getBalance)
                .orElse(0L);
    }

    @Override
    public void grantPoints(Long userId, Long points, String source, String description) {
        if (points <= 0) {
            throw new RuntimeException("积分数量必须大于0");
        }

        // 获取或创建积分账户
        PointAccount account = pointAccountRepository.findByUserId(userId)
                .orElseGet(() -> createAccount(userId));

        // 更新余额
        account.setBalance(account.getBalance() + points);
        account.setTotalEarned(account.getTotalEarned() + points);

        // 更新等级
        updateLevel(account);

        pointAccountRepository.save(account);

        // 记录交易
        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setType("earn");
        transaction.setAmount(points);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setSource(source);
        transaction.setDescription(description);

        pointTransactionRepository.save(transaction);
    }

    @Override
    public void spendPoints(Long userId, Long points, String source, String description) {
        if (points <= 0) {
            throw new RuntimeException("积分数量必须大于0");
        }

        PointAccount account = pointAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("积分账户不存在"));

        // 检查余额
        if (account.getBalance() < points) {
            throw new RuntimeException("积分余额不足");
        }

        // 扣除积分
        account.setBalance(account.getBalance() - points);
        account.setTotalSpent(account.getTotalSpent() + points);

        pointAccountRepository.save(account);

        // 记录交易
        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setType("spend");
        transaction.setAmount(-points);  // 负数表示消费
        transaction.setBalanceAfter(account.getBalance());
        transaction.setSource(source);
        transaction.setDescription(description);

        pointTransactionRepository.save(transaction);
    }

    @Override
    public List<PointTransaction> getTransactions(Long userId, int page, int size) {
        Page<PointTransaction> result = pointTransactionRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
        return result.getContent();
    }

    @Override
    public Map<String, Object> dailyCheckin(Long userId) {
        // 简化版：每次签到固定10积分
        Long pointsEarned = 10L;

        grantPoints(userId, pointsEarned, "daily_sign", "每日签到");

        Map<String, Object> result = new HashMap<>();
        result.put("pointsEarned", pointsEarned);
        result.put("newBalance", getBalance(userId));
        result.put("continuousDays", 1);  // 简化版：固定为1

        return result;
    }

    /**
     * 创建积分账户
     */
    private PointAccount createAccount(Long userId) {
        PointAccount account = new PointAccount();
        account.setUserId(userId);
        account.setBalance(0L);
        account.setTotalEarned(0L);
        account.setTotalSpent(0L);
        account.setLevel(1);
        return pointAccountRepository.save(account);
    }

    /**
     * 更新积分等级
     */
    private void updateLevel(PointAccount account) {
        Long totalEarned = account.getTotalEarned();
        if (totalEarned >= 200000) {
            account.setLevel(5);
        } else if (totalEarned >= 100000) {
            account.setLevel(4);
        } else if (totalEarned >= 50000) {
            account.setLevel(3);
        } else if (totalEarned >= 10000) {
            account.setLevel(2);
        } else {
            account.setLevel(1);
        }
    }
}
