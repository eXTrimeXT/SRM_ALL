alter table scc_perf_level_overall_score
    add WARNING_ID bigint null comment '预警单据ID';

alter table scc_perf_level_overall_score
    add 预警单据号 varchar(50) null comment '预警单据号';