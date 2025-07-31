package com.chengzhang.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一响应结果类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class Result<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    
    public Result() {
        this.timestamp = LocalDateTime.now();
    }
    
    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success");
    }
    
    /**
     * 成功响应带消息
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message);
    }
    
    /**
     * 成功响应带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    /**
     * 成功响应带消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }
    
    /**
     * 失败响应带错误码
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }
    
    /**
     * 参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }
    
    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message);
    }
    
    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message);
    }
    
    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message);
    }
}