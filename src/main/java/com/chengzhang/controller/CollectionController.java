package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.dto.CollectionDTO;
import com.chengzhang.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 合集控制器
 *
 * @author chengzhang
 * @since 1.0.0
 */
@RestController
@RequestMapping("/collections")
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CollectionController {

    private final CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    /**
     * 创建合集
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CollectionDTO>> createCollection(@Valid @RequestBody CollectionDTO collectionDTO) {
        try {
            CollectionDTO createdCollection = collectionService.createCollection(collectionDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("合集创建成功", createdCollection));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("创建合集失败: " + e.getMessage()));
        }
    }

    /**
     * 更新合集
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionDTO>> updateCollection(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody CollectionDTO collectionDTO) {
        try {
            CollectionDTO updatedCollection = collectionService.updateCollection(id, collectionDTO);
            return ResponseEntity.ok(ApiResponse.success("合集更新成功", updatedCollection));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新合集失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取合集
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionDTO>> getCollectionById(@PathVariable @NotBlank String id) {
        try {
            CollectionDTO collection = collectionService.getCollectionById(id);
            return ResponseEntity.ok(ApiResponse.success("获取合集成功", collection));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取合集失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有合集
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CollectionDTO>>> getAllCollections() {
        try {
            List<CollectionDTO> collections = collectionService.getAllCollections();
            return ResponseEntity.ok(ApiResponse.success("获取合集列表成功", collections));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取合集列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取启用的合集
     */
    @GetMapping("/enabled")
    public ResponseEntity<ApiResponse<List<CollectionDTO>>> getEnabledCollections() {
        try {
            List<CollectionDTO> collections = collectionService.getEnabledCollections();
            return ResponseEntity.ok(ApiResponse.success("获取启用合集列表成功", collections));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取启用合集列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取合集选项（用于下拉选择）
     */
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<CollectionDTO>>> getCollectionOptions() {
        try {
            List<CollectionDTO> options = collectionService.getCollectionOptions();
            return ResponseEntity.ok(ApiResponse.success("获取合集选项成功", options));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取合集选项失败: " + e.getMessage()));
        }
    }

    /**
     * 获取合集统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<CollectionDTO>>> getCollectionStats() {
        try {
            List<CollectionDTO> stats = collectionService.getCollectionStats();
            return ResponseEntity.ok(ApiResponse.success("获取合集统计信息成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取合集统计信息失败: " + e.getMessage()));
        }
    }

    /**
     * 删除合集
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(@PathVariable @NotBlank String id) {
        try {
            collectionService.deleteCollection(id);
            return ResponseEntity.ok(ApiResponse.<Void>success("合集删除成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("删除合集失败: " + e.getMessage()));
        }
    }

    /**
     * 启用/禁用合集
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CollectionDTO>> toggleCollectionStatus(
            @PathVariable @NotBlank String id,
            @RequestParam Boolean enabled) {
        try {
            CollectionDTO collection = collectionService.toggleCollectionStatus(id, enabled);
            String message = enabled ? "合集启用成功" : "合集禁用成功";
            return ResponseEntity.ok(ApiResponse.success(message, collection));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新合集状态失败: " + e.getMessage()));
        }
    }

    /**
     * 更新合集排序
     */
    @PatchMapping("/{id}/sort")
    public ResponseEntity<ApiResponse<CollectionDTO>> updateSortOrder(
            @PathVariable @NotBlank String id,
            @RequestParam @Min(0) Integer sortOrder) {
        try {
            CollectionDTO collection = collectionService.updateSortOrder(id, sortOrder);
            return ResponseEntity.ok(ApiResponse.success("合集排序更新成功", collection));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新合集排序失败: " + e.getMessage()));
        }
    }

    /**
     * 检查合集名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse<Boolean>> checkNameExists(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String excludeId) {
        try {
            boolean exists = collectionService.existsByName(name, excludeId);
            return ResponseEntity.ok(ApiResponse.success("检查合集名称成功", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("检查合集名称失败: " + e.getMessage()));
        }
    }
}