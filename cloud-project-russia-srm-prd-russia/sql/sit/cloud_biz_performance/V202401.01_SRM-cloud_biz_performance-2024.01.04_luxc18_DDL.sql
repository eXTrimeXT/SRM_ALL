alter table scc_npm_project_score_header
    add EXT_INVEST_NO varchar(50) null comment '投资编号';

alter table scc_npm_project_score_items
    add EXT_INVEST_NO varchar(50) null comment '投资编号';

alter table scc_npm_project_score_man
    add EXT_INVEST_NO varchar(50) null comment '投资编号';