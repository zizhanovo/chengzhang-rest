package com.chengzhang.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web配置类
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${chengzhang.image.upload-path:uploads/images/}")
    private String uploadPath;

    @Value("${chengzhang.image.access-path:/uploads/images/**}")
    private String accessPath;

    /**
     * 配置静态资源映射
     * 将图片访问路径映射到本地文件系统
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        String absoluteUploadPath = projectRoot + File.separator + uploadPath;
        
        // 确保路径以文件分隔符结尾
        if (!absoluteUploadPath.endsWith(File.separator)) {
            absoluteUploadPath += File.separator;
        }
        
        // 将URL路径映射到文件系统路径
        registry.addResourceHandler(accessPath)
                .addResourceLocations("file:" + absoluteUploadPath)
                .setCachePeriod(3600) // 缓存1小时
                .resourceChain(true);
        
        // 添加通用的uploads目录映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + projectRoot + File.separator + "uploads" + File.separator)
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}