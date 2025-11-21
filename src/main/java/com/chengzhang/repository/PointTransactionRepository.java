package com.chengzhang.repository;

import com.chengzhang.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分交易记录数据访问层
 */
@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    /**
     * 查找用户的积分交易记录（分页）
     */
    Page<PointTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 查找用户某个时间段的交易记录
     */
    List<PointTransaction> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计用户某段时间的总获得积分
     */
    @Query("SELECT SUM(t.amount) FROM PointTransaction t WHERE t.userId = ?1 AND t.type = 'earn' AND t.createdAt BETWEEN ?2 AND ?3")
    Long sumEarnedPoints(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计用户某段时间的总消费积分
     */
    @Query("SELECT SUM(ABS(t.amount)) FROM PointTransaction t WHERE t.userId = ?1 AND t.type = 'spend' AND t.createdAt BETWEEN ?2 AND ?3")
    Long sumSpentPoints(Long userId, LocalDateTime start, LocalDateTime end);
}
