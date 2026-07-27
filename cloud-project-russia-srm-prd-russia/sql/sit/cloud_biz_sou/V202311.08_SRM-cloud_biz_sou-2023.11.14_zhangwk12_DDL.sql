update scc_sou_inq_order_item set EXT_LEAD_TIME = 0 where 1 = 1;
alter table scc_sou_inq_order_item modify EXT_LEAD_TIME int null comment '供货周期';