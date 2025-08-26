package com.chengzhang.repository;

import com.chengzhang.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片数据访问层
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

    /**
     * 根据状态查询图片
     *
     * @param status   图片状态
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByStatus(String status, Pageable pageable);

    /**
     * 根据文章ID查询图片
     *
     * @param articleId 文章ID
     * @param pageable  分页参数
     * @return 图片分页列表
     */
    Page<Image> findByArticleId(String articleId, Pageable pageable);

    /**
     * 根据文章ID和状态查询图片
     *
     * @param articleId 文章ID
     * @param status    图片状态
     * @param pageable  分页参数
     * @return 图片分页列表
     */
    Page<Image> findByArticleIdAndStatus(String articleId, String status, Pageable pageable);

    /**
     * 根据文章ID查询图片列表（不分页）
     *
     * @param articleId 文章ID
     * @return 图片列表
     */
    List<Image> findByArticleId(String articleId);

    /**
     * 根据文章ID和状态查询图片列表（不分页）
     *
     * @param articleId 文章ID
     * @param status    图片状态
     * @return 图片列表
     */
    List<Image> findByArticleIdAndStatus(String articleId, String status);

    /**
     * 根据文件名查询图片
     *
     * @param fileName 文件名
     * @return 图片对象
     */
    Image findByFileName(String fileName);

    /**
     * 根据文件路径查询图片
     *
     * @param filePath 文件路径
     * @return 图片对象
     */
    Image findByFilePath(String filePath);

    /**
     * 根据MIME类型查询图片
     *
     * @param mimeType MIME类型
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByMimeType(String mimeType, Pageable pageable);

    /**
     * 根据MIME类型模糊查询图片
     *
     * @param mimeTypePattern MIME类型模式（如 image/%）
     * @param pageable        分页参数
     * @return 图片分页列表
     */
    @Query("SELECT i FROM Image i WHERE i.mimeType LIKE :mimeTypePattern")
    Page<Image> findByMimeTypeLike(@Param("mimeTypePattern") String mimeTypePattern, Pageable pageable);

    /**
     * 根据原始文件名搜索图片
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    @Query("SELECT i FROM Image i WHERE i.originalName LIKE %:keyword%")
    Page<Image> findByOriginalNameContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据描述搜索图片
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    @Query("SELECT i FROM Image i WHERE i.description LIKE %:keyword%")
    Page<Image> findByDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据标签搜索图片
     *
     * @param tag      标签
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    @Query("SELECT i FROM Image i WHERE i.tags LIKE %:tag%")
    Page<Image> findByTagsContaining(@Param("tag") String tag, Pageable pageable);

    /**
     * 综合搜索图片（原始文件名、描述、标签）
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    @Query("SELECT i FROM Image i WHERE i.originalName LIKE %:keyword% OR i.description LIKE %:keyword% OR i.tags LIKE %:keyword%")
    Page<Image> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据关键词和状态搜索图片
     *
     * @param keyword  搜索关键词
     * @param status   图片状态
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    @Query("SELECT i FROM Image i WHERE (i.originalName LIKE %:keyword% OR i.description LIKE %:keyword% OR i.tags LIKE %:keyword%) AND i.status = :status")
    Page<Image> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    /**
     * 根据MIME类型和状态查询图片
     *
     * @param mimeType MIME类型
     * @param status   图片状态
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByMimeTypeAndStatus(String mimeType, String status, Pageable pageable);

    /**
     * 根据文件大小范围查询图片
     *
     * @param minSize  最小文件大小
     * @param maxSize  最大文件大小
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByFileSizeBetween(Long minSize, Long maxSize, Pageable pageable);

    /**
     * 根据图片尺寸范围查询图片
     *
     * @param minWidth  最小宽度
     * @param maxWidth  最大宽度
     * @param minHeight 最小高度
     * @param maxHeight 最大高度
     * @param pageable  分页参数
     * @return 图片分页列表
     */
    Page<Image> findByWidthBetweenAndHeightBetween(Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight, Pageable pageable);

    /**
     * 根据日期范围查询图片
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param pageable  分页参数
     * @return 图片分页列表
     */
    Page<Image> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 根据上传IP查询图片
     *
     * @param uploadIp 上传IP
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByUploadIp(String uploadIp, Pageable pageable);

    /**
     * 统计图片总数
     *
     * @return 图片总数
     */
    long count();

    /**
     * 根据状态统计图片数量
     *
     * @param status 图片状态
     * @return 图片数量
     */
    long countByStatus(String status);

    /**
     * 根据文章ID统计图片数量
     *
     * @param articleId 文章ID
     * @return 图片数量
     */
    long countByArticleId(String articleId);

    /**
     * 根据文章ID和状态统计图片数量
     *
     * @param articleId 文章ID
     * @param status    图片状态
     * @return 图片数量
     */
    long countByArticleIdAndStatus(String articleId, String status);

    /**
     * 统计总文件大小
     *
     * @return 总文件大小（字节）
     */
    @Query("SELECT COALESCE(SUM(i.fileSize), 0) FROM Image i")
    Long sumFileSize();

    /**
     * 根据状态统计总文件大小
     *
     * @param status 图片状态
     * @return 总文件大小（字节）
     */
    @Query("SELECT COALESCE(SUM(i.fileSize), 0) FROM Image i WHERE i.status = :status")
    Long sumFileSizeByStatus(@Param("status") String status);

    /**
     * 获取所有MIME类型
     *
     * @return MIME类型列表
     */
    @Query("SELECT DISTINCT i.mimeType FROM Image i WHERE i.mimeType IS NOT NULL ORDER BY i.mimeType")
    List<String> findAllMimeTypes();

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    @Query("SELECT DISTINCT i.tags FROM Image i WHERE i.tags IS NOT NULL AND i.tags != '' ORDER BY i.tags")
    List<String> findAllTags();

    /**
     * 根据创建时间倒序查询最近的图片
     *
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 根据更新时间倒序查询最近的图片
     *
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    /**
     * 根据文件大小倒序查询图片
     *
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findAllByOrderByFileSizeDesc(Pageable pageable);

    /**
     * 根据原始文件名排序查询图片
     *
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findAllByOrderByOriginalNameAsc(Pageable pageable);

    /**
     * 查找孤立的图片（没有关联文章的图片）
     *
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByArticleIdIsNull(Pageable pageable);

    /**
     * 查找孤立的图片（没有关联文章的图片）且状态为指定状态
     *
     * @param status   图片状态
     * @param pageable 分页参数
     * @return 图片分页列表
     */
    Page<Image> findByArticleIdIsNullAndStatus(String status, Pageable pageable);

    /**
     * 删除指定日期之前的图片（物理删除）
     *
     * @param beforeDate 指定日期
     */
    void deleteByCreatedAtBefore(LocalDateTime beforeDate);

    /**
     * 删除指定状态和日期之前的图片（物理删除）
     *
     * @param status     图片状态
     * @param beforeDate 指定日期
     */
    void deleteByStatusAndCreatedAtBefore(String status, LocalDateTime beforeDate);
}