package com.chengzhang.dto;

import com.chengzhang.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章数据传输对象
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class ArticleDTO {

    /**
     * 文章ID
     */
    private String id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    private String title;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;

    /**
     * 文章状态：draft-草稿，published-已发布
     */
    private String status;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 文章合集ID
     */
    private String collectionId;

    /**
     * 文章标签
     */
    private List<String> tags;

    /**
     * 字数统计
     */
    private Integer wordCount;

    /**
     * 预计阅读时间（分钟）
     */
    private Integer readTime;

    /**
     * 文章图片
     */
    private List<String> images;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为DTO
     *
     * @param article 文章实体
     * @return ArticleDTO
     */
    public static ArticleDTO fromEntity(Article article) {
        if (article == null) {
            return null;
        }
        
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setStatus(article.getStatus());
        dto.setCategory(article.getCategory());
        dto.setCollectionId(article.getCollectionId());
        dto.setTags(article.getTagList());
        dto.setWordCount(article.getWordCount());
        dto.setReadTime(article.getReadTime());
        dto.setImages(article.getImageList());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        
        return dto;
    }

    /**
     * 转换为实体
     *
     * @return Article
     */
    public Article toEntity() {
        Article article = new Article();
        article.setId(this.id);
        article.setTitle(this.title);
        article.setContent(this.content);
        article.setSummary(this.summary);
        article.setStatus(this.status != null ? this.status : "draft");
        article.setCategory(this.category);
        article.setCollectionId(this.collectionId);
        article.setTagList(this.tags);
        article.setWordCount(this.wordCount != null ? this.wordCount : 0);
        article.setReadTime(this.readTime != null ? this.readTime : 0);
        article.setImageList(this.images);
        
        return article;
    }

    /**
     * 更新实体
     *
     * @param article 要更新的文章实体
     */
    public void updateEntity(Article article) {
        if (this.title != null) {
            article.setTitle(this.title);
        }
        if (this.content != null) {
            article.setContent(this.content);
        }
        if (this.summary != null) {
            article.setSummary(this.summary);
        }
        if (this.status != null) {
            article.setStatus(this.status);
        }
        if (this.category != null) {
            article.setCategory(this.category);
        }
        if (this.collectionId != null) {
            article.setCollectionId(this.collectionId);
        }
        if (this.tags != null) {
            article.setTagList(this.tags);
        }
        if (this.wordCount != null) {
            article.setWordCount(this.wordCount);
        }
        if (this.readTime != null) {
            article.setReadTime(this.readTime);
        }
        if (this.images != null) {
            article.setImageList(this.images);
        }
    }
}