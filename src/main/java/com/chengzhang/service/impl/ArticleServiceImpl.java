package com.chengzhang.service.impl;

import com.chengzhang.common.PageResult;
import com.chengzhang.dto.ArticleDTO;
import com.chengzhang.dto.ArticleQueryDTO;
import com.chengzhang.dto.BatchDeleteDTO;
import com.chengzhang.entity.Article;
import com.chengzhang.repository.ArticleRepository;
import com.chengzhang.service.ArticleService;
import com.chengzhang.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    
    private final ArticleRepository articleRepository;
    
    @Override
    public PageResult<ArticleDTO> getArticles(ArticleQueryDTO queryDTO) {
        // 构建分页和排序
        Sort sort = buildSort(queryDTO.getSortBy(), queryDTO.getSortOrder());
        Pageable pageable = PageRequest.of(queryDTO.getPage() - 1, queryDTO.getSize(), sort);
        
        // 执行查询
        Page<Article> articlePage = articleRepository.findByConditions(
            queryDTO.getKeyword(),
            queryDTO.getCategory(),
            queryDTO.getStatus(),
            pageable
        );
        
        // 转换为DTO
        List<ArticleDTO> articleDTOs = articlePage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return PageResult.of(articlePage.map(this::convertToDTO));
    }
    
    @Override
    public ArticleDTO getArticleById(String id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文章不存在"));
        return convertToDTO(article);
    }
    
    @Override
    @Transactional
    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        Article article = convertToEntity(articleDTO);
        article.setId("article_" + System.currentTimeMillis());
        article = articleRepository.save(article);
        log.info("创建文章成功，ID: {}", article.getId());
        return convertToDTO(article);
    }
    
    @Override
    @Transactional
    public ArticleDTO updateArticle(String id, ArticleDTO articleDTO) {
        Article existingArticle = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // 更新字段
        existingArticle.setTitle(articleDTO.getTitle());
        existingArticle.setContent(articleDTO.getContent());
        existingArticle.setSummary(articleDTO.getSummary());
        existingArticle.setCategory(articleDTO.getCategory());
        existingArticle.setStatus(articleDTO.getStatus());
        
        // 处理标签和图片
        if (articleDTO.getTags() != null) {
            existingArticle.setTags(JsonUtil.toJson(articleDTO.getTags()));
        }
        if (articleDTO.getImages() != null) {
            existingArticle.setImages(JsonUtil.toJson(articleDTO.getImages()));
        }
        
        existingArticle = articleRepository.save(existingArticle);
        log.info("更新文章成功，ID: {}", id);
        return convertToDTO(existingArticle);
    }
    
    @Override
    @Transactional
    public void deleteArticle(String id) {
        if (!articleRepository.existsById(id)) {
            throw new RuntimeException("文章不存在");
        }
        articleRepository.deleteById(id);
        log.info("删除文章成功，ID: {}", id);
    }
    
    @Override
    @Transactional
    public Map<String, Object> batchDeleteArticles(BatchDeleteDTO batchDeleteDTO) {
        List<String> ids = batchDeleteDTO.getIds();
        List<String> existingIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        
        for (String id : ids) {
            try {
                if (articleRepository.existsById(id)) {
                    articleRepository.deleteById(id);
                    existingIds.add(id);
                } else {
                    failedIds.add(id);
                }
            } catch (Exception e) {
                log.error("删除文章失败，ID: {}, 错误: {}", id, e.getMessage());
                failedIds.add(id);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", existingIds.size());
        result.put("failedIds", failedIds);
        
        log.info("批量删除文章完成，成功: {}, 失败: {}", existingIds.size(), failedIds.size());
        return result;
    }
    
    @Override
    public List<String> getAllCategories() {
        return articleRepository.findAllCategories();
    }
    
    @Override
    public Map<String, Object> getArticleStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalArticles", articleRepository.count());
        stats.put("publishedArticles", articleRepository.countByStatus("published"));
        stats.put("draftArticles", articleRepository.countByStatus("draft"));
        
        // 分类统计
        List<String> categories = getAllCategories();
        List<Map<String, Object>> categoryStats = categories.stream()
            .map(category -> {
                Map<String, Object> stat = new HashMap<>();
                stat.put("category", category);
                stat.put("count", articleRepository.countByCategory(category));
                return stat;
            })
            .collect(Collectors.toList());
        stats.put("categoriesStats", categoryStats);
        
        return stats;
    }
    
    @Override
    public List<ArticleDTO> getRecentArticles() {
        List<Article> recentArticles = articleRepository.findTop10ByOrderByCreatedAtDesc();
        return recentArticles.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 构建排序
     */
    private Sort buildSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        String property;
        switch (sortBy) {
            case "title":
                property = "title";
                break;
            case "updatedAt":
                property = "updatedAt";
                break;
            case "createdAt":
            default:
                property = "createdAt";
                break;
        }
        
        return Sort.by(direction, property);
    }
    
    /**
     * 实体转DTO
     */
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setCategory(article.getCategory());
        dto.setStatus(article.getStatus());
        dto.setWordCount(article.getWordCount());
        dto.setReadTime(article.getReadTime());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        
        // 处理JSON字段
        if (StringUtils.hasText(article.getTags())) {
            dto.setTags(JsonUtil.fromJson(article.getTags(), List.class));
        }
        if (StringUtils.hasText(article.getImages())) {
            dto.setImages(JsonUtil.fromJson(article.getImages(), List.class));
        }
        
        return dto;
    }
    
    /**
     * DTO转实体
     */
    private Article convertToEntity(ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSummary(dto.getSummary());
        article.setCategory(dto.getCategory());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : "draft");
        
        // 处理JSON字段
        if (dto.getTags() != null) {
            article.setTags(JsonUtil.toJson(dto.getTags()));
        }
        if (dto.getImages() != null) {
            article.setImages(JsonUtil.toJson(dto.getImages()));
        }
        
        return article;
    }
}