alter table scc_npm_sou_invite_history
    add CATEGORY_ID bigint null comment '品类id';

alter table scc_npm_sou_invite_history
    add CATEGORY_CODE varchar(250) null comment '品类编码';

alter table scc_npm_sou_invite_history
    add CATEGORY_NAME varchar(250) null comment '品类名称';

