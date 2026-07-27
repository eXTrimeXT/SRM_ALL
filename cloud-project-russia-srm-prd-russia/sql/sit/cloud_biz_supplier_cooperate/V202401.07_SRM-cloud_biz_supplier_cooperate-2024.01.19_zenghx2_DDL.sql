alter table ceea_sc_online_invoice add column EXT_PROFIT_CENTER_CODE varchar(50) comment '利润中心编码';
alter table ceea_sc_online_invoice add column EXT_PROFIT_CENTER_NAME varchar(100) comment '利润中心名称';
alter table ceea_sc_online_invoice add column EXT_SYNC_EAS varchar(10) comment '是否同步给EAS,Y/N';
alter table ceea_sc_online_invoice_detail drop column EXT_SYNC_EAS;