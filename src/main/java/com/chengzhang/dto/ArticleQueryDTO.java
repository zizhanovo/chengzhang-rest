package com.chengzhang.dto;

import lombok.Data;

/**
 * 文章查询请求DTO
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class ArticleQueryDTO {
    
    /**
     * 页码，默认1
     */
    private Integer page = 1;
    
    /**
     * 每页数量，默认10
     */
    private Integer size = 10;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 文章分类
     */
    private String category;
    
    /**
     * 文章状态：draft/published
     */
    private String status;
    
    /**
     * 排序字段：createdAt/updatedAt/title
     */
    private String sortBy = "createdAt";
    
    /**
     * 排序方向：asc/desc
     */
    private String sortOrder = "desc";
}