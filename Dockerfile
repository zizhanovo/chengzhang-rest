# 后端 Dockerfile
FROM maven:3.8.6-openjdk-8-slim AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用Docker缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段 - 使用 Eclipse Temurin (原 AdoptOpenJDK)
FROM eclipse-temurin:8-jre

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# 创建应用目录
WORKDIR /app

# 从构建阶段复制jar包
COPY --from=build /app/target/chengzhang-rest-*.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads/images && \
    mkdir -p /app/logs

# 暴露端口
EXPOSE 8081

# 健康检查（检查应用是否响应）
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/api/articles?page=0&size=1 || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]

