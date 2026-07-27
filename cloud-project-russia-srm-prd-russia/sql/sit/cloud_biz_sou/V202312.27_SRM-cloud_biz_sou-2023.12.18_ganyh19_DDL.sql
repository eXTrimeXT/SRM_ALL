alter table scc_sou_question
    add column EXT_CATEGORY_CODE VARCHAR(30) NULL DEFAULT NULL COMMENT '品类代码',
    add column EXT_CATEGORY_ID BIGINT NULL DEFAULT NULL COMMENT '品类ID',
    add column EXT_CATEGORY_NAME varchar(50) NULL DEFAULT NULL COMMENT '品类名称';

alter table scc_sou_answer
    add column EXT_CATEGORY_CODE VARCHAR(30) NULL DEFAULT NULL COMMENT '品类代码',
    add column EXT_CATEGORY_ID BIGINT NULL DEFAULT NULL COMMENT '品类ID',
    add column EXT_CATEGORY_NAME varchar(50) NULL DEFAULT NULL COMMENT '品类名称';


