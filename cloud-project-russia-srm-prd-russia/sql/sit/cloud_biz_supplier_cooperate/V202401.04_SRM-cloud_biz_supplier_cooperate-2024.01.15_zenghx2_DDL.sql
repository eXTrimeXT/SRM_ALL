alter table ceea_sc_online_invoice_detail add column `EXT_CHECK_DETAIL_ID` bigint(20) COMMENT '验收单明细id';
alter table scc_npm_check_order_detail add column INVOICE_QTY decimal(30, 8) DEFAULT 0 COMMENT '开票数量';

alter table scc_npm_check_order drop column INVOICE_QTY;

alter table ceea_sc_online_invoice_detail add index idx_online_invoice_detail_check_detail_id(EXT_CHECK_DETAIL_ID);
