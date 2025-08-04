package com.chengzhang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章实体类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "articles")
public class Article {

    /**
     * 文章ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 文章内容（Markdown格式）
     */
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    @Column(name = "summary", length = 500)
    private String summary;

    /**
     * 文章状态：draft-草稿，published-已发布
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "draft";

    /**
     * 文章分类
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 文章标签（JSON格式存储）
     */
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    /**
     * 字数统计
     */
    @Column(name = "word_count")
    private Integer wordCount = 0;

    /**
     * 预计阅读时间（分钟）
     */
    @Column(name = "read_time")
    private Integer readTime = 0;

    /**
     * 文章图片（JSON格式存储）
     */
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建时间自动设置
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 更新时间自动设置
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 获取标签列表
     */
    @Transient
    public List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return new ObjectMapper().readValue(tags, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 设置标签列表
     */
    @Transient
    public void setTagList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
            return;
        }
        try {
            this.tags = new ObjectMapper().writeValueAsString(tagList);
        } catch (Exception e) {
            this.tags = null;
        }
    }

    /**
     * 获取图片列表
     */
    @Transient
    public List<String> getImageList() {
        if (images == null || images.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return new ObjectMapper().readValue(images, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 设置图片列表
     */
    @Transient
    public void setImageList(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            this.images = null;
            return;
        }
        try {
            this.images = new ObjectMapper().writeValueAsString(imageList);
        } catch (Exception e) {
            this.images = null;
        }
    }
}