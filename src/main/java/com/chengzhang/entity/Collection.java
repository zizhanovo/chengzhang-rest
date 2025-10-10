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
 * 合集实体类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "collections")
public class Collection {

    /**
     * 合集ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    /**
     * 合集名称
     */
    @NotBlank(message = "合集名称不能为空")
    @Size(max = 100, message = "合集名称长度不能超过100个字符")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 合集描述
     */
    @Size(max = 500, message = "合集描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 合集颜色（十六进制颜色值）
     */
    @Size(max = 9, message = "颜色值长度不能超过9个字符")
    @Column(name = "color", length = 9)
    private String color = "#409EFF";

    /**
     * 合集图标
     */
    @Size(max = 50, message = "图标长度不能超过50个字符")
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * 排序字段（数值越小排序越靠前）
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 是否启用（true-启用，false-禁用）
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at", nullable = false)
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
}