ALTER TABLE scc_sou_jc_agreement_info MODIFY MATERIAL_LINE INT(20);
ALTER TABLE scc_sou_jc_agreement_info MODIFY LEAD_TIME INT(20);
ALTER TABLE scc_sou_jc_agreement_info MODIFY SELL_BY_DATE INT(20);
ALTER TABLE scc_sou_jc_agreement_info MODIFY IS_TIERED_PRICING INT(2);

ALTER TABLE scc_sou_tiered_pricing MODIFY MORE_NUM INT(20);
ALTER TABLE scc_sou_tiered_pricing MODIFY LESS_NUM INT(20);
ALTER TABLE scc_sou_tiered_pricing MODIFY PRICE_TAX DECIMAL(20,2);
ALTER TABLE scc_sou_tiered_pricing MODIFY RATE_PRICE DECIMAL(20,2);
ALTER TABLE scc_sou_tiered_pricing MODIFY REFER_PRICE DECIMAL(20,2);

