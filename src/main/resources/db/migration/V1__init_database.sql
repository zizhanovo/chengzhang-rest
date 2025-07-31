-- 创建数据库
CREATE DATABASE IF NOT EXISTS chengzhang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE chengzhang;

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content LONGTEXT COMMENT '内容',
    summary TEXT COMMENT '摘要',
    tags JSON COMMENT '标签',
    category VARCHAR(100) COMMENT '分类',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，published-已发布，archived-已归档',
    word_count INT DEFAULT 0 COMMENT '字数',
    read_time INT DEFAULT 0 COMMENT '预计阅读时间（分钟）',
    images JSON COMMENT '文章中的图片',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    INDEX idx_updated_time (updated_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 图片表
CREATE TABLE IF NOT EXISTS images (
    id VARCHAR(36) PRIMARY KEY,
    url VARCHAR(500) NOT NULL COMMENT '图片URL',
    filename VARCHAR(255) COMMENT '文件名',
    size BIGINT COMMENT '文件大小（字节）',
    width INT COMMENT '图片宽度',
    height INT COMMENT '图片高度',
    platform VARCHAR(50) DEFAULT 'smms' COMMENT '图床平台：smms, imgbb, github等',
    user_id VARCHAR(36) DEFAULT 'default' COMMENT '用户ID',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_platform (platform),
    INDEX idx_user_id (user_id),
    INDEX idx_upload_time (upload_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片表';

-- 设置表
CREATE TABLE IF NOT EXISTS settings (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL DEFAULT 'default' COMMENT '用户ID',
    
    -- 编辑器设置
    editor_theme VARCHAR(50) DEFAULT 'vs-dark' COMMENT '编辑器主题',
    editor_font_size INT DEFAULT 14 COMMENT '字体大小',
    editor_font_family VARCHAR(100) DEFAULT 'Consolas, Monaco, monospace' COMMENT '字体',
    editor_line_height DECIMAL(3,1) DEFAULT 1.5 COMMENT '行高',
    editor_tab_size INT DEFAULT 4 COMMENT 'Tab大小',
    editor_word_wrap BOOLEAN DEFAULT TRUE COMMENT '自动换行',
    editor_line_numbers BOOLEAN DEFAULT TRUE COMMENT '显示行号',
    editor_minimap BOOLEAN DEFAULT TRUE COMMENT '显示缩略图',
    editor_auto_save BOOLEAN DEFAULT TRUE COMMENT '自动保存',
    editor_auto_save_delay INT DEFAULT 2000 COMMENT '自动保存延迟（毫秒）',
    
    -- 图片上传设置
    image_platform VARCHAR(50) DEFAULT 'smms' COMMENT '默认图床',
    image_compress BOOLEAN DEFAULT TRUE COMMENT '图片压缩',
    image_quality INT DEFAULT 80 COMMENT '图片质量',
    image_max_size INT DEFAULT 10 COMMENT '最大文件大小（MB）',
    image_watermark BOOLEAN DEFAULT FALSE COMMENT '添加水印',
    
    -- 导出设置
    export_format VARCHAR(20) DEFAULT 'markdown' COMMENT '导出格式',
    export_include_images BOOLEAN DEFAULT TRUE COMMENT '包含图片',
    export_include_toc BOOLEAN DEFAULT TRUE COMMENT '包含目录',
    export_template VARCHAR(50) DEFAULT 'default' COMMENT '导出模板',
    
    -- 通用设置
    language VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言',
    theme VARCHAR(20) DEFAULT 'light' COMMENT '主题',
    notifications BOOLEAN DEFAULT TRUE COMMENT '通知',
    backup_enabled BOOLEAN DEFAULT TRUE COMMENT '启用备份',
    backup_interval INT DEFAULT 24 COMMENT '备份间隔（小时）',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';

-- 插入默认设置
INSERT INTO settings (id, user_id) VALUES (UUID(), 'default') ON DUPLICATE KEY UPDATE id=id;

-- 插入示例文章
INSERT INTO articles (id, title, content, summary, tags, category, status, word_count, read_time) VALUES 
(UUID(), '欢迎使用成长写作编辑器', 
'# 欢迎使用成长写作编辑器\n\n这是一个专为自媒体博主设计的写作编辑器，具有以下特点：\n\n## 主要功能\n\n- 📝 **Markdown编辑器**：支持实时预览，语法高亮\n- 🖼️ **图片管理**：支持多种图床，一键上传\n- 📊 **数据统计**：文章字数、阅读时间统计\n- 🎨 **主题切换**：多种编辑器主题可选\n- ⚙️ **个性化设置**：丰富的编辑器配置选项\n\n## 开始使用\n\n1. 在左侧文章列表中创建新文章\n2. 使用Markdown语法进行写作\n3. 通过工具栏快速插入图片和格式\n4. 实时查看字数和预计阅读时间\n5. 支持草稿保存和发布管理\n\n祝您写作愉快！', 
'这是一个专为自媒体博主设计的写作编辑器使用指南', 
'["教程", "指南", "Markdown"]', 
'教程', 
'published', 
200, 
2);