alter table scc_npm_pr_require_cancel_line add CATEGORY_ID bigint null comment '所属品类ID';
alter table scc_npm_pr_require_cancel_line add CATEGORY_CODE varchar(50) null comment '所属品类编码';
alter table scc_npm_pr_require_cancel_line add CATEGORY_NAME varchar(255) null comment '所属品类名称';