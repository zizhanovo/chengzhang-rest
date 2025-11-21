-- ============================================
-- 成章写作 - 会员系统 + 虚拟货币系统
-- ============================================

-- 1. 用户表
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `phone` VARCHAR(20) COMMENT '手机号',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_email` (`email`),
  INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 会员订阅表
CREATE TABLE `subscriptions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订阅ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `plan_type` VARCHAR(50) NOT NULL COMMENT '会员类型：happy_island_6y（幸福岛6年）',
  `plan_name` VARCHAR(100) NOT NULL COMMENT '会员名称',
  `price` DECIMAL(10,2) NOT NULL COMMENT '订阅价格',
  `start_date` TIMESTAMP NOT NULL COMMENT '订阅开始时间',
  `end_date` TIMESTAMP NOT NULL COMMENT '订阅结束时间',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '订阅状态：active-有效 expired-过期 cancelled-已取消',
  `auto_renew` TINYINT DEFAULT 0 COMMENT '是否自动续费：0-否 1-是',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_end_date` (`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员订阅表';

-- 3. 虚拟货币账户表（成长积分）
CREATE TABLE `point_accounts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账户ID',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `balance` BIGINT DEFAULT 0 COMMENT '当前积分余额',
  `total_earned` BIGINT DEFAULT 0 COMMENT '累计获得积分',
  `total_spent` BIGINT DEFAULT 0 COMMENT '累计消费积分',
  `level` INT DEFAULT 1 COMMENT '积分等级',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_balance` (`balance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分账户表';

-- 4. 积分交易记录表
CREATE TABLE `point_transactions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '交易类型：earn-获得 spend-消费',
  `amount` BIGINT NOT NULL COMMENT '积分数量（正数为获得，负数为消费）',
  `balance_after` BIGINT NOT NULL COMMENT '交易后余额',
  `source` VARCHAR(50) NOT NULL COMMENT '来源：subscription-订阅 daily_sign-签到 article_publish-发文 service_consume-服务消费等',
  `source_id` VARCHAR(100) COMMENT '来源ID（如订阅ID、文章ID等）',
  `description` VARCHAR(255) COMMENT '交易描述',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_source` (`source`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分交易记录表';

-- 5. 订阅积分发放记录表（每月自动发放）
CREATE TABLE `subscription_point_grants` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '发放记录ID',
  `subscription_id` BIGINT NOT NULL COMMENT '订阅ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `grant_month` VARCHAR(7) NOT NULL COMMENT '发放月份：YYYY-MM',
  `points` BIGINT NOT NULL COMMENT '发放积分数',
  `status` VARCHAR(20) DEFAULT 'granted' COMMENT '状态：granted-已发放 pending-待发放',
  `granted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发放时间',
  FOREIGN KEY (`subscription_id`) REFERENCES `subscriptions`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_subscription_month` (`subscription_id`, `grant_month`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_grant_month` (`grant_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅积分发放记录表';

-- 6. 积分商品/服务表
CREATE TABLE `point_services` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '服务ID',
  `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `description` TEXT COMMENT '服务描述',
  `category` VARCHAR(50) NOT NULL COMMENT '类别：writing_review-写作点评 ai_assist-AI辅助 course-课程 coaching-私教等',
  `points_required` BIGINT NOT NULL COMMENT '所需积分',
  `stock` INT DEFAULT -1 COMMENT '库存：-1表示无限制',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-上架',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分服务表';

-- 7. 积分服务消费记录表
CREATE TABLE `point_service_orders` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `service_id` BIGINT NOT NULL COMMENT '服务ID',
  `points_spent` BIGINT NOT NULL COMMENT '消费积分',
  `status` VARCHAR(20) DEFAULT 'completed' COMMENT '订单状态：completed-已完成 cancelled-已取消',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`service_id`) REFERENCES `point_services`(`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分服务消费记录表';

-- 8. 每日签到记录表
CREATE TABLE `daily_checkins` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '签到ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `checkin_date` DATE NOT NULL COMMENT '签到日期',
  `points_earned` BIGINT DEFAULT 10 COMMENT '获得积分',
  `continuous_days` INT DEFAULT 1 COMMENT '连续签到天数',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_checkin_date` (`checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日签到表';

-- 9. 支付订单表（可选 - 用于支付流程）
CREATE TABLE `payment_orders` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `subscription_id` BIGINT COMMENT '关联的订阅ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `payment_method` VARCHAR(20) COMMENT '支付方式：wechat-微信 alipay-支付宝',
  `payment_status` VARCHAR(20) DEFAULT 'pending' COMMENT '支付状态：pending-待支付 paid-已支付 cancelled-已取消 refunded-已退款',
  `paid_at` TIMESTAMP NULL COMMENT '支付时间',
  `transaction_id` VARCHAR(100) COMMENT '第三方交易ID',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入积分服务示例
INSERT INTO `point_services` (`name`, `description`, `category`, `points_required`, `sort_order`) VALUES
('文章深度点评', '专业导师对您的文章进行深度点评和修改建议', 'writing_review', 100, 1),
('AI智能润色', '使用AI对文章进行智能润色和优化', 'ai_assist', 50, 2),
('素材图片下载', '下载高质量写作素材图片', 'material', 20, 3),
('精品课程（入门）', '写作技巧入门课程', 'course', 200, 4),
('精品课程（进阶）', '写作技巧进阶课程', 'course', 500, 5),
('一对一私教咨询（1小时）', '与资深导师进行一对一写作指导', 'coaching', 1000, 6),
('敦煌素材包', '泡泡柱拍摄的敦煌风景大片素材包', 'material', 500, 7);

-- ============================================
-- 存储过程：会员购买时自动发放初始积分
-- ============================================
DELIMITER $$

CREATE PROCEDURE grant_initial_points(
    IN p_user_id BIGINT,
    IN p_subscription_id BIGINT,
    IN p_initial_points BIGINT
)
BEGIN
    DECLARE current_balance BIGINT;

    -- 查询当前余额
    SELECT balance INTO current_balance FROM point_accounts WHERE user_id = p_user_id;

    -- 如果账户不存在则创建
    IF current_balance IS NULL THEN
        INSERT INTO point_accounts (user_id, balance, total_earned)
        VALUES (p_user_id, p_initial_points, p_initial_points);
        SET current_balance = p_initial_points;
    ELSE
        -- 更新账户余额
        UPDATE point_accounts
        SET balance = balance + p_initial_points,
            total_earned = total_earned + p_initial_points
        WHERE user_id = p_user_id;
        SET current_balance = current_balance + p_initial_points;
    END IF;

    -- 记录交易
    INSERT INTO point_transactions (user_id, type, amount, balance_after, source, source_id, description)
    VALUES (p_user_id, 'earn', p_initial_points, current_balance, 'subscription', p_subscription_id, '会员订阅初始积分赠送');
END$$

DELIMITER ;

-- ============================================
-- 存储过程：每月自动发放订阅积分
-- ============================================
DELIMITER $$

CREATE PROCEDURE grant_monthly_points(
    IN p_user_id BIGINT,
    IN p_subscription_id BIGINT,
    IN p_grant_month VARCHAR(7),
    IN p_monthly_points BIGINT
)
BEGIN
    DECLARE current_balance BIGINT;
    DECLARE already_granted INT;

    -- 检查本月是否已发放
    SELECT COUNT(*) INTO already_granted
    FROM subscription_point_grants
    WHERE subscription_id = p_subscription_id AND grant_month = p_grant_month;

    IF already_granted = 0 THEN
        -- 查询当前余额
        SELECT balance INTO current_balance FROM point_accounts WHERE user_id = p_user_id;

        -- 更新账户余额
        UPDATE point_accounts
        SET balance = balance + p_monthly_points,
            total_earned = total_earned + p_monthly_points
        WHERE user_id = p_user_id;

        SET current_balance = current_balance + p_monthly_points;

        -- 记录交易
        INSERT INTO point_transactions (user_id, type, amount, balance_after, source, source_id, description)
        VALUES (p_user_id, 'earn', p_monthly_points, current_balance, 'subscription', p_subscription_id,
                CONCAT(p_grant_month, ' 月度会员积分发放'));

        -- 记录发放
        INSERT INTO subscription_point_grants (subscription_id, user_id, grant_month, points, granted_at)
        VALUES (p_subscription_id, p_user_id, p_grant_month, p_monthly_points, NOW());
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 存储过程：消费积分
-- ============================================
DELIMITER $$

CREATE PROCEDURE spend_points(
    IN p_user_id BIGINT,
    IN p_service_id BIGINT,
    IN p_points BIGINT,
    OUT p_success BOOLEAN,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE current_balance BIGINT;

    -- 查询当前余额
    SELECT balance INTO current_balance FROM point_accounts WHERE user_id = p_user_id;

    -- 检查余额是否足够
    IF current_balance >= p_points THEN
        -- 扣除积分
        UPDATE point_accounts
        SET balance = balance - p_points,
            total_spent = total_spent + p_points
        WHERE user_id = p_user_id;

        SET current_balance = current_balance - p_points;

        -- 记录交易
        INSERT INTO point_transactions (user_id, type, amount, balance_after, source, source_id, description)
        VALUES (p_user_id, 'spend', -p_points, current_balance, 'service_consume', p_service_id, '积分服务消费');

        -- 记录服务订单
        INSERT INTO point_service_orders (user_id, service_id, points_spent)
        VALUES (p_user_id, p_service_id, p_points);

        SET p_success = TRUE;
        SET p_message = '积分消费成功';
    ELSE
        SET p_success = FALSE;
        SET p_message = '积分余额不足';
    END IF;
END$$

DELIMITER ;
