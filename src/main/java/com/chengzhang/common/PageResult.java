package com.chengzhang.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应结果类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> content;
    
    /**
     * 分页信息
     */
    private Pagination pagination;
    
    public PageResult() {}
    
    public PageResult(List<T> content, Pagination pagination) {
        this.content = content;
        this.pagination = pagination;
    }
    
    /**
     * 从Spring Data Page对象创建
     */
    public static <T> PageResult<T> of(Page<T> page) {
        Pagination pagination = new Pagination(
            page.getNumber() + 1, // Spring Data页码从0开始，前端从1开始
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
        return new PageResult<>(page.getContent(), pagination);
    }
    
    /**
     * 分页信息内部类
     */
    @Data
    public static class Pagination {
        /**
         * 当前页码
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
        
        public Pagination() {}
        
        public Pagination(Integer page, Integer size, Long total, Integer totalPages) {
            this.page = page;
            this.size = size;
            this.total = total;
            this.totalPages = totalPages;
        }
    }
}