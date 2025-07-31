package com.chengzhang.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章DTO类
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
    @Size(max = 100, message = "文章标题长度不能超过100个字符")
    private String title;
    
    /**
     * 文章内容（Markdown格式）
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;
    
    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 文章分类
     */
    private String category;
    
    /**
     * 文章状态：draft-草稿，published-已发布
     */
    private String status;
    
    /**
     * 字数统计
     */
    private Integer wordCount;
    
    /**
     * 预估阅读时间（分钟）
     */
    private Integer readTime;
    
    /**
     * 文章中的图片列表
     */
    private List<String> images;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;
}