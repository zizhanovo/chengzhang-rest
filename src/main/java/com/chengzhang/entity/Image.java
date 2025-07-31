package com.chengzhang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 图片实体类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Entity
@Table(name = "images")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Image {
    
    /**
     * 图片ID
     */
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    /**
     * 图片URL
     */
    @NotBlank(message = "图片URL不能为空")
    @Column(name = "url", nullable = false, length = 500)
    private String url;
    
    /**
     * 原始文件名
     */
    @Column(name = "filename", length = 255)
    private String filename;
    
    /**
     * 文件大小（字节）
     */
    @Column(name = "size")
    private Long size;
    
    /**
     * 图片宽度
     */
    @Column(name = "width")
    private Integer width;
    
    /**
     * 图片高度
     */
    @Column(name = "height")
    private Integer height;
    
    /**
     * 图床平台
     */
    @Column(name = "platform", length = 50)
    private String platform;
    
    /**
     * 上传时间
     */
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "upload_time", nullable = false, updatable = false)
    private LocalDateTime uploadTime;
    
    /**
     * 用户ID（预留字段）
     */
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = "img_" + System.currentTimeMillis();
        }
    }
}