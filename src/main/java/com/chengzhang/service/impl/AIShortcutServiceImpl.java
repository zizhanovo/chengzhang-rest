package com.chengzhang.service.impl;

import com.chengzhang.dto.AIShortcutDTO;
import com.chengzhang.entity.AIShortcut;
import com.chengzhang.repository.AIShortcutRepository;
import com.chengzhang.service.AIShortcutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AI快捷操作服务实现类
 */
@Service
@Transactional
public class AIShortcutServiceImpl implements AIShortcutService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIShortcutServiceImpl.class);
    
    @Autowired
    private AIShortcutRepository shortcutRepository;
    
    /**
     * 应用启动后初始化默认快捷操作
     */
    @PostConstruct
    public void init() {
        try {
            // 检查是否已有快捷操作，如果没有则初始化默认的
            List<AIShortcut> existing = shortcutRepository.findAll();
            if (existing.isEmpty()) {
                logger.info("初始化默认AI快捷操作...");
                initializeDefaultShortcuts();
            }
        } catch (Exception e) {
            logger.error("初始化默认快捷操作失败", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AIShortcutDTO> getAllActive() {
        try {
            List<AIShortcut> shortcuts = shortcutRepository.findByIsActiveTrueOrderBySortOrderAscCreatedAtAsc();
            return shortcuts.stream()
                    .map(AIShortcutDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取激活的快捷操作失败", e);
            throw new RuntimeException("获取快捷操作失败", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public AIShortcutDTO getById(Long id) {
        try {
            Optional<AIShortcut> shortcut = shortcutRepository.findById(id);
            if (shortcut.isPresent()) {
                return new AIShortcutDTO(shortcut.get());
            } else {
                throw new RuntimeException("快捷操作不存在: " + id);
            }
        } catch (Exception e) {
            logger.error("根据ID获取快捷操作失败: " + id, e);
            throw new RuntimeException("获取快捷操作失败", e);
        }
    }
    
    @Override
    public AIShortcutDTO create(AIShortcutDTO shortcutDTO) {
        try {
            // 验证数据
            validateShortcutData(shortcutDTO, null);
            
            // 检查名称是否重复
            if (shortcutRepository.existsByNameAndIdNot(shortcutDTO.getName(), null)) {
                throw new RuntimeException("快捷操作名称已存在: " + shortcutDTO.getName());
            }
            
            // 创建实体
            AIShortcut entity = shortcutDTO.toEntity();
            
            // 如果没有设置排序顺序，设置为最大值+1
            if (entity.getSortOrder() == null || entity.getSortOrder() == 0) {
                List<AIShortcut> allShortcuts = shortcutRepository.findAll();
                int maxOrder = allShortcuts.stream()
                        .mapToInt(s -> s.getSortOrder() != null ? s.getSortOrder() : 0)
                        .max()
                        .orElse(0);
                entity.setSortOrder(maxOrder + 1);
            }
            
            // 保存
            AIShortcut saved = shortcutRepository.save(entity);
            logger.info("创建快捷操作成功: {}", saved.getName());
            
            return new AIShortcutDTO(saved);
        } catch (Exception e) {
            logger.error("创建快捷操作失败", e);
            throw new RuntimeException("创建快捷操作失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public AIShortcutDTO update(Long id, AIShortcutDTO shortcutDTO) {
        try {
            // 验证数据
            validateShortcutData(shortcutDTO, id);
            
            // 查找现有实体
            Optional<AIShortcut> existingOpt = shortcutRepository.findById(id);
            if (!existingOpt.isPresent()) {
                throw new RuntimeException("快捷操作不存在: " + id);
            }
            
            AIShortcut existing = existingOpt.get();
            
            // 检查名称是否重复（排除当前记录）
            if (shortcutDTO.getName() != null && 
                !shortcutDTO.getName().equals(existing.getName()) &&
                shortcutRepository.existsByNameAndIdNot(shortcutDTO.getName(), id)) {
                throw new RuntimeException("快捷操作名称已存在: " + shortcutDTO.getName());
            }
            
            // 更新实体
            shortcutDTO.updateEntity(existing);
            
            // 保存
            AIShortcut updated = shortcutRepository.save(existing);
            logger.info("更新快捷操作成功: {}", updated.getName());
            
            return new AIShortcutDTO(updated);
        } catch (Exception e) {
            logger.error("更新快捷操作失败: " + id, e);
            throw new RuntimeException("更新快捷操作失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void delete(Long id) {
        try {
            Optional<AIShortcut> existing = shortcutRepository.findById(id);
            if (!existing.isPresent()) {
                throw new RuntimeException("快捷操作不存在: " + id);
            }
            
            shortcutRepository.deleteById(id);
            logger.info("删除快捷操作成功: {}", existing.get().getName());
        } catch (Exception e) {
            logger.error("删除快捷操作失败: " + id, e);
            throw new RuntimeException("删除快捷操作失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void batchDelete(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return;
            }
            
            List<AIShortcut> toDelete = shortcutRepository.findAllById(ids);
            shortcutRepository.deleteAll(toDelete);
            
            logger.info("批量删除快捷操作成功，数量: {}", toDelete.size());
        } catch (Exception e) {
            logger.error("批量删除快捷操作失败", e);
            throw new RuntimeException("批量删除快捷操作失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AIShortcutDTO> searchByName(String name) {
        try {
            List<AIShortcut> shortcuts = shortcutRepository.findByNameContainingIgnoreCase(name);
            return shortcuts.stream()
                    .map(AIShortcutDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("搜索快捷操作失败: " + name, e);
            throw new RuntimeException("搜索快捷操作失败", e);
        }
    }
    
    @Override
    public void updateSortOrder(Long id, Integer sortOrder) {
        try {
            Optional<AIShortcut> existing = shortcutRepository.findById(id);
            if (!existing.isPresent()) {
                throw new RuntimeException("快捷操作不存在: " + id);
            }
            
            AIShortcut shortcut = existing.get();
            shortcut.setSortOrder(sortOrder);
            shortcutRepository.save(shortcut);
            
            logger.info("更新快捷操作排序成功: {} -> {}", shortcut.getName(), sortOrder);
        } catch (Exception e) {
            logger.error("更新快捷操作排序失败: " + id, e);
            throw new RuntimeException("更新排序失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void toggleActive(Long id) {
        try {
            Optional<AIShortcut> existing = shortcutRepository.findById(id);
            if (!existing.isPresent()) {
                throw new RuntimeException("快捷操作不存在: " + id);
            }
            
            AIShortcut shortcut = existing.get();
            shortcut.setIsActive(!shortcut.getIsActive());
            shortcutRepository.save(shortcut);
            
            logger.info("切换快捷操作状态成功: {} -> {}", shortcut.getName(), shortcut.getIsActive());
        } catch (Exception e) {
            logger.error("切换快捷操作状态失败: " + id, e);
            throw new RuntimeException("切换状态失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void initializeDefaultShortcuts() {
        try {
            // 创建默认快捷操作
            AIShortcut[] defaultShortcuts = {
                new AIShortcut(
                    "润色文章",
                    "请帮我润色这篇文章，使其更加流畅、专业和易读。请保持原文的核心观点和结构，主要优化语言表达和逻辑连贯性。",
                    "优化文章语言表达"
                ),
                new AIShortcut(
                    "生成标题",
                    "请根据文章内容生成5个吸引人的标题，要求简洁有力，能够准确概括文章主题，并具有一定的吸引力。",
                    "为文章生成标题"
                ),
                new AIShortcut(
                    "写作建议",
                    "请分析这篇文章的结构和内容，提供具体的改进建议，包括但不限于：逻辑结构、论证方式、语言表达、内容完整性等方面。",
                    "获取写作改进建议"
                ),
                new AIShortcut(
                    "生成摘要",
                    "请为这篇文章生成一个简洁的摘要，控制在200字以内，要求能够准确概括文章的主要内容和核心观点。",
                    "生成文章摘要"
                ),
                new AIShortcut(
                    "检查语法",
                    "请检查这篇文章的语法错误、错别字和标点符号使用问题，并提供修改建议。",
                    "检查语法和错别字"
                )
            };
            
            for (int i = 0; i < defaultShortcuts.length; i++) {
                defaultShortcuts[i].setSortOrder(i + 1);
                shortcutRepository.save(defaultShortcuts[i]);
            }
            
            logger.info("初始化默认快捷操作完成，数量: {}", defaultShortcuts.length);
        } catch (Exception e) {
            logger.error("初始化默认快捷操作失败", e);
            throw new RuntimeException("初始化默认快捷操作失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证快捷操作数据
     */
    private void validateShortcutData(AIShortcutDTO shortcutDTO, Long excludeId) {
        if (shortcutDTO == null) {
            throw new IllegalArgumentException("快捷操作数据不能为空");
        }
        
        if (shortcutDTO.getName() == null || shortcutDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("快捷操作名称不能为空");
        }
        
        if (shortcutDTO.getName().length() > 50) {
            throw new IllegalArgumentException("快捷操作名称不能超过50个字符");
        }
        
        if (shortcutDTO.getPrompt() == null || shortcutDTO.getPrompt().trim().isEmpty()) {
            throw new IllegalArgumentException("提示词不能为空");
        }
        
        if (shortcutDTO.getPrompt().length() > 1000) {
            throw new IllegalArgumentException("提示词不能超过1000个字符");
        }
        
        if (shortcutDTO.getDescription() != null && shortcutDTO.getDescription().length() > 200) {
            throw new IllegalArgumentException("描述不能超过200个字符");
        }
    }
}