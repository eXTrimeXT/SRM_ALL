alter table scc_sou_ch_ledger add column HR_SECTOR_CODE VARCHAR(30) NULL DEFAULT NULL COMMENT '板块代码',
                                 add column HR_SECTOR_ID BIGINT NULL DEFAULT NULL COMMENT '板块ID',
                                 add column HR_SECTOR_NAME varchar(50) NULL DEFAULT NULL COMMENT '板块名称',
                                 add column HR_DEPT_ID BIGINT NULL DEFAULT NULL COMMENT '所属部门ID',
                                 add column HR_DEPT_NAME VARCHAR(30) NULL DEFAULT NULL COMMENT '所属部门名称',
                                 add column HR_DEPT_CODE VARCHAR(20) NULL DEFAULT NULL COMMENT '所属部门代码',
                                 add column HR_COMPANY_ID BIGINT NULL DEFAULT NULL COMMENT '所属公司ID',
                                 add column HR_COMPANY_NAME VARCHAR(30) NULL DEFAULT NULL COMMENT '所属公司名称',
                                 add column HR_COMPANY_CODE VARCHAR(20) NULL DEFAULT NULL COMMENT '所属公司代码';