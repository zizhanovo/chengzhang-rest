package com.chengzhang.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON工具类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Slf4j
public class JsonUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败", e);
            return null;
        }
    }
    
    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败", e);
            return null;
        }
    }
}