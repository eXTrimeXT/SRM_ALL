alter table scc_npm_project_score_man_detail
    add TEMPLATE_INDS_LINE_ID bigint(20) null comment '绩效模型指标行表-ID';

alter table scc_npm_project_score_man_detail
    add PEF_SCORE decimal(20,2) null comment '绩效模型指标行表-取值方式值(保存操作‘直接取值’和‘按区间折算’时必填)';

