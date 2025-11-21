package com.chengzhang.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 积分交易记录实体
 */
@Data
@Entity
@Table(name = "point_transactions")
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 交易类型：earn-获得 spend-消费
     */
    @Column(nullable = false, length = 20)
    private String type;

    /**
     * 积分数量（正数为获得，负数为消费）
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * 交易后余额
     */
    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    /**
     * 来源：subscription-订阅 daily_sign-签到 article_publish-发文 service_consume-服务消费等
     */
    @Column(nullable = false, length = 50)
    private String source;

    /**
     * 来源ID（如订阅ID、文章ID等）
     */
    @Column(name = "source_id", length = 100)
    private String sourceId;

    /**
     * 交易描述
     */
    @Column(length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 关联用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
