alter table scc_npm_project_score_header
    add PERFORMANCE_TYPE MEDIUMTEXT null comment '履约类型',
    add SUPPLIER_REMARK MEDIUMTEXT null comment '处理备注',
    add WARNING_STATUS VARCHAR(50) null comment '预警状态',
	add WARNING_CODE VARCHAR(50) null comment '预警单据号';