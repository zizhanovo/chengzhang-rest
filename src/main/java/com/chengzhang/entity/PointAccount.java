package com.chengzhang.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 积分账户实体
 */
@Data
@Entity
@Table(name = "point_accounts")
public class PointAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 当前积分余额
     */
    @Column(nullable = false)
    private Long balance = 0L;

    /**
     * 累计获得积分
     */
    @Column(name = "total_earned", nullable = false)
    private Long totalEarned = 0L;

    /**
     * 累计消费积分
     */
    @Column(name = "total_spent", nullable = false)
    private Long totalSpent = 0L;

    /**
     * 积分等级
     */
    @Column(nullable = false)
    private Integer level = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 关联用户
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
