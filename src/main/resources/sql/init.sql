CREATE DATABASE /*!32312 IF NOT EXISTS*/`chengzhang` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `chengzhang`;

/*Table structure for table `ai_shortcuts` */

DROP TABLE IF EXISTS `ai_shortcuts`;

CREATE TABLE `ai_shortcuts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prompt` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int(11) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `article_tags` */

DROP TABLE IF EXISTS `article_tags`;

CREATE TABLE `article_tags` (
  `article_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tag_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  KEY `FKr17guaxramkeyxq0f1xn3bxbw` (`tag_id`),
  KEY `FKeoil73g36agokjw6vuklqgli3` (`article_id`),
  CONSTRAINT `FKeoil73g36agokjw6vuklqgli3` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`),
  CONSTRAINT `FKr17guaxramkeyxq0f1xn3bxbw` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `articles` */

DROP TABLE IF EXISTS `articles`;

CREATE TABLE `articles` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文章内容（Markdown格式）',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章摘要',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT '文章状态：draft-草稿，published-已发布',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章分类',
  `tags` text COLLATE utf8mb4_unicode_ci COMMENT '文章标签（JSON格式存储）',
  `word_count` int(11) DEFAULT '0' COMMENT '字数统计',
  `read_time` int(11) DEFAULT '0' COMMENT '预计阅读时间（分钟）',
  `images` text COLLATE utf8mb4_unicode_ci COMMENT '文章图片（JSON格式存储）',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `category_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_updated_at` (`updated_at`),
  KEY `idx_status_category` (`status`,`category`),
  KEY `idx_status_created_at` (`status`,`created_at`),
  KEY `idx_category_created_at` (`category`,`created_at`),
  KEY `FK7i4rryg7kqwyyrr08temnc71e` (`category_id`),
  FULLTEXT KEY `idx_content` (`content`),
  FULLTEXT KEY `idx_title_content` (`title`,`content`),
  CONSTRAINT `FK7i4rryg7kqwyyrr08temnc71e` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

/*Table structure for table `categories` */

DROP TABLE IF EXISTS `categories`;

CREATE TABLE `categories` (
  `id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `article_count` int(11) DEFAULT NULL,
  `color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_t8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `images` */

DROP TABLE IF EXISTS `images`;

CREATE TABLE `images` (
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片ID，使用UUID',
  `original_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名（重命名后）',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储路径',
  `file_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小（字节）',
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MIME类型',
  `width` int(11) DEFAULT NULL COMMENT '图片宽度（像素）',
  `height` int(11) DEFAULT NULL COMMENT '图片高度（像素）',
  `article_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联文章ID',
  `upload_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传IP地址',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态：active-正常，deleted-已删除',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '图片描述',
  `tags` json DEFAULT NULL COMMENT '图片标签（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_name` (`file_name`),
  KEY `idx_file_path` (`file_path`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_status` (`status`),
  KEY `idx_mime_type` (`mime_type`),
  KEY `idx_upload_ip` (`upload_ip`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`),
  KEY `idx_file_size` (`file_size`),
  KEY `idx_dimensions` (`width`,`height`),
  KEY `idx_status_article` (`status`,`article_id`),
  KEY `idx_status_create_time` (`status`,`create_time`),
  KEY `idx_article_create_time` (`article_id`,`create_time`),
  FULLTEXT KEY `idx_fulltext_search` (`original_name`,`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片管理表';

/*Table structure for table `settings` */

DROP TABLE IF EXISTS `settings`;

CREATE TABLE `settings` (
  `id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `app_settings` json DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `editor_config` json DEFAULT NULL,
  `editor_settings` json DEFAULT NULL,
  `export_config` json DEFAULT NULL,
  `export_settings` json DEFAULT NULL,
  `image_config` json DEFAULT NULL,
  `image_host_settings` json DEFAULT NULL,
  `settings_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `style_copy_settings` json DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `tags` */

DROP TABLE IF EXISTS `tags`;

CREATE TABLE `tags` (
  `id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `usage_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_t48xdq560gs3gap9g7jg36kgc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;