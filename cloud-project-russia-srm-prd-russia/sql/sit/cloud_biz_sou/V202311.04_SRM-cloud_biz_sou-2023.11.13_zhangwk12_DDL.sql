alter table scc_sou_inq_order_item add INVOICE_TYPE varchar(20) null comment '发票类型';
alter table scc_sou_inq_order_item add PRICE_TAX_TOTAL decimal(24, 8) null comment '价税合计';
alter table scc_sou_inq_order_item add SPECIAL_PAYMENT_REMARK text null comment '特殊付款说明';
alter table scc_sou_inq_order_item add EXT_LEAD_TIME varchar(30) null comment '供货周期';
alter table scc_sou_inq_order_item add EXT_WARRANTY_PERIOD int null comment '保修期(保质期)';

create table scc_npm_sou_inq_order (
    ORDER_ID                        bigint(20)                      not null primary key,
    PROJECT_ID                      bigint(20)                      not null comment '询价单ID',
    EXT_PRICE_START_TIME            date                            null comment '价格有效期从',
    EXT_PRICE_END_TIME              date                            null comment '价格有效期到',
    EXT_ORDER_BY_NICKNAME           varchar(50)                     null comment '报价人',
    EXT_ORDER_PHONE                 varchar(80)                     null comment '报价联系方式',

    CREATED_ID                      bigint                          not null comment '创建人ID',
    CREATED_BY                      varchar(50)                     not null comment '创建人',
    CREATION_DATE                   datetime                        not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)                    not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)                    null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                          null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)                     null comment '更新人',
    LAST_UPDATE_DATE                datetime                        null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)                    null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)                    null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)                     null comment '租户ID',
    VERSION                         bigint     default 0            null comment '版本号'
) comment '长城询比价报价单拓展';