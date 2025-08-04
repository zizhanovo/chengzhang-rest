package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.common.PageResponse;
import com.chengzhang.dto.ArticleDTO;
import com.chengzhang.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文章控制器
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"}, allowCredentials = "true")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 获取文章列表
     *
     * @param page      页码，默认1
     * @param size      每页数量，默认10
     * @param keyword   搜索关键词
     * @param category  文章分类
     * @param status    文章状态：draft/published
     * @param sortBy    排序字段：createdAt/updatedAt/title
     * @param sortOrder 排序方向：asc/desc
     * @return 文章分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<ArticleDTO>> getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        log.info("获取文章列表 - page: {}, size: {}, keyword: {}, category: {}, status: {}", 
                page, size, keyword, category, status);
        
        try {
            // 页码从1开始，转换为从0开始
            Pageable pageable = PageRequest.of(page - 1, Math.min(size, 100));
            
            Page<ArticleDTO> articlePage = articleService.getArticles(
                    pageable, keyword, category, status, sortBy, sortOrder);
            
            PageResponse<ArticleDTO> pageResponse = PageResponse.of(articlePage);
            
            return ApiResponse.success(pageResponse);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return ApiResponse.error("获取文章列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ArticleDTO> getArticleById(@PathVariable String id) {
        log.info("获取文章详情 - id: {}", id);
        
        try {
            ArticleDTO article = articleService.getArticleById(id);
            return ApiResponse.success(article);
        } catch (RuntimeException e) {
            log.error("获取文章详情失败 - id: {}", id, e);
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取文章详情失败 - id: {}", id, e);
            return ApiResponse.error("获取文章详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建文章
     *
     * @param articleDTO 文章数据
     * @return 创建的文章
     */
    @PostMapping
    public ApiResponse<ArticleDTO> createArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        log.info("创建文章 - title: {}", articleDTO.getTitle());
        
        try {
            ArticleDTO createdArticle = articleService.createArticle(articleDTO);
            return ApiResponse.created("文章创建成功", createdArticle);
        } catch (Exception e) {
            log.error("创建文章失败 - title: {}", articleDTO.getTitle(), e);
            return ApiResponse.error("创建文章失败: " + e.getMessage());
        }
    }

    /**
     * 更新文章
     *
     * @param id         文章ID
     * @param articleDTO 更新的文章数据
     * @return 更新后的文章
     */
    @PutMapping("/{id}")
    public ApiResponse<ArticleDTO> updateArticle(@PathVariable String id, 
                                                @Valid @RequestBody ArticleDTO articleDTO) {
        log.info("更新文章 - id: {}, title: {}", id, articleDTO.getTitle());
        
        try {
            ArticleDTO updatedArticle = articleService.updateArticle(id, articleDTO);
            return ApiResponse.success("文章更新成功", updatedArticle);
        } catch (RuntimeException e) {
            log.error("更新文章失败 - id: {}", id, e);
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("更新文章失败 - id: {}", id, e);
            return ApiResponse.error("更新文章失败: " + e.getMessage());
        }
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable String id) {
        log.info("删除文章 - id: {}", id);
        
        try {
            articleService.deleteArticle(id);
            return ApiResponse.success("文章删除成功", null);
        } catch (RuntimeException e) {
            log.error("删除文章失败 - id: {}", id, e);
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("删除文章失败 - id: {}", id, e);
            return ApiResponse.error("删除文章失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除文章
     *
     * @param request 批量删除请求
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ApiResponse<Map<String, Object>> batchDeleteArticles(
            @Valid @RequestBody BatchDeleteRequest request) {
        log.info("批量删除文章 - ids: {}", request.getIds());
        
        try {
            Map<String, Object> result = articleService.batchDeleteArticles(request.getIds());
            return ApiResponse.success("批量删除成功", result);
        } catch (Exception e) {
            log.error("批量删除文章失败", e);
            return ApiResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 搜索文章
     *
     * @param keyword       搜索关键词
     * @param searchIn      搜索范围：title/content/all，默认all
     * @param caseSensitive 是否区分大小写，默认false
     * @param wholeWord     是否全词匹配，默认false
     * @param status        文章状态筛选：draft/published
     * @param tags          标签筛选
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @param page          页码，默认1
     * @param size          每页数量，默认10
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String searchIn,
            @RequestParam(defaultValue = "false") Boolean caseSensitive,
            @RequestParam(defaultValue = "false") Boolean wholeWord,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("搜索文章 - keyword: {}, searchIn: {}, status: {}", keyword, searchIn, status);
        
        try {
            Pageable pageable = PageRequest.of(page - 1, Math.min(size, 100));
            
            Map<String, Object> result = articleService.searchArticles(
                    keyword, searchIn, caseSensitive, wholeWord, status, tags, 
                    startDate, endDate, pageable);
            
            return ApiResponse.success("搜索成功", result);
        } catch (Exception e) {
            log.error("搜索文章失败", e);
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 导出所有文章数据
     *
     * @return 导出数据
     */
    @GetMapping("/export")
    public ApiResponse<Map<String, Object>> exportAllArticles() {
        log.info("导出所有文章数据");
        
        try {
            Map<String, Object> exportData = articleService.exportAllArticles();
            return ApiResponse.success(exportData);
        } catch (Exception e) {
            log.error("导出文章数据失败", e);
            return ApiResponse.error("导出失败: " + e.getMessage());
        }
    }

    /**
     * 导入文章数据
     *
     * @param request 导入请求
     * @return 导入结果
     */
    @PostMapping("/import")
    public ApiResponse<Map<String, Object>> importArticles(
            @Valid @RequestBody ImportRequest request) {
        log.info("导入文章数据 - 数量: {}, 合并模式: {}", 
                request.getArticles().size(), request.getMerge());
        
        try {
            Map<String, Object> result = articleService.importArticles(
                    request.getArticles(), request.getMerge());
            return ApiResponse.success("导入成功", result);
        } catch (Exception e) {
            log.error("导入文章数据失败", e);
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 清空所有文章数据
     *
     * @return 清空结果
     */
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearAllArticles() {
        log.info("清空所有文章数据");
        
        try {
            articleService.clearAllArticles();
            return ApiResponse.success("数据清空成功", null);
        } catch (Exception e) {
            log.error("清空文章数据失败", e);
            return ApiResponse.error("清空失败: " + e.getMessage());
        }
    }

    /**
     * 获取文章统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getArticleStatistics() {
        log.info("获取文章统计信息");
        
        try {
            Map<String, Object> statistics = articleService.getArticleStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取文章统计信息失败", e);
            return ApiResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<String>> getAllCategories() {
        log.info("获取所有分类");
        
        try {
            List<String> categories = articleService.getAllCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ApiResponse.error("获取分类失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除请求
     */
    public static class BatchDeleteRequest {
        @NotEmpty(message = "文章ID列表不能为空")
        private List<String> ids;

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }

    /**
     * 导入请求
     */
    public static class ImportRequest {
        @NotEmpty(message = "文章列表不能为空")
        private List<ArticleDTO> articles;
        
        private Boolean merge = false;

        public List<ArticleDTO> getArticles() {
            return articles;
        }

        public void setArticles(List<ArticleDTO> articles) {
            this.articles = articles;
        }

        public Boolean getMerge() {
            return merge;
        }

        public void setMerge(Boolean merge) {
            this.merge = merge;
        }
    }
}