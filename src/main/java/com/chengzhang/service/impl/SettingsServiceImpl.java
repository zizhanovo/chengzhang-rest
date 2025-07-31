package com.chengzhang.service.impl;

import com.chengzhang.entity.Settings;
import com.chengzhang.repository.SettingsRepository;
import com.chengzhang.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设置服务实现类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SettingsServiceImpl implements SettingsService {
    
    private final SettingsRepository settingsRepository;
    
    @Override
    public Settings getSettings(String userId) {
        Settings settings = settingsRepository.findByUserId(userId);
        return settings != null ? settings : createDefaultSettings(userId);
    }
    
    @Override
    @Transactional
    public Settings updateSettings(String userId, Settings settings) {
        Settings existingSettings = settingsRepository.findByUserId(userId);
        if (existingSettings == null) {
            existingSettings = createDefaultSettings(userId);
        }
        
        // 更新编辑器设置
        if (settings.getEditorTheme() != null) {
            existingSettings.setEditorTheme(settings.getEditorTheme());
        }
        if (settings.getEditorFontSize() != null) {
            existingSettings.setEditorFontSize(settings.getEditorFontSize());
        }
        if (settings.getEditorFontFamily() != null) {
            existingSettings.setEditorFontFamily(settings.getEditorFontFamily());
        }
        if (settings.getEditorLineHeight() != null) {
            existingSettings.setEditorLineHeight(settings.getEditorLineHeight());
        }
        if (settings.getEditorTabSize() != null) {
            existingSettings.setEditorTabSize(settings.getEditorTabSize());
        }
        if (settings.getEditorWordWrap() != null) {
            existingSettings.setEditorWordWrap(settings.getEditorWordWrap());
        }
        if (settings.getEditorLineNumbers() != null) {
            existingSettings.setEditorLineNumbers(settings.getEditorLineNumbers());
        }
        if (settings.getEditorMinimap() != null) {
            existingSettings.setEditorMinimap(settings.getEditorMinimap());
        }
        if (settings.getEditorAutoSave() != null) {
            existingSettings.setEditorAutoSave(settings.getEditorAutoSave());
        }
        if (settings.getEditorAutoSaveDelay() != null) {
            existingSettings.setEditorAutoSaveDelay(settings.getEditorAutoSaveDelay());
        }
        
        // 更新图片上传设置
        if (settings.getImagePlatform() != null) {
            existingSettings.setImagePlatform(settings.getImagePlatform());
        }
        if (settings.getImageCompress() != null) {
            existingSettings.setImageCompress(settings.getImageCompress());
        }
        if (settings.getImageQuality() != null) {
            existingSettings.setImageQuality(settings.getImageQuality());
        }
        if (settings.getImageMaxSize() != null) {
            existingSettings.setImageMaxSize(settings.getImageMaxSize());
        }
        if (settings.getImageWatermark() != null) {
            existingSettings.setImageWatermark(settings.getImageWatermark());
        }
        
        // 更新导出设置
        if (settings.getExportFormat() != null) {
            existingSettings.setExportFormat(settings.getExportFormat());
        }
        if (settings.getExportIncludeImages() != null) {
            existingSettings.setExportIncludeImages(settings.getExportIncludeImages());
        }
        if (settings.getExportIncludeToc() != null) {
            existingSettings.setExportIncludeToc(settings.getExportIncludeToc());
        }
        if (settings.getExportTemplate() != null) {
            existingSettings.setExportTemplate(settings.getExportTemplate());
        }
        
        // 更新通用设置
        if (settings.getLanguage() != null) {
            existingSettings.setLanguage(settings.getLanguage());
        }
        if (settings.getTheme() != null) {
            existingSettings.setTheme(settings.getTheme());
        }
        if (settings.getNotifications() != null) {
            existingSettings.setNotifications(settings.getNotifications());
        }
        if (settings.getBackupEnabled() != null) {
            existingSettings.setBackupEnabled(settings.getBackupEnabled());
        }
        if (settings.getBackupInterval() != null) {
            existingSettings.setBackupInterval(settings.getBackupInterval());
        }
        
        Settings savedSettings = settingsRepository.save(existingSettings);
        log.info("用户设置更新成功，用户ID: {}", userId);
        
        return savedSettings;
    }
    
    @Override
    @Transactional
    public Settings resetToDefault(String userId) {
        // 删除现有设置
        Settings existingSettings = settingsRepository.findByUserId(userId);
        if (existingSettings != null) {
            settingsRepository.deleteById(existingSettings.getId());
        }
        
        // 创建默认设置
        Settings defaultSettings = createDefaultSettings(userId);
        log.info("用户设置重置为默认值，用户ID: {}", userId);
        
        return defaultSettings;
    }
    
    /**
     * 创建默认设置
     */
    private Settings createDefaultSettings(String userId) {
        Settings settings = new Settings();
        settings.setUserId(userId);
        return settingsRepository.save(settings);
    }
}