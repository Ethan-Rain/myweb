CREATE TABLE IF NOT EXISTS `file_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `path` varchar(1024) NOT NULL COMMENT '文件路径',
  `name` varchar(255) NOT NULL COMMENT '文件名',
  `size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `file_hash` varchar(64) DEFAULT NULL COMMENT '文件hash',
  `category` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `is_directory` tinyint(1) DEFAULT '0' COMMENT '是否是目录',
  `mime_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `scan_time` datetime DEFAULT NULL COMMENT '扫描时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_path` (`path`),
  INDEX `idx_category` (`category`),
  INDEX `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

