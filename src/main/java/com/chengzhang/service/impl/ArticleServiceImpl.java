package com.chengzhang.service.impl;

import com.chengzhang.dto.ArticleDTO;
import com.chengzhang.entity.Article;
import com.chengzhang.repository.ArticleRepository;
import com.chengzhang.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    // 中文字符正则表达式
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
    // Markdown语法正则表达式
    private static final Pattern MARKDOWN_PATTERN = Pattern.compile("[#*`_~\\[\\]()!-]");
    // 平均阅读速度（字/分钟）
    private static final int READING_SPEED = 200;

    @Override
    public Page<ArticleDTO> getArticles(Pageable pageable, String keyword, String category, String status, String sortBy, String sortOrder) {
        log.debug("获取文章列表 - keyword: {}, category: {}, status: {}, sortBy: {}, sortOrder: {}", 
                keyword, category, status, sortBy, sortOrder);

        // 构建排序
        Sort sort = buildSort(sortBy, sortOrder);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Article> articlePage;

        // 根据条件查询
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
                articlePage = articleRepository.findByKeywordAndStatus(keyword, status, sortedPageable);
            } else {
                articlePage = articleRepository.findByKeyword(keyword, sortedPageable);
            }
        } else if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
            if (StringUtils.isNotBlank(category)) {
                articlePage = articleRepository.findByStatusAndCategory(status, category, sortedPageable);
            } else {
                articlePage = articleRepository.findByStatus(status, sortedPageable);
            }
        } else if (StringUtils.isNotBlank(category)) {
            articlePage = articleRepository.findByCategory(category, sortedPageable);
        } else {
            articlePage = articleRepository.findAll(sortedPageable);
        }

        return articlePage.map(ArticleDTO::fromEntity);
    }

    @Override
    public ArticleDTO getArticleById(String id) {
        log.debug("获取文章详情 - id: {}", id);
        
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在: " + id));
        
        return ArticleDTO.fromEntity(article);
    }

    @Override
    @Transactional
    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        log.debug("创建文章 - title: {}", articleDTO.getTitle());
        
        Article article = articleDTO.toEntity();
        
        // 自动生成摘要和统计信息
        if (StringUtils.isBlank(article.getSummary()) && StringUtils.isNotBlank(article.getContent())) {
            article.setSummary(generateSummary(article.getContent(), 200));
        }
        
        if (StringUtils.isNotBlank(article.getContent())) {
            article.setWordCount(countWords(article.getContent()));
            article.setReadTime(calculateReadTime(article.getContent()));
        }
        
        Article savedArticle = articleRepository.save(article);
        log.info("文章创建成功 - id: {}, title: {}", savedArticle.getId(), savedArticle.getTitle());
        
        return ArticleDTO.fromEntity(savedArticle);
    }

    @Override
    @Transactional
    public ArticleDTO updateArticle(String id, ArticleDTO articleDTO) {
        log.debug("更新文章 - id: {}, title: {}", id, articleDTO.getTitle());
        
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在: " + id));
        
        // 更新字段
        articleDTO.updateEntity(existingArticle);
        
        // 重新计算摘要和统计信息
        if (StringUtils.isNotBlank(existingArticle.getContent())) {
            if (StringUtils.isBlank(existingArticle.getSummary())) {
                existingArticle.setSummary(generateSummary(existingArticle.getContent(), 200));
            }
            existingArticle.setWordCount(countWords(existingArticle.getContent()));
            existingArticle.setReadTime(calculateReadTime(existingArticle.getContent()));
        }
        
        Article updatedArticle = articleRepository.save(existingArticle);
        log.info("文章更新成功 - id: {}, title: {}", updatedArticle.getId(), updatedArticle.getTitle());
        
        return ArticleDTO.fromEntity(updatedArticle);
    }

    @Override
    @Transactional
    public void deleteArticle(String id) {
        log.debug("删除文章 - id: {}", id);
        
        if (!articleRepository.existsById(id)) {
            throw new RuntimeException("文章不存在: " + id);
        }
        
        articleRepository.deleteById(id);
        log.info("文章删除成功 - id: {}", id);
    }

    @Override
    @Transactional
    public Map<String, Object> batchDeleteArticles(List<String> ids) {
        log.debug("批量删除文章 - ids: {}", ids);
        
        List<String> existingIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        
        for (String id : ids) {
            if (articleRepository.existsById(id)) {
                existingIds.add(id);
            } else {
                failedIds.add(id);
            }
        }
        
        if (!existingIds.isEmpty()) {
            articleRepository.deleteAllById(existingIds);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", existingIds.size());
        result.put("failedIds", failedIds);
        
        log.info("批量删除文章完成 - 成功: {}, 失败: {}", existingIds.size(), failedIds.size());
        
        return result;
    }

    @Override
    public Map<String, Object> searchArticles(String keyword, String searchIn, Boolean caseSensitive, 
                                            Boolean wholeWord, String status, List<String> tags, 
                                            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("搜索文章 - keyword: {}, searchIn: {}, status: {}", keyword, searchIn, status);
        
        long startTime = System.currentTimeMillis();
        
        Page<Article> articlePage;
        
        // 根据搜索范围和条件查询
        if (StringUtils.isNotBlank(keyword)) {
            if ("title".equals(searchIn)) {
                articlePage = articleRepository.findByTitleContaining(keyword, pageable);
            } else if ("content".equals(searchIn)) {
                articlePage = articleRepository.findByContentContaining(keyword, pageable);
            } else {
                if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
                    articlePage = articleRepository.findByKeywordAndStatus(keyword, status, pageable);
                } else {
                    articlePage = articleRepository.findByKeyword(keyword, pageable);
                }
            }
        } else if (startDate != null && endDate != null) {
            articlePage = articleRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        } else {
            articlePage = articleRepository.findAll(pageable);
        }
        
        // 标签过滤（后处理）
        if (tags != null && !tags.isEmpty()) {
            List<Article> filteredArticles = articlePage.getContent().stream()
                    .filter(article -> {
                        List<String> articleTags = article.getTagList();
                        return tags.stream().anyMatch(articleTags::contains);
                    })
                    .collect(Collectors.toList());
            // 这里简化处理，实际应该重新构建Page对象
        }
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("articles", articlePage.getContent().stream()
                .map(ArticleDTO::fromEntity)
                .collect(Collectors.toList()));
        result.put("total", articlePage.getTotalElements());
        result.put("searchTime", searchTime);
        
        return result;
    }

    @Override
    public Map<String, Object> exportAllArticles() {
        log.debug("导出所有文章数据");
        
        List<Article> articles = articleRepository.findAll();
        List<ArticleDTO> articleDTOs = articles.stream()
                .map(ArticleDTO::fromEntity)
                .collect(Collectors.toList());
        
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("articles", articleDTOs);
        exportData.put("exportTime", LocalDateTime.now());
        exportData.put("version", "1.0.0");
        
        log.info("导出文章数据完成 - 数量: {}", articles.size());
        
        return exportData;
    }

    @Override
    @Transactional
    public Map<String, Object> importArticles(List<ArticleDTO> articles, Boolean merge) {
        log.debug("导入文章数据 - 数量: {}, 合并模式: {}", articles.size(), merge);
        
        if (!merge) {
            // 非合并模式，先清空现有数据
            articleRepository.deleteAll();
        }
        
        int imported = 0;
        int skipped = 0;
        
        for (ArticleDTO articleDTO : articles) {
            try {
                if (merge && StringUtils.isNotBlank(articleDTO.getId()) && 
                    articleRepository.existsById(articleDTO.getId())) {
                    skipped++;
                    continue;
                }
                
                Article article = articleDTO.toEntity();
                if (merge && StringUtils.isNotBlank(articleDTO.getId()) && 
                    articleRepository.existsById(articleDTO.getId())) {
                    // ID冲突，生成新ID
                    article.setId(null);
                }
                
                // 自动生成摘要和统计信息
                if (StringUtils.isBlank(article.getSummary()) && StringUtils.isNotBlank(article.getContent())) {
                    article.setSummary(generateSummary(article.getContent(), 200));
                }
                
                if (StringUtils.isNotBlank(article.getContent())) {
                    article.setWordCount(countWords(article.getContent()));
                    article.setReadTime(calculateReadTime(article.getContent()));
                }
                
                articleRepository.save(article);
                imported++;
            } catch (Exception e) {
                log.error("导入文章失败 - title: {}, error: {}", articleDTO.getTitle(), e.getMessage());
                skipped++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("imported", imported);
        result.put("skipped", skipped);
        result.put("total", articleRepository.count());
        
        log.info("导入文章数据完成 - 导入: {}, 跳过: {}", imported, skipped);
        
        return result;
    }

    @Override
    @Transactional
    public void clearAllArticles() {
        log.debug("清空所有文章数据");
        
        long count = articleRepository.count();
        articleRepository.deleteAll();
        
        log.info("清空文章数据完成 - 删除数量: {}", count);
    }

    @Override
    public Map<String, Object> getArticleStatistics() {
        log.debug("获取文章统计信息");
        
        long totalArticles = articleRepository.count();
        long publishedArticles = articleRepository.countByStatus("published");
        long draftArticles = articleRepository.countByStatus("draft");
        Long totalWords = articleRepository.sumWordCount();
        Long totalReadTime = articleRepository.sumReadTime();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalArticles", totalArticles);
        stats.put("publishedArticles", publishedArticles);
        stats.put("draftArticles", draftArticles);
        stats.put("totalWords", totalWords != null ? totalWords : 0L);
        stats.put("totalReadTime", totalReadTime != null ? totalReadTime : 0L);
        stats.put("lastUpdated", LocalDateTime.now());
        
        // 标签统计（简化实现）
        List<Article> articles = articleRepository.findAll();
        Map<String, Long> tagsStats = articles.stream()
                .flatMap(article -> article.getTagList().stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        
        List<Map<String, Object>> tagsStatsList = tagsStats.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> tagStat = new HashMap<>();
                    tagStat.put("tag", entry.getKey());
                    tagStat.put("count", entry.getValue());
                    return tagStat;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")))
                .collect(Collectors.toList());
        
        stats.put("tagsStats", tagsStatsList);
        
        return stats;
    }

    @Override
    public List<String> getAllCategories() {
        return articleRepository.findAllCategories();
    }

    @Override
    public String generateSummary(String content, Integer maxLength) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        
        // 移除Markdown语法
        String cleanContent = MARKDOWN_PATTERN.matcher(content).replaceAll("");
        cleanContent = cleanContent.replaceAll("\n+", " ").trim();
        
        if (cleanContent.length() <= maxLength) {
            return cleanContent;
        }
        
        return cleanContent.substring(0, maxLength) + "...";
    }

    @Override
    public Integer countWords(String content) {
        if (StringUtils.isBlank(content)) {
            return 0;
        }
        
        // 移除Markdown语法
        String cleanContent = MARKDOWN_PATTERN.matcher(content).replaceAll("");
        
        // 统计中文字符
        Matcher chineseMatcher = CHINESE_PATTERN.matcher(cleanContent);
        long chineseCount = 0;
        while (chineseMatcher.find()) {
            chineseCount++;
        }
        
        // 统计英文单词
        String[] words = cleanContent.replaceAll("[\u4e00-\u9fa5]", " ")
                .split("\\s+");
        long englishWords = Arrays.stream(words)
                .filter(word -> !word.trim().isEmpty())
                .count();
        
        return (int) (chineseCount + englishWords);
    }

    @Override
    public Integer calculateReadTime(String content) {
        Integer wordCount = countWords(content);
        return Math.max(1, (int) Math.ceil((double) wordCount / READING_SPEED));
    }

    /**
     * 构建排序
     */
    private Sort buildSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        String sortField;
        switch (StringUtils.defaultString(sortBy, "updatedAt")) {
            case "createdAt":
                sortField = "createdAt";
                break;
            case "title":
                sortField = "title";
                break;
            case "updatedAt":
            default:
                sortField = "updatedAt";
                break;
        }
        
        return Sort.by(direction, sortField);
    }
}