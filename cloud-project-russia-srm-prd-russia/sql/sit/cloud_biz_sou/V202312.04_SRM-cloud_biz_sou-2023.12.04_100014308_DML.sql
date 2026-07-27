ALTER TABLE scc_sou_jc_agreement_info add GOODS_TYPE_ID bigint(20) null comment '商品分类id' after GOODS_TYPE;
ALTER TABLE scc_sou_jc_agreement_info add GOODS_TYPE_CODE varchar(50) null comment '商品分类编码' after GOODS_TYPE_ID;
ALTER TABLE scc_sou_jc_agreement_info add GOODS_TYPE_NAME varchar(50) null comment '商品分类名称' after GOODS_TYPE_CODE;