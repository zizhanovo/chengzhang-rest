package com.chengzhang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 图片上传配置类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chengzhang.image")
public class ImageConfig {

    /**
     * 上传路径
     */
    private String uploadPath = "uploads/images/";

    /**
     * 访问路径前缀
     */
    private String accessPath = "/uploads/images/**";

    /**
     * 文件大小限制（字节）
     */
    private Long maxFileSize = 10485760L; // 10MB

    /**
     * 允许的MIME类型
     */
    private List<String> allowedMimeTypes = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/bmp"
    );

    /**
     * 图片压缩配置
     */
    private Compression compression = new Compression();

    /**
     * 缩略图配置
     */
    private Thumbnail thumbnail = new Thumbnail();

    /**
     * 图片压缩配置
     */
    @Data
    public static class Compression {
        /**
         * 是否启用压缩
         */
        private Boolean enabled = true;

        /**
         * 压缩质量 (0.0 - 1.0)
         */
        private Double quality = 0.8;

        /**
         * 最大宽度
         */
        private Integer maxWidth = 1920;

        /**
         * 最大高度
         */
        private Integer maxHeight = 1080;
    }

    /**
     * 缩略图配置
     */
    @Data
    public static class Thumbnail {
        /**
         * 是否启用缩略图
         */
        private Boolean enabled = true;

        /**
         * 缩略图宽度
         */
        private Integer width = 200;

        /**
         * 缩略图高度
         */
        private Integer height = 200;

        /**
         * 缩略图文件名后缀
         */
        private String suffix = "_thumb";
    }

    /**
     * 获取完整的上传路径
     *
     * @return 完整的上传路径
     */
    public String getFullUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + "/" + uploadPath;
    }

    /**
     * 检查MIME类型是否被允许
     *
     * @param mimeType MIME类型
     * @return 是否允许
     */
    public boolean isAllowedMimeType(String mimeType) {
        return allowedMimeTypes.contains(mimeType.toLowerCase());
    }

    /**
     * 检查文件大小是否超出限制
     *
     * @param fileSize 文件大小
     * @return 是否超出限制
     */
    public boolean isFileSizeExceeded(long fileSize) {
        return fileSize > maxFileSize;
    }

    /**
     * 获取格式化的文件大小限制
     *
     * @return 格式化的文件大小
     */
    public String getFormattedMaxFileSize() {
        if (maxFileSize >= 1024 * 1024) {
            return (maxFileSize / (1024 * 1024)) + "MB";
        } else if (maxFileSize >= 1024) {
            return (maxFileSize / 1024) + "KB";
        } else {
            return maxFileSize + "B";
        }
    }
}