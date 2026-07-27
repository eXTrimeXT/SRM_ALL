alter table scc_npm_pr_require_head add IF_APPOINT_BRAND char(1) default 'N' not null comment '是否指定品牌';
alter table scc_npm_pr_require_head add APPOINT_BRAND_FILE_ID bigint null comment '指定品牌文件ID';
alter table scc_npm_pr_require_head add APPOINT_BRAND_FILE_NAME varchar(150) null comment '指定品牌文件名称';
alter table scc_npm_pr_require_head add IF_QUALIFY_UNIT char(1) default 'N' not null comment '是否限制单位';
alter table scc_npm_pr_require_head add QUALIFY_UNIT_FILE_ID bigint null comment '限制单位文件ID';
alter table scc_npm_pr_require_head add QUALIFY_UNIT_FILE_NAME varchar(150) null comment '限制单位文件名称';
