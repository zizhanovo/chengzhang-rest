package com.chengzhang.dto;

import com.chengzhang.entity.AIShortcut;
import java.time.LocalDateTime;

/**
 * AI快捷操作DTO类
 */
public class AIShortcutDTO {
    
    private Long id;
    private String name;
    private String prompt;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public AIShortcutDTO() {
    }
    
    public AIShortcutDTO(AIShortcut entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.prompt = entity.getPrompt();
        this.description = entity.getDescription();
        this.sortOrder = entity.getSortOrder();
        this.isActive = entity.getIsActive();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
    
    // 转换为实体类
    public AIShortcut toEntity() {
        AIShortcut entity = new AIShortcut();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setPrompt(this.prompt);
        entity.setDescription(this.description);
        entity.setSortOrder(this.sortOrder != null ? this.sortOrder : 0);
        entity.setIsActive(this.isActive != null ? this.isActive : true);
        return entity;
    }
    
    // 更新实体类
    public void updateEntity(AIShortcut entity) {
        if (this.name != null) {
            entity.setName(this.name);
        }
        if (this.prompt != null) {
            entity.setPrompt(this.prompt);
        }
        if (this.description != null) {
            entity.setDescription(this.description);
        }
        if (this.sortOrder != null) {
            entity.setSortOrder(this.sortOrder);
        }
        if (this.isActive != null) {
            entity.setIsActive(this.isActive);
        }
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "AIShortcutDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prompt='" + prompt + '\'' +
                ", description='" + description + '\'' +
                ", sortOrder=" + sortOrder +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}