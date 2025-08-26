package com.chengzhang.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Base64图片上传请求DTO
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class Base64UploadRequest {

    /**
     * Base64编码的图片数据
     */
    @NotBlank(message = "Base64数据不能为空")
    private String base64Data;

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    /**
     * 关联文章ID（可选）
     */
    private String articleId;

    /**
     * 图片描述（可选）
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    /**
     * 图片标签（可选）
     */
    private String tags;
}