alter table ceea_pr_shop_cart
    add EXT_REFERENCE_PRICE decimal(30, 8) null comment '参考价';

alter table ceea_pr_shop_cart
    add EXT_CATALOG_ON_SHELVES_ID bigint null comment '采购目录上下架头表ID';
