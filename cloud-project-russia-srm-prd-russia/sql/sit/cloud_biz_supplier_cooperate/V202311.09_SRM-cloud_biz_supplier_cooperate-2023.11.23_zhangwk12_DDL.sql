alter table scc_pr_requirement_line add IF_CREATE_INQ char default 'N' null comment '是否已创建询比价';
alter table scc_pr_requirement_line add IF_CREATE_FIX_PRICE char default 'N' null comment '是否已定价';