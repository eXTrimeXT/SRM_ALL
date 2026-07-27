ALTER TABLE scc_sou_ch_design_plan ADD COLUMN ORG_BU_ID bigint(20) NULL COMMENT '所属板块ID';
ALTER TABLE scc_sou_ch_design_plan ADD COLUMN ORG_BU_CODE varchar(50) NULL COMMENT '所属板块编码';
ALTER TABLE scc_sou_ch_design_plan ADD COLUMN ORG_BU_NAME varchar(255) NULL COMMENT '所属板块名称';
ALTER TABLE scc_sou_ch_design_plan ADD COLUMN ORG_ID bigint(20) NULL COMMENT '所属公司id';
ALTER TABLE scc_sou_ch_design_plan ADD COLUMN ORG_CODE varchar(50) NULL COMMENT '所属公司编码';
ALTER TABLE scc_sou_ch_design_plan ADD COLUMN ORG_NAME varchar(255) NULL COMMENT '所属公司名称';