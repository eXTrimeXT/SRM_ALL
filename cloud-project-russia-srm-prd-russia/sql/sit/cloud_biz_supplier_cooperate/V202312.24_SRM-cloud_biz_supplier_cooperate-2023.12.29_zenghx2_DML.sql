ALTER TABLE `scc_pr_requirement_line`
    CHANGE COLUMN `ORGANIZATION_CODE` `ORGANIZATION_CODE` VARCHAR(30) COMMENT '库存组织编码' AFTER `ORGANIZATION_ID`,
    CHANGE COLUMN `ORGANIZATION_NAME` `ORGANIZATION_NAME` VARCHAR(50) COMMENT '库存组织名称' AFTER `ORGANIZATION_CODE`,
    CHANGE COLUMN `UNIT_CODE` `UNIT_CODE` VARCHAR(20) COMMENT '单位编码' AFTER `PURCHASE_ORGANIZATION`,
    CHANGE COLUMN `UNIT` `UNIT` VARCHAR(20) COMMENT '单位描述' AFTER `UNIT_CODE`,
    CHANGE COLUMN `CEEA_SUP_USER_NICKNAME` `CEEA_SUP_USER_NICKNAME` VARCHAR(50) COMMENT '供应商管理采购员名称' AFTER `CEEA_SUP_USER_ID`,
    CHANGE COLUMN `CEEA_SUP_USER_NAME` `CEEA_SUP_USER_NAME` VARCHAR(50) COMMENT '供应商管理采购员账号' AFTER `CEEA_SUP_USER_NICKNAME`,
    CHANGE COLUMN `CEEA_STRATEGY_USER_NICKNAME` `CEEA_STRATEGY_USER_NICKNAME` VARCHAR(50) COMMENT '策略负责采购员名称' AFTER `CEEA_STRATEGY_USER_ID`,
    CHANGE COLUMN `CEEA_STRATEGY_USER_NAME` `CEEA_STRATEGY_USER_NAME` VARCHAR(50) COMMENT '策略负责采购员账号' AFTER `CEEA_STRATEGY_USER_NICKNAME`,
    CHANGE COLUMN `CEEA_PERFORM_USER_NICKNAME` `CEEA_PERFORM_USER_NICKNAME` VARCHAR(50) COMMENT '采购履行采购员名称' AFTER `CEEA_PERFORM_USER_ID`,
    CHANGE COLUMN `CEEA_PERFORM_USER_NAME` `CEEA_PERFORM_USER_NAME` VARCHAR(50) COMMENT '采购履行采购员账号' AFTER `CEEA_PERFORM_USER_NICKNAME`;


alter table scc_pr_requirement_line add column EXT_HISTORY_VENDOR_BRAND1 VARCHAR(50) comment '历史供应商品牌1';
alter table scc_pr_requirement_line add column EXT_HISTORY_ORDER_DETAIL1 bigint(20) comment '历史供应商订单1';
alter table scc_pr_requirement_line add column EXT_HISTORY_VENDOR_BRAND2 VARCHAR(50) comment '历史供应商品牌2';
alter table scc_pr_requirement_line add column EXT_HISTORY_ORDER_DETAIL2 bigint(20) comment '历史供应商订单2';
alter table scc_pr_requirement_line add column EXT_HISTORY_VENDOR_BRAND3 VARCHAR(50) comment '历史供应商品牌3';
alter table scc_pr_requirement_line add column EXT_HISTORY_ORDER_DETAIL3 bigint(20) comment '历史供应商订单3';


alter table scc_npm_check_order add column CHECK_ADVICE VARCHAR(200) comment '验收意见';
alter table scc_npm_check_order add column `ORG_BU_ID` bigint(20) COMMENT '板块id';
alter table scc_npm_check_order add column `ORG_BU_CODE` varchar(50) COMMENT '板块编码';
alter table scc_npm_check_order add column `ORG_BU_NAME` varchar(50) COMMENT '板块名称';