package com.chengzhang.repository;

import com.chengzhang.entity.AIShortcut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI快捷操作Repository接口
 */
@Repository
public interface AIShortcutRepository extends JpaRepository<AIShortcut, Long> {
    
    /**
     * 查找所有激活的快捷操作，按排序顺序排列
     */
    @Query("SELECT a FROM AIShortcut a WHERE a.isActive = true ORDER BY a.sortOrder ASC, a.createdAt ASC")
    List<AIShortcut> findAllActiveOrderBySortOrder();
    
    /**
     * 根据名称查找快捷操作
     */
    List<AIShortcut> findByNameContainingIgnoreCase(String name);
    
    /**
     * 查找所有激活的快捷操作
     */
    List<AIShortcut> findByIsActiveTrue();
    
    /**
     * 根据排序顺序查找快捷操作
     */
    List<AIShortcut> findByIsActiveTrueOrderBySortOrderAscCreatedAtAsc();
    
    /**
     * 检查名称是否已存在（排除指定ID）
     */
    @Query("SELECT COUNT(a) > 0 FROM AIShortcut a WHERE a.name = ?1 AND (?2 IS NULL OR a.id != ?2)")
    boolean existsByNameAndIdNot(String name, Long excludeId);
}