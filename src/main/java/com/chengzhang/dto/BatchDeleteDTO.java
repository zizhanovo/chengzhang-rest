package com.chengzhang.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量删除请求DTO
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@Data
public class BatchDeleteDTO {
    
    /**
     * 要删除的ID列表
     */
    @NotEmpty(message = "删除ID列表不能为空")
    @Size(max = 50, message = "单次最多删除50条记录")
    private List<String> ids;
}