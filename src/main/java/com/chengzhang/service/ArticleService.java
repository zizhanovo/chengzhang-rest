package com.chengzhang.service;

import com.chengzhang.common.PageResult;
import com.chengzhang.dto.ArticleDTO;
import com.chengzhang.dto.ArticleQueryDTO;
import com.chengzhang.dto.BatchDeleteDTO;
import com.chengzhang.entity.Article;

import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 * 
 * @author chengzhang
 * @since 1.0.0
 */
public interface ArticleService {
    
    /**
     * 分页查询文章列表
     */
    PageResult<ArticleDTO> getArticles(ArticleQueryDTO queryDTO);
    
    /**
     * 根据ID获取文章详情
     */
    ArticleDTO getArticleById(String id);
    
    /**
     * 创建文章
     */
    ArticleDTO createArticle(ArticleDTO articleDTO);
    
    /**
     * 更新文章
     */
    ArticleDTO updateArticle(String id, ArticleDTO articleDTO);
    
    /**
     * 删除文章
     */
    void deleteArticle(String id);
    
    /**
     * 批量删除文章
     */
    Map<String, Object> batchDeleteArticles(BatchDeleteDTO batchDeleteDTO);
    
    /**
     * 获取所有分类
     */
    List<String> getAllCategories();
    
    /**
     * 获取文章统计信息
     */
    Map<String, Object> getArticleStats();
    
    /**
     * 获取最近文章
     */
    List<ArticleDTO> getRecentArticles();
}