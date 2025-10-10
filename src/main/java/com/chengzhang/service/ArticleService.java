package com.chengzhang.service;

import com.chengzhang.dto.ArticleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
     * 获取文章列表（分页）
     *
     * @param pageable   分页参数
     * @param keyword    搜索关键词
     * @param category   文章分类
     * @param status     文章状态
     * @param collection 合集ID
     * @param sortBy     排序字段
     * @param sortOrder  排序方向
     * @return 文章分页列表
     */
    Page<ArticleDTO> getArticles(Pageable pageable, String keyword, String category, String status, String collection, String sortBy, String sortOrder);

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleDTO getArticleById(String id);

    /**
     * 创建文章
     *
     * @param articleDTO 文章数据
     * @return 创建的文章
     */
    ArticleDTO createArticle(ArticleDTO articleDTO);

    /**
     * 更新文章
     *
     * @param id         文章ID
     * @param articleDTO 更新的文章数据
     * @return 更新后的文章
     */
    ArticleDTO updateArticle(String id, ArticleDTO articleDTO);

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    void deleteArticle(String id);

    /**
     * 批量删除文章
     *
     * @param ids 文章ID列表
     * @return 删除结果
     */
    Map<String, Object> batchDeleteArticles(List<String> ids);

    /**
     * 搜索文章
     *
     * @param keyword       搜索关键词
     * @param searchIn      搜索范围：title/content/all
     * @param caseSensitive 是否区分大小写
     * @param wholeWord     是否全词匹配
     * @param status        文章状态筛选
     * @param tags          标签筛选
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @param pageable      分页参数
     * @return 搜索结果
     */
    Map<String, Object> searchArticles(String keyword, String searchIn, Boolean caseSensitive, 
                                      Boolean wholeWord, String status, List<String> tags, 
                                      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 导出所有文章数据
     *
     * @return 导出数据
     */
    Map<String, Object> exportAllArticles();

    /**
     * 导入文章数据
     *
     * @param articles 文章列表
     * @param merge    是否合并模式
     * @return 导入结果
     */
    Map<String, Object> importArticles(List<ArticleDTO> articles, Boolean merge);

    /**
     * 清空所有文章数据
     */
    void clearAllArticles();

    /**
     * 获取文章统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getArticleStatistics();

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    List<String> getAllCategories();

    /**
     * 自动生成文章摘要
     *
     * @param content   文章内容
     * @param maxLength 最大长度
     * @return 文章摘要
     */
    String generateSummary(String content, Integer maxLength);

    /**
     * 计算文章字数
     *
     * @param content 文章内容
     * @return 字数
     */
    Integer countWords(String content);

    /**
     * 计算预计阅读时间
     *
     * @param content 文章内容
     * @return 阅读时间（分钟）
     */
    Integer calculateReadTime(String content);
}