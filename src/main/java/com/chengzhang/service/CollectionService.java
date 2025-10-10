package com.chengzhang.service;

import com.chengzhang.dto.CollectionDTO;
import java.util.List;

/**
 * 合集服务接口
 *
 * @author chengzhang
 * @since 1.0.0
 */
public interface CollectionService {

    /**
     * 创建合集
     *
     * @param collectionDTO 合集信息
     * @return 创建的合集
     */
    CollectionDTO createCollection(CollectionDTO collectionDTO);

    /**
     * 更新合集
     *
     * @param id 合集ID
     * @param collectionDTO 更新的合集信息
     * @return 更新后的合集
     */
    CollectionDTO updateCollection(String id, CollectionDTO collectionDTO);

    /**
     * 根据ID获取合集
     *
     * @param id 合集ID
     * @return 合集信息
     */
    CollectionDTO getCollectionById(String id);

    /**
     * 获取所有合集
     *
     * @return 合集列表
     */
    List<CollectionDTO> getAllCollections();

    /**
     * 获取启用的合集
     *
     * @return 启用的合集列表
     */
    List<CollectionDTO> getEnabledCollections();

    /**
     * 删除合集
     *
     * @param id 合集ID
     */
    void deleteCollection(String id);

    /**
     * 启用/禁用合集
     *
     * @param id 合集ID
     * @param enabled 是否启用
     * @return 更新后的合集
     */
    CollectionDTO toggleCollectionStatus(String id, Boolean enabled);

    /**
     * 获取合集统计信息（包含文章数量）
     *
     * @return 合集统计信息列表
     */
    List<CollectionDTO> getCollectionStats();

    /**
     * 检查合集名称是否存在
     *
     * @param name 合集名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByName(String name, String excludeId);

    /**
     * 获取合集选项（用于下拉选择）
     *
     * @return 合集选项列表
     */
    List<CollectionDTO> getCollectionOptions();

    /**
     * �整合合排序
     *
     * @param id 合集ID
     * @param newSortOrder 新的排序值
     * @return 更新后的合集
     */
    CollectionDTO updateSortOrder(String id, Integer newSortOrder);
}