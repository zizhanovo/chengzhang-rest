package com.chengzhang.service;

import com.chengzhang.dto.ImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 图片服务接口
 *
 * @author chengzhang
 * @since 1.0.0
 */
public interface ImageService {

    /**
     * 上传单个图片
     *
     * @param file      图片文件
     * @param articleId 关联的文章ID（可选）
     * @param uploadIp  上传者IP地址
     * @return 上传结果
     */
    ImageDTO uploadImage(MultipartFile file, String articleId, String uploadIp);

    /**
     * 批量上传图片
     *
     * @param files     图片文件列表
     * @param articleId 关联的文章ID（可选）
     * @param uploadIp  上传者IP地址
     * @return 批量上传结果
     */
    Map<String, Object> batchUploadImages(List<MultipartFile> files, String articleId, String uploadIp);

    /**
     * Base64上传图片
     *
     * @param base64Data Base64编码的图片数据
     * @param fileName   文件名
     * @param articleId  关联的文章ID（可选）
     * @param uploadIp   上传者IP地址
     * @return 上传结果
     */
    ImageDTO uploadImageFromBase64(String base64Data, String fileName, String articleId, String uploadIp);

    /**
     * 获取图片列表（分页）
     *
     * @param pageable  分页参数
     * @param keyword   搜索关键词
     * @param articleId 文章ID筛选
     * @param mimeType  文件类型筛选
     * @param status    图片状态筛选
     * @param sortBy    排序字段
     * @param sortOrder 排序方向
     * @return 图片分页列表
     */
    Page<ImageDTO> getImages(Pageable pageable, String keyword, String articleId, String mimeType, String status, String sortBy, String sortOrder);

    /**
     * 根据ID获取图片详情
     *
     * @param id 图片ID
     * @return 图片详情
     */
    ImageDTO getImageById(String id);

    /**
     * 根据文件名获取图片
     *
     * @param fileName 文件名
     * @return 图片详情
     */
    ImageDTO getImageByFileName(String fileName);

    /**
     * 根据文件路径获取图片
     *
     * @param filePath 文件路径
     * @return 图片详情
     */
    ImageDTO getImageByFilePath(String filePath);

    /**
     * 根据文章ID获取图片列表
     *
     * @param articleId 文章ID
     * @param status    图片状态（可选）
     * @return 图片列表
     */
    List<ImageDTO> getImagesByArticleId(String articleId, String status);

    /**
     * 更新图片信息
     *
     * @param id       图片ID
     * @param imageDTO 更新的图片数据
     * @return 更新后的图片
     */
    ImageDTO updateImage(String id, ImageDTO imageDTO);

    /**
     * 删除图片（逻辑删除）
     *
     * @param id 图片ID
     */
    void deleteImage(String id);

    /**
     * 物理删除图片（删除文件和数据库记录）
     *
     * @param id 图片ID
     */
    void physicalDeleteImage(String id);

    /**
     * 批量删除图片
     *
     * @param ids      图片ID列表
     * @param physical 是否物理删除
     * @return 删除结果
     */
    Map<String, Object> batchDeleteImages(List<String> ids, Boolean physical);

    /**
     * 搜索图片
     *
     * @param keyword       搜索关键词
     * @param searchIn      搜索范围：name/description/tags/all
     * @param caseSensitive 是否区分大小写
     * @param mimeType      文件类型筛选
     * @param minSize       最小文件大小
     * @param maxSize       最大文件大小
     * @param minWidth      最小宽度
     * @param maxWidth      最大宽度
     * @param minHeight     最小高度
     * @param maxHeight     最大高度
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @param pageable      分页参数
     * @return 搜索结果
     */
    Map<String, Object> searchImages(String keyword, String searchIn, Boolean caseSensitive,
                                    String mimeType, Long minSize, Long maxSize,
                                    Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight,
                                    LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 获取图片统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getImageStatistics();

    /**
     * 获取所有MIME类型
     *
     * @return MIME类型列表
     */
    List<String> getAllMimeTypes();

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<String> getAllTags();

    /**
     * 生成图片预览URL
     *
     * @param filePath 文件路径
     * @return 预览URL
     */
    String generatePreviewUrl(String filePath);

    /**
     * 生成图片缩略图
     *
     * @param imageId 图片ID
     * @param width   缩略图宽度
     * @param height  缩略图高度
     * @return 缩略图路径
     */
    String generateThumbnail(String imageId, Integer width, Integer height);

    /**
     * 压缩图片
     *
     * @param imageId 图片ID
     * @param quality 压缩质量（0-100）
     * @return 压缩后的图片路径
     */
    String compressImage(String imageId, Integer quality);

    /**
     * 清理孤立的图片（没有关联文章的图片）
     *
     * @param beforeDate 指定日期之前的图片
     * @param physical   是否物理删除
     * @return 清理结果
     */
    Map<String, Object> cleanOrphanImages(LocalDateTime beforeDate, Boolean physical);

    /**
     * 清理已删除状态的图片
     *
     * @param beforeDate 指定日期之前的图片
     * @return 清理结果
     */
    Map<String, Object> cleanDeletedImages(LocalDateTime beforeDate);

    /**
     * 验证图片文件
     *
     * @param file 图片文件
     * @return 验证结果
     */
    Map<String, Object> validateImageFile(MultipartFile file);

    /**
     * 获取图片文件信息
     *
     * @param file 图片文件
     * @return 文件信息
     */
    Map<String, Object> getImageFileInfo(MultipartFile file);

    /**
     * 检查存储空间使用情况
     *
     * @return 存储空间信息
     */
    Map<String, Object> getStorageInfo();

    /**
     * 修复图片数据（重新计算文件大小、尺寸等）
     *
     * @param imageId 图片ID（为空则修复所有图片）
     * @return 修复结果
     */
    Map<String, Object> repairImageData(String imageId);

    /**
     * 同步图片文件和数据库记录
     *
     * @return 同步结果
     */
    Map<String, Object> syncImageFiles();
}