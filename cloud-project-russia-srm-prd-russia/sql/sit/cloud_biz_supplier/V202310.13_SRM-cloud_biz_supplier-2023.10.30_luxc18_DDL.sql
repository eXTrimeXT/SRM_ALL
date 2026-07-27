alter table scc_sup_company_info modify FOCUS_FLAG varchar(10) default 'N' not null comment '是否重点关注';

alter table scc_sup_company_info modify POSITION_LIMIT_FLAG varchar(10) default 'N' not null comment '是否单位受限';

alter table scc_sup_company_info modify CATEGORY_LIMIT_FLAG varchar(10) default 'N' not null comment '是否品类受限';

alter table scc_sup_company_info modify TIME_LIMIT_FLAG varchar(10) default 'N' not null comment '是否时间受限';

alter table scc_sup_company_info modify CONTRACT_VERIFICATION varchar(10) default 'N' not null comment '锲约验证';

alter table scc_sup_company_info modify BIDDING_FLAG varchar(10) default 'N' not null comment '是否竞价';

alter table scc_sup_company_info modify KEY_SUPERVISION_FLAG varchar(10) default 'N' not null comment '是否重点监督';

alter table scc_sup_company_info modify ALLOW_CLEAR_WITHOUT_SEAL_FLAG varchar(5) default 'N' not null comment '是否允许澄清免签章';

alter table scc_sup_company_info modify ALLOW_BID_WITHOUT_SEAL_FLAG varchar(5) default 'N' not null comment '是否允许投标免签章';

alter table scc_sup_company_info modify ALLOW_QUOTATION_WITHOUT_SEAL_FLAG varchar(5) default 'N' not null comment '是否允许报价免签章';

