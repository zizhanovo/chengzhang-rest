package com.chengzhang.repository;

import com.chengzhang.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订阅数据访问层
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * 查找用户的所有订阅
     */
    List<Subscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 查找用户的有效订阅
     */
    @Query("SELECT s FROM Subscription s WHERE s.userId = ?1 AND s.status = 'active' AND s.endDate > ?2")
    Optional<Subscription> findActiveSubscription(Long userId, LocalDateTime now);

    /**
     * 查找即将过期的订阅（用于定时任务）
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'active' AND s.endDate BETWEEN ?1 AND ?2")
    List<Subscription> findExpiringSoon(LocalDateTime start, LocalDateTime end);
}
