alter table scc_sup_company_info
    add ALLOW_CLEAR_WITHOUT_SEAL_FLAG VARCHAR(5) default 'N' null comment '是否允许澄清免签章',
    add ALLOW_BID_WITHOUT_SEAL_FLAG VARCHAR(5) default 'N' null comment '是否允许投标免签章',
    add ALLOW_QUOTATION_WITHOUT_SEAL_FLAG VARCHAR(5) default 'N' null comment '是否允许报价免签章';