package com.chengzhang.controller;

import com.chengzhang.common.PageResult;
import com.chengzhang.common.Result;
import com.chengzhang.dto.ArticleDTO;
import com.chengzhang.dto.ArticleQueryDTO;
import com.chengzhang.dto.BatchDeleteDTO;
import com.chengzhang.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 文章控制器
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ArticleController {
    
    private final ArticleService articleService;
    
    /**
     * 获取文章列表
     */
    @GetMapping
    public Result<PageResult<ArticleDTO>> getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setKeyword(keyword);
        queryDTO.setCategory(category);
        queryDTO.setStatus(status);
        queryDTO.setSortBy(sortBy);
        queryDTO.setSortOrder(sortOrder);
        
        PageResult<ArticleDTO> result = articleService.getArticles(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleDTO> getArticle(@PathVariable String id) {
        try {
            ArticleDTO article = articleService.getArticleById(id);
            return Result.success(article);
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }
    
    /**
     * 创建文章
     */
    @PostMapping
    public Result<ArticleDTO> createArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        try {
            ArticleDTO createdArticle = articleService.createArticle(articleDTO);
            return Result.success("文章创建成功", createdArticle);
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return Result.error("创建文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public Result<ArticleDTO> updateArticle(@PathVariable String id, 
                                           @Valid @RequestBody ArticleDTO articleDTO) {
        try {
            ArticleDTO updatedArticle = articleService.updateArticle(id, articleDTO);
            return Result.success("文章更新成功", updatedArticle);
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("更新文章失败", e);
            return Result.error("更新文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable String id) {
        try {
            articleService.deleteArticle(id);
            return Result.success("文章删除成功");
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("删除文章失败", e);
            return Result.error("删除文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除文章
     */
    @DeleteMapping("/batch")
    public Result<Map<String, Object>> batchDeleteArticles(@Valid @RequestBody BatchDeleteDTO batchDeleteDTO) {
        try {
            Map<String, Object> result = articleService.batchDeleteArticles(batchDeleteDTO);
            return Result.success("批量删除成功", result);
        } catch (Exception e) {
            log.error("批量删除文章失败", e);
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        List<String> categories = articleService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 获取文章统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getArticleStats() {
        Map<String, Object> stats = articleService.getArticleStats();
        return Result.success(stats);
    }
    
    /**
     * 获取最近文章
     */
    @GetMapping("/recent")
    public Result<List<ArticleDTO>> getRecentArticles() {
        List<ArticleDTO> recentArticles = articleService.getRecentArticles();
        return Result.success(recentArticles);
    }
}