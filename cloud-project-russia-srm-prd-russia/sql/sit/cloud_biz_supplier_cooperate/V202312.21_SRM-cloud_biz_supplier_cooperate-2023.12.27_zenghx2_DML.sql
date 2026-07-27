alter table scc_npm_check_order add column APPROVE_USER_ID bigint comment '审批人id';
alter table scc_npm_check_order add column APPROVE_USER_CODE VARCHAR(50) comment '审批人工号';
alter table scc_npm_check_order add column APPROVE_USER_NAME VARCHAR(50) comment '审批人名称';
alter table scc_npm_check_order add column APPROVE_COMMENT VARCHAR(200) comment '审批备注';