alter table scc_perf_level_overall_score drop column WARNING_ID;
alter table scc_perf_level_overall_score drop column WARNING_CODE;
alter table scc_perf_level_overall_score drop column WARNING_STATUS;

alter table scc_perf_overall_score
    add WARNING_ID bigint null comment '预警单据ID';

alter table scc_perf_overall_score
    add WARNING_CODE varchar(50) null comment '预警单据号';

alter table scc_perf_overall_score
    add WARNING_STATUS varchar(50) null comment '预警状态';