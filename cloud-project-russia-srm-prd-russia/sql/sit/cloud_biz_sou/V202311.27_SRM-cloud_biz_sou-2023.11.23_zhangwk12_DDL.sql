alter table scc_sou_inq_order_item add HAS_FIX_PRICE char default 'N' null comment '是否已定价';
alter table scc_sou_inq_order_item add EXT_FIX_PRICE_HEAD_ID bigint null comment '定价单ID';
alter table scc_sou_inq_order_item add EXT_FIX_PRICE_NO varchar(50) null comment '定价单号';
alter table scc_sou_inq_order_item add EXT_FIX_PRICE_LINE_ID bigint null comment '定价明细ID';