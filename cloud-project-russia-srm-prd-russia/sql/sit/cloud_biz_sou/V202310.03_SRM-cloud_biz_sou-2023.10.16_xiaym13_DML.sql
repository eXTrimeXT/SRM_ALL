-- auto-generated definition
create table scc_npm_sou_invite_head
(
    INVITE_HEAD_ID         bigint           not null comment '主键'
        primary key,
    VENDOR_ID              bigint           not null comment '供应商id',
    VENDOR_CODE            varchar(30)      not null comment '供应商编码/企业标识',
    VENDOR_NAME            varchar(240)     not null comment '供应商名称',
    IS_INTELLIGENT_BID     varchar(2)       null comment '是否被推荐（Y是，N否）',
    CONTACT_NAME           varchar(50)      null comment '联系人',
    PHONE                  varchar(30)      null comment '电话',
    EMAIL                  varchar(50)      null comment '邮箱',
    MAIN_CUSTOMERS         text             null comment '主要客户',
    BID_COUNT              int    default 0 not null comment '投标次数',
    SUCC_BID_COUNT         int    default 0 not null comment '中标次数',
    INVALID_BID_COUNT      int    default 0 not null comment '废标次数',
    CREATED_ID             bigint           not null comment '创建人ID',
    CREATED_BY             varchar(50)      not null comment '创建人',
    CREATION_DATE          datetime         not null comment '创建时间',
    CREATED_BY_IP          varchar(150)     not null comment '创建人IP',
    CREATED_FULL_NAME      varchar(100)     null comment '创建人姓名',
    LAST_UPDATED_ID        bigint           null comment '最后更新人ID',
    LAST_UPDATED_BY        varchar(50)      null comment '更新人',
    LAST_UPDATE_DATE       datetime         null comment '最后更新时间',
    LAST_UPDATED_BY_IP     varchar(150)     null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME varchar(100)     null comment '最后更新人姓名',
    TENANT_ID              varchar(30)      null comment '租户ID',
    VERSION                bigint default 0 null comment '版本号',
    constraint scc_npm_sou_invite_head_VENDOR_ID_uindex
        unique (VENDOR_ID)
)
    comment '邀请供应商头表';



-- auto-generated definition
CREATE TABLE scc_npm_sou_invite_item
(
    INVITE_ITEM_ID           bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    INVITE_HEAD_ID           bigint           NOT NULL COMMENT '邀请供应商头表ID',
    PROJECT_NO               varchar(80)      NULL COMMENT '项目编号',
    PROJECT_NAME             varchar(80)      NULL COMMENT '项目名称',
    ORG_BU_ID                bigint           NULL COMMENT '板块ID',
    ORG_BU_CODE              varchar(32)      NULL COMMENT '板块编码',
    ORG_BU_NAME              varchar(250)     NULL COMMENT '板块名称',
    ORG_ID                   bigint           NULL COMMENT '公司ID(对应产品的业务实体id)',
    ORG_CODE                 varchar(32)      NULL COMMENT '公司编码(对应产品的业务实体编码)',
    ORG_NAME                 varchar(250)     NULL COMMENT '公司名称(对应产品的业务实体名称)',
    BID_CREATION_DATE        datetime         NOT NULL COMMENT '创建时间',
    CATEGORY_ID              bigint           NULL COMMENT '品类ID',
    CATEGORY_CODE            varchar(100)     NULL COMMENT '品类编码',
    CATEGORY_NAME            varchar(256)     NULL COMMENT '品类',
    IS_BID                   varchar(2)       NULL COMMENT '是否投标（Y是，N否）',
    IS_INVALID_BID           varchar(2)       NULL COMMENT '是否废标（Y是，N否）',
    IS_SUCC_BID              varchar(2)       NULL COMMENT '是否中标（Y是，N否）',
    TECH_SCORE               decimal(20, 4)   NULL COMMENT '技术得分',
    TOTAL_SCORE              decimal(20, 4)   NULL COMMENT '综合得分',
    PERFORMANCE_SCORE        decimal(20, 4)   NULL COMMENT '绩效得分',
    NOT_PARTICIPATING_REASON varchar(1000)    NULL COMMENT '不参与的原因',
    CREATED_ID               bigint           NOT NULL COMMENT '创建人ID',
    CREATED_BY               varchar(50)      NOT NULL COMMENT '创建人',
    CREATION_DATE            datetime         NOT NULL COMMENT '创建时间',
    CREATED_BY_IP            varchar(150)     NOT NULL COMMENT '创建人IP',
    CREATED_FULL_NAME        varchar(100)     NULL COMMENT '创建人姓名',
    LAST_UPDATED_ID          bigint           NULL COMMENT '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)      NULL COMMENT '更新人',
    LAST_UPDATE_DATE         datetime         NULL COMMENT '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)     NULL COMMENT '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)     NULL COMMENT '最后更新人姓名',
    TENANT_ID                varchar(30)      NULL COMMENT '租户ID',
    VERSION                  bigint DEFAULT 0 NULL COMMENT '版本号'
)
    COMMENT '邀请供应商行表';

-- auto-generated definition
CREATE TABLE scc_npm_sou_invite_history
(
    INVITE_HISTORY_ID      bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    INVITE_HEAD_ID         bigint           null comment '邀请供应商头ID',
    PROJECT_NAME           varchar(80)      NOT NULL COMMENT '项目名称',
    SOU_TYPE               varchar(10)      NOT NULL COMMENT '来源单类型(寻源单RFP,申请单PR)',
    SOU_ID                 bigint           NOT NULL COMMENT '来源单主键ID',
    SOU_NO                 varchar(250)     NOT NULL COMMENT '来源单号',
    VENDOR_ID              bigint           NOT NULL COMMENT '供应商id',
    VENDOR_CODE            varchar(30)      NOT NULL COMMENT '供应商编码/企业标识',
    VENDOR_NAME            varchar(240)     NOT NULL COMMENT '供应商名称',
    PHONE                  varchar(30)      NOT NULL COMMENT '电话',
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
    COMMENT '邀请供应商历史表';

