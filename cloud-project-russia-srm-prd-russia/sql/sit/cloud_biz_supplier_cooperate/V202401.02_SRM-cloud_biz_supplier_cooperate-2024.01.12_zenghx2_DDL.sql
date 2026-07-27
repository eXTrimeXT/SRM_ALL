alter table ceea_sc_online_invoice add column EXT_PRINCIPAL_CODE varchar(50) DEFAULT NULL COMMENT '开票主体编码';
alter table ceea_sc_online_invoice add column EXT_PRINCIPAL_NAME varchar(100) DEFAULT NULL COMMENT '开票主体名称';
