ALTER TABLE scc_pr_requirement_line add EXT_CLOSED_FILE_ID BIGINT(20) null comment '关闭附件id';
ALTER TABLE scc_pr_requirement_line add EXT_CLOSED_FILE_NAME varchar(500) null comment '关闭附件名称';