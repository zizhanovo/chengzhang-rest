package com.chengzhang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Entity
@Table(name = "articles")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Article {
    
    /**
     * 文章ID
     */
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "文章标题长度不能超过100个字符")
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    /**
     * 文章内容（Markdown格式）
     */
    @NotBlank(message = "文章内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    @Column(name = "summary", length = 500)
    private String summary;
    
    /**
     * 标签（JSON格式存储）
     */
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;
    
    /**
     * 文章分类
     */
    @Column(name = "category", length = 50)
    private String category;
    
    /**
     * 文章状态：draft-草稿，published-已发布
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "draft";
    
    /**
     * 字数统计
     */
    @Column(name = "word_count")
    private Integer wordCount = 0;
    
    /**
     * 预估阅读时间（分钟）
     */
    @Column(name = "read_time")
    private Integer readTime = 0;
    
    /**
     * 文章中的图片列表（JSON格式存储）
     */
    @Column(name = "images", columnDefinition = "JSON")
    private String images;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 用户ID（预留字段，用于后续多用户支持）
     */
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = "article_" + System.currentTimeMillis();
        }
        calculateWordCountAndReadTime();
    }
    
    @PreUpdate
    public void preUpdate() {
        calculateWordCountAndReadTime();
    }
    
    /**
     * 计算字数和阅读时间
     */
    private void calculateWordCountAndReadTime() {
        if (this.content != null) {
            // 简单的字数统计（去除Markdown标记）
            String plainText = this.content.replaceAll("[#*`>\\-\\[\\]\\(\\)]", "");
            this.wordCount = plainText.length();
            // 按照每分钟250字的阅读速度计算
            this.readTime = Math.max(1, this.wordCount / 250);
        }
    }
}