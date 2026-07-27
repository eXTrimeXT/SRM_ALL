alter table scc_npm_sou_purinq_project add CREATE_USER_ORG_OU_ID bigint(20) null comment '创建人所在公司ID';
alter table scc_npm_sou_purinq_project add CREATE_USER_ORG_OU_CODE varchar(50) null comment '创建人所在公司编码';
alter table scc_npm_sou_purinq_project add CREATE_USER_ORG_OU_NAME varchar(255) null comment '创建人所在公司名称';
alter table scc_npm_sou_purinq_project add CREATE_USER_DEPT_ID bigint(20) null comment '创建人所在部门ID';
alter table scc_npm_sou_purinq_project add CREATE_USER_DEPT_CODE varchar(50) null comment '创建人所在部门编码';
alter table scc_npm_sou_purinq_project add CREATE_USER_DEPT_NAME varchar(255) null comment '创建人所在部门名称';

alter table scc_npm_sou_purfix_price_head add CREATE_USER_DEPT_ID bigint(20) null comment '创建人所在部门ID';
alter table scc_npm_sou_purfix_price_head add CREATE_USER_DEPT_CODE varchar(50) null comment '创建人所在部门编码';
alter table scc_npm_sou_purfix_price_head add CREATE_USER_DEPT_NAME varchar(255) null comment '创建人所在部门名称';