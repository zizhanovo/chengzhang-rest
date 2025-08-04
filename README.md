# 成章写作工作台 - 后端服务

## 项目简介

成章写作工作台后端服务是一个基于Spring Boot开发的RESTful API服务，为前端Vue.js应用提供文章管理、搜索、导入导出等功能。

## 技术栈

- **框架**: Spring Boot 2.7.18
- **ORM**: Hibernate (Spring Data JPA)
- **数据库**: MySQL 5.7
- **构建工具**: Maven
- **Java版本**: JDK 8+

## 功能特性

### 核心功能
- ✅ 文章CRUD操作（创建、读取、更新、删除）
- ✅ 文章分页查询和排序
- ✅ 文章搜索（标题、内容、标签）
- ✅ 文章分类管理
- ✅ 批量操作（批量删除）
- ✅ 数据导入导出（JSON格式）
- ✅ 文章统计信息
- ✅ 自动生成摘要和字数统计

### 技术特性
- ✅ RESTful API设计
- ✅ 统一响应格式
- ✅ 全局异常处理
- ✅ 参数校验
- ✅ 跨域支持
- ✅ 数据库连接池
- ✅ 事务管理
- ✅ 日志记录

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+
- MySQL 5.7+

### 数据库准备

1. 创建数据库：
```sql
CREATE DATABASE chengzhang DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p chengzhang < src/main/resources/sql/init.sql
```

### 配置文件

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chengzhang?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 运行项目

1. 克隆项目：
```bash
git clone <repository-url>
cd chengzhang-rest
```

2. 编译项目：
```bash
mvn clean compile
```

3. 运行项目：
```bash
mvn spring-boot:run
```

4. 或者打包后运行：
```bash
mvn clean package
java -jar target/chengzhang-rest-1.0.0.jar
```

### 访问服务

- 服务地址：http://localhost:8080/api
- 健康检查：http://localhost:8080/api/actuator/health（如果启用了actuator）

## API 文档

### 文章管理接口

#### 1. 获取文章列表
```
GET /api/articles
```

**请求参数：**
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `keyword`: 搜索关键词
- `category`: 文章分类
- `status`: 文章状态（draft/published）
- `sortBy`: 排序字段（createdAt/updatedAt/title）
- `sortOrder`: 排序方向（asc/desc）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "article_001",
        "title": "文章标题",
        "content": "文章内容...",
        "summary": "文章摘要",
        "status": "published",
        "category": "技术",
        "tags": ["Vue.js", "前端"],
        "wordCount": 1200,
        "readTime": 6,
        "createdAt": "2024-01-01 10:00:00",
        "updatedAt": "2024-01-01 15:30:00"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 25,
      "totalPages": 3,
      "hasNext": true,
      "hasPrevious": false
    }
  },
  "timestamp": "2024-01-01 16:00:00"
}
```

#### 2. 获取文章详情
```
GET /api/articles/{id}
```

#### 3. 创建文章
```
POST /api/articles
```

**请求体：**
```json
{
  "title": "文章标题",
  "content": "文章内容（Markdown格式）",
  "summary": "文章摘要",
  "status": "draft",
  "category": "技术",
  "tags": ["标签1", "标签2"]
}
```

#### 4. 更新文章
```
PUT /api/articles/{id}
```

#### 5. 删除文章
```
DELETE /api/articles/{id}
```

#### 6. 批量删除文章
```
DELETE /api/articles/batch
```

**请求体：**
```json
{
  "ids": ["article_001", "article_002"]
}
```

### 搜索接口

#### 搜索文章
```
GET /api/articles/search?keyword=Vue&searchIn=title&status=published
```

### 数据管理接口

#### 1. 导出数据
```
GET /api/articles/export
```

#### 2. 导入数据
```
POST /api/articles/import
```

#### 3. 清空数据
```
DELETE /api/articles/clear
```

### 统计接口

#### 1. 获取统计信息
```
GET /api/articles/statistics
```

#### 2. 获取所有分类
```
GET /api/articles/categories
```

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── chengzhang/
│   │           ├── ChengzhangApplication.java     # 主启动类
│   │           ├── common/                        # 通用类
│   │           │   ├── ApiResponse.java          # 统一响应格式
│   │           │   └── PageResponse.java         # 分页响应格式
│   │           ├── config/                        # 配置类
│   │           │   └── CorsConfig.java           # 跨域配置
│   │           ├── controller/                    # 控制器
│   │           │   └── ArticleController.java    # 文章控制器
│   │           ├── dto/                          # 数据传输对象
│   │           │   └── ArticleDTO.java          # 文章DTO
│   │           ├── entity/                       # 实体类
│   │           │   └── Article.java             # 文章实体
│   │           ├── exception/                    # 异常处理
│   │           │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │           ├── repository/                   # 数据访问层
│   │           │   └── ArticleRepository.java   # 文章仓库
│   │           └── service/                      # 服务层
│   │               ├── ArticleService.java      # 文章服务接口
│   │               └── impl/
│   │                   └── ArticleServiceImpl.java # 文章服务实现
│   └── resources/
│       ├── application.yml                       # 应用配置
│       └── sql/
│           └── init.sql                         # 数据库初始化脚本
└── test/                                        # 测试代码
```

## 开发指南

### 代码规范

- 使用Lombok减少样板代码
- 遵循RESTful API设计原则
- 统一异常处理和响应格式
- 使用@Valid进行参数校验
- 合理使用事务注解@Transactional

### 数据库设计

- 使用UUID作为主键
- 合理设计索引提高查询性能
- 使用JSON格式存储数组类型数据
- 设置合适的字段长度和约束

### 性能优化

- 使用分页查询避免大量数据加载
- 合理使用数据库索引
- 避免N+1查询问题
- 使用连接池管理数据库连接

## 部署说明

### 开发环境

```bash
# 启动开发服务器
mvn spring-boot:run
```

### 生产环境

1. 打包应用：
```bash
mvn clean package -Dmaven.test.skip=true
```

2. 运行应用：
```bash
java -jar target/chengzhang-rest-1.0.0.jar --spring.profiles.active=prod
```

3. 使用Docker部署：
```dockerfile
FROM openjdk:8-jre-alpine
VOLUME /tmp
COPY target/chengzhang-rest-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 常见问题

### 1. 数据库连接失败
- 检查数据库服务是否启动
- 确认数据库连接信息是否正确
- 检查防火墙设置

### 2. 跨域问题
- 确认前端地址是否在CORS配置中
- 检查请求头是否正确

### 3. 中文乱码
- 确保数据库字符集为utf8mb4
- 检查连接URL中的字符编码设置

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 作者：chengzhang
- 邮箱：your.email@example.com
- 项目地址：https://github.com/your-username/chengzhang-rest