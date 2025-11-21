# 后端实现指南 - 会员系统

## 📦 已创建的文件清单

### 数据层 (已完成 ✅)
- `src/main/resources/sql/member_system.sql` - 完整数据库表结构
- `src/main/java/com/chengzhang/entity/`
  - `User.java` - 用户实体
  - `Subscription.java` - 订阅实体
  - `PointAccount.java` - 积分账户实体
  - `PointTransaction.java` - 积分交易记录实体

### Repository层 (已完成 ✅)
- `src/main/java/com/chengzhang/repository/`
  - `UserRepository.java`
  - `SubscriptionRepository.java`
  - `PointAccountRepository.java`
  - `PointTransactionRepository.java`

### DTO层 (已完成 ✅)
- `src/main/java/com/chengzhang/dto/`
  - `RegisterRequest.java` - 注册请求
  - `LoginRequest.java` - 登录请求
  - `UserDTO.java` - 用户响应

### 工具类 (已完成 ✅)
- `src/main/java/com/chengzhang/common/ApiResponse.java` - 统一响应格式
- `src/main/java/com/chengzhang/util/JwtUtil.java` - JWT工具类

---

## 🚀 快速部署指南

### 1. 数据库准备

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE chengzhang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE chengzhang;

# 执行SQL脚本
source /Users/zizhan/lookworld1/chengzhang-rest/src/main/resources/sql/member_system.sql
```

### 2. 配置文件 (application.yml)

需要创建或更新 `src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  application:
    name: chengzhang-rest
    
  datasource:
    url: jdbc:mysql://localhost:3306/chengzhang?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL5InnoDBDialect
        format_sql: true
        
logging:
  level:
    com.chengzhang: DEBUG
    org.hibernate.SQL: DEBUG
```

### 3. 启动后端服务

```bash
cd /Users/zizhan/lookworld1/chengzhang-rest

# 编译
mvn clean compile

# 运行
mvn spring-boot:run
```

---

## 📋 待完成任务清单

### Service层 (需要创建)

需要创建以下Service：

#### 1. AuthService.java
```java
package com.chengzhang.service;

public interface AuthService {
    // 用户注册
    UserDTO register(RegisterRequest request);
    
    // 用户登录
    Map<String, Object> login(LoginRequest request);
    
    // 验证Token
    boolean validateToken(String token);
    
    // 获取当前用户信息
    UserDTO getCurrentUser(Long userId);
}
```

#### 2. UserService.java
```java
package com.chengzhang.service;

public interface UserService {
    // 获取用户信息
    UserDTO getUserInfo(Long userId);
    
    // 更新用户信息
    UserDTO updateUserInfo(Long userId, UserDTO userDTO);
    
    // 修改密码
    void changePassword(Long userId, String oldPassword, String newPassword);
}
```

#### 3. SubscriptionService.java
```java
package com.chengzhang.service;

public interface SubscriptionService {
    // 创建订阅（购买会员）
    Subscription createSubscription(Long userId, String planType);
    
    // 获取用户的有效订阅
    Subscription getActiveSubscription(Long userId);
    
    // 检查用户是否是会员
    boolean isMember(Long userId);
}
```

#### 4. PointService.java
```java
package com.chengzhang.service;

public interface PointService {
    // 获取积分余额
    Long getBalance(Long userId);
    
    // 赠送积分
    void grantPoints(Long userId, Long points, String source, String description);
    
    // 消费积分
    void spendPoints(Long userId, Long points, String source, String description);
    
    // 获取积分交易记录
    Page<PointTransaction> getTransactions(Long userId, int page, int size);
    
    // 每日签到
    void dailyCheckin(Long userId);
}
```

### Controller层 (需要创建)

#### 1. AuthController.java
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/register")  // 注册
    @PostMapping("/login")      // 登录
    @PostMapping("/logout")     // 登出
    @GetMapping("/me")          // 获取当前用户
}
```

#### 2. SubscriptionController.java
```java
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    @GetMapping("")             // 获取订阅列表
    @PostMapping("")            // 购买会员
    @GetMapping("/active")      // 获取有效订阅
}
```

#### 3. PointController.java
```java
@RestController
@RequestMapping("/api/points")
public class PointController {
    @GetMapping("/balance")     // 获取余额
    @GetMapping("/transactions") // 获取交易记录
    @PostMapping("/checkin")    // 每日签到
    @PostMapping("/spend")      // 消费积分
}
```

---

## 🔐 安全配置 (需要添加)

### 1. 密码加密工具

需要添加 BCrypt 密码加密：

```java
package com.chengzhang.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 2. JWT拦截器

需要创建JWT拦截器验证Token：

```java
package com.chengzhang.interceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && JwtUtil.validateToken(token)) {
            Long userId = JwtUtil.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            return true;
        }
        response.setStatus(401);
        return false;
    }
}
```

---

## 📊 API文档

### 认证接口

#### 注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test123456",
  "nickname": "测试用户"
}

Response:
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户"
  }
}
```

#### 登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Test123456",
  "remember": false
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGc...",
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "membership": {
        "isMember": false
      },
      "points": {
        "balance": 0
      }
    }
  }
}
```

### 会员接口

#### 购买会员
```http
POST /api/subscriptions
Authorization: Bearer {token}
Content-Type: application/json

{
  "planType": "happy_island_6y"
}

Response:
{
  "code": 200,
  "message": "会员购买成功",
  "data": {
    "id": 1,
    "planType": "happy_island_6y",
    "planName": "幸福岛6年会员",
    "startDate": "2024-01-01 00:00:00",
    "endDate": "2030-01-01 00:00:00",
    "status": "active"
  }
}
```

### 积分接口

#### 获取积分余额
```http
GET /api/points/balance
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "balance": 10000,
    "totalEarned": 10000,
    "totalSpent": 0,
    "level": 2
  }
}
```

#### 每日签到
```http
POST /api/points/checkin
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "签到成功，获得10积分",
  "data": {
    "pointsEarned": 10,
    "continuousDays": 1,
    "newBalance": 10010
  }
}
```

---

## 🎯 核心业务流程

### 用户注册并购买会员流程

```
1. POST /api/auth/register
   创建用户账户
   
2. POST /api/auth/login
   用户登录获取Token
   
3. POST /api/subscriptions
   购买幸福岛6年会员
   - 创建订阅记录
   - 自动创建积分账户
   - 自动发放10,000初始积分
   - 记录积分交易
   
4. GET /api/points/balance
   查看积分余额（应为10,000）
```

### 积分消费流程

```
1. GET /api/points/services
   查看可用的积分服务
   
2. POST /api/points/spend
   {
     "serviceId": 1,
     "points": 100
   }
   消费积分购买服务
   
3. GET /api/points/transactions
   查看积分交易记录
```

---

## 🐛 常见问题

### Q1: 数据库连接失败
**A:** 检查 application.yml 中的数据库配置，确保MySQL服务已启动

### Q2: JWT Token无效
**A:** Token可能已过期，需要重新登录

### Q3: 积分扣除失败
**A:** 检查积分余额是否足够，确保在事务中执行

---

## 📝 下一步开发计划

1. ✅ 完成Service层实现
2. ✅ 完成Controller层实现
3. ✅ 添加全局异常处理
4. ✅ 添加参数验证
5. ⏳ 实现定时任务（每月发放积分）
6. ⏳ 集成支付接口
7. ⏳ 添加Redis缓存
8. ⏳ 前端API对接

---

## 💻 简化版实现建议

考虑到时间，建议采用以下简化实现：

1. **暂不实现支付**：购买会员时直接创建订阅记录（模拟已支付）
2. **暂不实现定时任务**：首次购买时一次性发放全部积分（46,000）
3. **暂不使用Spring Security**：使用简单的JWT拦截器即可
4. **密码加密可选**：开发阶段可以明文存储，生产环境再加密

这样可以快速让系统跑起来，后续再逐步完善！
