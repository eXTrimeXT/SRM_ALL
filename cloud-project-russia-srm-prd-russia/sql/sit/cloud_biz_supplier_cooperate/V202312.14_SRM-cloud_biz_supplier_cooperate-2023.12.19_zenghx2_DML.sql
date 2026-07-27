alter table scc_sc_order add column EXT_APPROVE_USER_ID bigint comment '审批人id';
alter table scc_sc_order add column EXT_APPROVE_USER_CODE bigint comment '审批人工号';
alter table scc_sc_order add column EXT_APPROVE_USER_NAME bigint comment '审批人名称';
alter table scc_sc_order add column EXT_APPROVE_COMMENT bigint comment '审批备注';