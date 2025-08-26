package com.chengzhang.dto;

import com.chengzhang.entity.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 图片数据传输对象
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class ImageDTO {

    /**
     * 图片ID
     */
    private String id;

    /**
     * 原始文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String originalName;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 图片宽度
     */
    private Integer width;

    /**
     * 图片高度
     */
    private Integer height;

    /**
     * 关联文章ID
     */
    private String articleId;

    /**
     * 图片标签
     */
    private List<String> tags;

    /**
     * 图片描述
     */
    @Size(max = 500, message = "图片描述长度不能超过500个字符")
    private String description;

    /**
     * 图片状态：active-正常，deleted-已删除
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为DTO
     *
     * @param image 图片实体
     * @return ImageDTO
     */
    public static ImageDTO fromEntity(Image image) {
        if (image == null) {
            return null;
        }
        
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setOriginalName(image.getOriginalName());
        dto.setFileName(image.getFileName());
        dto.setFilePath(image.getFilePath());
        // URL字段需要通过服务层生成
        dto.setFileSize(image.getFileSize());
        dto.setMimeType(image.getMimeType());
        dto.setWidth(image.getWidth());
        dto.setHeight(image.getHeight());
        dto.setArticleId(image.getArticleId());
        // 解析tags字符串为List
        if (image.getTags() != null && !image.getTags().isEmpty()) {
            dto.setTags(Arrays.asList(image.getTags().split(",")));
        }
        dto.setDescription(image.getDescription());
        dto.setStatus(image.getStatus());
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        
        return dto;
    }

    /**
     * 转换为实体
     *
     * @return Image
     */
    public Image toEntity() {
        Image image = new Image();
        image.setId(this.id);
        image.setOriginalName(this.originalName);
        image.setFileName(this.fileName);
        image.setFilePath(this.filePath);
        image.setFileSize(this.fileSize != null ? this.fileSize : 0L);
        image.setMimeType(this.mimeType);
        image.setWidth(this.width);
        image.setHeight(this.height);
        image.setArticleId(this.articleId);
        // 将tags List转换为逗号分隔的字符串
        if (this.tags != null && !this.tags.isEmpty()) {
            image.setTags(String.join(",", this.tags));
        }
        image.setDescription(this.description);
        image.setStatus(this.status != null ? this.status : "active");
        
        return image;
    }

    /**
     * 更新实体
     *
     * @param image 要更新的图片实体
     */
    public void updateEntity(Image image) {
        if (this.originalName != null) {
            image.setOriginalName(this.originalName);
        }
        if (this.fileName != null) {
            image.setFileName(this.fileName);
        }
        if (this.filePath != null) {
            image.setFilePath(this.filePath);
        }
        if (this.fileSize != null) {
            image.setFileSize(this.fileSize);
        }
        if (this.mimeType != null) {
            image.setMimeType(this.mimeType);
        }
        if (this.width != null) {
            image.setWidth(this.width);
        }
        if (this.height != null) {
            image.setHeight(this.height);
        }
        if (this.articleId != null) {
            image.setArticleId(this.articleId);
        }
        if (this.tags != null && !this.tags.isEmpty()) {
            image.setTags(String.join(",", this.tags));
        }
        if (this.description != null) {
            image.setDescription(this.description);
        }
        if (this.status != null) {
            image.setStatus(this.status);
        }
    }

    /**
     * 获取格式化的文件大小
     *
     * @return 格式化的文件大小
     */
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) {
            return "0B";
        }
        
        if (fileSize >= 1024 * 1024) {
            return String.format("%.2fMB", fileSize / (1024.0 * 1024.0));
        } else if (fileSize >= 1024) {
            return String.format("%.2fKB", fileSize / 1024.0);
        } else {
            return fileSize + "B";
        }
    }

    /**
     * 获取图片尺寸信息
     *
     * @return 图片尺寸字符串
     */
    public String getDimensions() {
        if (width != null && height != null) {
            return width + "x" + height;
        }
        return "未知";
    }

    /**
     * 检查是否为图片文件
     *
     * @return 是否为图片
     */
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
}