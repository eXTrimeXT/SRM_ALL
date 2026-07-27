ALTER TABLE scc_npm_sou_fix_price_head add column CREATE_USER_ORG_BU_ID bigint null comment '创建人所在板块ID';
ALTER TABLE scc_npm_sou_fix_price_head add column CREATE_USER_ORG_BU_CODE varchar(50) null comment '创建人所在板块编码';
ALTER TABLE scc_npm_sou_fix_price_head add column CREATE_USER_ORG_BU_NAME varchar(100) null comment '创建人所在板块名称';

ALTER TABLE scc_npm_sou_purfix_price_head add column CREATE_USER_ORG_BU_ID bigint null comment '创建人所在板块ID';
ALTER TABLE scc_npm_sou_purfix_price_head add column CREATE_USER_ORG_BU_CODE varchar(50) null comment '创建人所在板块编码';
ALTER TABLE scc_npm_sou_purfix_price_head add column CREATE_USER_ORG_BU_NAME varchar(100) null comment '创建人所在板块名称';

ALTER TABLE scc_npm_sou_purinq_project add column CREATE_USER_ORG_BU_ID bigint null comment '创建人所在板块ID';
ALTER TABLE scc_npm_sou_purinq_project add column CREATE_USER_ORG_BU_CODE varchar(50) null comment '创建人所在板块编码';
ALTER TABLE scc_npm_sou_purinq_project add column CREATE_USER_ORG_BU_NAME varchar(100) null comment '创建人所在板块名称';

ALTER TABLE scc_sou_inq_project add column CREATE_USER_ORG_BU_ID bigint null comment '创建人所在板块ID';
ALTER TABLE scc_sou_inq_project add column CREATE_USER_ORG_BU_CODE varchar(50) null comment '创建人所在板块编码';
ALTER TABLE scc_sou_inq_project add column CREATE_USER_ORG_BU_NAME varchar(100) null comment '创建人所在板块名称';