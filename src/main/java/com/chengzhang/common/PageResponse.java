package com.chengzhang.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页响应结果
 *
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class PageResponse<T> {

    /**
     * 数据列表
     */
    private List<T> content;

    /**
     * 分页信息
     */
    private Pagination pagination;

    /**
     * 分页信息内部类
     */
    @Data
    public static class Pagination {
        /**
         * 当前页码（从1开始）
         */
        private Integer page;

        /**
         * 每页大小
         */
        private Integer size;

        /**
         * 总记录数
         */
        private Long total;

        /**
         * 总页数
         */
        private Integer totalPages;

        /**
         * 是否有下一页
         */
        private Boolean hasNext;

        /**
         * 是否有上一页
         */
        private Boolean hasPrevious;

        /**
         * 是否是第一页
         */
        private Boolean isFirst;

        /**
         * 是否是最后一页
         */
        private Boolean isLast;
    }

    /**
     * 私有构造函数
     */
    private PageResponse() {
    }

    /**
     * 从Spring Data的Page对象创建PageResponse
     *
     * @param page Spring Data的Page对象
     * @param <T>  数据类型
     * @return PageResponse
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.content = page.getContent();
        
        Pagination pagination = new Pagination();
        pagination.page = page.getNumber() + 1; // Spring Data的页码从0开始，转换为从1开始
        pagination.size = page.getSize();
        pagination.total = page.getTotalElements();
        pagination.totalPages = page.getTotalPages();
        pagination.hasNext = page.hasNext();
        pagination.hasPrevious = page.hasPrevious();
        pagination.isFirst = page.isFirst();
        pagination.isLast = page.isLast();
        
        response.pagination = pagination;
        return response;
    }

    /**
     * 创建空的分页响应
     *
     * @param page 页码
     * @param size 每页大小
     * @param <T>  数据类型
     * @return PageResponse
     */
    public static <T> PageResponse<T> empty(Integer page, Integer size) {
        PageResponse<T> response = new PageResponse<>();
        response.content = new ArrayList<>();
        
        Pagination pagination = new Pagination();
        pagination.page = page;
        pagination.size = size;
        pagination.total = 0L;
        pagination.totalPages = 0;
        pagination.hasNext = false;
        pagination.hasPrevious = false;
        pagination.isFirst = true;
        pagination.isLast = true;
        
        response.pagination = pagination;
        return response;
    }

    /**
     * 手动创建分页响应
     *
     * @param content    数据列表
     * @param page       当前页码
     * @param size       每页大小
     * @param total      总记录数
     * @param <T>        数据类型
     * @return PageResponse
     */
    public static <T> PageResponse<T> of(List<T> content, Integer page, Integer size, Long total) {
        PageResponse<T> response = new PageResponse<>();
        response.content = content;
        
        Pagination pagination = new Pagination();
        pagination.page = page;
        pagination.size = size;
        pagination.total = total;
        pagination.totalPages = (int) Math.ceil((double) total / size);
        pagination.hasNext = page < pagination.totalPages;
        pagination.hasPrevious = page > 1;
        pagination.isFirst = page == 1;
        pagination.isLast = page.equals(pagination.totalPages) || pagination.totalPages == 0;
        
        response.pagination = pagination;
        return response;
    }
}