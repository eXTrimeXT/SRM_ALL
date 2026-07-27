alter table scc_npm_project_score_items
    add CHECK_STATUS VARCHAR(50) null comment '复核状态';

alter table scc_npm_project_score_items_person
    add APPROVE_STATUS varchar(50) null comment '审批状态';

