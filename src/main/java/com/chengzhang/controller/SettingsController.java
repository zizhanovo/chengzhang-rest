package com.chengzhang.controller;

import com.chengzhang.common.Result;
import com.chengzhang.entity.Settings;
import com.chengzhang.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 设置控制器
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {
    
    private final SettingsService settingsService;
    
    /**
     * 获取用户设置
     */
    @GetMapping
    public Result<Settings> getSettings(
            @RequestParam(defaultValue = "default") String userId) {
        try {
            Settings settings = settingsService.getSettings(userId);
            return Result.success(settings);
        } catch (Exception e) {
            log.error("获取用户设置失败", e);
            return Result.error("获取用户设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户设置
     */
    @PutMapping
    public Result<Settings> updateSettings(
            @RequestParam(defaultValue = "default") String userId,
            @RequestBody Settings settings) {
        try {
            Settings updatedSettings = settingsService.updateSettings(userId, settings);
            return Result.success("设置更新成功", updatedSettings);
        } catch (Exception e) {
            log.error("更新用户设置失败", e);
            return Result.error("更新用户设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置为默认设置
     */
    @PostMapping("/reset")
    public Result<Settings> resetToDefault(
            @RequestParam(defaultValue = "default") String userId) {
        try {
            Settings defaultSettings = settingsService.resetToDefault(userId);
            return Result.success("设置已重置为默认值", defaultSettings);
        } catch (Exception e) {
            log.error("重置用户设置失败", e);
            return Result.error("重置用户设置失败: " + e.getMessage());
        }
    }
}