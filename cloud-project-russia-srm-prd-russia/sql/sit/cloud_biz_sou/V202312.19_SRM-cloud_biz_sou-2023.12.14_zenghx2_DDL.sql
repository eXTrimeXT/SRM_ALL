alter table scc_npm_recruit add column     `ORG_BU_ID`                bigint(20)           DEFAULT NULL COMMENT '板块ID';
alter table scc_npm_recruit add column     `ORG_BU_CODE`              varchar(50)          DEFAULT NULL COMMENT '板块编码';
alter table scc_npm_recruit add column     `ORG_BU_NAME`              varchar(50)          DEFAULT NULL COMMENT '板块名称';
alter table scc_npm_recruit add column     `ORG_ID`                   bigint(20)           DEFAULT NULL COMMENT '业务实体ID';
alter table scc_npm_recruit add column     `ORG_CODE`                 varchar(50)          DEFAULT NULL COMMENT '业务实体编码';
alter table scc_npm_recruit add column     `ORG_NAME`                 varchar(50)          DEFAULT NULL COMMENT '业务实体名称';
alter table scc_npm_recruit add column     `DEPARTMENT_ID`            bigint(20)           DEFAULT NULL COMMENT '部门id';
alter table scc_npm_recruit add column     `DEPARTMENT_CODE`          varchar(50)          DEFAULT NULL COMMENT '部门编码';
alter table scc_npm_recruit add column     `DEPARTMENT_NAME`          varchar(50)          DEFAULT NULL COMMENT '部门名称';

alter table scc_npm_inspect add column     `ORG_BU_ID`                bigint(20)           DEFAULT NULL COMMENT '板块ID';
alter table scc_npm_inspect add column     `ORG_BU_CODE`              varchar(50)          DEFAULT NULL COMMENT '板块编码';
alter table scc_npm_inspect add column     `ORG_BU_NAME`              varchar(50)          DEFAULT NULL COMMENT '板块名称';

alter table scc_npm_borrow add column     `ORG_BU_ID`                bigint(20)           DEFAULT NULL COMMENT '板块ID';
alter table scc_npm_borrow add column     `ORG_BU_CODE`              varchar(50)          DEFAULT NULL COMMENT '板块编码';
alter table scc_npm_borrow add column     `ORG_BU_NAME`              varchar(50)          DEFAULT NULL COMMENT '板块名称';
