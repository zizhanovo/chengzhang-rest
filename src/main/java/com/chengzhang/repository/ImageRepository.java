package com.chengzhang.repository;

import com.chengzhang.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 图片Repository接口
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    
    /**
     * 根据平台查询图片
     */
    Page<Image> findByPlatform(String platform, Pageable pageable);
    
    /**
     * 根据用户ID查询图片（预留）
     */
    Page<Image> findByUserId(String userId, Pageable pageable);
    
    /**
     * 根据URL查询图片
     */
    Image findByUrl(String url);
    
    /**
     * 获取最近上传的图片
     */
    List<Image> findTop20ByOrderByUploadTimeDesc();
    
    /**
     * 统计图片总数
     */
    long count();
    
    /**
     * 根据平台统计图片数量
     */
    long countByPlatform(String platform);
    
    /**
     * 计算图片总大小
     */
    @Query("SELECT SUM(i.size) FROM Image i")
    Long getTotalSize();
    
    /**
     * 根据平台计算图片总大小
     */
    @Query("SELECT SUM(i.size) FROM Image i WHERE i.platform = :platform")
    Long getTotalSizeByPlatform(@Param("platform") String platform);
}