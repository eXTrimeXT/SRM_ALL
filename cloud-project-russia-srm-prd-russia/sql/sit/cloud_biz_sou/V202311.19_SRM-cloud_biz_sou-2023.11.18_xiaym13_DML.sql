CREATE TABLE scc_npm_pre_bid_back_attach
(
    BACK_ATTACH_ID      bigint           NOT NULL COMMENT 'ID'
        PRIMARY KEY,
    BID_FEEDBACK_ID            bigint           NOT NULL COMMENT '标前反馈单ID',
    FILE_TYPE              varchar(30)      NULL COMMENT '文件类型',
    FILE_ID                bigint           NULL COMMENT '文件ID',
    FILE_NAME              varchar(255)     NULL COMMENT '文件名称',
    REMARK                 varchar(1000)    NULL COMMENT '附件备注',
    SORT_INDEX             int              NOT NULL COMMENT '排序',
    VENDOR_ID                bigint           NOT NULL COMMENT '供应商id',
    VENDOR_CODE              varchar(30)      NOT NULL COMMENT '供应商编码/企业标识',
    VENDOR_NAME              varchar(240)     NOT NULL COMMENT '供应商名称',
    CREATED_ID             bigint           NOT NULL COMMENT '创建人ID',
    CREATED_BY             varchar(50)      NOT NULL COMMENT '创建人',
    CREATION_DATE          datetime         NOT NULL COMMENT '创建时间',
    CREATED_BY_IP          varchar(150)     NOT NULL COMMENT '创建人IP',
    CREATED_FULL_NAME      varchar(100)     NULL COMMENT '创建人姓名',
    LAST_UPDATED_ID        bigint           NULL COMMENT '最后更新人ID',
    LAST_UPDATED_BY        varchar(50)      NULL COMMENT '更新人',
    LAST_UPDATE_DATE       datetime         NULL COMMENT '最后更新时间',
    LAST_UPDATED_BY_IP     varchar(150)     NULL COMMENT '最后更新人IP',
    LAST_UPDATED_FULL_NAME varchar(100)     NULL COMMENT '最后更新人姓名',
    TENANT_ID              varchar(30)      NULL COMMENT '租户ID',
    VERSION                bigint DEFAULT 0 NULL COMMENT '版本号'
)
    COMMENT '标前反馈单问题清单反馈附件表';
