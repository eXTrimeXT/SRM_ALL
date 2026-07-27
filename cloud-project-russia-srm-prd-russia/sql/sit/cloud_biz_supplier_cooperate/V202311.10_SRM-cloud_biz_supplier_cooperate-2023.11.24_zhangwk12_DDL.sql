alter table scc_pr_requirement_line add FIX_PRICE_HEAD_ID bigint null comment '定价单ID';
alter table scc_pr_requirement_line add FIX_PRICE_NO varchar(50) null comment '定价单号';
alter table scc_pr_requirement_line add FIX_PRICE_LINE_ID bigint null comment '定价单明细ID';