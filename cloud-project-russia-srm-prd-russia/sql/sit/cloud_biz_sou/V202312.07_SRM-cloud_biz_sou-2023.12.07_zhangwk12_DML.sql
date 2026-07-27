create table scc_npm_sou_purinq_currency (
    SOU_CURRENCY_ID                 bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    CURRENCY_CODE                   varchar(20)             not null comment '币种编码',
    PRICE_PRECISION                 smallint                null comment '供应商报价精度',
    EXCHANGE_RATE_ID                bigint(20)              null comment '汇率ID',
    PRICE_TAX                       decimal(24,8)           null comment '汇率',
    SORT_INDEX                      int                     not null comment '排序',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-可用币种';

create table scc_npm_sou_purinq_item (
    SOU_ITEM_ID                     bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    AREA                            varchar(50)             null comment '区域',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-物料需求';

create table scc_npm_sou_purinq_item_round (
    INQ_SOU_ITEM_ROUND_ID           bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    SOU_ITEM_ID                     bigint(20)              not null comment '物料需求ID',
    ROUND                           int                     not null comment '轮次',
    CAN_ORDER                       char(1) default 'Y'     not null comment '物料在指定轮次是否可报价',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-物料轮次信息';

create table scc_npm_sou_purinq_order (
    ORDER_ID                        bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    ORDER_BY_NICKNAME               varchar(50)             null comment '报价人名称',
    ORDER_PHONE                     varchar(100)            null comment '报价电话',
    ORDER_EMAIL                     varchar(150)            null comment '报价邮箱',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-报价单';

create table scc_sou_purinq_order_item (
    ORDER_ITEM_ID                   bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    SOU_ITEM_ID                     bigint(20)              not null comment '物料需求ID',
    ORDER_ID                        bigint(20)              not null comment '报价单ID',
    INVOICE_TYPE                    varchar(30)             null comment '发票类型(EXT_SOU_PURINQ_ORDER_INVOICE_TYPE)',
    PRICE_TAX_TOTAL                 decimal(24,8)           null comment '价税合计',
    EXT_LEAD_TIME                   int                     null comment '供货周期(自然日)',
    EXT_WARRANTY_PERIOD             int                     null comment '质保期(自然日)',
    EXT_WIN_REASON                  text                    null comment '中标原因',
    HAS_FIX_PRICE                   char(1) default 'N'     not null comment '是否已定价',
    EXT_FIX_PRICE_HEAD_ID           bigint(20)              null comment '定价单ID',
    EXT_FIX_PRICE_NO                varchar(50)             null comment '定价单号',
    EXT_FIX_PRICE_LINE_ID           bigint(20)              null comment '定价单行ID',
    LATEST_PRICE_TAG                char(1)                 not null comment '是否最新报价',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-报价明细';

create table scc_npm_sou_purinq_project (
    PROJECT_ID                      bigint(20)              not null primary key,
    EXT_PROJECT_STATUS              varchar(20)             not null comment '询价状态(EXT_PUR_INQ_SOU_PROJECT_STATUS)',
    ORG_DEPT_ID                     bigint(20)              null comment '采购申请部门ID',
    ORG_DEPT_CODE                   varchar(50)             null comment '采购申请部门编码',
    ORG_DEPT_NAME                   varchar(150)            null comment '采购申请部门名称',
    DESIGN_ID                       bigint(20)              null comment '项目策划方案ID',
    DESIGN_PROJECT_CODE             varchar(50)             null comment '项目策划方案编码',
    DESIGN_PROJECT_NAME             varchar(150)            null comment '项目策划方案名称',
    DESIGN_NUM                      int                     null comment '项目策划轮数',
    DESIGN_CREATE_USERNAME          varchar(50)             null comment '项目策划创建人账号',
    DESIGN_CREATE_NICK_NAME         varchar(50)             null comment '项目策划创建人昵称',
    DESIGN_PROJ_INTRODUCE           text                    null comment '项目策划介绍',
    DESIGN_PRICING_IDEAS            text                    null comment '项目策划定价思路',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-询价单';

create table scc_npm_sou_purinq_vendor (
    SOU_VENDOR_ID                   bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    VENDOR_ID                       bigint(20)              not null comment '供应商ID',
    SOURCE_FROM_TYPE                varchar(20)             not null comment '数据来源',
    NEW_VENDOR_TAG                  char(1) default 'N'     not null comment '是否新供应商',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-邀请供应商';

create table scc_npm_sou_purinq_vendor_del (
    SOU_VENDOR_DEL_ID               bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    VENDOR_ID                       bigint(20)              null comment '供应商ID',
    VENDOR_CODE                     varchar(100)            null comment '供应商编码',
    VENDOR_NAME                     varchar(150)            null comment '供应商名称',
    LINKMAN_NAME                    varchar(100)            null comment '联系人名称',
    PHONE                           varchar(100)            null comment '电话',
    EMAIL                           varchar(150)            null comment '邮箱',
    SOURCE_FROM_TYPE                varchar(20)             null comment '数据来源',
    DEL_REASON                      text                    null comment '删除原因',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-邀请供应商删除记录';

create table scc_npm_sou_purinq_vendorround (
    INQ_SOU_VENDOR_ROUND_ID         bigint(20)              not null primary key,
    PROJECT_ID                      bigint(20)              not null comment '询价单ID',
    ROUND                           int                     not null comment '轮次',
    VENDOR_ID                       bigint(20)              not null comment '供应商ID',
    CAN_ORDER                       char(1) default 'Y'     not null comment '供应商在指定轮次是否可报价',

    CREATED_ID                      bigint                  not null comment '创建人ID',
    CREATED_BY                      varchar(50)             not null comment '创建人',
    CREATION_DATE                   datetime                not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)            not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)            null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                  null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)             null comment '更新人',
    LAST_UPDATE_DATE                datetime                null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)            null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)            null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)             null comment '租户ID',
    VERSION                         bigint     default 0    null comment '版本号'
) comment '长城集采询比价-供应商轮次信息';