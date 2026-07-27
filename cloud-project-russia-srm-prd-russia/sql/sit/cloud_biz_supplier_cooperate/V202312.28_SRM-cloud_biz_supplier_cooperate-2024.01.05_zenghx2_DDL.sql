alter table scc_sc_delivery_note add column EXT_PURCHASER_NO VARCHAR(30) comment '采购员工号';
alter table scc_sc_delivery_note add column EXT_PURCHASER_ID BIGINT(20) comment '采购员id';
alter table scc_sc_delivery_note add column EXT_PURCHASER_NAME VARCHAR(30) comment '采购员名称';
alter table scc_sc_delivery_note add column EXT_PURCHASER_ORG_NAME VARCHAR(50) comment '采购员单位名称';
alter table scc_sc_delivery_note add column EXT_PURCHASER_PHONE VARCHAR(30) comment '采购员电话';
alter table scc_sc_delivery_note add column EXT_PURCHASER_EMAIL VARCHAR(50) comment '采购员邮箱';