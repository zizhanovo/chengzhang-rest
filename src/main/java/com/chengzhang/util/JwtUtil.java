package com.chengzhang.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类（简化版，不依赖外部库）
 */
public class JwtUtil {

    private static final String SECRET_KEY = "chengzhang_secret_key_2024";
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7天
    private static final long EXPIRATION_TIME_REMEMBER = 30L * 24 * 60 * 60 * 1000; // 30天

    /**
     * 生成Token（简化版）
     */
    public static String generateToken(Long userId, String email, boolean remember) {
        long expiration = remember ? EXPIRATION_TIME_REMEMBER : EXPIRATION_TIME;
        long exp = System.currentTimeMillis() + expiration;

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("email", email);
        payload.put("iat", System.currentTimeMillis());
        payload.put("exp", exp);

        String payloadJson = toJson(payload);
        return Base64.getEncoder().encodeToString(payloadJson.getBytes());
    }

    /**
     * 验证Token
     */
    public static boolean validateToken(String token) {
        try {
            Map<String, Object> payload = parseToken(token);
            Long exp = ((Number) payload.get("exp")).longValue();
            return exp > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Map<String, Object> payload = parseToken(token);
            return ((Number) payload.get("userId")).longValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析Token
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseToken(String token) {
        String json = new String(Base64.getDecoder().decode(token));
        return fromJson(json, Map.class);
    }

    /**
     * 简易JSON序列化
     */
    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 简易JSON反序列化
     */
    @SuppressWarnings("unchecked")
    private static <T> T fromJson(String json, Class<T> clazz) {
        Map<String, Object> map = new HashMap<>();
        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].replaceAll("\"", "").trim();
            String value = keyValue[1].trim();
            if (value.startsWith("\"")) {
                map.put(key, value.replaceAll("\"", ""));
            } else {
                map.put(key, Long.parseLong(value));
            }
        }
        return (T) map;
    }
}
