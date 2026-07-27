alter table ceea_pr_shop_cart
    add EXT_SHELVES_ATTACH_ID bigint null comment '主图';

alter table ceea_pr_shop_cart
    add EXT_ORDER_QUANTITY_MINIMUM decimal(30, 8) null comment '最小起订量';

alter table ceea_pr_shop_cart
    add EXT_SECOND_CATEGORY_ID bigint null comment '二级品类ID';

alter table ceea_pr_shop_cart
    add EXT_SECOND_CATEGORY_CODE varchar(250) null comment '二级品类编码';

alter table ceea_pr_shop_cart
    add EXT_SECOND_CATEGORY_NAME varchar(250) null comment '二级品类名称';

alter table ceea_pr_shop_cart
    add EXT_CEEA_DEPTID varchar(30) null comment '使用部门编码';

alter table ceea_pr_shop_cart
    add EXT_DEPARTMENT varchar(200) null comment '使用部门名称';

alter table ceea_pr_shop_cart
    add EXT_ADDRESS varchar(1000) null comment '收货地址';

alter table ceea_pr_shop_cart
    add EXT_AREA_ID bigint null comment '区域ID';

alter table ceea_pr_shop_cart
    add EXT_AREA_CODE varchar(250) null comment '区域编码';

alter table ceea_pr_shop_cart
    add EXT_AREA_NAME varchar(250) null comment '区域名称';

alter table ceea_pr_shop_cart
    add EXT_IS_GOODS varchar(2) null comment '是否商品(Y是，N否)';

