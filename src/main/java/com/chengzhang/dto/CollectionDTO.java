package com.chengzhang.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 合集数据传输对象
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class CollectionDTO {

    /**
     * 合集ID
     */
    private String id;

    /**
     * 合集名称
     */
    @NotBlank(message = "合集名称不能为空")
    @Size(max = 100, message = "合集名称长度不能超过100个字符")
    private String name;

    /**
     * 合集描述
     */
    @Size(max = 500, message = "合集描述长度不能超过500个字符")
    private String description;

    /**
     * 合集颜色（十六进制颜色值）
     */
    @Size(max = 9, message = "颜色值长度不能超过9个字符")
    private String color;

    /**
     * 合集图标
     */
    @Size(max = 50, message = "图标长度不能超过50个字符")
    private String icon;

    /**
     * 排序字段
     */
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    private Boolean isEnabled = true;

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
     * 文章数量（用于统计信息）
     */
    private Integer articleCount = 0;
}