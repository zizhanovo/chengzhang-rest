package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.common.PageResponse;
import com.chengzhang.dto.Base64UploadRequest;
import com.chengzhang.dto.ImageDTO;
import com.chengzhang.entity.Image;
import com.chengzhang.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 图片管理控制器
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImageController {

    private final ImageService imageService;

    /**
     * 分页获取图片列表
     *
     * @param keyword   搜索关键词（文件名、原始名称、描述）
     * @param status    图片状态：active/deleted
     * @param mimeType  MIME类型筛选
     * @param articleId 关联文章ID
     * @param tags      标签筛选
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param page      页码，默认1
     * @param size      每页数量，默认20
     * @param sortBy    排序字段：uploadTime/fileName/fileSize，默认uploadTime
     * @param sortDir   排序方向：asc/desc，默认desc
     * @return 图片列表
     */
    @GetMapping
    public ApiResponse<PageResponse<ImageDTO>> getImages(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(required = false) String mimeType,
            @RequestParam(required = false) String articleId,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "uploadTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("获取图片列表 - keyword: {}, status: {}, page: {}, size: {}", keyword, status, page, size);
        
        try {
            Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page - 1, Math.min(size, 100), Sort.by(direction, sortBy));
            
            Page<ImageDTO> imagePage = imageService.getImages(
                    pageable, keyword, articleId, mimeType, status, sortBy, sortDir);
            
            return ApiResponse.success(PageResponse.of(imagePage));
        } catch (Exception e) {
            log.error("获取图片列表失败", e);
            return ApiResponse.error("获取图片列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取图片详情
     *
     * @param id 图片ID
     * @return 图片详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ImageDTO> getImageById(@PathVariable String id) {
        log.info("获取图片详情 - id: {}", id);
        
        try {
            ImageDTO image = imageService.getImageById(id);
            if (image == null) {
                return ApiResponse.notFound("图片不存在");
            }
            return ApiResponse.success(image);
        } catch (Exception e) {
            log.error("获取图片详情失败 - id: {}", id, e);
            return ApiResponse.error("获取图片详情失败: " + e.getMessage());
        }
    }

    /**
     * 单文件上传
     *
     * @param file      上传的文件
     * @param articleId 关联文章ID（可选）
     * @param description 图片描述（可选）
     * @param tags      图片标签（可选）
     * @param request   HTTP请求对象
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ApiResponse<ImageDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "articleId", required = false) String articleId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags,
            HttpServletRequest request) {
        
        log.info("图片上传 - fileName: {}, size: {}, articleId: {}", 
                file.getOriginalFilename(), file.getSize(), articleId);
        
        try {
            String uploadIp = getClientIpAddress(request);
            ImageDTO image = imageService.uploadImage(file, articleId, uploadIp);
            return ApiResponse.created("图片上传成功", image);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量文件上传
     *
     * @param files     上传的文件列表
     * @param articleId 关联文章ID（可选）
     * @param request   HTTP请求对象
     * @return 上传结果
     */
    @PostMapping(value = "/batch-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> batchUploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(required = false) String articleId,
            HttpServletRequest request) {
        
        log.info("批量上传图片 - 文件数量: {}, articleId: {}", files.length, articleId);
        
        try {
            String uploadIp = getClientIpAddress(request);
            Map<String, Object> result = imageService.batchUploadImages(Arrays.asList(files), articleId, uploadIp);
            return ApiResponse.success("批量上传完成", result);
        } catch (Exception e) {
            log.error("批量上传图片失败", e);
            return ApiResponse.error("批量上传失败: " + e.getMessage());
        }
    }

    /**
     * Base64图片上传
     *
     * @param request 上传请求
     * @param httpRequest HTTP请求对象
     * @return 上传结果
     */
    @PostMapping("/upload/base64")
    public ApiResponse<ImageDTO> uploadImageFromBase64(
            @Valid @RequestBody Base64UploadRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Base64图片上传 - fileName: {}, articleId: {}", 
                request.getFileName(), request.getArticleId());
        
        try {
            String uploadIp = getClientIpAddress(httpRequest);
            ImageDTO image = imageService.uploadImageFromBase64(
                    request.getBase64Data(), request.getFileName(), 
                    request.getArticleId(), uploadIp);
            return ApiResponse.created("图片上传成功", image);
        } catch (Exception e) {
            log.error("Base64图片上传失败", e);
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 更新图片信息
     *
     * @param id      图片ID
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResponse<ImageDTO> updateImage(
            @PathVariable String id,
            @Valid @RequestBody ImageUpdateRequest request) {
        
        log.info("更新图片信息 - id: {}", id);
        
        try {
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setDescription(request.getDescription());
            imageDTO.setTags(request.getTags());
            
            ImageDTO updatedImage = imageService.updateImage(id, imageDTO);
            if (updatedImage == null) {
                return ApiResponse.notFound("图片不存在");
            }
            return ApiResponse.success("更新成功", updatedImage);
        } catch (Exception e) {
            log.error("更新图片信息失败 - id: {}", id, e);
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 逻辑删除图片
     *
     * @param id 图片ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable String id) {
        log.info("删除图片 - id: {}", id);
        
        try {
            imageService.deleteImage(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除图片失败 - id: {}", id, e);
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 物理删除图片
     *
     * @param id 图片ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}/physical")
    public ApiResponse<Void> physicalDeleteImage(@PathVariable String id) {
        log.info("物理删除图片 - id: {}", id);
        
        try {
            imageService.physicalDeleteImage(id);
            return ApiResponse.success("物理删除成功", null);
        } catch (Exception e) {
            log.error("物理删除图片失败 - id: {}", id, e);
            return ApiResponse.error("物理删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除图片
     *
     * @param request 批量删除请求
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ApiResponse<Map<String, Object>> batchDeleteImages(
            @Valid @RequestBody BatchDeleteRequest request) {
        log.info("批量删除图片 - ids: {}", request.getIds());
        
        try {
            Map<String, Object> result = imageService.batchDeleteImages(request.getIds(), false);
            return ApiResponse.success("批量删除成功", result);
        } catch (Exception e) {
            log.error("批量删除图片失败", e);
            return ApiResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取图片统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getImageStatistics() {
        log.info("获取图片统计信息");
        
        try {
            Map<String, Object> statistics = imageService.getImageStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取图片统计信息失败", e);
            return ApiResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有MIME类型
     *
     * @return MIME类型列表
     */
    @GetMapping("/mime-types")
    public ApiResponse<List<String>> getAllMimeTypes() {
        log.info("获取所有MIME类型");
        
        try {
            List<String> mimeTypes = imageService.getAllMimeTypes();
            return ApiResponse.success(mimeTypes);
        } catch (Exception e) {
            log.error("获取MIME类型列表失败", e);
            return ApiResponse.error("获取MIME类型失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    @GetMapping("/tags")
    public ApiResponse<List<String>> getAllTags() {
        log.info("获取所有标签");
        
        try {
            List<String> tags = imageService.getAllTags();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            return ApiResponse.error("获取标签失败: " + e.getMessage());
        }
    }

    /**
     * 获取图片访问URL
     *
     * @param id 图片ID
     * @return 访问URL
     */
    @GetMapping("/{id}/url")
    public ApiResponse<String> getImageUrl(@PathVariable String id) {
        log.info("获取图片访问URL - id: {}", id);
        
        try {
            ImageDTO image = imageService.getImageById(id);
            if (image == null) {
                return ApiResponse.notFound("图片不存在");
            }
            String url = imageService.generatePreviewUrl(image.getFilePath());
            return ApiResponse.success(url);
        } catch (Exception e) {
            log.error("获取图片访问URL失败 - id: {}", id, e);
            return ApiResponse.error("获取访问URL失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Base64上传请求
     */
    public static class Base64UploadRequest {
        @NotNull(message = "Base64数据不能为空")
        private String base64Data;
        
        @NotNull(message = "文件名不能为空")
        private String fileName;
        
        private String articleId;
        private String description;
        private List<String> tags;

        // Getters and Setters
        public String getBase64Data() {
            return base64Data;
        }

        public void setBase64Data(String base64Data) {
            this.base64Data = base64Data;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    /**
     * 图片更新请求
     */
    public static class ImageUpdateRequest {
        private String description;
        private List<String> tags;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    /**
     * 批量删除请求
     */
    public static class BatchDeleteRequest {
        @NotEmpty(message = "图片ID列表不能为空")
        private List<String> ids;

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }
}