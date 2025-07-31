package com.chengzhang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户设置实体类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Entity
@Table(name = "settings")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Settings {
    
    /**
     * 设置ID
     */
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    /**
     * 用户ID
     */
    @NotNull
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId = "default";
    
    // 编辑器设置
    @Column(name = "editor_theme", length = 50)
    private String editorTheme = "vs-dark";
    
    @Column(name = "editor_font_size")
    private Integer editorFontSize = 14;
    
    @Column(name = "editor_font_family", length = 100)
    private String editorFontFamily = "Consolas, Monaco, monospace";
    
    @Column(name = "editor_line_height", precision = 3, scale = 1)
    private BigDecimal editorLineHeight = new BigDecimal("1.5");
    
    @Column(name = "editor_tab_size")
    private Integer editorTabSize = 4;
    
    @Column(name = "editor_word_wrap")
    private Boolean editorWordWrap = true;
    
    @Column(name = "editor_line_numbers")
    private Boolean editorLineNumbers = true;
    
    @Column(name = "editor_minimap")
    private Boolean editorMinimap = true;
    
    @Column(name = "editor_auto_save")
    private Boolean editorAutoSave = true;
    
    @Column(name = "editor_auto_save_delay")
    private Integer editorAutoSaveDelay = 2000;
    
    // 图片上传设置
    @Column(name = "image_platform", length = 50)
    private String imagePlatform = "smms";
    
    @Column(name = "image_compress")
    private Boolean imageCompress = true;
    
    @Min(1)
    @Max(100)
    @Column(name = "image_quality")
    private Integer imageQuality = 80;
    
    @Column(name = "image_max_size")
    private Integer imageMaxSize = 10;
    
    @Column(name = "image_watermark")
    private Boolean imageWatermark = false;
    
    // 导出设置
    @Column(name = "export_format", length = 20)
    private String exportFormat = "markdown";
    
    @Column(name = "export_include_images")
    private Boolean exportIncludeImages = true;
    
    @Column(name = "export_include_toc")
    private Boolean exportIncludeToc = true;
    
    @Column(name = "export_template", length = 50)
    private String exportTemplate = "default";
    
    // 通用设置
    @Column(name = "language", length = 10)
    private String language = "zh-CN";
    
    @Column(name = "theme", length = 20)
    private String theme = "light";
    
    @Column(name = "notifications")
    private Boolean notifications = true;
    
    @Column(name = "backup_enabled")
    private Boolean backupEnabled = true;
    
    @Column(name = "backup_interval")
    private Integer backupInterval = 24;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}