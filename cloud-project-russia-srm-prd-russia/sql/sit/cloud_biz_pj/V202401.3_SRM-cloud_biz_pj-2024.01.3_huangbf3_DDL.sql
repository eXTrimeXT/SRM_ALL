CREATE TABLE IF NOT EXISTS `scc_bpm_new_flag` (
    `BPM_NEW_FLAG_ID` bigint(20) NOT NULL COMMENT 'ID',
    `BUSINESS_ID` bigint(20) COLLATE utf8mb4_unicode_ci COMMENT '业务单据ID',
    `BUSSINESS_TYPE` text COLLATE utf8mb4_unicode_ci COMMENT '业务单据类型',
    `NEW_BPM_FLAG` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '走新BPM审批流标志，Y/N',
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
    `VERSION` bigint(20) DEFAULT '0' COMMENT '版本号',
    PRIMARY KEY (`BPM_NEW_FLAG_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='新BPM审批流标志表';
