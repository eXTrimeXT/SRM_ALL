-- 品类关系加字段
alter table scc_sup_org_category
    add PJ_ORG_STATUS VARCHAR(50) null comment '组织状态';

alter table scc_sup_org_category
    add PJ_CATEGORY_STATUS VARCHAR(50) null comment '品类状态';

-- 供应商主数据,信息变更表,加状态字段,gscp等
alter table scc_sup_company_info
    add GSCP_STATUS VARCHAR(100) null comment 'GSCP状态';

alter table scc_sup_company_info_change
    add GSCP_STATUS VARCHAR(100) null comment 'GSCP状态';

-- 异常记录表,增加单据id,合作终止用上
alter table scc_npm_company_exception_info
    add BUSINESS_ID BIGINT(20) null comment '单据ID';

-- 准入流程,增加字段,维护加的现场评审,物料试用,样品确认字段,换字段
alter table scc_sup_entry_config
    add PJ_IF_AUTH varchar(20) null comment '二开-是否现场评审',
	add PJ_IF_AUTH_SAMPLE varchar(20) null comment '二开-是否样品确认',
	add PJ_IF_MATERIAL varchar(20) null comment '二开-是否物流试用';






