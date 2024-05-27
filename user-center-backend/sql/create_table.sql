-- auto-generated definition
create table user
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态  0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '0 - 用户  1 - 管理员',
    planetCode   varchar(512)                       null comment '编号'
)
    comment '用户';


DROP TABLE IF EXISTS `media_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_files` (
                               `id`  varchar(32) NOT NULL comment '文件id,md5值',
                               `filename` varchar(255)  NOT NULL COMMENT '文件名称',
                               `fileType` varchar(12)  DEFAULT NULL COMMENT '文件类型（图片、文档，视频）',
                               `tags` varchar(120)  DEFAULT NULL COMMENT '标签',
                               `bucket` varchar(128)  DEFAULT NULL COMMENT '存储目录',
                               `filePath` varchar(512)  DEFAULT NULL COMMENT '存储路径',
                               `fileId` varchar(32)  NOT NULL COMMENT '文件id',
                               `url` varchar(1024)  DEFAULT NULL COMMENT '媒资文件访问地址',
                               `username` varchar(60)  DEFAULT NULL COMMENT '上传人',
                               `createTime` datetime default CURRENT_TIMESTAMP null COMMENT '上传时间',
                               `updateTime` datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP COMMENT '修改时间',
                               `status` varchar(12)  DEFAULT '1' COMMENT '状态,1:正常，0:不展示',
                               `remark` varchar(32) DEFAULT NULL COMMENT '备注',
                               `auditStatus` varchar(12) DEFAULT NULL COMMENT '审核状态',
                               `auditMind` varchar(255)  DEFAULT NULL COMMENT '审核意见',
                               `fileSize` bigint DEFAULT NULL COMMENT '文件大小',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `unique_fileid` (`fileId`) USING BTREE COMMENT '文件id唯一索引 '
) COMMENT '媒资文件信息';


-- 暂时不用
DROP TABLE IF EXISTS `media_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_process` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `file_id` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件标识',
                                 `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
                                 `bucket` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储桶',
                                 `file_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '存储路径',
                                 `status` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态,1:未处理，2：处理成功  3处理失败',
                                 `create_date` datetime default CURRENT_TIMESTAMP NULL COMMENT '上传时间',
                                 `finish_date` datetime default CURRENT_TIMESTAMP NULL on update CURRENT_TIMESTAMPCOMMENT COMMENT '完成时间',
                                 `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '媒资文件访问地址',
                                 `errormsg` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '失败原因',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `unique_fileid` (`file_id`) USING BTREE
) comment '媒资存储过程';



-- 暂时不用
DROP TABLE IF EXISTS `media_process_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_process_history` (
                                         `id` bigint NOT NULL,
                                         `file_id` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件标识',
                                         `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
                                         `bucket` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储源',
                                         `status` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态,1:未处理，2：处理成功  3处理失败',
                                         `create_date` datetime default CURRENT_TIMESTAMP NULL COMMENT '上传时间',
                                         `finish_date` datetime default CURRENT_TIMESTAMP NULL on update CURRENT_TIMESTAMPCOMMENT COMMENT '完成时间',
                                         `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '媒资文件访问地址',
                                         `file_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件路径',
                                         `errormsg` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '失败原因',
                                         PRIMARY KEY (`id`) USING BTREE
) comment '媒资信息存储记录';