alter table scc_npm_pr_require_cancel_line add ORG_BU_ID bigint null comment '所属板块ID';
alter table scc_npm_pr_require_cancel_line add ORG_BU_CODE varchar(50) null comment '所属板块编码';
alter table scc_npm_pr_require_cancel_line add ORG_BU_NAME varchar(255) null comment '所属板块名称';
alter table scc_npm_pr_require_cancel_line add ORG_ID bigint null comment '所属公司名称';
alter table scc_npm_pr_require_cancel_line add ORG_CODE varchar(50) null comment '所属公司编码';
alter table scc_npm_pr_require_cancel_line add ORG_NAME varchar(255) null comment '所属公司名称';
alter table scc_npm_pr_require_cancel_line add CEEA_DEPARTMENT_ID varchar(50) null comment '所属部门ID';
alter table scc_npm_pr_require_cancel_line add CEEA_DEPARTMENT_NAME varchar(255) null comment '所属部门名称';