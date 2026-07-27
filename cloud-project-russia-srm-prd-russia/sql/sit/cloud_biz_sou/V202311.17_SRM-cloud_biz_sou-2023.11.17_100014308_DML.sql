ALTER TABLE `scc_sou_jc_agreement` MODIFY COLUMN `MIX_AMOUNT` decimal(10, 2) COMMENT '最小起订金额';
ALTER TABLE `scc_sou_jc_agreement_info` MODIFY COLUMN `PRICE_TAX` decimal(10, 2) COMMENT '未税单价';
ALTER TABLE `scc_sou_jc_agreement_info` MODIFY COLUMN `TAX_RATE` decimal(10, 4) COMMENT '税率';
ALTER TABLE `scc_sou_jc_agreement_info` MODIFY COLUMN `RATE_PRICE` decimal(10, 4) COMMENT '含税单价';
ALTER TABLE `scc_sou_jc_agreement_info` MODIFY COLUMN `REFERENCE_PRICE` decimal(10, 4) COMMENT '参考价';