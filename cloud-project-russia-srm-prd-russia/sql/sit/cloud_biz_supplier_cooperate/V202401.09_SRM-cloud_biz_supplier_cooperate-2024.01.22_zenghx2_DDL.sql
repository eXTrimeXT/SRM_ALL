alter table scc_sc_order_detail add column EXT_AGREEMENT_TYPE varchar(50) comment '协议性质';
alter table scc_sc_order_detail add column EXT_AGREEMENT_INFO_ID bigint comment '协议行id';

alter table scc_sc_delivery_note_detail add column EXT_STORAGE_TIME DATETIME comment 'EAS入库时间';
alter table scc_sc_delivery_note_detail add column EXT_RECEIVE_TIME DATETIME comment 'EAS收货时间';
