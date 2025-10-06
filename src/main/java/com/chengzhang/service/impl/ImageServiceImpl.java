package com.chengzhang.service.impl;

import com.chengzhang.dto.ImageDTO;
import com.chengzhang.entity.Image;
import com.chengzhang.repository.ImageRepository;
import com.chengzhang.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.HashSet;

/**
 * 图片服务实现类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${app.upload.path:uploads/images}")
    private String uploadPath;

    @Value("${app.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${app.upload.allowed-types:image/jpeg,image/png,image/gif,image/webp}")
    private String allowedTypes;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    // 支持的图片格式
    private static final Set<String> SUPPORTED_FORMATS = new HashSet<String>() {{
        add("image/jpeg");
        add("image/jpg");
        add("image/png");
        add("image/gif");
        add("image/webp");
        add("image/bmp");
    }};

    // Base64图片数据正则
    private static final Pattern BASE64_PATTERN = Pattern.compile(
            "^data:image/(jpeg|jpg|png|gif|webp|bmp);base64,(.+)$"
    );

    @Override
    @Transactional
    public ImageDTO uploadImage(MultipartFile file, String articleId, String uploadIp) {
        log.debug("上传图片 - fileName: {}, size: {}, articleId: {}", 
                file.getOriginalFilename(), file.getSize(), articleId);

        // 验证文件
        Map<String, Object> validation = validateImageFile(file);
        if (!(Boolean) validation.get("valid")) {
            throw new RuntimeException((String) validation.get("message"));
        }

        try {
            // 生成文件名和路径
            String originalName = file.getOriginalFilename();
            String fileName = generateFileName(originalName);
            String filePath = saveFile(file, fileName);

            // 获取图片尺寸
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage != null ? bufferedImage.getWidth() : 0;
            int height = bufferedImage != null ? bufferedImage.getHeight() : 0;

            // 创建图片实体
            Image image = new Image();
            image.setOriginalName(originalName);
            image.setFileName(fileName);
            image.setFilePath(filePath);
            image.setFileSize(file.getSize());
            image.setMimeType(file.getContentType());
            image.setWidth(width);
            image.setHeight(height);
            image.setArticleId(articleId);
            image.setUploadIp(uploadIp);
            image.setStatus("active");

            Image savedImage = imageRepository.save(image);
            log.info("图片上传成功 - id: {}, fileName: {}", savedImage.getId(), fileName);

            return ImageDTO.fromEntity(savedImage);
        } catch (Exception e) {
            log.error("图片上传失败 - fileName: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> batchUploadImages(List<MultipartFile> files, String articleId, String uploadIp) {
        log.debug("批量上传图片 - count: {}, articleId: {}", files.size(), articleId);

        List<ImageDTO> successList = new ArrayList<>();
        List<Map<String, Object>> failedList = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                ImageDTO imageDTO = uploadImage(file, articleId, uploadIp);
                successList.add(imageDTO);
            } catch (Exception e) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("fileName", file.getOriginalFilename());
                failed.put("error", e.getMessage());
                failedList.add(failed);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());
        result.put("failedCount", failedList.size());
        result.put("successList", successList);
        result.put("failedList", failedList);

        log.info("批量上传图片完成 - 成功: {}, 失败: {}", successList.size(), failedList.size());
        return result;
    }

    @Override
    @Transactional
    public ImageDTO uploadImageFromBase64(String base64Data, String fileName, String articleId, String uploadIp) {
        log.debug("Base64上传图片 - fileName: {}, articleId: {}", fileName, articleId);

        try {
            // 解析Base64数据
            java.util.regex.Matcher matcher = BASE64_PATTERN.matcher(base64Data);
            if (!matcher.matches()) {
                throw new RuntimeException("无效的Base64图片数据格式");
            }

            String format = matcher.group(1);
            String data = matcher.group(2);
            String mimeType = "image/" + format;

            // 验证格式
            if (!SUPPORTED_FORMATS.contains(mimeType)) {
                throw new RuntimeException("不支持的图片格式: " + format);
            }

            // 解码Base64
            byte[] imageBytes = Base64.getDecoder().decode(data);
            if (imageBytes.length > maxFileSize) {
                throw new RuntimeException("文件大小超过限制: " + maxFileSize + " bytes");
            }

            // 生成文件名和保存文件
            String generatedFileName = generateFileName(fileName != null ? fileName : "image." + format);
            String filePath = saveBase64File(imageBytes, generatedFileName);

            // 获取图片尺寸
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int width = bufferedImage != null ? bufferedImage.getWidth() : 0;
            int height = bufferedImage != null ? bufferedImage.getHeight() : 0;

            // 创建图片实体
            Image image = new Image();
            image.setOriginalName(fileName != null ? fileName : "image." + format);
            image.setFileName(generatedFileName);
            image.setFilePath(filePath);
            image.setFileSize((long) imageBytes.length);
            image.setMimeType(mimeType);
            image.setWidth(width);
            image.setHeight(height);
            image.setArticleId(articleId);
            image.setUploadIp(uploadIp);
            image.setStatus("active");

            Image savedImage = imageRepository.save(image);
            log.info("Base64图片上传成功 - id: {}, fileName: {}", savedImage.getId(), generatedFileName);

            return ImageDTO.fromEntity(savedImage);
        } catch (Exception e) {
            log.error("Base64图片上传失败 - fileName: {}", fileName, e);
            throw new RuntimeException("Base64图片上传失败: " + e.getMessage());
        }
    }

    @Override
    public Page<ImageDTO> getImages(Pageable pageable, String keyword, String articleId, String mimeType, String status, String sortBy, String sortOrder) {
        log.debug("获取图片列表 - keyword: {}, articleId: {}, mimeType: {}, status: {}", 
                keyword, articleId, mimeType, status);

        // 构建排序
        Sort sort = buildSort(sortBy, sortOrder);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Image> imagePage;

        // 根据条件查询
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
                imagePage = imageRepository.findByKeywordAndStatus(keyword, status, sortedPageable);
            } else {
                imagePage = imageRepository.findByKeyword(keyword, sortedPageable);
            }
        } else if (StringUtils.isNotBlank(articleId)) {
            if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
                imagePage = imageRepository.findByArticleIdAndStatus(articleId, status, sortedPageable);
            } else {
                imagePage = imageRepository.findByArticleId(articleId, sortedPageable);
            }
        } else if (StringUtils.isNotBlank(mimeType)) {
            if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
                imagePage = imageRepository.findByMimeTypeAndStatus(mimeType, status, sortedPageable);
            } else {
                imagePage = imageRepository.findByMimeType(mimeType, sortedPageable);
            }
        } else if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
            imagePage = imageRepository.findByStatus(status, sortedPageable);
        } else {
            imagePage = imageRepository.findAll(sortedPageable);
        }

        return imagePage.map(ImageDTO::fromEntity);
    }

    @Override
    public ImageDTO getImageById(String id) {
        log.debug("获取图片详情 - id: {}", id);
        
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图片不存在: " + id));
        
        return ImageDTO.fromEntity(image);
    }

    @Override
    public ImageDTO getImageByFileName(String fileName) {
        log.debug("根据文件名获取图片 - fileName: {}", fileName);
        
        Image image = imageRepository.findByFileName(fileName);
        if (image == null) {
            throw new RuntimeException("图片不存在: " + fileName);
        }
        
        return ImageDTO.fromEntity(image);
    }

    @Override
    public ImageDTO getImageByFilePath(String filePath) {
        log.debug("根据文件路径获取图片 - filePath: {}", filePath);
        
        Image image = imageRepository.findByFilePath(filePath);
        if (image == null) {
            throw new RuntimeException("图片不存在: " + filePath);
        }
        
        return ImageDTO.fromEntity(image);
    }

    @Override
    public List<ImageDTO> getImagesByArticleId(String articleId, String status) {
        log.debug("根据文章ID获取图片列表 - articleId: {}, status: {}", articleId, status);
        
        List<Image> images;
        if (StringUtils.isNotBlank(status) && !"all".equals(status)) {
            images = imageRepository.findByArticleIdAndStatus(articleId, status);
        } else {
            images = imageRepository.findByArticleId(articleId);
        }
        
        return images.stream()
                .map(ImageDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    // 私有辅助方法
    private String generateFileName(String originalName) {
        String extension = getFileExtension(originalName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }

    private String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    private String saveFile(MultipartFile file, String fileName) throws IOException {
        // 创建按日期分组的目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadDir = Paths.get(uploadPath, dateDir);
        Files.createDirectories(uploadDir);

        // 保存文件
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return dateDir + "/" + fileName;
    }

    private String saveBase64File(byte[] data, String fileName) throws IOException {
        // 创建按日期分组的目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadDir = Paths.get(uploadPath, dateDir);
        Files.createDirectories(uploadDir);

        // 保存文件
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, data);

        return dateDir + "/" + fileName;
    }

    private Sort buildSort(String sortBy, String sortOrder) {
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "createdAt";
        }
        
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        return Sort.by(direction, sortBy);
    }

    @Override
    public Map<String, Object> validateImageFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        if (file == null || file.isEmpty()) {
            result.put("valid", false);
            result.put("message", "文件不能为空");
            return result;
        }
        
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            result.put("valid", false);
            result.put("message", "文件大小超过限制: " + maxFileSize + " bytes");
            return result;
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (!SUPPORTED_FORMATS.contains(contentType)) {
            result.put("valid", false);
            result.put("message", "不支持的文件类型: " + contentType);
            return result;
        }
        
        result.put("valid", true);
        result.put("message", "文件验证通过");
        return result;
    }

    @Override
    public String generatePreviewUrl(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        return contextPath + "/api/uploads/images/" + filePath;
    }

    // 其他方法的基础实现（可根据需要完善）
    @Override
    @Transactional
    public ImageDTO updateImage(String id, ImageDTO imageDTO) {
        // TODO: 实现图片信息更新
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public void deleteImage(String id) {
        // TODO: 实现逻辑删除
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public void physicalDeleteImage(String id) {
        // TODO: 实现物理删除
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public Map<String, Object> batchDeleteImages(List<String> ids, Boolean physical) {
        // TODO: 实现批量删除
        throw new RuntimeException("功能待实现");
    }

    @Override
    public Map<String, Object> searchImages(String keyword, String searchIn, Boolean caseSensitive,
                                          String mimeType, Long minSize, Long maxSize,
                                          Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight,
                                          LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // TODO: 实现高级搜索
        throw new RuntimeException("功能待实现");
    }

    @Override
    public Map<String, Object> getImageStatistics() {
        // TODO: 实现统计信息
        throw new RuntimeException("功能待实现");
    }

    @Override
    public List<String> getAllMimeTypes() {
        return imageRepository.findAllMimeTypes();
    }

    @Override
    public List<String> getAllTags() {
        return imageRepository.findAllTags();
    }

    @Override
    public String generateThumbnail(String imageId, Integer width, Integer height) {
        // TODO: 实现缩略图生成
        throw new RuntimeException("功能待实现");
    }

    @Override
    public String compressImage(String imageId, Integer quality) {
        // TODO: 实现图片压缩
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public Map<String, Object> cleanOrphanImages(LocalDateTime beforeDate, Boolean physical) {
        // TODO: 实现孤立图片清理
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public Map<String, Object> cleanDeletedImages(LocalDateTime beforeDate) {
        // TODO: 实现已删除图片清理
        throw new RuntimeException("功能待实现");
    }

    @Override
    public Map<String, Object> getImageFileInfo(MultipartFile file) {
        // TODO: 实现文件信息获取
        throw new RuntimeException("功能待实现");
    }

    @Override
    public Map<String, Object> getStorageInfo() {
        // TODO: 实现存储空间信息
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public Map<String, Object> repairImageData(String imageId) {
        // TODO: 实现图片数据修复
        throw new RuntimeException("功能待实现");
    }

    @Override
    @Transactional
    public Map<String, Object> syncImageFiles() {
        // TODO: 实现文件同步
        throw new RuntimeException("功能待实现");
    }
}