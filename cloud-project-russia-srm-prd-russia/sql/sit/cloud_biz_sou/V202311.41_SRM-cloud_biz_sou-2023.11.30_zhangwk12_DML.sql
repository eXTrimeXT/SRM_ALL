alter table scc_npm_sou_fix_price_head drop column PAYMENT_METHOD;
alter table scc_npm_sou_fix_price_head drop column PAYMENT_TERM;

alter table scc_npm_sou_fix_price_line add PAYMENT_METHOD varchar(20) null comment '付款方式';
alter table scc_npm_sou_fix_price_line add PAYMENT_TERM varchar(100) null comment '付款条款';