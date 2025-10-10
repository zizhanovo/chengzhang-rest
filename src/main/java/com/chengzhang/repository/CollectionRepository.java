package com.chengzhang.repository;

import com.chengzhang.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 合集数据访问接口
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Repository
public interface CollectionRepository extends JpaRepository<Collection, String> {

    /**
     * 根据名称查找合集
     *
     * @param name 合集名称
     * @return 合集对象
     */
    Optional<Collection> findByName(String name);

    /**
     * 根据启用状态查找合集
     *
     * @param isEnabled 是否启用
     * @return 合集列表
     */
    List<Collection> findByIsEnabledOrderBySortOrderAsc(Boolean isEnabled);

    /**
     * 查找所有启用的合集，按排序字段排序
     *
     * @return 合集列表
     */
    List<Collection> findByIsEnabledTrueOrderBySortOrderAsc();

    /**
     * 检查名称是否存在（排除指定ID）
     *
     * @param name 合集名称
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(c) > 0 FROM Collection c WHERE c.name = :name AND c.id != :excludeId")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("excludeId") String excludeId);

    /**
     * 检查名称是否存在
     *
     * @param name 合集名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据排序字段范围查找合集
     *
     * @param minSort 最小排序值
     * @param maxSort 最大排序值
     * @return 合集列表
     */
    List<Collection> findBySortOrderBetweenOrderBySortOrderAsc(Integer minSort, Integer maxSort);

    /**
     * 获取最大排序值
     *
     * @return 最大排序值
     */
    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM Collection c WHERE c.isEnabled = true")
    Integer getMaxSortOrder();
}