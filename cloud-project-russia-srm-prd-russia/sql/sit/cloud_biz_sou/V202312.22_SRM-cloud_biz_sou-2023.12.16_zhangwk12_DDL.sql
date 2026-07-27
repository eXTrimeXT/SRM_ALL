ALTER TABLE scc_npm_sou_purinq_project ADD COLUMN DESIGN_CREATE_PHONE varchar(50) NULL COMMENT '项目策划创建人联系方式';
ALTER TABLE scc_npm_sou_purinq_project ADD COLUMN DESIGN_ORG_DEPT_ID bigint(20) NULL COMMENT '项目策划部门ID';
ALTER TABLE scc_npm_sou_purinq_project ADD COLUMN DESIGN_ORG_DEPT_CODE varchar(50) NULL COMMENT '项目策划部门编码';
ALTER TABLE scc_npm_sou_purinq_project ADD COLUMN DESIGN_ORG_DEPT_NAME varchar(50) NULL COMMENT '项目策划部门名称';
ALTER TABLE scc_npm_sou_purinq_project ADD COLUMN DESIGN_PROJ_MONEY decimal(24,8) NULL COMMENT '项目策划项目金额(万元)';
ALTER TABLE scc_npm_sou_purinq_project ADD COLUMN DESIGN_AREA varchar(30) NULL COMMENT '项目策划供货区域';