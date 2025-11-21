package com.chengzhang.controller;

import com.chengzhang.common.ApiResponse;
import com.chengzhang.entity.PointAccount;
import com.chengzhang.entity.PointTransaction;
import com.chengzhang.service.PointService;
import com.chengzhang.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分控制器
 */
@RestController
@RequestMapping("/points")
@CrossOrigin(origins = "*")
public class PointController {

    @Autowired
    private PointService pointService;

    /**
     * 获取积分余额
     */
    @GetMapping("/balance")
    public ApiResponse<Map<String, Object>> getBalance(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            PointAccount account = pointService.getAccount(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("balance", account.getBalance());
            data.put("totalEarned", account.getTotalEarned());
            data.put("totalSpent", account.getTotalSpent());
            data.put("level", account.getLevel());

            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取积分交易记录
     */
    @GetMapping("/transactions")
    public ApiResponse<List<PointTransaction>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = getUserIdFromToken(token);
            List<PointTransaction> transactions = pointService.getTransactions(userId, page, size);
            return ApiResponse.success(transactions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 每日签到
     */
    @PostMapping("/checkin")
    public ApiResponse<Map<String, Object>> dailyCheckin(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Map<String, Object> result = pointService.dailyCheckin(userId);
            return ApiResponse.success("签到成功，获得" + result.get("pointsEarned") + "积分", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 消费积分
     */
    @PostMapping("/spend")
    public ApiResponse<Map<String, Object>> spendPoints(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = getUserIdFromToken(token);
            Long points = ((Number) request.get("points")).longValue();
            String service = (String) request.getOrDefault("service", "service_consume");
            String description = (String) request.getOrDefault("description", "积分消费");

            pointService.spendPoints(userId, points, service, description);

            Map<String, Object> result = new HashMap<>();
            result.put("newBalance", pointService.getBalance(userId));
            result.put("pointsSpent", points);

            return ApiResponse.success("积分消费成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 从Token获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.getUserIdFromToken(token);
    }
}
