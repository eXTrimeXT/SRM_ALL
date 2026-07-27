CREATE TABLE `scc_npm_check_order_attach`
(
    `CHECK_ORDER_ATTACH_ID`      bigint(20)  NOT NULL COMMENT '主键id',
    `CHECK_ORDER_ID`             bigint(20)  NOT NULL COMMENT '验收单id',
    `ATTACH_ID`              bigint(20)           DEFAULT NULL COMMENT '附件ID',
    `ATTACH_NAME`            varchar(50)          DEFAULT NULL COMMENT '附件名称',
    `CREATED_ID`             bigint(20)  NOT NULL COMMENT '创建人ID',
    `CREATED_BY`             varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE`          datetime    NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP`          varchar(150)         DEFAULT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME`      varchar(100)         DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID`        bigint(20)           DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY`        varchar(50)          DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE`       datetime             DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP`     varchar(150)         DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100)         DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID`              varchar(30)          DEFAULT NULL COMMENT '租户ID',
    `VERSION`                bigint(20)  NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`CHECK_ORDER_ATTACH_ID`) USING BTREE,
    KEY `scc_npm_check_order_attach_CHECK_ORDER_ID_IDX` (`CHECK_ORDER_ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='验收单附件';