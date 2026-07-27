alter table scc_npm_pr_recent_purchase_data add column LEAD_TIME int comment '供货周期';
alter table scc_npm_pr_recent_purchase_data add column INVOICE_TYPE VARCHAR(50) comment '发票类型';
alter table scc_npm_pr_recent_purchase_data add column WARRANTY_PERIOD VARCHAR(50) comment '质保期';
alter table scc_npm_pr_recent_purchase_data add column ADVANCE_PAYMENT_REMARK VARCHAR(10) comment '预付款说明';


alter table scc_pr_requirement_line add column EXT_LEAD_TIME int comment '供货周期';
alter table scc_pr_requirement_line add column EXT_INVOICE_TYPE VARCHAR(50) comment '发票类型';
alter table scc_pr_requirement_line add column EXT_WARRANTY_PERIOD VARCHAR(50) comment '质保期';
alter table scc_pr_requirement_line add column EXT_ADVANCE_PAYMENT_REMARK VARCHAR(10) comment '预付款说明';