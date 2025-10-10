package com.chengzhang.service.impl;

import com.chengzhang.dto.CollectionDTO;
import com.chengzhang.entity.Collection;
import com.chengzhang.repository.CollectionRepository;
import com.chengzhang.repository.ArticleRepository;
import com.chengzhang.service.CollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * /**
 * 合集服务实现类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final ArticleRepository articleRepository;

    @Autowired
    public CollectionServiceImpl(CollectionRepository collectionRepository,
                               ArticleRepository articleRepository) {
        this.collectionRepository = collectionRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public CollectionDTO createCollection(CollectionDTO collectionDTO) {
        // 检查名称是否已存在
        if (collectionRepository.existsByName(collectionDTO.getName())) {
            throw new IllegalArgumentException("合集名称已存在: " + collectionDTO.getName());
        }

        Collection collection = new Collection();
        BeanUtils.copyProperties(collectionDTO, collection);

        // 确保颜色字段正确设置
        if (collectionDTO.getColor() != null && !collectionDTO.getColor().trim().isEmpty()) {
            collection.setColor(collectionDTO.getColor());
        }

        // 设置默认排序值
        if (collection.getSortOrder() == null || collection.getSortOrder() == 0) {
            Integer maxSortOrder = collectionRepository.getMaxSortOrder();
            collection.setSortOrder(maxSortOrder + 1);
        }

        Collection savedCollection = collectionRepository.save(collection);
        return convertToDTO(savedCollection);
    }

    @Override
    @Transactional
    public CollectionDTO updateCollection(String id, CollectionDTO collectionDTO) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合集不存在: " + id));

        // 检查名称是否与其他合集重复
        if (!collection.getName().equals(collectionDTO.getName()) &&
            collectionRepository.existsByNameAndIdNot(collectionDTO.getName(), id)) {
            throw new IllegalArgumentException("合集名称已存在: " + collectionDTO.getName());
        }

        // 检查是否有文章引用了不存在的合集ID（数据一致性检查）
        try {
            long articleCount = articleRepository.countByCollectionId(id);
            log.debug("合集 {} 当前关联的文章数量: {}", id, articleCount);
        } catch (Exception e) {
            log.warn("检查合集关联文章时出现异常: {}", e.getMessage());
            // 清理可能存在的无效引用
            try {
                articleRepository.updateCollectionIdToNull(id);
                log.info("已清理合集 {} 的无效文章引用", id);
            } catch (Exception cleanupException) {
                log.error("清理无效引用失败: {}", cleanupException.getMessage());
            }
        }

        // 更新字段
        collection.setName(collectionDTO.getName());
        collection.setDescription(collectionDTO.getDescription());
        collection.setColor(collectionDTO.getColor());
        collection.setIcon(collectionDTO.getIcon());
        collection.setSortOrder(collectionDTO.getSortOrder());
        collection.setIsEnabled(collectionDTO.getIsEnabled());

        Collection updatedCollection = collectionRepository.save(collection);
        return convertToDTO(updatedCollection);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionDTO getCollectionById(String id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合集不存在: " + id));
        return convertToDTO(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDTO> getAllCollections() {
        List<Collection> collections = collectionRepository.findAll(
                Sort.by(Sort.Direction.ASC, "sortOrder"));
        return collections.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDTO> getEnabledCollections() {
        List<Collection> collections = collectionRepository.findByIsEnabledTrueOrderBySortOrderAsc();
        return collections.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCollection(String id) {
        if (!collectionRepository.existsById(id)) {
            throw new IllegalArgumentException("合集不存在: " + id);
        }

        // 检查是否有关联的文章
        long articleCount = articleRepository.countByCollectionId(id);
        if (articleCount > 0) {
            throw new IllegalStateException("无法删除合集，存在 " + articleCount + " 篇关联文章");
        }

        collectionRepository.deleteById(id);
    }

    @Override
    public CollectionDTO toggleCollectionStatus(String id, Boolean enabled) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合集不存在: " + id));

        collection.setIsEnabled(enabled);
        Collection updatedCollection = collectionRepository.save(collection);
        return convertToDTO(updatedCollection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDTO> getCollectionStats() {
        List<Collection> collections = collectionRepository.findByIsEnabledTrueOrderBySortOrderAsc();

        return collections.stream()
                .map(collection -> {
                    CollectionDTO dto = convertToDTO(collection);
                    // 统计文章数量
                    long articleCount = articleRepository.countByCollectionId(collection.getId());
                    dto.setArticleCount((int) articleCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name, String excludeId) {
        if (excludeId == null || excludeId.trim().isEmpty()) {
            return collectionRepository.existsByName(name);
        } else {
            return collectionRepository.existsByNameAndIdNot(name, excludeId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDTO> getCollectionOptions() {
        List<Collection> collections = collectionRepository.findByIsEnabledTrueOrderBySortOrderAsc();
        return collections.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CollectionDTO updateSortOrder(String id, Integer newSortOrder) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合集不存在: " + id));

        collection.setSortOrder(newSortOrder);
        Collection updatedCollection = collectionRepository.save(collection);
        return convertToDTO(updatedCollection);
    }

    /**
     * 实体转DTO
     */
    private CollectionDTO convertToDTO(Collection collection) {
        CollectionDTO dto = new CollectionDTO();
        BeanUtils.copyProperties(collection, dto);
        return dto;
    }
}