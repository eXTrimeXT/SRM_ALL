alter table scc_sc_order_detail
    add column `EXT_INVOICE_TYPE` VARCHAR(50) COMMENT '发票类型';
alter table scc_sc_order_detail
    add column `EXT_WARRANTY_PERIOD` INT COMMENT '质保日期';

alter table scc_npm_pr_push_config
    add column `STATUS` VARCHAR(30) COMMENT '状态Y/N';
alter table scc_npm_pr_push_config
    add column `CREATOR_ORG_NAME` VARCHAR(100) COMMENT '创建人组织';