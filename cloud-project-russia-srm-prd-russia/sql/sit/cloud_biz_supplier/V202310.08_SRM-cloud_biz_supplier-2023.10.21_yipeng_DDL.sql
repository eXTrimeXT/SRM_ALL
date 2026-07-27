CREATE TABLE `ceea_sup_vendor_association` (
                                               `ASSOCIATION_ID` BIGINT(20) NOT NULL COMMENT '关联供应商ID',
                                               `VENDOR_ID_A` BIGINT(20) NOT NULL COMMENT 'A供应商ID',
                                               `VENDOR_CODE_A` VARCHAR(50) NOT NULL COMMENT 'A供应商编码' COLLATE 'utf8mb4_general_ci',
                                               `VENDOR_NAME_A` VARCHAR(50) NOT NULL COMMENT 'A供应商名称' COLLATE 'utf8mb4_general_ci',
                                               `VENDOR_ID_B` VARCHAR(50) NOT NULL COMMENT 'B供应商ID' COLLATE 'utf8mb4_general_ci',
                                               `VENDOR_CODE_B` VARCHAR(50) NOT NULL COMMENT 'B供应商CODE' COLLATE 'utf8mb4_general_ci',
                                               `VENDOR_NAME_B` VARCHAR(50) NOT NULL COMMENT 'B供应商NAME' COLLATE 'utf8mb4_general_ci',
                                               `ASSOCIATION_TYPE` VARCHAR(50) NOT NULL COMMENT '类型' COLLATE 'utf8mb4_general_ci',
                                               `ASSOCIATION_REMARK` VARCHAR(50) NOT NULL COMMENT '关联关系备注' COLLATE 'utf8mb4_general_ci',
                                               `CREATED_ID` BIGINT(20) NOT NULL COMMENT '创建人ID',
                                               `CREATED_BY` VARCHAR(50) NOT NULL COMMENT '创建人' COLLATE 'utf8mb4_general_ci',
                                               `CREATION_DATE` DATETIME NOT NULL COMMENT '创建时间',
                                               `CREATED_BY_IP` VARCHAR(150) NOT NULL COMMENT '创建人IP' COLLATE 'utf8mb4_general_ci',
                                               `CREATED_FULL_NAME` VARCHAR(100) NULL DEFAULT NULL COMMENT '创建人姓名' COLLATE 'utf8mb4_general_ci',
                                               `LAST_UPDATED_ID` BIGINT(20) NULL DEFAULT NULL COMMENT '最后更新人ID',
                                               `LAST_UPDATED_BY` VARCHAR(50) NULL DEFAULT NULL COMMENT '更新人' COLLATE 'utf8mb4_general_ci',
                                               `LAST_UPDATE_DATE` DATETIME NULL DEFAULT NULL COMMENT '最后更新时间',
                                               `LAST_UPDATED_BY_IP` VARCHAR(150) NULL DEFAULT NULL COMMENT '最后更新人IP' COLLATE 'utf8mb4_general_ci',
                                               `LAST_UPDATED_FULL_NAME` VARCHAR(100) NULL DEFAULT NULL COMMENT '最后更新人姓名' COLLATE 'utf8mb4_general_ci',
                                               `TENANT_ID` VARCHAR(30) NULL DEFAULT NULL COMMENT '租户ID' COLLATE 'utf8mb4_general_ci',
                                               `VERSION` BIGINT(20) NULL DEFAULT '0' COMMENT '版本号',
                                               `OPINION` TINYTEXT NULL DEFAULT NULL COMMENT '审批意见' COLLATE 'utf8mb4_general_ci',
                                               `CEEA_DEPT_NAME` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建人部门' COLLATE 'utf8mb4_general_ci',
                                               PRIMARY KEY (`ASSOCIATION_ID`) USING BTREE
)
    COMMENT='关联供应商'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

ALTER TABLE `ceea_sup_vendor_association`
    CHANGE COLUMN `VENDOR_ID_B` `VENDOR_ID_B` BIGINT NOT NULL DEFAULT 0 COMMENT 'B供应商ID' COLLATE 'utf8mb4_general_ci' AFTER `VENDOR_NAME_A`;

ALTER TABLE `ceea_sup_vendor_association`
    CHANGE COLUMN `VENDOR_CODE_A` `VENDOR_CODE_A` VARCHAR(50) NULL COMMENT 'A供应商编码' COLLATE 'utf8mb4_general_ci' AFTER `VENDOR_ID_A`,
    CHANGE COLUMN `VENDOR_NAME_A` `VENDOR_NAME_A` VARCHAR(50) NULL COMMENT 'A供应商名称' COLLATE 'utf8mb4_general_ci' AFTER `VENDOR_CODE_A`,
    CHANGE COLUMN `VENDOR_CODE_B` `VENDOR_CODE_B` VARCHAR(50) NULL COMMENT 'B供应商CODE' COLLATE 'utf8mb4_general_ci' AFTER `VENDOR_ID_B`,
    CHANGE COLUMN `VENDOR_NAME_B` `VENDOR_NAME_B` VARCHAR(50) NULL COMMENT 'B供应商NAME' COLLATE 'utf8mb4_general_ci' AFTER `VENDOR_CODE_B`;
