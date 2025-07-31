package com.chengzhang.service.impl;

import com.chengzhang.common.PageResult;
import com.chengzhang.entity.Image;
import com.chengzhang.repository.ImageRepository;
import com.chengzhang.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片服务实现类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    
    private final ImageRepository imageRepository;
    
    @Override
    @Transactional
    public Image uploadImage(MultipartFile file, String platform, Boolean compress) {
        try {
            // 获取图片尺寸
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage != null ? bufferedImage.getWidth() : 0;
            int height = bufferedImage != null ? bufferedImage.getHeight() : 0;
            
            // 模拟上传到图床（实际项目中需要调用具体图床API）
            String imageUrl = simulateUploadToImageBed(file, platform);
            
            // 创建图片记录
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setFilename(file.getOriginalFilename());
            image.setSize(file.getSize());
            image.setWidth(width);
            image.setHeight(height);
            image.setPlatform(platform);
            
            image = imageRepository.save(image);
            log.info("图片上传成功，ID: {}, URL: {}", image.getId(), image.getUrl());
            
            return image;
            
        } catch (IOException e) {
            log.error("读取图片文件失败", e);
            throw new RuntimeException("图片文件格式错误");
        } catch (Exception e) {
            log.error("上传图片失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Image> getImages(Integer page, Integer size, String platform) {
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        Page<Image> imagePage;
        if (StringUtils.hasText(platform)) {
            imagePage = imageRepository.findByPlatform(platform, pageable);
        } else {
            imagePage = imageRepository.findAll(pageable);
        }
        
        return PageResult.of(imagePage);
    }
    
    @Override
    @Transactional
    public void deleteImage(String id) {
        Image image = imageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("图片不存在"));
        
        // 这里可以添加删除图床上图片的逻辑
        // deleteFromImageBed(image.getUrl(), image.getPlatform());
        
        imageRepository.deleteById(id);
        log.info("删除图片成功，ID: {}", id);
    }
    
    @Override
    public Map<String, Object> getImageStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalImages", imageRepository.count());
        stats.put("totalSize", imageRepository.getTotalSize());
        
        // 按平台统计
        Map<String, Object> platformStats = new HashMap<>();
        platformStats.put("smms", imageRepository.countByPlatform("smms"));
        platformStats.put("imgbb", imageRepository.countByPlatform("imgbb"));
        platformStats.put("github", imageRepository.countByPlatform("github"));
        stats.put("platformStats", platformStats);
        
        return stats;
    }
    
    /**
     * 模拟上传到图床
     * 实际项目中需要根据不同平台调用相应的API
     */
    private String simulateUploadToImageBed(MultipartFile file, String platform) {
        // 这里只是模拟，实际需要调用真实的图床API
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = file.getOriginalFilename();
        String extension = filename != null && filename.contains(".") ? 
            filename.substring(filename.lastIndexOf(".")) : ".jpg";
        
        switch (platform) {
            case "smms":
                return "https://i.loli.net/2024/01/02/" + timestamp + extension;
            case "imgbb":
                return "https://i.ibb.co/" + timestamp + "/" + timestamp + extension;
            case "github":
                return "https://raw.githubusercontent.com/user/repo/main/images/" + timestamp + extension;
            default:
                return "https://example.com/images/" + timestamp + extension;
        }
    }
}