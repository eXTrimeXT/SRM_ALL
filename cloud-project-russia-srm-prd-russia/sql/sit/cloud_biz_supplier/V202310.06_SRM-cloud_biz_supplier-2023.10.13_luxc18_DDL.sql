alter table scc_sup_company_info modify EXT_ID_CARD_OPPOSITE_FILE_ID bigint(20) null comment '身份证反面文件id';

alter table scc_sup_company_info modify EXT_ID_CARD_OPPOSITE_FILE_NAME varchar(200) null comment '身份证反面文件名';

alter table scc_sup_company_info_change modify EXT_ID_CARD_OPPOSITE_FILE_ID bigint(20) null comment '身份证反面文件id';

alter table scc_sup_company_info_change modify EXT_ID_CARD_OPPOSITE_FILE_NAME varchar(200) null comment '身份证反面文件名';
