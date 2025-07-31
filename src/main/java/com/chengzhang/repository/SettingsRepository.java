package com.chengzhang.repository;

import com.chengzhang.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 设置Repository接口
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Repository
public interface SettingsRepository extends JpaRepository<Settings, String> {
    
    /**
     * 根据用户ID查询设置（预留）
     */
    Settings findByUserId(String userId);
    
    /**
     * 获取默认设置（当前版本使用）
     */
    default Settings getDefaultSettings() {
        return findAll().stream().findFirst().orElse(null);
    }
}