package com.chengzhang.service;

import com.chengzhang.dto.AIShortcutDTO;
import com.chengzhang.entity.AIShortcut;

import java.util.List;

/**
 * AI快捷操作服务接口
 */
public interface AIShortcutService {
    
    /**
     * 获取所有激活的快捷操作
     */
    List<AIShortcutDTO> getAllActive();
    
    /**
     * 根据ID获取快捷操作
     */
    AIShortcutDTO getById(Long id);
    
    /**
     * 创建快捷操作
     */
    AIShortcutDTO create(AIShortcutDTO shortcutDTO);
    
    /**
     * 更新快捷操作
     */
    AIShortcutDTO update(Long id, AIShortcutDTO shortcutDTO);
    
    /**
     * 删除快捷操作
     */
    void delete(Long id);
    
    /**
     * 批量删除快捷操作
     */
    void batchDelete(List<Long> ids);
    
    /**
     * 根据名称搜索快捷操作
     */
    List<AIShortcutDTO> searchByName(String name);
    
    /**
     * 更新排序顺序
     */
    void updateSortOrder(Long id, Integer sortOrder);
    
    /**
     * 切换激活状态
     */
    void toggleActive(Long id);
    
    /**
     * 初始化默认快捷操作
     */
    void initializeDefaultShortcuts();
}