package com.chengzhang.service;

import com.chengzhang.common.PageResult;
import com.chengzhang.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 图片服务接口
 * 
 * @author chengzhang
 * @since 1.0.0
 */
public interface ImageService {
    
    /**
     * 上传图片到图床
     */
    Image uploadImage(MultipartFile file, String platform, Boolean compress);
    
    /**
     * 分页获取图片列表
     */
    PageResult<Image> getImages(Integer page, Integer size, String platform);
    
    /**
     * 删除图片
     */
    void deleteImage(String id);
    
    /**
     * 获取图片统计信息
     */
    Map<String, Object> getImageStats();
}