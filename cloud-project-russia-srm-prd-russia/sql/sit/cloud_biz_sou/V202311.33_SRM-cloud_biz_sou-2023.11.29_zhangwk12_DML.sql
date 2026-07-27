create table scc_npm_sou_inq_item_round (
    INQ_SOU_ITEM_ROUND_ID           bigint(20) not null             primary key,
    PROJECT_ID                      bigint(20) not null             comment '询价单ID',
    SOU_ITEM_ID                     bigint(20) not null             comment '物料需求ID',
    ROUND                           int        not null             comment '轮次',
    CAN_ORDER                       char(1)    not null default 'Y' comment '物料在指定轮次是否可报价',

    CREATED_ID                      bigint                 not null comment '创建人ID',
    CREATED_BY                      varchar(50)            not null comment '创建人',
    CREATION_DATE                   datetime               not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)           not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)           null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint                 null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)            null comment '更新人',
    LAST_UPDATE_DATE                datetime               null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)           null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)           null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)            null comment '租户ID',
    VERSION                         bigint     default 0   null comment '版本号'
) comment '长城询比价物料轮次信息';