package com.chengzhang.repository;

import com.chengzhang.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章数据访问层
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    /**
     * 根据状态查询文章
     *
     * @param status   文章状态
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findByStatus(String status, Pageable pageable);

    /**
     * 根据分类查询文章
     *
     * @param category 文章分类
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findByCategory(String category, Pageable pageable);

    /**
     * 根据状态和分类查询文章
     *
     * @param status   文章状态
     * @param category 文章分类
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findByStatusAndCategory(String status, String category, Pageable pageable);

    /**
     * 根据关键词搜索文章（标题和内容）
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.content LIKE %:keyword% OR a.summary LIKE %:keyword%")
    Page<Article> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据关键词和状态搜索文章
     *
     * @param keyword  搜索关键词
     * @param status   文章状态
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE (a.title LIKE %:keyword% OR a.content LIKE %:keyword% OR a.summary LIKE %:keyword%) AND a.status = :status")
    Page<Article> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    /**
     * 根据标题搜索文章
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword%")
    Page<Article> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据内容搜索文章
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.content LIKE %:keyword%")
    Page<Article> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据标签搜索文章
     *
     * @param tag      标签
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.tags LIKE %:tag%")
    Page<Article> findByTagsContaining(@Param("tag") String tag, Pageable pageable);

    /**
     * 根据日期范围查询文章
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param pageable  分页参数
     * @return 文章分页列表
     */
    Page<Article> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 统计文章总数
     *
     * @return 文章总数
     */
    long count();

    /**
     * 根据状态统计文章数量
     *
     * @param status 文章状态
     * @return 文章数量
     */
    long countByStatus(String status);

    /**
     * 统计总字数
     *
     * @return 总字数
     */
    @Query("SELECT COALESCE(SUM(a.wordCount), 0) FROM Article a")
    Long sumWordCount();

    /**
     * 统计总阅读时间
     *
     * @return 总阅读时间
     */
    @Query("SELECT COALESCE(SUM(a.readTime), 0) FROM Article a")
    Long sumReadTime();

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.category IS NOT NULL ORDER BY a.category")
    List<String> findAllCategories();

    /**
     * 根据更新时间倒序查询最近的文章
     *
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    /**
     * 根据创建时间倒序查询最近的文章
     *
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 根据标题排序查询文章
     *
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findAllByOrderByTitleAsc(Pageable pageable);
}