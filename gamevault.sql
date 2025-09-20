-- MySQL dump 10.13  Distrib 9.3.0, for Win64 (x86_64)
--
-- Host: localhost    Database: gamevault
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary view structure for view `asset_stats`
--

DROP TABLE IF EXISTS `asset_stats`;
/*!50001 DROP VIEW IF EXISTS `asset_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `asset_stats` AS SELECT 
 1 AS `id`,
 1 AS `name`,
 1 AS `file_type`,
 1 AS `category`,
 1 AS `status`,
 1 AS `file_size`,
 1 AS `download_count`,
 1 AS `view_count`,
 1 AS `uploader_name`,
 1 AS `project_name`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `assets`
--

DROP TABLE IF EXISTS `assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assets` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '资产ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `file_hash` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件哈希值(用于去重)',
  `thumbnail_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图路径',
  `project_id` int DEFAULT NULL COMMENT '所属项目ID',
  `uploader_id` int NOT NULL COMMENT '上传者ID',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'other',
  `tags` json DEFAULT NULL COMMENT '资产标签',
  `metadata` json DEFAULT NULL COMMENT '资产元数据',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `view_count` int DEFAULT '0' COMMENT '查看次数',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_uploader_id` (`uploader_id`),
  CONSTRAINT `assets_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE SET NULL,
  CONSTRAINT `assets_ibfk_2` FOREIGN KEY (`uploader_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assets`
--

LOCK TABLES `assets` WRITE;
/*!40000 ALTER TABLE `assets` DISABLE KEYS */;
INSERT INTO `assets` VALUES (1003,'battle_music.mp3','Intense battle background music suitable for action scenes','/uploads/battle_music.mp3',8388608,'audio','audio/mpeg','sha256:battle_music_hash',NULL,2,2,'audio','[]','{\"license\": \"Royalty Free\", \"version\": \"1.0\", \"originalName\": \"battle_music.mp3\"}',0,2,'pending','2024-01-13 01:15:00','2025-07-09 18:12:17'),(1004,'player_controller.cs','Unity player controller script with movement, jump, attack functions','/uploads/player_controller.cs',12288,'script','text/plain','sha256:player_controller_hash',NULL,3,1,'script','[\"script\", \"controller\", \"unity\"]','{\"license\": \"MIT\", \"version\": \"1.0\", \"originalName\": \"player_controller.cs\"}',0,0,'approved','2024-01-12 08:45:00','2024-01-12 08:45:00'),(1005,'sword_animation.anim','Sword weapon attack animation file with combo actions','/uploads/sword_animation.anim',2097152,'animation','application/octet-stream','sha256:sword_animation_hash',NULL,1,1,'animation','[\"animation\", \"weapon\", \"attack\"]','{\"license\": \"MIT\", \"version\": \"1.0\", \"originalName\": \"sword_animation.anim\"}',0,0,'approved','2024-01-11 03:30:00','2024-01-11 03:30:00'),(1006,'ui_icons.png','Game UI icon collection including buttons and status icons','/uploads/ui_icons.png',1048576,'texture','image/png','sha256:ui_icons_hash','/thumbnails/ui_icons.jpg',4,2,'texture','[\"ui\", \"icons\", \"interface\"]','{\"license\": \"CC0\", \"version\": \"1.0\", \"originalName\": \"ui_icons.png\"}',0,0,'rejected','2024-01-10 05:20:00','2024-01-10 05:20:00');
/*!40000 ALTER TABLE `assets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audits`
--

DROP TABLE IF EXISTS `audits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audits` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `asset_id` int NOT NULL COMMENT '资产ID',
  `reviewer_id` int DEFAULT NULL COMMENT '审核者ID',
  `status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '审核状态',
  `comments` text COLLATE utf8mb4_unicode_ci COMMENT '审核意见',
  `score` int DEFAULT NULL COMMENT '评分(1-5)',
  `reviewed_at` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `audits_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE CASCADE,
  CONSTRAINT `audits_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audits`
--

LOCK TABLES `audits` WRITE;
/*!40000 ALTER TABLE `audits` DISABLE KEYS */;
/*!40000 ALTER TABLE `audits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_storage`
--

DROP TABLE IF EXISTS `file_storage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_storage` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '存储ID',
  `file_hash` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件哈希',
  `original_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `storage_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储路径',
  `file_size` bigint NOT NULL COMMENT '文件大小',
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `reference_count` int DEFAULT '0' COMMENT '引用计数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_hash` (`file_hash`),
  KEY `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件存储表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_storage`
--

LOCK TABLES `file_storage` WRITE;
/*!40000 ALTER TABLE `file_storage` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_storage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_members`
--

DROP TABLE IF EXISTS `project_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_members` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `project_id` int NOT NULL COMMENT '项目ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `role` enum('owner','admin','developer','designer','tester','viewer') COLLATE utf8mb4_unicode_ci DEFAULT 'viewer' COMMENT '项目角色',
  `permissions` json DEFAULT NULL COMMENT '权限列表',
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `project_members_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `project_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_members`
--

LOCK TABLES `project_members` WRITE;
/*!40000 ALTER TABLE `project_members` DISABLE KEYS */;
INSERT INTO `project_members` VALUES (1,1,2,'owner',NULL,'2025-07-04 10:21:00'),(2,1,3,'developer',NULL,'2025-07-04 10:21:00'),(3,1,4,'designer',NULL,'2025-07-04 10:21:00'),(4,2,2,'owner',NULL,'2025-07-04 10:21:00'),(5,2,4,'designer',NULL,'2025-07-04 10:21:00'),(6,3,1,'owner',NULL,'2025-07-04 10:21:00'),(7,4,2,'owner',NULL,'2025-07-04 10:21:00');
/*!40000 ALTER TABLE `project_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `project_stats`
--

DROP TABLE IF EXISTS `project_stats`;
/*!50001 DROP VIEW IF EXISTS `project_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `project_stats` AS SELECT 
 1 AS `id`,
 1 AS `name`,
 1 AS `type`,
 1 AS `status`,
 1 AS `progress`,
 1 AS `owner_name`,
 1 AS `asset_count`,
 1 AS `member_count`,
 1 AS `created_at`,
 1 AS `updated_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projects` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `type` enum('game','app','web','other') COLLATE utf8mb4_unicode_ci DEFAULT 'game' COMMENT '项目类型',
  `status` enum('planning','development','testing','completed','archived') COLLATE utf8mb4_unicode_ci DEFAULT 'planning' COMMENT '项目状态',
  `owner_id` int NOT NULL COMMENT '项目负责人ID',
  `team_members` json DEFAULT NULL COMMENT '团队成员ID列表',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `budget` decimal(15,2) DEFAULT NULL COMMENT '项目预算',
  `progress` int DEFAULT '0' COMMENT '项目进度(0-100)',
  `tags` json DEFAULT NULL COMMENT '项目标签',
  `repository_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代码仓库地址',
  `demo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '演示地址',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `projects_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects`
--

LOCK TABLES `projects` WRITE;
/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
INSERT INTO `projects` VALUES (1,'RPG 冒险游戏','一款经典的角色扮演游戏，包含丰富的剧情和战斗系统','game','development',2,NULL,NULL,NULL,NULL,65,NULL,NULL,NULL,'2025-07-04 10:21:00','2025-07-04 10:21:00'),(2,'移动端休闲游戏','简单易上手的移动端休闲游戏','game','testing',2,NULL,NULL,NULL,NULL,85,NULL,NULL,NULL,'2025-07-04 10:21:00','2025-07-04 10:21:00'),(3,'Web 管理系统','企业级Web管理系统','web','planning',1,NULL,NULL,NULL,NULL,20,NULL,NULL,NULL,'2025-07-04 10:21:00','2025-07-04 10:21:00'),(4,'VR 体验应用','虚拟现实体验应用','app','completed',2,NULL,NULL,NULL,NULL,100,NULL,NULL,NULL,'2025-07-04 10:21:00','2025-07-04 10:21:00');
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_logs`
--

DROP TABLE IF EXISTS `system_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_logs` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `level` enum('error','warning','info','debug') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志级别',
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志消息',
  `module` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模块名称',
  `action` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COLLATE utf8mb4_unicode_ci COMMENT '用户代理',
  `request_data` json DEFAULT NULL COMMENT '请求数据',
  `response_data` json DEFAULT NULL COMMENT '响应数据',
  `execution_time` decimal(10,3) DEFAULT NULL COMMENT '执行时间(秒)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_level` (`level`),
  KEY `idx_module` (`module`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `system_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_logs`
--

LOCK TABLES `system_logs` WRITE;
/*!40000 ALTER TABLE `system_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_sessions`
--

DROP TABLE IF EXISTS `user_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_sessions` (
  `id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COLLATE utf8mb4_unicode_ci COMMENT '用户代理',
  `last_activity` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后活动时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expires_at` timestamp NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expires_at` (`expires_at`),
  CONSTRAINT `user_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_sessions`
--

LOCK TABLES `user_sessions` WRITE;
/*!40000 ALTER TABLE `user_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `user_stats`
--

DROP TABLE IF EXISTS `user_stats`;
/*!50001 DROP VIEW IF EXISTS `user_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `user_stats` AS SELECT 
 1 AS `id`,
 1 AS `username`,
 1 AS `email`,
 1 AS `role`,
 1 AS `project_count`,
 1 AS `asset_count`,
 1 AS `login_count`,
 1 AS `last_login_at`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希',
  `role` enum('admin','manager','user') COLLATE utf8mb4_unicode_ci DEFAULT 'user' COMMENT '用户角色',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像路径',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `bio` text COLLATE utf8mb4_unicode_ci COMMENT '个人简介',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` enum('active','inactive','banned') COLLATE utf8mb4_unicode_ci DEFAULT 'active' COMMENT '用户状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin@test.com','123','admin',NULL,NULL,'系统管理员',NULL,0,'2025-07-05 03:21:03','2025-07-05 04:10:09','active'),(2,'manager','manager@test.com','123','manager',NULL,NULL,'项目经理',NULL,0,'2025-07-05 03:21:03','2025-07-05 04:08:11','active'),(3,'developer','developer@test.com','123','user',NULL,NULL,'开发人员',NULL,0,'2025-07-05 03:21:03','2025-07-05 04:08:11','active'),(4,'designer','designer@test.com','123','user',NULL,NULL,'设计师',NULL,0,'2025-07-05 03:21:03','2025-07-05 04:08:11','active'),(5,'user','user@test.com','123','user',NULL,NULL,'普通用户',NULL,0,'2025-07-05 03:21:03','2025-07-05 04:08:11','active'),(6,'test033786','','202cb962ac59075b964b07152d234b70','user',NULL,NULL,'测试用户',NULL,0,'2025-07-05 03:27:14','2025-07-05 03:27:14','active'),(17,'testuser492029','user1751691492488@placeholder.com','123','user',NULL,NULL,'',NULL,0,'2025-07-05 04:58:12','2025-07-05 04:58:12','active'),(18,'testuser492568','user1751691492575@placeholder.com','123','user',NULL,NULL,'',NULL,0,'2025-07-05 04:58:13','2025-07-05 04:58:13','active'),(19,'testuser492582','testuser492582@test.com','123','user',NULL,NULL,'',NULL,0,'2025-07-05 04:58:13','2025-07-05 04:58:13','active'),(20,'testuser','testuser@example.com','123456','user',NULL,NULL,'',NULL,0,'2025-07-09 18:30:43','2025-07-09 18:30:43','active');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'gamevault'
--
/*!50003 DROP PROCEDURE IF EXISTS `CleanupExpiredData` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `CleanupExpiredData`()
BEGIN
    -- 清理过期会话
    DELETE FROM user_sessions WHERE expires_at < NOW();

    -- 清理30天前的调试日志
    DELETE FROM system_logs WHERE level = 'debug' AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

    -- 清理90天前的信息日志
    DELETE FROM system_logs WHERE level = 'info' AND created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

    -- 更新文件引用计数
    UPDATE file_storage fs
    SET reference_count = (
        SELECT COUNT(*) FROM assets a WHERE a.file_hash = fs.file_hash
    );

    -- 删除无引用的文件记录
    DELETE FROM file_storage WHERE reference_count = 0;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetUserDashboard` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetUserDashboard`(IN user_id INT)
BEGIN
    -- 用户基本信息
    SELECT username, email, role, avatar, last_login_at FROM users WHERE id = user_id;

    -- 用户项目统计
    SELECT
        COUNT(*) as total_projects,
        SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_projects,
        SUM(CASE WHEN status = 'development' THEN 1 ELSE 0 END) as active_projects
    FROM projects WHERE owner_id = user_id;

    -- 用户资产统计
    SELECT
        COUNT(*) as total_assets,
        SUM(CASE WHEN status = 'approved' THEN 1 ELSE 0 END) as approved_assets,
        SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending_assets
    FROM assets WHERE uploader_id = user_id;

    -- 最近活动
    SELECT 'asset' as type, name, created_at FROM assets WHERE uploader_id = user_id
    UNION ALL
    SELECT 'project' as type, name, created_at FROM projects WHERE owner_id = user_id
    ORDER BY created_at DESC LIMIT 10;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `asset_stats`
--

/*!50001 DROP VIEW IF EXISTS `asset_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `asset_stats` AS select `a`.`id` AS `id`,`a`.`name` AS `name`,`a`.`file_type` AS `file_type`,`a`.`category` AS `category`,`a`.`status` AS `status`,`a`.`file_size` AS `file_size`,`a`.`download_count` AS `download_count`,`a`.`view_count` AS `view_count`,`u`.`username` AS `uploader_name`,`p`.`name` AS `project_name`,`a`.`created_at` AS `created_at` from ((`assets` `a` left join `users` `u` on((`a`.`uploader_id` = `u`.`id`))) left join `projects` `p` on((`a`.`project_id` = `p`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `project_stats`
--

/*!50001 DROP VIEW IF EXISTS `project_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `project_stats` AS select `p`.`id` AS `id`,`p`.`name` AS `name`,`p`.`type` AS `type`,`p`.`status` AS `status`,`p`.`progress` AS `progress`,`u`.`username` AS `owner_name`,count(distinct `a`.`id`) AS `asset_count`,count(distinct `pm`.`user_id`) AS `member_count`,`p`.`created_at` AS `created_at`,`p`.`updated_at` AS `updated_at` from (((`projects` `p` left join `users` `u` on((`p`.`owner_id` = `u`.`id`))) left join `assets` `a` on((`p`.`id` = `a`.`project_id`))) left join `project_members` `pm` on((`p`.`id` = `pm`.`project_id`))) group by `p`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_stats`
--

/*!50001 DROP VIEW IF EXISTS `user_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_stats` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`email` AS `email`,`u`.`role` AS `role`,count(distinct `p`.`id`) AS `project_count`,count(distinct `a`.`id`) AS `asset_count`,`u`.`login_count` AS `login_count`,`u`.`last_login_at` AS `last_login_at`,`u`.`created_at` AS `created_at` from ((`users` `u` left join `projects` `p` on((`u`.`id` = `p`.`owner_id`))) left join `assets` `a` on((`u`.`id` = `a`.`uploader_id`))) group by `u`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-10 13:39:52
