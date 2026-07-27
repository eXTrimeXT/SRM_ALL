alter table scc_catalog_on_shelves
    add MIX_AMOUNT decimal(24, 8) null comment '最小起订金额';

alter table ceea_pr_shop_cart
    modify EXT_CEEA_DEPTID bigint null comment '使用部门ID';

alter table ceea_pr_shop_cart
    add EXT_DEPARTMENTCODE varchar(250) null comment '使用部门编码' after EXT_CEEA_DEPTID;

alter table ceea_pr_shop_cart
    add EXT_ADDRESS_ID bigint null  comment '收货地址id' after EXT_DEPARTMENT;

alter table ceea_pr_shop_cart
    add EXT_ADDRESS_NAME varchar(1000) null  comment '收货地址名称' after EXT_ADDRESS_ID;

alter table ceea_pr_shop_cart
    modify EXT_ADDRESS varchar(1000) null comment '收货地址编码';

alter table ceea_pr_shop_cart
    add EXT_RECEIVER varchar(50) null comment '收货人' after EXT_ADDRESS;

