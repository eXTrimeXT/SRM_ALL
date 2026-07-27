alter table ceea_sc_online_invoice add column EXT_SOURCE VARCHAR(30) comment '单据来源';
alter table ceea_sc_online_invoice add column extBehalfInvoice VARCHAR(10) comment '是否代开发票,Y/N';
alter table ceea_sc_online_invoice add column extInputTax VARCHAR(10) comment '是否进项税转出,Y/N';
alter table ceea_sc_online_invoice add column extFreeOfCharge VARCHAR(10) comment '是否免赠,Y/N';

alter table ceea_sc_online_invoice_detail add column EXT_SOURCE VARCHAR(30) comment '单据来源';
alter table ceea_sc_online_invoice_detail add column `extAdjustAmount` decimal(30, 8) COMMENT '尾差调整金额';
alter table ceea_sc_online_invoice_detail add column `extInputTaxAmount` decimal(30, 8) COMMENT '转出税额';
alter table ceea_sc_online_invoice_detail add column `extSapCostCode` varchar(50) COMMENT 'sap成本中心编码';
alter table ceea_sc_online_invoice_detail add column `extSapCostContent` varchar(100) COMMENT 'sap成本中心编码';