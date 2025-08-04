package com.chengzhang.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一API响应结果
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class ApiResponse<T> {

    /**
     * 响应状态码
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
     * 响应时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 私有构造函数
     */
    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 私有构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 成功响应（带消息和数据）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "创建成功", data);
    }

    /**
     * 创建成功响应（带消息）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data);
    }

    /**
     * 错误响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 错误响应（带状态码）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 参数错误响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null);
    }

    /**
     * 未找到响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code >= 200 && this.code < 300;
    }
}