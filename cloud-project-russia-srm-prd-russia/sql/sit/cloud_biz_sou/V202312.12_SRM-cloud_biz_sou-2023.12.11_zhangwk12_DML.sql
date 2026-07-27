alter table scc_sou_inq_project add CREATE_USER_ORG_OU_ID bigint null comment '创建人所在公司ID';
alter table scc_sou_inq_project add CREATE_USER_ORG_OU_CODE varchar(50) null comment '创建人所在公司编码';
alter table scc_sou_inq_project add CREATE_USER_ORG_OU_NAME varchar(150) null comment '创建人所在公司名称';