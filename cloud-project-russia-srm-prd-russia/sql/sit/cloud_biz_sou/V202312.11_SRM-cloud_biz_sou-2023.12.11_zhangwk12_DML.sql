alter table scc_npm_sou_fix_price_line add HAS_CANCEL char default 'N' not null comment '是否取消';
alter table scc_npm_sou_fix_price_line add CANCEL_REASON text null comment '取消原因';