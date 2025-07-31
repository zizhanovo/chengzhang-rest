package com.chengzhang.service;

import com.chengzhang.entity.Settings;

/**
 * 设置服务接口
 * 
 * @author chengzhang
 * @since 1.0.0
 */
public interface SettingsService {
    
    /**
     * 获取用户设置
     */
    Settings getSettings(String userId);
    
    /**
     * 更新用户设置
     */
    Settings updateSettings(String userId, Settings settings);
    
    /**
     * 重置为默认设置
     */
    Settings resetToDefault(String userId);
}