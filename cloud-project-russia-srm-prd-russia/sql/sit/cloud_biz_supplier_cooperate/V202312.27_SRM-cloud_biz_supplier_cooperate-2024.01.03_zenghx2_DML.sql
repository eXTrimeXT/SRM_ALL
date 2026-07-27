alter table ceea_sc_online_invoice drop column extBehalfInvoice;
alter table ceea_sc_online_invoice drop column extInputTax;
alter table ceea_sc_online_invoice drop column extFreeOfCharge;
alter table ceea_sc_online_invoice_detail drop column `extAdjustAmount`;
alter table ceea_sc_online_invoice_detail drop column `extInputTaxAmount`;
alter table ceea_sc_online_invoice_detail drop column `extSapCostCode`;
alter table ceea_sc_online_invoice_detail drop column `extSapCostContent`;

alter table ceea_sc_online_invoice add column EXT_BEHALF_INVOICE VARCHAR(10) comment '是否代开发票,Y/N';
alter table ceea_sc_online_invoice add column EXT_INPUT_TAX VARCHAR(10) comment '是否进项税转出,Y/N';
alter table ceea_sc_online_invoice add column EXT_FREE_OF_CHARGE VARCHAR(10) comment '是否免赠,Y/N';

alter table ceea_sc_online_invoice_detail add column `EXT_ADJUST_AMOUNT` decimal(30, 8) COMMENT '尾差调整金额';
alter table ceea_sc_online_invoice_detail add column `EXT_INPUT_TAX_AMOUNT` decimal(30, 8) COMMENT '转出税额';
alter table ceea_sc_online_invoice_detail add column `EXT_SAP_COST_CODE` varchar(50) COMMENT 'sap成本中心编码';
alter table ceea_sc_online_invoice_detail add column `EXT_SAP_COST_CONTENT` varchar(100) COMMENT 'sap成本中心编码';
alter table ceea_sc_online_invoice_detail add column `EXT_INVOICE_USAGE` varchar(30) COMMENT '发票用途编码';