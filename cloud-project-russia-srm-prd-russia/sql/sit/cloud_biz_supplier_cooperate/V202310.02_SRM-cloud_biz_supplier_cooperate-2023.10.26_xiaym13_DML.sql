alter table scc_catalog_on_shelves
    add EXT_PRICE_LIBRARY_STATUS varchar(50) null comment '协议状态 ';

alter table scc_catalog_on_shelves
    add EXT_GOODS_CODE varchar(100) null comment '商品编号';

alter table scc_catalog_on_shelves
    add EXT_GOODS_NAME varchar(250) null comment '商品名称';

alter table scc_catalog_on_shelves
    add EXT_REFERENCE_PRICE decimal(30, 8) null comment '参考价';

alter table scc_catalog_on_shelves
    add EXT_SHELF_LIFE decimal(24, 8) null comment '质保期';

alter table scc_catalog_on_shelves
    add EXT_AREA_ID bigint null comment '区域ID';

alter table scc_catalog_on_shelves
    add EXT_AREA_CODE varchar(100) null comment '区域编号';

alter table scc_catalog_on_shelves
    add EXT_AREA_NAME varchar(100) null comment '区域名称';

alter table scc_catalog_on_shelves
    add EXT_ORG_ID_LIST varchar(1000) null comment '业务实体ID集合';

alter table scc_catalog_on_shelves
    add EXT_ORG_CODE_LIST varchar(2000) null comment '业务实体编码集合';

alter table scc_catalog_on_shelves
    add EXT_ORG_NAME_LIST varchar(2000) null comment '业务实体名称集合';
