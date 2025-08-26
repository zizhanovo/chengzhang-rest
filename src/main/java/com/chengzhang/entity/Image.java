package com.chengzhang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 图片实体类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "images")
public class Image {

    /**
     * 图片ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = 32)
    private String id;

    /**
     * 原始文件名
     */
    @NotBlank(message = "原始文件名不能为空")
    @Size(max = 255, message = "原始文件名长度不能超过255个字符")
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    /**
     * 存储文件名（UUID生成）
     */
    @NotBlank(message = "存储文件名不能为空")
    @Size(max = 255, message = "存储文件名长度不能超过255个字符")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /**
     * 文件路径（相对路径）
     */
    @NotBlank(message = "文件路径不能为空")
    @Size(max = 500, message = "文件路径长度不能超过500个字符")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    @Size(max = 100, message = "文件类型长度不能超过100个字符")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * 图片宽度（像素）
     */
    @Column(name = "width")
    private Integer width;

    /**
     * 图片高度（像素）
     */
    @Column(name = "height")
    private Integer height;

    /**
     * 关联的文章ID
     */
    @Column(name = "article_id", length = 36)
    private String articleId;

    /**
     * 上传者IP地址
     */
    @Size(max = 45, message = "IP地址长度不能超过45个字符")
    @Column(name = "upload_ip", length = 45)
    private String uploadIp;

    /**
     * 图片状态：active-正常，deleted-已删除
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "active";

    /**
     * 图片描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 图片标签（用于分类和搜索）
     */
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建时间自动设置
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 更新时间自动设置
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 获取文件扩展名
     */
    @Transient
    public String getFileExtension() {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 获取格式化的文件大小
     */
    @Transient
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "0 B";
        }
        
        long size = fileSize;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", (double) size, units[unitIndex]);
    }

    /**
     * 检查是否为图片文件
     */
    @Transient
    public boolean isImageFile() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * 获取完整的访问URL（需要配合配置文件中的基础URL）
     */
    @Transient
    public String getAccessUrl(String baseUrl) {
        if (baseUrl == null || filePath == null) {
            return null;
        }
        return baseUrl + "/" + filePath.replace("\\", "/");
    }
}