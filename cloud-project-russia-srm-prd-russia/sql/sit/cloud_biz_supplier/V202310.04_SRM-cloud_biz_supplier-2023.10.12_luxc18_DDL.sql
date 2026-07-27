alter table scc_npm_company_exception_info
    add ORG_OR_CATEGORY_ID bigint(20)  null comment '组织/品类id',
    add ORG_OR_CATEGORY_CODE VARCHAR(250)  null comment '组织/品类编码';
