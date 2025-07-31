package com.chengzhang.controller;

import com.chengzhang.common.PageResult;
import com.chengzhang.common.Result;
import com.chengzhang.entity.Image;
import com.chengzhang.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传控制器
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    
    private final ImageService imageService;
    
    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "smms") String platform,
            @RequestParam(defaultValue = "true") Boolean compress) {
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.badRequest("请选择要上传的文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.badRequest("只能上传图片文件");
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.badRequest("图片大小不能超过10MB");
            }
            
            // 上传图片
            Image image = imageService.uploadImage(file, platform, compress);
            
            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("url", image.getUrl());
            result.put("filename", image.getFilename());
            result.put("size", image.getSize());
            result.put("width", image.getWidth());
            result.put("height", image.getHeight());
            result.put("platform", image.getPlatform());
            result.put("uploadTime", image.getUploadTime());
            
            return Result.success("图片上传成功", result);
            
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取图片列表
     */
    @GetMapping("/images")
    public Result<PageResult<Image>> getImages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String platform) {
        
        try {
            PageResult<Image> result = imageService.getImages(page, size, platform);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取图片列表失败", e);
            return Result.error("获取图片列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除图片
     */
    @DeleteMapping("/images/{id}")
    public Result<Void> deleteImage(@PathVariable String id) {
        try {
            imageService.deleteImage(id);
            return Result.success("图片删除成功");
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("删除图片失败", e);
            return Result.error("删除图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取图片统计信息
     */
    @GetMapping("/images/stats")
    public Result<Map<String, Object>> getImageStats() {
        try {
            Map<String, Object> stats = imageService.getImageStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取图片统计失败", e);
            return Result.error("获取图片统计失败: " + e.getMessage());
        }
    }
}