alter table scc_perf_level_overall_score drop column 预警单据号;

alter table scc_perf_level_overall_score
    add WARNING_CODE varchar(50) null comment '预警单据号';

alter table scc_perf_level_overall_score
    add WARNING_STATUS varchar(50) null comment '预警状态';