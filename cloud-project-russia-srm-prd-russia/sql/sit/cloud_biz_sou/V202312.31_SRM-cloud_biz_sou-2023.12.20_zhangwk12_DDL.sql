ALTER TABLE scc_npm_sou_purinq_item ADD COLUMN DESIGN_VENDOR_ID bigint(20) NULL COMMENT '历史供应商ID';
ALTER TABLE scc_npm_sou_purinq_item ADD COLUMN DESIGN_VENDOR_CODE varchar(50) NULL COMMENT '历史供应商编码';
ALTER TABLE scc_npm_sou_purinq_item ADD COLUMN DESIGN_VENDOR_NAME varchar(255) NULL COMMENT '历史供应商名称';
ALTER TABLE scc_npm_sou_purinq_item ADD COLUMN DESIGN_NOTAX_PRICE decimal(24,8) NULL COMMENT '历史未税价格';
