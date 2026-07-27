-- 订单
alter table scc_sc_order add column `EXT_STATUS` varchar(30) DEFAULT NULL COMMENT '补充状态';
alter table scc_sc_order add column `EXT_VENDOR_CONTACTS` varchar(30) DEFAULT NULL COMMENT '供应商联系人';
alter table scc_sc_order add column `EXT_VENDOR_PHONE` varchar(30) DEFAULT NULL COMMENT '供应商联系人电话';
alter table scc_sc_order add column `EXT_PURCHASER_PHONE` varchar(30) DEFAULT NULL COMMENT '采购员电话';
alter table scc_sc_order add column `EXT_PURCHASER_EMAIL` varchar(50) DEFAULT NULL COMMENT '采购员邮箱';
alter table scc_sc_order add column `EXT_ORDER_PROPERTY` varchar(30) DEFAULT NULL COMMENT '订单性质';
alter table scc_sc_order add column `EXT_APPLICANT_NAME` varchar(30) DEFAULT NULL COMMENT '申请人名称';
alter table scc_sc_order add column `EXT_APPLICANT_CODE` varchar(30) DEFAULT NULL COMMENT '申请人工号';
alter table scc_sc_order add column `EXT_APPLY_DATE` datetime DEFAULT NULL COMMENT '申请日期';

alter table scc_sc_order_detail add column `EXT_DETAIL_STATUS` varchar(30) DEFAULT NULL COMMENT '补充状态';
alter table scc_sc_order_detail add column `EXT_BUY_TYPE` varchar(30) DEFAULT NULL COMMENT '购买类型';
alter table scc_sc_order_detail add column `EXT_ATTACH_ID` bigint DEFAULT NULL COMMENT '附件id';
alter table scc_sc_order_detail add column `EXT_ATTACH_NAME` varchar(30) DEFAULT NULL COMMENT '附件名称';
alter table scc_sc_order_detail add column `EXT_RETURN_REQUIREMNT` varchar(10) DEFAULT NULL COMMENT '是否退回需求池，Y/N';
alter table scc_sc_order_detail add column `EXT_BRAND` varchar(50) DEFAULT NULL COMMENT '品牌';
alter table scc_sc_order_detail add column `EXT_USE_DEPARTMENT_CODE` varchar(50) DEFAULT NULL COMMENT '使用部门编码';
alter table scc_sc_order_detail add column `EXT_USE_DEPARTMENT_NAME` varchar(50) DEFAULT NULL COMMENT '使用部门名称';
alter table scc_sc_order_detail add column `EXT_USER_NAME` varchar(50) DEFAULT NULL COMMENT '使用人名称';
alter table scc_sc_order_detail add column `EXT_USER_CODE` varchar(50) DEFAULT NULL COMMENT '使用人工号';
alter table scc_sc_order_detail add column `EXT_CHECK_QTY` decimal(30,8) DEFAULT 0 COMMENT '验收数量';
alter table scc_sc_order_detail add column `EXT_AREA_ID` bigint DEFAULT NULL COMMENT '区域ID';
alter table scc_sc_order_detail add column `EXT_AREA_CODE` varchar(50) DEFAULT NULL COMMENT '区域编码';
alter table scc_sc_order_detail add column `EXT_AREA_NAME` varchar(50) DEFAULT NULL COMMENT '区域名称';

-- 送货单
alter table scc_sc_delivery_note add column `EXT_STATUS` varchar(30) DEFAULT NULL COMMENT '补充状态';
alter table scc_sc_delivery_note add column `EXT_VENDOR_CONTACTS` varchar(30) DEFAULT NULL COMMENT '供应商联系人';
alter table scc_sc_delivery_note add column `EXT_VENDOR_PHONE` varchar(30) DEFAULT NULL COMMENT '供应商联系人电话';
alter table scc_sc_delivery_note add column `EXT_EXPRESS_TYPE` varchar(30) DEFAULT NULL COMMENT '物流货运方式';
alter table scc_sc_delivery_note add column `EXT_EXPRESS_NO` varchar(30) DEFAULT NULL COMMENT '物流快递单号';
alter table scc_sc_delivery_note add column `EXT_EXPRESSMAN` varchar(30) DEFAULT NULL COMMENT '物流配送人员';
alter table scc_sc_delivery_note add column `EXT_EXPRESSPHONE` varchar(30) DEFAULT NULL COMMENT '物流联系方式';
alter table scc_sc_delivery_note add column `EXT_DEPARTMENT_ID` varchar(30) DEFAULT NULL COMMENT '申请部门id';
alter table scc_sc_delivery_note add column `EXT_DEPARTMENT_CODE` varchar(30) DEFAULT NULL COMMENT '申请部门编码';
alter table scc_sc_delivery_note add column `EXT_DEPARTMENT_NAME` varchar(30) DEFAULT NULL COMMENT '申请部门名称';
alter table scc_sc_delivery_note add column `EXT_APPLICANT_NAME` varchar(30) DEFAULT NULL COMMENT '申请人名称';
alter table scc_sc_delivery_note add column `EXT_APPLICANT_CODE` varchar(30) DEFAULT NULL COMMENT '申请人工号';
alter table scc_sc_delivery_note add column `EXT_APPLY_DATE` datetime DEFAULT NULL COMMENT '申请日期';

alter table scc_sc_delivery_note_detail add column `EXT_DETAIL_STATUS` varchar(30) DEFAULT NULL COMMENT '补充状态';
alter table scc_sc_delivery_note_detail add column `EXT_STORAGE_QTY` varchar(30) DEFAULT 0 COMMENT '已入库数量';
alter table scc_sc_delivery_note_detail add column `EXT_CANCEL_QTY` varchar(30) DEFAULT 0 COMMENT '已取消数量';
alter table scc_sc_delivery_note_detail add column `EXT_FINISH_TIME` varchar(30) DEFAULT NULL COMMENT '完成时间';

-- 对账单
alter table scc_sc_invoice_notice add column `EXT_STATUS` varchar(30) DEFAULT NULL COMMENT '补充状态';
alter table scc_sc_invoice_notice add column `EXT_VENDOR_CONTACTS` varchar(30) DEFAULT NULL COMMENT '供应商联系人';
alter table scc_sc_invoice_notice add column `EXT_VENDOR_PHONE` varchar(30) DEFAULT NULL COMMENT '供应商联系人电话';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_TYPE` varchar(30) DEFAULT NULL COMMENT '发票类型';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_COMPANY` varchar(100) DEFAULT NULL COMMENT '发票公司名称';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_OPENING_NAME` varchar(100) DEFAULT NULL COMMENT '发票开户行';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_OPENING_ACCOUNT` varchar(30) DEFAULT NULL COMMENT '发票银行账号';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_TAXPAYER_NUM` varchar(30) DEFAULT NULL COMMENT '纳税识别号';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_ADDRESS` varchar(100) DEFAULT NULL COMMENT '发票地址';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_PHONE` varchar(30) DEFAULT NULL COMMENT '发票电话';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_RECEIVER` varchar(30) DEFAULT NULL COMMENT '发票收票人';
alter table scc_sc_invoice_notice add column `EXT_INVOICE_RECEIVE_ADDR` varchar(100) DEFAULT NULL COMMENT '发票收票人地址';
alter table scc_sc_invoice_notice add column `EXT_DEPARTMENT_ID` varchar(30) DEFAULT NULL COMMENT '申请部门id';
alter table scc_sc_invoice_notice add column `EXT_DEPARTMENT_CODE` varchar(30) DEFAULT NULL COMMENT '申请部门编码';
alter table scc_sc_invoice_notice add column `EXT_DEPARTMENT_NAME` varchar(30) DEFAULT NULL COMMENT '申请部门名称';
alter table scc_sc_invoice_notice add column `EXT_APPLICANT_NAME` varchar(30) DEFAULT NULL COMMENT '申请人名称';
alter table scc_sc_invoice_notice add column `EXT_APPLICANT_CODE` varchar(30) DEFAULT NULL COMMENT '申请人工号';
alter table scc_sc_invoice_notice add column `EXT_APPLY_DATE` datetime DEFAULT NULL COMMENT '申请日期';

-- 验收单
CREATE TABLE `scc_npm_check_order` (
    `CHECK_ORDER_ID` bigint(20) NOT NULL COMMENT '主键,验收单ID',
    `CHECK_ORDER_NUMBER` varchar(30) DEFAULT NULL COMMENT '验收单号',
    `CHECK_ORDER_STATUS` varchar(30) DEFAULT NULL COMMENT '验收单状态',
    `ORG_ID` bigint(20) DEFAULT NULL COMMENT '业务实体ID',
    `ORG_CODE` varchar(50) DEFAULT NULL COMMENT '业务实体编码',
    `ORG_NAME` varchar(30) DEFAULT NULL COMMENT '业务实体名称',
    `VENDOR_ID` bigint(20) DEFAULT NULL COMMENT '供应商ID',
    `VENDOR_CODE` varchar(30) DEFAULT NULL COMMENT '供应商编码',
    `VENDOR_NAME` varchar(100) DEFAULT NULL COMMENT '供应商名称',
    `DEPARTMENT_ID` varchar(30) DEFAULT NULL COMMENT '申请部门id',
    `DEPARTMENT_CODE` varchar(30) DEFAULT NULL COMMENT '申请部门编码',
    `DEPARTMENT_NAME` varchar(30) DEFAULT NULL COMMENT '申请部门名称',
    `APPLICANT_NAME` varchar(30) DEFAULT NULL COMMENT '申请人名称',
    `APPLICANT_CODE` varchar(30) DEFAULT NULL COMMENT '申请人工号',
    `APPLY_DATE` datetime DEFAULT NULL COMMENT '申请日期',
    `NO_TAX_TOTAL_AMOUNT` decimal(30,8) DEFAULT 0 COMMENT '未税总金额',
    `TAX_TOTAL_AMOUNT` decimal(30,8) DEFAULT 0 COMMENT '含税总金额',
    `CURRENCY_ID` bigint(20) DEFAULT NULL COMMENT '币种ID',
    `CURRENCY_NAME` varchar(50) DEFAULT NULL COMMENT '币种名称',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`CHECK_ORDER_ID`),
    KEY `scc_npm_check_order_CHECK_ORDER_NUMBER_IDX` (`CHECK_ORDER_NUMBER`) USING BTREE,
    KEY `scc_npm_check_order_ORG_ID_IDX` (`ORG_ID`) USING BTREE,
    KEY `scc_npm_check_order_CHECK_ORDER_STATUS_IDX` (`CHECK_ORDER_STATUS`) USING BTREE
) ENGINE=InnoDB COMMENT='验收单';

CREATE TABLE `scc_npm_check_order_detail` (
  `CHECK_ORDER_DETAIL_ID` bigint(20) NOT NULL COMMENT '主键,验收单明细id',
  `CHECK_ORDER_ID` bigint(20) NOT NULL COMMENT '验收单ID',
  `ORDER_DETAIL_ID` bigint(20) NOT NULL COMMENT '订单明细ID',
  `CHECK_QTY` decimal(30,8) DEFAULT 0 COMMENT '本次验收数量',
  `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
  `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
  `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
  `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
  `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
  `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
  `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
  `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
  `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
  `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
  `VERSION` bigint(20) DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`CHECK_ORDER_DETAIL_ID`),
  KEY `scc_npm_check_order_detail_CHECK_ORDER_ID_IDX` (`CHECK_ORDER_ID`) USING BTREE,
  KEY `scc_npm_check_order_detail_ORDER_DETAIL_ID_IDX` (`ORDER_DETAIL_ID`) USING BTREE
) ENGINE=InnoDB COMMENT='验收单明细';

-- 自动转订单
CREATE TABLE `scc_npm_order_config` (
    `CONFIG_ID` bigint(20) NOT NULL COMMENT '主键,配置id',
    `CONFIG_NUM` varchar(30) NOT NULL COMMENT '规则编码',
    `CONFIG_NAME` varchar(100) DEFAULT NULL COMMENT '规则名称',
    `CREATOR_ORG_ID` bigint(20) DEFAULT NULL COMMENT '创建人公司ID',
    `CREATOR_ORG_CODE` varchar(50) DEFAULT NULL COMMENT '创建人公司编码',
    `CREATOR_ORG_NAME` varchar(30) DEFAULT NULL COMMENT '创建人公司名称',
    `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
    `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
    `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
    `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
    `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
    `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
    `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
    `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
    `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
    `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
    `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
    `VERSION` bigint(20) DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`CONFIG_ID`)
) ENGINE=InnoDB COMMENT='自动转订单配置';

CREATE TABLE `scc_npm_order_config_category` (
     `CONFIG_CATEGORY_ID` bigint(20) NOT NULL COMMENT '主键,id',
     `CONFIG_ID` bigint(20) NOT NULL COMMENT '配置id',
     `CATEGORY_ID` bigint(20) DEFAULT NULL COMMENT '品类id',
     `CATEGORY_NAME` varchar(100) DEFAULT NULL COMMENT '品类名称',
     `CATEGORY_CODE` varchar(50) DEFAULT NULL COMMENT '品类编码',
     `ORG_ID` bigint(20) DEFAULT NULL COMMENT '公司id',
     `ORG_NAME` varchar(100) DEFAULT NULL COMMENT '公司名称',
     `ORG_CODE` varchar(50) DEFAULT NULL COMMENT '公司编码',
     `CREATED_ID` bigint(20) NOT NULL COMMENT '创建人ID',
     `CREATED_BY` varchar(50) NOT NULL COMMENT '创建人',
     `CREATION_DATE` datetime NOT NULL COMMENT '创建时间',
     `CREATED_BY_IP` varchar(150) NOT NULL COMMENT '创建人IP',
     `CREATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
     `LAST_UPDATED_ID` bigint(20) DEFAULT NULL COMMENT '最后更新人ID',
     `LAST_UPDATED_BY` varchar(50) DEFAULT NULL COMMENT '更新人',
     `LAST_UPDATE_DATE` datetime DEFAULT NULL COMMENT '最后更新时间',
     `LAST_UPDATED_BY_IP` varchar(150) DEFAULT NULL COMMENT '最后更新人IP',
     `LAST_UPDATED_FULL_NAME` varchar(100) DEFAULT NULL COMMENT '最后更新人姓名',
     `TENANT_ID` varchar(30) DEFAULT NULL COMMENT '租户ID',
     `VERSION` bigint(20) DEFAULT 0 COMMENT '版本号',
     PRIMARY KEY (`CONFIG_CATEGORY_ID`),
     UNIQUE KEY `scc_npm_order_config_category_CATEGORY_ID_IDX` (`CATEGORY_ID`,`ORG_ID`) USING BTREE,
     KEY `scc_npm_order_config_category_CONFIG_ID_IDX` (`CONFIG_ID`) USING BTREE
) ENGINE=InnoDB COMMENT='自动转订单品类配置';