-- 导出  表 meicloud_uat_usrm_cloud_biz_pj.scc_api_interface_log 结构
CREATE TABLE IF NOT EXISTS `scc_api_interface_log` (
    `LOG_ID` bigint(20) NOT NULL COMMENT '日志ID',
    `SERVICE_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接口名称',
    `SERVICE_TYPE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接口类型(HTTP,WEBSERVICE)',
    `TYPE` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '传输类型(RECEIVE:接收，SEND:发送)',
    `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(SUCCESS:成功，FAIL:失败)',
    `BILL_ID` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务单据ID',
    `BILL_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单据类型',
    `DEAL_TIME` bigint(20) DEFAULT NULL COMMENT '推送次数',
    `FINISH_DATE` datetime DEFAULT NULL COMMENT '完成时间',
    `LINE_LOG_ID` bigint(20) DEFAULT NULL COMMENT '最新行ID',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT 0 COMMENT '版本号',
    `SERVICE_INFO` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '传输内容',
    `RETURN_INFO` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '返回信息',
    `ERROR_INFO` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报错信息',
    `TARGET_SYS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标系统',
    `URL` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
    PRIMARY KEY (`LOG_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='接口日志表';

-- 数据导出被取消选择。

-- 导出  表 meicloud_uat_usrm_cloud_biz_pj.scc_api_interface_log_line 结构
CREATE TABLE IF NOT EXISTS `scc_api_interface_log_line` (
    `LOG_LINE_ID` bigint(20) NOT NULL COMMENT '行ID',
    `LOG_ID` bigint(20) DEFAULT NULL COMMENT '日志ID',
    `SERVICE_INFO` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '传输内容',
    `RETURN_INFO` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '返回信息',
    `ERROR_INFO` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报错信息',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`LOG_LINE_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='接口日志行表';
