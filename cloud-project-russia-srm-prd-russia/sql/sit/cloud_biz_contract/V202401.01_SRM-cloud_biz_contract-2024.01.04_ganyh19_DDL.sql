alter table scc_cont_per_plan add column EXT_HR_SECTOR_CODE VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人所属板块代码',
                                 add column EXT_HR_SECTOR_ID BIGINT NULL DEFAULT NULL COMMENT '创建人所属板块ID',
                                 add column EXT_HR_SECTOR_NAME varchar(50) NULL DEFAULT NULL COMMENT '创建人所属板块名称',
                                 add column EXT_HR_DEPT_ID BIGINT NULL DEFAULT NULL COMMENT '创建人所属部门ID',
                                 add column EXT_HR_DEPT_NAME VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人所属部门名称',
                                 add column EXT_HR_DEPT_CODE VARCHAR(20) NULL DEFAULT NULL COMMENT '创建人所属部门代码',
                                 add column EXT_HR_COMPANY_ID BIGINT NULL DEFAULT NULL COMMENT '创建人所属公司ID',
                                 add column EXT_HR_COMPANY_NAME VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人所属公司名称',
                                 add column EXT_HR_COMPANY_CODE VARCHAR(20) NULL DEFAULT NULL COMMENT '创建人所属公司代码';

alter table scc_cont_per_acceptance add column EXT_HR_SECTOR_CODE VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人所属板块代码',
                              add column EXT_HR_SECTOR_ID BIGINT NULL DEFAULT NULL COMMENT '创建人所属板块ID',
                              add column EXT_HR_SECTOR_NAME varchar(50) NULL DEFAULT NULL COMMENT '创建人所属板块名称',
                              add column EXT_HR_DEPT_ID BIGINT NULL DEFAULT NULL COMMENT '创建人所属部门ID',
                              add column EXT_HR_DEPT_NAME VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人所属部门名称',
                              add column EXT_HR_DEPT_CODE VARCHAR(20) NULL DEFAULT NULL COMMENT '创建人所属部门代码',
                              add column EXT_HR_COMPANY_ID BIGINT NULL DEFAULT NULL COMMENT '创建人所属公司ID',
                              add column EXT_HR_COMPANY_NAME VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人所属公司名称',
                              add column EXT_HR_COMPANY_CODE VARCHAR(20) NULL DEFAULT NULL COMMENT '创建人所属公司代码';