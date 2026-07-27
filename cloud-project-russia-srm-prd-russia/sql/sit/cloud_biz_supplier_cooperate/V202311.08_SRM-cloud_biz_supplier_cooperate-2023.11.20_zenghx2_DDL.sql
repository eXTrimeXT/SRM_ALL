alter table scc_sc_order_detail
    add column `EXT_DELIVERY_CYCLE` INT DEFAULT NULL COMMENT '交货周期';


alter table scc_sc_delivery_note add column `EXT_UN_TAX_AMOUNT` decimal(30,8) DEFAULT NULL COMMENT '未税总金额';
alter table scc_sc_delivery_note add column `EXT_IN_TAX_AMOUNT` decimal(30,8) DEFAULT NULL COMMENT '含税总金额';
alter table scc_sc_delivery_note add column `EXT_CURRENCY_ID` bigint DEFAULT NULL COMMENT '币种id';
alter table scc_sc_delivery_note add column `EXT_CURRENCY_CODE` varchar(30) DEFAULT NULL COMMENT '币种编码';
alter table scc_sc_delivery_note add column `EXT_CURRENCY_NAME` varchar(30) DEFAULT NULL COMMENT '币种名称';


alter table scc_sc_delivery_note change `EXT_EXPRESSMAN` `EXT_EXPRESS_MAN` varchar(30) DEFAULT NULL COMMENT '物流配送人员';
alter table scc_sc_delivery_note change `EXT_EXPRESSPHONE` `EXT_EXPRESS_PHONE` varchar(30) DEFAULT NULL COMMENT '物流联系方式';