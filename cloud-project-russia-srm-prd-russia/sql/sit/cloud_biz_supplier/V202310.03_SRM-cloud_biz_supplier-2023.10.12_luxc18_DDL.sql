alter table scc_npm_company_exception_info
    add DELETE_FLAG VARCHAR(10) default 'N' null comment '删除标识(Y/N)';
