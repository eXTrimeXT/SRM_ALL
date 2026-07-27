alter table scc_perf_score_man_scoring_v1
    add SCORING_STATUS VARCHAR(50) default 'DRAFT' null comment '评分状态';

alter table scc_perf_score_man_scoring_v1
    add REJECT_REMARK TEXT null comment '驳回原因';

