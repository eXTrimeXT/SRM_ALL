ALTER TABLE scc_npm_sou_ca
	ADD COLUMN approval_user_id BIGINT(20) NULL DEFAULT NULL COMMENT '第一层级审批人ID',
	ADD COLUMN approval_user_name VARCHAR(50) NULL DEFAULT NULL COMMENT '第一层级审批人账号',
	ADD COLUMN approval_nickname VARCHAR(200) NULL DEFAULT NULL COMMENT '第一层级审批人姓名';