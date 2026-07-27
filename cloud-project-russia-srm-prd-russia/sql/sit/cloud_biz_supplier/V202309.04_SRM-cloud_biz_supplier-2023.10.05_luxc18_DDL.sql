-- 供应商主数据,信息变更表,加状态字段,gscp等
alter table scc_sup_company_info
    add PJ_COMPANY_STATUS VARCHAR(100) null comment '二开-供应商状态';

alter table scc_sup_company_info_change
    add PJ_COMPANY_STATUS VARCHAR(100) null comment '二开-供应商状态';


