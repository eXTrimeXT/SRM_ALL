alter table scc_npm_sou_inq_order drop column EXT_PRICE_START_TIME;
alter table scc_npm_sou_inq_order drop column EXT_PRICE_END_TIME;
alter table scc_npm_sou_inq_order add PRICE_ACTIVE_DAY decimal(6,2) null after PROJECT_ID;
