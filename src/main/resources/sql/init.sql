-- 成章写作工作台数据库初始化脚本
-- 数据库版本：MySQL 5.7
-- 创建时间：2024-01-01
-- 作者：chengzhang

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `chengzhang` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `chengzhang`;

-- 创建文章表
CREATE TABLE IF NOT EXISTS `articles` (
    `id` VARCHAR(36) NOT NULL COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `content` LONGTEXT COMMENT '文章内容（Markdown格式）',
    `summary` VARCHAR(500) COMMENT '文章摘要',
    `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '文章状态：draft-草稿，published-已发布',
    `category` VARCHAR(50) COMMENT '文章分类',
    `tags` TEXT COMMENT '文章标签（JSON格式存储）',
    `word_count` INT DEFAULT 0 COMMENT '字数统计',
    `read_time` INT DEFAULT 0 COMMENT '预计阅读时间（分钟）',
    `images` TEXT COMMENT '文章图片（JSON格式存储）',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_title` (`title`),
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_updated_at` (`updated_at`),
    FULLTEXT INDEX `idx_content` (`content`),
    FULLTEXT INDEX `idx_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 插入示例数据
INSERT INTO `articles` (
    `id`, `title`, `content`, `summary`, `status`, `category`, `tags`, 
    `word_count`, `read_time`, `images`, `created_at`, `updated_at`
) VALUES 
(
    'article_001',
    'Vue.js 入门指南',
    '# Vue.js 入门指南\n\n## 什么是Vue.js\n\nVue.js是一个渐进式JavaScript框架，用于构建用户界面。与其它大型框架不同的是，Vue被设计为可以自底向上逐层应用。\n\n## 核心特性\n\n### 1. 响应式数据绑定\n\nVue.js提供了响应式的数据绑定系统，当数据发生变化时，视图会自动更新。\n\n```javascript\nvar app = new Vue({\n  el: "#app",\n  data: {\n    message: "Hello Vue!"\n  }\n});\n```\n\n### 2. 组件系统\n\nVue.js拥有强大的组件系统，可以将复杂的界面拆分成独立、可复用的组件。\n\n### 3. 指令系统\n\nVue.js提供了丰富的指令，如v-if、v-for、v-model等，让模板更加简洁和直观。\n\n## 总结\n\nVue.js是一个优秀的前端框架，适合初学者入门，也能满足复杂项目的需求。',
    'Vue.js是一个渐进式JavaScript框架，用于构建用户界面。本文介绍了Vue.js的核心特性，包括响应式数据绑定、组件系统和指令系统。',
    'published',
    '技术',
    '["Vue.js", "前端", "JavaScript", "框架"]',
    1200,
    6,
    '[]',
    '2024-01-01 10:00:00',
    '2024-01-01 15:30:00'
),
(
    'article_002',
    'Spring Boot 快速开发指南',
    '# Spring Boot 快速开发指南\n\n## 简介\n\nSpring Boot是Spring框架的一个子项目，旨在简化Spring应用的创建、运行、调试、部署等操作。\n\n## 核心特性\n\n### 1. 自动配置\n\nSpring Boot能够根据类路径中的jar包、类，为jar包里的类自动配置Bean。\n\n### 2. 起步依赖\n\nSpring Boot提供了一系列起步依赖，简化了Maven或Gradle的配置。\n\n### 3. 内嵌服务器\n\nSpring Boot内嵌了Tomcat、Jetty等服务器，无需部署WAR文件。\n\n## 快速开始\n\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```\n\n## 总结\n\nSpring Boot大大简化了Spring应用的开发过程，是Java Web开发的首选框架。',
    'Spring Boot是Spring框架的一个子项目，旨在简化Spring应用的开发。本文介绍了Spring Boot的核心特性和快速开始方法。',
    'published',
    '技术',
    '["Spring Boot", "Java", "后端", "框架"]',
    800,
    4,
    '[]',
    '2024-01-02 09:00:00',
    '2024-01-02 14:20:00'
),
(
    'article_003',
    '我的编程学习心得',
    '# 我的编程学习心得\n\n## 前言\n\n作为一名程序员，我想分享一些自己在编程学习路上的心得体会。\n\n## 学习方法\n\n### 1. 理论与实践结合\n\n光看书是不够的，一定要动手实践，通过写代码来加深理解。\n\n### 2. 循序渐进\n\n不要急于求成，要按照学习路线一步步来，打好基础很重要。\n\n### 3. 多做项目\n\n通过实际项目来检验学习成果，发现知识盲点。\n\n## 技术栈选择\n\n根据自己的兴趣和市场需求来选择技术栈，不要盲目跟风。\n\n## 持续学习\n\n技术更新很快，要保持学习的习惯，关注新技术的发展。\n\n## 总结\n\n编程学习是一个长期的过程，需要耐心和坚持。',
    '作为一名程序员，分享编程学习路上的心得体会，包括学习方法、技术栈选择和持续学习的重要性。',
    'draft',
    '心得',
    '["编程", "学习", "心得", "经验"]',
    600,
    3,
    '[]',
    '2024-01-03 16:00:00',
    '2024-01-03 16:00:00'
);

-- 创建索引优化查询性能
-- 复合索引：状态+分类
CREATE INDEX `idx_status_category` ON `articles` (`status`, `category`);

-- 复合索引：状态+创建时间
CREATE INDEX `idx_status_created_at` ON `articles` (`status`, `created_at`);

-- 复合索引：分类+创建时间
CREATE INDEX `idx_category_created_at` ON `articles` (`category`, `created_at`);

-- 查看表结构
DESC `articles`;

-- 查看索引
SHOW INDEX FROM `articles`;

-- 查看示例数据
SELECT * FROM `articles` ORDER BY `updated_at` DESC;