alter table scc_npm_pr_push_config add column PUSH_USER_ID bigint comment '分单人id';
alter table scc_npm_pr_push_config add column PUSH_USER_CODE varchar(50) comment '分单人工号';
alter table scc_npm_pr_push_config add column PUSH_USER_NAME varchar(50) comment '分单人名称';

ALTER TABLE `scc_sc_order`
    CHANGE COLUMN `EXT_APPROVE_USER_CODE` `EXT_APPROVE_USER_CODE` VARCHAR(50) COMMENT '审批人工号',
    CHANGE COLUMN `EXT_APPROVE_USER_NAME` `EXT_APPROVE_USER_NAME` VARCHAR(50) COMMENT '审批人名称',
    CHANGE COLUMN `EXT_APPROVE_COMMENT` `EXT_APPROVE_COMMENT` VARCHAR(200) COMMENT '审批备注';


alter table scc_sc_delivery_note_detail add column EXT_CANCEL_REASON VARCHAR(200) COMMENT '取消原因' ;