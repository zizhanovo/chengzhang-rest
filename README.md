# 成长写作编辑器 - 后端服务

这是一个专为自媒体博主设计的写作编辑器的后端服务，基于Spring Boot框架开发。

## 技术栈

- **框架**: Spring Boot 2.7.0
- **ORM**: Hibernate (Spring Data JPA)
- **数据库**: MySQL 5.7
- **构建工具**: Maven
- **Java版本**: JDK 8+

## 主要功能

- 📝 文章管理（CRUD操作）
- 🖼️ 图片上传和管理
- ⚙️ 用户设置管理
- 📊 数据统计
- 🔍 文章搜索和分类
- 📄 分页查询

## 快速开始

### 1. 环境要求

- JDK 8 或更高版本
- Maven 3.6+
- MySQL 5.7

### 2. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE chengzhang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chengzhang?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 3. 运行项目

1. 克隆项目到本地
2. 进入项目目录
3. 运行以下命令：

```bash
# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

或者使用IDE直接运行 `ChengzhangApplication.java`

### 4. 访问接口

项目启动后，可以通过以下地址访问：

- 基础URL: `http://localhost:8080/api`
- 接口文档: 参考前端项目中的API文档

## API接口

### 文章管理
- `GET /api/articles` - 获取文章列表
- `GET /api/articles/{id}` - 获取文章详情
- `POST /api/articles` - 创建文章
- `PUT /api/articles/{id}` - 更新文章
- `DELETE /api/articles/{id}` - 删除文章
- `DELETE /api/articles/batch` - 批量删除文章
- `GET /api/articles/categories` - 获取分类列表
- `GET /api/articles/stats` - 获取文章统计
- `GET /api/articles/recent` - 获取最近文章

### 图片管理
- `POST /api/upload/image` - 上传图片
- `GET /api/upload/images` - 获取图片列表
- `DELETE /api/upload/images/{id}` - 删除图片
- `GET /api/upload/images/stats` - 获取图片统计

### 设置管理
- `GET /api/settings` - 获取用户设置
- `PUT /api/settings` - 更新用户设置
- `POST /api/settings/reset` - 重置为默认设置

## 项目结构

```
src/main/java/com/chengzhang/
├── ChengzhangApplication.java          # 启动类
├── common/                             # 通用类
│   ├── Result.java                     # 统一响应格式
│   └── PageResult.java                 # 分页响应格式
├── config/                             # 配置类
│   └── CorsConfig.java                 # CORS配置
├── controller/                         # 控制器
│   ├── ArticleController.java          # 文章控制器
│   ├── UploadController.java           # 上传控制器
│   └── SettingsController.java         # 设置控制器
├── dto/                                # 数据传输对象
│   ├── ArticleDTO.java                 # 文章DTO
│   ├── ArticleQueryDTO.java            # 文章查询DTO
│   └── BatchDeleteDTO.java             # 批量删除DTO
├── entity/                             # 实体类
│   ├── Article.java                    # 文章实体
│   ├── Image.java                      # 图片实体
│   └── Settings.java                   # 设置实体
├── exception/                          # 异常处理
│   └── GlobalExceptionHandler.java     # 全局异常处理器
├── repository/                         # 数据访问层
│   ├── ArticleRepository.java          # 文章仓库
│   ├── ImageRepository.java            # 图片仓库
│   └── SettingsRepository.java         # 设置仓库
├── service/                            # 服务接口
│   ├── ArticleService.java             # 文章服务接口
│   ├── ImageService.java               # 图片服务接口
│   └── SettingsService.java            # 设置服务接口
├── service/impl/                       # 服务实现
│   ├── ArticleServiceImpl.java         # 文章服务实现
│   ├── ImageServiceImpl.java           # 图片服务实现
│   └── SettingsServiceImpl.java        # 设置服务实现
└── util/                               # 工具类
    └── JsonUtil.java                   # JSON工具类
```

## 配置说明

### application.yml 主要配置项

- `server.port`: 服务端口（默认8080）
- `server.servlet.context-path`: 上下文路径（/api）
- `spring.datasource`: 数据库连接配置
- `spring.jpa`: JPA配置
- `spring.servlet.multipart`: 文件上传配置
- `logging`: 日志配置

## 注意事项

1. 首次运行时，Hibernate会自动创建数据库表结构
2. 图片上传功能目前使用模拟实现，实际使用时需要配置真实的图床API
3. 项目默认使用内存中的用户系统（userId=default），实际使用时需要集成真实的用户认证系统
4. 建议在生产环境中配置适当的日志级别和数据库连接池

## 开发说明

- 所有接口都返回统一的JSON格式
- 使用了全局异常处理器处理各种异常情况
- 支持跨域请求，方便前端开发调试
- 使用了Lombok简化代码，需要IDE安装Lombok插件

## 许可证

MIT License