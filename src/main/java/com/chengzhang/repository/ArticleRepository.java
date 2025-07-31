package com.chengzhang.repository;

import com.chengzhang.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章Repository接口
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    
    /**
     * 根据标题或内容搜索文章
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.content LIKE %:keyword%")
    Page<Article> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据分类查询文章
     */
    Page<Article> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据状态查询文章
     */
    Page<Article> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据分类和状态查询文章
     */
    Page<Article> findByCategoryAndStatus(String category, String status, Pageable pageable);
    
    /**
     * 根据关键词、分类和状态查询文章
     */
    @Query("SELECT a FROM Article a WHERE " +
           "(:keyword IS NULL OR a.title LIKE %:keyword% OR a.content LIKE %:keyword%) AND " +
           "(:category IS NULL OR a.category = :category) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Article> findByConditions(@Param("keyword") String keyword,
                                   @Param("category") String category,
                                   @Param("status") String status,
                                   Pageable pageable);
    
    /**
     * 获取所有分类
     */
    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.category IS NOT NULL")
    List<String> findAllCategories();
    
    /**
     * 统计文章总数
     */
    long count();
    
    /**
     * 根据状态统计文章数量
     */
    long countByStatus(String status);
    
    /**
     * 根据分类统计文章数量
     */
    long countByCategory(String category);
    
    /**
     * 获取最近的文章
     */
    List<Article> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根据用户ID查询文章（预留）
     */
    Page<Article> findByUserId(String userId, Pageable pageable);
}