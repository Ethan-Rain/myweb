-- 分类表（需要先创建，因为其他表会引用它）
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类ID',
    level INT NOT NULL DEFAULT 1 COMMENT '层级',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status ENUM('ACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='媒体分类表';

-- 标签表（需要先创建，因为media_tag会引用它）
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    description VARCHAR(255) COMMENT '标签描述',
    status ENUM('ACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='标签表';

-- 基础媒体表（需要在category和tag之后创建）
CREATE TABLE IF NOT EXISTS media (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    media_type ENUM('IMAGE', 'VIDEO') NOT NULL COMMENT '媒体类型',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    upload_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status ENUM('ACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    category_id BIGINT COMMENT '分类ID',
    user_id BIGINT NOT NULL COMMENT '上传用户ID',
    size BIGINT NOT NULL COMMENT '文件大小（字节）',
    duration INT COMMENT '时长（秒），仅视频使用',
    thumbnail_id BIGINT COMMENT '缩略图ID',
    tags JSON COMMENT '标签',
    metadata JSON COMMENT '元数据',
    INDEX idx_media_type (media_type),
    INDEX idx_status (status),
    INDEX idx_upload_time (upload_time),
    INDEX idx_user_id (user_id)
) COMMENT='媒体基础信息表';

-- 媒体内容表（需要在media之后创建）
CREATE TABLE IF NOT EXISTS media_content (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    media_id BIGINT NOT NULL COMMENT '媒体ID',
    file_path VARCHAR(512) NOT NULL COMMENT '文件存储路径',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_extension VARCHAR(10) NOT NULL COMMENT '文件扩展名',
    storage_type ENUM('LOCAL', 'AWS_S3', 'ALIYUN_OSS') NOT NULL COMMENT '存储类型',
    storage_region VARCHAR(50) COMMENT '存储区域',
    cdn_url VARCHAR(512) COMMENT 'CDN加速地址',
    watermark_status BOOLEAN DEFAULT FALSE COMMENT '是否添加水印',
    transcode_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING' COMMENT '转码状态',
    transcode_profile JSON COMMENT '转码配置',
    INDEX idx_media_id (media_id),
    INDEX idx_storage_type (storage_type)
) COMMENT='媒体内容存储表';

-- 媒体-标签关联表（需要在media和tag之后创建）
CREATE TABLE IF NOT EXISTS media_tag (
    media_id BIGINT NOT NULL COMMENT '媒体ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (media_id, tag_id)
) COMMENT='媒体-标签关联表';

-- 添加外键约束（在所有表创建完成后添加）
ALTER TABLE media ADD CONSTRAINT fk_media_category FOREIGN KEY (category_id) REFERENCES category(id);
ALTER TABLE media ADD CONSTRAINT fk_media_thumbnail FOREIGN KEY (thumbnail_id) REFERENCES media(id);
ALTER TABLE media_content ADD CONSTRAINT fk_media_content_media FOREIGN KEY (media_id) REFERENCES media(id);
ALTER TABLE media_tag ADD CONSTRAINT fk_media_tag_media FOREIGN KEY (media_id) REFERENCES media(id);
ALTER TABLE media_tag ADD CONSTRAINT fk_media_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id);
ALTER TABLE category ADD CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id);
