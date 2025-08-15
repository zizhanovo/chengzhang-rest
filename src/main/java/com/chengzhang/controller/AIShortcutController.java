package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.dto.AIShortcutDTO;
import com.chengzhang.service.AIShortcutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * AI快捷操作控制器
 */
@RestController
@RequestMapping("/ai-shortcuts")
@CrossOrigin(origins = "*")
public class AIShortcutController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIShortcutController.class);
    
    @Autowired
    private AIShortcutService shortcutService;
    
    /**
     * 获取所有激活的快捷操作
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AIShortcutDTO>>> getAllActive() {
        try {
            List<AIShortcutDTO> shortcuts = shortcutService.getAllActive();
            return ResponseEntity.ok(ApiResponse.success(shortcuts));
        } catch (Exception e) {
            logger.error("获取快捷操作失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取快捷操作
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AIShortcutDTO>> getById(@PathVariable Long id) {
        try {
            AIShortcutDTO shortcut = shortcutService.getById(id);
            return ResponseEntity.ok(ApiResponse.success(shortcut));
        } catch (Exception e) {
            logger.error("获取快捷操作失败: " + id, e);
            return ResponseEntity.ok(ApiResponse.error("获取快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建快捷操作
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AIShortcutDTO>> create(@Valid @RequestBody AIShortcutDTO shortcutDTO) {
        try {
            AIShortcutDTO created = shortcutService.create(shortcutDTO);
            return ResponseEntity.ok(ApiResponse.success(created));
        } catch (Exception e) {
            logger.error("创建快捷操作失败", e);
            return ResponseEntity.ok(ApiResponse.error("创建快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新快捷操作
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AIShortcutDTO>> update(
            @PathVariable Long id, 
            @Valid @RequestBody AIShortcutDTO shortcutDTO) {
        try {
            AIShortcutDTO updated = shortcutService.update(id, shortcutDTO);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (Exception e) {
            logger.error("更新快捷操作失败: " + id, e);
            return ResponseEntity.ok(ApiResponse.error("更新快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除快捷操作
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            shortcutService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            logger.error("删除快捷操作失败: " + id, e);
            return ResponseEntity.ok(ApiResponse.error("删除快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量删除快捷操作
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> batchDelete(@RequestBody List<Long> ids) {
        try {
            shortcutService.batchDelete(ids);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            logger.error("批量删除快捷操作失败", e);
            return ResponseEntity.ok(ApiResponse.error("批量删除快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索快捷操作
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AIShortcutDTO>>> search(@RequestParam String name) {
        try {
            List<AIShortcutDTO> shortcuts = shortcutService.searchByName(name);
            return ResponseEntity.ok(ApiResponse.success(shortcuts));
        } catch (Exception e) {
            logger.error("搜索快捷操作失败: " + name, e);
            return ResponseEntity.ok(ApiResponse.error("搜索快捷操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新排序
     */
    @PutMapping("/{id}/sort")
    public ResponseEntity<ApiResponse<Void>> updateSortOrder(
            @PathVariable Long id, 
            @RequestParam Integer sortOrder) {
        try {
            shortcutService.updateSortOrder(id, sortOrder);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            logger.error("更新快捷操作排序失败: " + id, e);
            return ResponseEntity.ok(ApiResponse.error("更新排序失败: " + e.getMessage()));
        }
    }
    
    /**
     * 切换激活状态
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        try {
            shortcutService.toggleActive(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            logger.error("切换快捷操作状态失败: " + id, e);
            return ResponseEntity.ok(ApiResponse.error("切换状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 初始化默认快捷操作
     */
    @PostMapping("/init-defaults")
    public ResponseEntity<ApiResponse<Void>> initializeDefaults() {
        try {
            shortcutService.initializeDefaultShortcuts();
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            logger.error("初始化默认快捷操作失败", e);
            return ResponseEntity.ok(ApiResponse.error("初始化失败: " + e.getMessage()));
        }
    }
}