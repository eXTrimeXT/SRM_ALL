create table scc_npm_pr_require_bid_result (
    BID_RESULT_ID                   bigint              not null comment 'ID' primary key,
    REQUIREMENT_HEAD_ID             bigint              not null comment '招标计划ID',
    VENDOR_ID                       bigint              null comment '供应商ID',
    VENDOR_CODE                     varchar(50)         null comment '供应商编码',
    VENDOR_NAME                     varchar(150)        null comment '供应商名称',
    LINKMAN                         varchar(50)         null comment '供应商联系人',
    PHONE                           varchar(30)         null comment '联系电话',
    BID_AMOUNT_BY_TEN_KILO          decimal(24,8)       null comment '定标金额(万元)',

    CREATED_ID                      bigint              not null comment '创建人ID',
    CREATED_BY                      varchar(50)         not null comment '创建人',
    CREATION_DATE                   datetime            not null comment '创建时间',
    CREATED_BY_IP                   varchar(150)        not null comment '创建人IP',
    CREATED_FULL_NAME               varchar(100)        null comment '创建人姓名',
    LAST_UPDATED_ID                 bigint              null comment '最后更新人ID',
    LAST_UPDATED_BY                 varchar(50)         null comment '更新人',
    LAST_UPDATE_DATE                datetime            null comment '最后更新时间',
    LAST_UPDATED_BY_IP              varchar(150)        null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME          varchar(100)        null comment '最后更新人姓名',
    TENANT_ID                       varchar(30)         null comment '租户ID',
    VERSION                         bigint default 0    null comment '版本号'
) comment '招标计划定标信息表';