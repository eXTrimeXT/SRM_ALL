ALTER TABLE scc_npm_sou_expert_score_line add PROXY_USER_ID bigint null comment '代理评分用户';
ALTER TABLE scc_npm_sou_expert_score add SCORE_FOR_LEADER char(1) default 'N' not null comment '评分对象是否为组长';