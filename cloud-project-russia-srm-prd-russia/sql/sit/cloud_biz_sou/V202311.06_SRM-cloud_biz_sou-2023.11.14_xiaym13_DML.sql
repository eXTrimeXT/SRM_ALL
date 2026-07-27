alter table scc_npm_sou_req_head
    add ORGANIZATION_ID bigint null comment '适用板块组织ID' after PROJECT_VIEWS_COUNT;

alter table scc_npm_sou_req_head
    add ORGANIZATION_CODE varchar(100) null comment '适用板块组织编码' after ORGANIZATION_ID;

alter table scc_npm_sou_req_head
    add ORGANIZATION_NAME varchar(100) null comment '适用板块组织名称' after ORGANIZATION_CODE;

