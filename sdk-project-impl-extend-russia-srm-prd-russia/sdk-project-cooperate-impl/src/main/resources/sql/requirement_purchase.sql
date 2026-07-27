alter table scc_pr_requirement_line
    modify `MATERIAL_NAME` varchar(50) DEFAULT NULL COMMENT '物料名称';
alter table scc_pr_requirement_line
    modify `ORG_CODE` varchar(50) DEFAULT NULL COMMENT '业务实体编码';
alter table scc_pr_requirement_line
    modify `ORG_NAME` varchar(50) DEFAULT NULL COMMENT '业务实体名称';
alter table scc_pr_requirement_line
    modify `CATEGORY_NAME` varchar(50) DEFAULT NULL COMMENT '物料小类名称';
alter table scc_pr_requirement_line
    modify `REQUIREMENT_DEPARTMENT` varchar(50) DEFAULT NULL COMMENT '需求部门';

alter table scc_pr_requirement_head
    add column `EXT_ORG_BU_ID` bigint(20) DEFAULT NULL COMMENT '板块id';
alter table scc_pr_requirement_head
    add column `EXT_ORG_BU_CODE` varchar(50) DEFAULT NULL COMMENT '板块编码';
alter table scc_pr_requirement_head
    add column `EXT_ORG_BU_NAME` varchar(50) DEFAULT NULL COMMENT '板块名称';
alter table scc_pr_requirement_head
    add column `EXT_IN_POOL` varchar(10) DEFAULT 'N' COMMENT '是否领单，Y/N';
alter table scc_pr_requirement_head
    add column `EXT_APPROVE_TIME` datetime DEFAULT NULL COMMENT '审批时间';
alter table scc_pr_requirement_head
    add column `EXT_BID_FLAG` varchar(20) DEFAULT 'Y' COMMENT '是否招标，Y/N';

alter table scc_pr_requirement_line
    add column `EXT_FEE_SUBJECT` varchar(50) DEFAULT NULL COMMENT '费用科目';
alter table scc_pr_requirement_line
    add column `EXT_POOL_STATUS` varchar(10) DEFAULT NULL COMMENT '需求池状态,Y有效 N已关闭）';
alter table scc_pr_requirement_line
    add column `EXT_CLOSED_CAUSE` varchar(50) DEFAULT NULL COMMENT '关闭原因';
alter table scc_pr_requirement_line
    add column `EXT_ATTACH_ID` bigint(20) DEFAULT NULL COMMENT '附件ID';
alter table scc_pr_requirement_line
    add column `EXT_ATTACH_NAME` varchar(50) DEFAULT NULL COMMENT '附件名称';
alter table scc_pr_requirement_line
    add column `EXT_PRODUCT_FLAG` varchar(10) DEFAULT NULL COMMENT '是否商品，Y是，N否';
alter table scc_pr_requirement_line
    add column `EXT_PREDICT_PRICE` decimal(30, 8) DEFAULT NULL COMMENT '预估单价';
alter table scc_pr_requirement_line
    add column `EXT_PREDICT_AMOUNT` decimal(30, 8) DEFAULT NULL COMMENT '预估总价';
alter table scc_pr_requirement_line
    add column `EXT_USE_DEPARTMENT_ID` bigint DEFAULT NULL COMMENT '使用部门id';
alter table scc_pr_requirement_line
    add column `EXT_USE_DEPARTMENT_CODE` varchar(50) DEFAULT NULL COMMENT '使用部门编码';
alter table scc_pr_requirement_line
    add column `EXT_USE_DEPARTMENT_NAME` varchar(50) DEFAULT NULL COMMENT '使用部门名称';
alter table scc_pr_requirement_line
    add column `EXT_USER_NAME` varchar(50) DEFAULT NULL COMMENT '使用人名称';
alter table scc_pr_requirement_line
    add column `EXT_USER_CODE` varchar(50) DEFAULT NULL COMMENT '使用人工号';
alter table scc_pr_requirement_line
    add column `EXT_PUSH_USER_NAME` varchar(50) DEFAULT NULL COMMENT '分单人名称';
alter table scc_pr_requirement_line
    add column `EXT_PUSH_USER_CODE` varchar(50) DEFAULT NULL COMMENT '分单人工号';
alter table scc_pr_requirement_line
    add column `EXT_PUSH_TIME` datetime DEFAULT NULL COMMENT '分单时间';
alter table scc_pr_requirement_line
    add column `EXT_USE_TO` varchar(50) DEFAULT NULL COMMENT '用途';
alter table scc_pr_requirement_line
    add column `EXT_MATERIAL_MODEL` varchar(50) DEFAULT NULL COMMENT '型号';
alter table scc_pr_requirement_line
    add column `EXT_BUY_TYPE` varchar(50) DEFAULT NULL COMMENT '购买类型';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_FLAG` varchar(50) DEFAULT NULL COMMENT '是否引出历史供应商，Y/N';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_CODE1` varchar(50) DEFAULT NULL COMMENT '历史供应商编码1';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_NAME1` varchar(50) DEFAULT NULL COMMENT '历史供应商名称1';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_PRICE1` decimal(30, 8) DEFAULT NULL COMMENT '历史供应商价格1';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_CODE2` varchar(50) DEFAULT NULL COMMENT '历史供应商编码2';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_NAME2` varchar(50) DEFAULT NULL COMMENT '历史供应商名称2';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_PRICE2` decimal(30, 8) DEFAULT NULL COMMENT '历史供应商价格2';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_CODE3` varchar(50) DEFAULT NULL COMMENT '历史供应商编码3';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_NAME3` varchar(50) DEFAULT NULL COMMENT '历史供应商名称3';
alter table scc_pr_requirement_line
    add column `EXT_HISTORY_VENDOR_PRICE3` decimal(30, 8) DEFAULT NULL COMMENT '历史供应商价格3';
alter table scc_pr_requirement_line
    add column `EXT_AREA_ID` bigint DEFAULT NULL COMMENT '区域ID';
alter table scc_pr_requirement_line
    add column `EXT_AREA_CODE` varchar(50) DEFAULT NULL COMMENT '区域编码';
alter table scc_pr_requirement_line
    add column `EXT_AREA_NAME` varchar(50) DEFAULT NULL COMMENT '区域名称';
alter table scc_pr_requirement_line
    add column `EXT_SHARE_STOCK` decimal(30, 8) DEFAULT NULL COMMENT '共享库存';
alter table scc_pr_requirement_line
    add column `EXT_ACTUAL_STOCK` decimal(30, 8) DEFAULT NULL COMMENT '实时库存';


-- 自动分单

CREATE TABLE `scc_npm_pr_push_config`
(
    `CONFIG_ID`              bigint(20)  NOT NULL COMMENT '主键id',
    `ORG_ID`                 bigint(20)           DEFAULT NULL COMMENT '业务实体ID',
    `ORG_CODE`               varchar(50)          DEFAULT NULL COMMENT '业务实体编码',
    `ORG_NAME`               varchar(50)          DEFAULT NULL COMMENT '业务实体名称',
    `PUSH_DATE`              varchar(200)         DEFAULT NULL COMMENT '执行周期',
    `PUSH_TIME`              varchar(200)         DEFAULT NULL COMMENT '执行时间',
    `NOTIFY_FLAG`            varchar(10)          DEFAULT NULL COMMENT '是否通知，Y/N',
    `NOTIFY_TYPE`            varchar(10)          DEFAULT NULL COMMENT '短信触发人员',
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
    PRIMARY KEY (`CONFIG_ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='自动分单配置';


-- 自动分单通知

CREATE TABLE `scc_npm_pr_push_notify`
(
    `ID`                     bigint(20)  NOT NULL COMMENT '主键id',
    `CONFIG_ID`              bigint(20)  NOT NULL COMMENT '配置id',
    `USER_CODE`              varchar(30)          DEFAULT NULL COMMENT '人员工号',
    `USER_NAME`              varchar(30)          DEFAULT NULL COMMENT '人员名称',
    `USER_MOBILE`            varchar(30)          DEFAULT NULL COMMENT '人员电话',
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
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='自动分单通知人员';


-- 近期采购配置

CREATE TABLE `scc_npm_pr_recent_purchase_config`
(
    `CONFIG_ID`              bigint(20)  NOT NULL COMMENT '主键id',
    `CONFIG_NUM`             varchar(30) NOT NULL COMMENT '配置编码',
    `ORG_ID`                 bigint(20)           DEFAULT NULL COMMENT '业务实体ID',
    `ORG_CODE`               varchar(50)          DEFAULT NULL COMMENT '业务实体编码',
    `ORG_NAME`               varchar(50)          DEFAULT NULL COMMENT '业务实体名称',
    `AMOUNT`                 decimal(30, 8)       DEFAULT NULL COMMENT '配置金额',
    `MULTIPLE`               decimal(30, 8)       DEFAULT NULL COMMENT '倍数',
    `START_TIME`             datetime             DEFAULT NULL COMMENT '开始时间',
    `END_TIME`               datetime             DEFAULT NULL COMMENT '结束时间',
    `VALID_DAYS`             int(11)              DEFAULT NULL COMMENT '有效天数',
    `COMMENT`                varchar(500)         DEFAULT NULL COMMENT '参考信息',
    `CREATED_BY_department`  varchar(50) NOT NULL COMMENT '创建人部门',
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
    PRIMARY KEY (`CONFIG_ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='近期采购配置';


-- 近期采购数据

CREATE TABLE `scc_npm_pr_recent_purchase_data`
(
    `DATA_ID`                bigint(20)  NOT NULL COMMENT '主键id',
    `CONFIG_ID`              bigint(20)  NOT NULL COMMENT '配置id',
    `ORG_ID`                 bigint(20)           DEFAULT NULL COMMENT '业务实体ID',
    `ORG_CODE`               varchar(50)          DEFAULT NULL COMMENT '业务实体编码',
    `ORG_NAME`               varchar(50)          DEFAULT NULL COMMENT '业务实体名称',
    `VENDOR_ID`              bigint(20)           DEFAULT NULL COMMENT '供应商ID',
    `VENDOR_NAME`            varchar(50)          DEFAULT NULL COMMENT '供应商名称',
    `VENDOR_CODE`            varchar(30)          DEFAULT NULL COMMENT '供应商编码',
    `MATERIAL_ID`            bigint(20)           DEFAULT NULL COMMENT '物料ID',
    `MATERIAL_CODE`          varchar(50)          DEFAULT NULL COMMENT '物料编码',
    `MATERIAL_NAME`          varchar(50)          DEFAULT NULL COMMENT '物料名称',
    `MATERIAL_MODEL`         varchar(50)          DEFAULT NULL COMMENT '规格型号',
    `UNIT`                   varchar(30)          DEFAULT NULL COMMENT '单位',
    `UNIT_CODE`              varchar(50)          DEFAULT NULL COMMENT '单位编码',
    `ORDER_NUM`              decimal(30, 8)       DEFAULT NULL COMMENT '订单数量',
    `TAX_PRICE`              decimal(30, 8)       DEFAULT NULL COMMENT '含税单价',
    `NO_TAX_PRICE`           decimal(30, 8)       DEFAULT NULL COMMENT '未税单价',
    `TAX_RATE`               decimal(30, 8)       DEFAULT NULL COMMENT '税率',
    `ORDER_AMOUNT`           decimal(30, 8)       DEFAULT NULL COMMENT '采购金额',
    `TAX_AMOUNT`             decimal(30, 8)       DEFAULT NULL COMMENT '税额',
    `DELIVERY_CYCLE`         decimal(30, 8)       DEFAULT NULL COMMENT '到货周期',
    `START_TIME`             datetime             DEFAULT NULL COMMENT '开始时间',
    `END_TIME`               datetime             DEFAULT NULL COMMENT '结束时间',
    `STATUS`                 varchar(50)          DEFAULT NULL COMMENT '状态',
    `CREATED_BY_department`  varchar(50) NOT NULL COMMENT '创建人部门',
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
    PRIMARY KEY (`DATA_ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='近期采购数据';


-- 历史供应商区域

CREATE TABLE `scc_npm_pr_vendor_area`
(
    `ID`                     bigint(20)  NOT NULL COMMENT '主键id',
    `CONFIG_ID`              bigint(20)  NOT NULL COMMENT '配置id',
    `AREA_ID`                varchar(50)          DEFAULT NULL COMMENT '区域id',
    `AREA_CODE`              varchar(50)          DEFAULT NULL COMMENT '区域编码',
    `AREA_NAME`              varchar(50)          DEFAULT NULL COMMENT '区域名称',
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
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='供应商配置区域';


-- 历史供应商

CREATE TABLE `scc_npm_pr_vendor_config`
(
    `CONFIG_ID`              bigint(20)  NOT NULL COMMENT '主键id',
    `ORG_ID`                 bigint(20)           DEFAULT NULL COMMENT '业务实体ID',
    `ORG_CODE`               varchar(50)          DEFAULT NULL COMMENT '业务实体编码',
    `ORG_NAME`               varchar(50)          DEFAULT NULL COMMENT '业务实体名称',
    `RANG_FLAG`              varchar(10)          DEFAULT NULL COMMENT '是否参照本单元，Y/N',
    `STATUS`                 varchar(50)          DEFAULT NULL COMMENT '启用状态，Y/N',
    `COMMENT`                varchar(500)         DEFAULT NULL COMMENT '备注',
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
    PRIMARY KEY (`CONFIG_ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='历史供应商配置';

CREATE TABLE `scc_npm_pr_share_stock`
(
    `STOCK_ID`              bigint(20)  NOT NULL COMMENT '主键id',
    `MATERIAL_CODE`                varchar(50)            DEFAULT NULL COMMENT '物料编码',
    `MATERIAL_NAME`                varchar(100)            DEFAULT NULL COMMENT '物料名称',
    `MODEL`                varchar(100)            DEFAULT NULL COMMENT '规格型号',
    `UNIT`                varchar(20)            DEFAULT NULL COMMENT '计量单位',
    `QTY`                varchar(20)            DEFAULT NULL COMMENT '数量',
    `PRICE`                varchar(20)            DEFAULT NULL COMMENT '参考单价',
    `COMPANY`                varchar(20)            DEFAULT NULL COMMENT '公司名称',
    `ORG`                varchar(20)            DEFAULT NULL COMMENT '业务实体',
    `AREA`                varchar(20)            DEFAULT NULL COMMENT '区域',
    `DEPARTMENT`                varchar(20)            DEFAULT NULL COMMENT '创建单位',
    `STORE_NAME`                varchar(20)            DEFAULT NULL COMMENT '库位',
    `STORE_ADDRESS`                varchar(20)            DEFAULT NULL COMMENT '库房地址',
    `STORE_CONTACTS`                varchar(20)            DEFAULT NULL COMMENT '库房联系人',
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
    PRIMARY KEY (`STOCK_ID`) USING BTREE
) ENGINE = InnoDB COMMENT ='共享库存';


-- 快查
-- 物料快查 scc_npm_pr_material_query

-- 字典编码
-- 需求类型 DEMAND_TYPE
-- 采购属性 PURCHASE_TYPE
-- 购买类型 PR_BUY_TYPE
-- 主控部门 main_control_department
-- 申请单类型 application_form_type