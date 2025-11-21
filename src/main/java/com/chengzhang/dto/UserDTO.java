package com.chengzhang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer status;

    /**
     * 会员信息
     */
    private MembershipInfo membership;

    /**
     * 积分信息
     */
    private PointInfo points;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembershipInfo {
        private Boolean isMember;
        private String planType;
        private String planName;
        private String endDate;
        private Integer daysRemaining;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointInfo {
        private Long balance;
        private Long totalEarned;
        private Long totalSpent;
        private Integer level;
    }
}
