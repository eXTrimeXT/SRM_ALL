-- auto-generated definition
CREATE TABLE scc_npm_pre_bid_notice
(
    BID_NOTICE_ID            bigint                      NOT NULL COMMENT '主键'
        PRIMARY KEY,
    BID_NOTICE_NO            varchar(250)                NULL COMMENT '标前交流通知单号',
    BID_NOTICE_TITLE         varchar(500)                NULL COMMENT '标前交流通知单标题',
    REQUIREMENT_HEAD_ID      bigint                      NULL COMMENT '申请单ID',
    REQUIREMENT_HEAD_NO      varchar(250)                NULL COMMENT '申请单号',
    PROJECT_NAME             varchar(80)                 NULL COMMENT '项目名称',
    ORG_BU_ID                bigint                      NULL COMMENT '板块ID',
    ORG_BU_CODE              varchar(32)                 NULL COMMENT '板块编码',
    ORG_BU_NAME              varchar(250)                NULL COMMENT '板块名称',
    ORG_ID                   bigint                      NULL COMMENT '公司ID(对应产品的业务实体id)',
    ORG_CODE                 varchar(32)                 NULL COMMENT '公司编码(对应产品的业务实体编码)',
    ORG_NAME                 varchar(250)                NULL COMMENT '公司名称(对应产品的业务实体名称)',
    DEMAND_DEPARTMENT_ID     varchar(20)                 NULL COMMENT '需求部门ID',
    DEMAND_DEPARTMENT_CODE   varchar(30)                 NULL COMMENT '需求部门编码',
    DEMAND_DEPARTMENT_NAME   varchar(50)                 NULL COMMENT '需求部门名称',
    STATUS                   varchar(50) DEFAULT 'DRAFT' NOT NULL COMMENT '单据状态',
    DEMAND_USER_ID           varchar(200)                NULL COMMENT '需求人id(跟申请单保持类型一致)',
    DEMAND_USER_NAME         varchar(200)                NULL COMMENT '需求人工号',
    DEMAND_USER_NICKNAME     varchar(200)                NULL COMMENT '需求人名称',
    VENDOR_USER_ID           bigint                      NULL COMMENT '供应商负责人用户ID',
    VENDOR_USER_NICKNAME     varchar(150)                NULL COMMENT '供应商负责人用户名称',
    VENDOR_USER_PHONE        varchar(50)                 NULL COMMENT '供应商负责人联系电话',
    VENDOR_USER_OFFICE_PHONE varchar(50)                 NULL COMMENT '供应商负责人办公电话',
    BID_USER_ID              bigint                      NULL COMMENT '招标负责人用户ID',
    BID_USER_NICKNAME        varchar(150)                NULL COMMENT '招标负责人用户名称',
    REMARK                   varchar(1000)               NULL COMMENT '备注',
    CREATED_ID               bigint                      NOT NULL COMMENT '创建人ID',
    CREATED_BY               varchar(50)                 NOT NULL COMMENT '创建人',
    CREATION_DATE            datetime                    NOT NULL COMMENT '创建时间',
    CREATED_BY_IP            varchar(150)                NOT NULL COMMENT '创建人IP',
    CREATED_FULL_NAME        varchar(100)                NULL COMMENT '创建人姓名',
    LAST_UPDATED_ID          bigint                      NULL COMMENT '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)                 NULL COMMENT '更新人',
    LAST_UPDATE_DATE         datetime                    NULL COMMENT '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)                NULL COMMENT '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)                NULL COMMENT '最后更新人姓名',
    TENANT_ID                varchar(30)                 NULL COMMENT '租户ID',
    VERSION                  bigint      DEFAULT 0       NULL COMMENT '版本号'
)
    COMMENT '标前交流通知表';

-- auto-generated definition
CREATE TABLE scc_npm_pre_bid_notice_vendor
(
    BID_NOTICE_VENDOR_ID   bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    BID_NOTICE_ID          bigint           NOT NULL COMMENT '标前交流通知表主键',
    VENDOR_ID              bigint           NOT NULL COMMENT '供应商id',
    VENDOR_CODE            varchar(30)      NOT NULL COMMENT '供应商编码/企业标识',
    VENDOR_NAME            varchar(240)     NOT NULL COMMENT '供应商名称',
    CONTACT_NAME           varchar(50)      NULL COMMENT '报名联系人',
    VENDOR_ATTRIBUTE       varchar(100)     NOT NULL COMMENT '供应商属性',
    PHONE                  varchar(30)      NULL COMMENT '报名联系手机号',
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
    COMMENT '标前交流通知-技术交流供应商表';

-- auto-generated definition
CREATE TABLE scc_npm_pre_bid_feedback
(
    BID_FEEDBACK_ID          bigint                      NOT NULL COMMENT '主键'
        PRIMARY KEY,
    BID_FEEDBACK_NO          varchar(250)                NULL COMMENT '标前交流反馈单号',
    BID_NOTICE_ID            bigint                      NOT NULL COMMENT '标前交流通知主键',
    BID_NOTICE_NO            varchar(250)                NULL COMMENT '标前交流通知单号',
    PROJECT_NAME             varchar(80)                 NULL COMMENT '项目名称',
    ORG_BU_ID                bigint                      NULL COMMENT '板块ID',
    ORG_BU_CODE              varchar(32)                 NULL COMMENT '板块编码',
    ORG_BU_NAME              varchar(250)                NULL COMMENT '板块名称',
    ORG_ID                   bigint                      NULL COMMENT '公司ID(对应产品的业务实体id)',
    ORG_CODE                 varchar(32)                 NULL COMMENT '公司编码(对应产品的业务实体编码)',
    ORG_NAME                 varchar(250)                NULL COMMENT '公司名称(对应产品的业务实体名称)',
    DEMAND_DEPARTMENT_ID     varchar(20)                 NULL COMMENT '需求部门ID',
    DEMAND_DEPARTMENT_CODE   varchar(30)                 NULL COMMENT '需求部门编码',
    DEMAND_DEPARTMENT_NAME   varchar(50)                 NULL COMMENT '需求部门名称',
    STATUS                   varchar(50) DEFAULT 'DRAFT' NOT NULL COMMENT '单据状态',
    DEMAND_USER_ID           varchar(200)                NULL COMMENT '需求人id(跟申请单保持类型一致)',
    DEMAND_USER_NAME         varchar(200)                NULL COMMENT '需求人工号',
    DEMAND_USER_NICKNAME     varchar(200)                NULL COMMENT '需求人名称',
    VENDOR_USER_ID           bigint                      NULL COMMENT '供应商负责人用户ID',
    VENDOR_USER_NICKNAME     varchar(150)                NULL COMMENT '供应商负责人用户名称',
    VENDOR_USER_PHONE        varchar(50)                 NULL COMMENT '供应商负责人联系电话',
    VENDOR_USER_OFFICE_PHONE varchar(50)                 NULL COMMENT '供应商负责人办公电话',
    REMARK                   varchar(1000)               NULL COMMENT '备注',
    CREATED_ID               bigint                      NOT NULL COMMENT '创建人ID',
    CREATED_BY               varchar(50)                 NOT NULL COMMENT '创建人',
    CREATION_DATE            datetime                    NOT NULL COMMENT '创建时间',
    CREATED_BY_IP            varchar(150)                NOT NULL COMMENT '创建人IP',
    CREATED_FULL_NAME        varchar(100)                NULL COMMENT '创建人姓名',
    LAST_UPDATED_ID          bigint                      NULL COMMENT '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)                 NULL COMMENT '更新人',
    LAST_UPDATE_DATE         datetime                    NULL COMMENT '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)                NULL COMMENT '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)                NULL COMMENT '最后更新人姓名',
    TENANT_ID                varchar(30)                 NULL COMMENT '租户ID',
    VERSION                  bigint      DEFAULT 0       NULL COMMENT '版本号'
)
    COMMENT '标前交流反馈表';

-- auto-generated definition
CREATE TABLE scc_npm_pre_bid_feedback_vendor
(
    BID_FEEDBACK_VENDOR_ID bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    BID_FEEDBACK_ID        bigint           NOT NULL COMMENT '标前交流通知表主键',
    VENDOR_ID              bigint           NOT NULL COMMENT '供应商id',
    VENDOR_CODE            varchar(30)      NOT NULL COMMENT '供应商编码/企业标识',
    VENDOR_NAME            varchar(240)     NOT NULL COMMENT '供应商名称',
    CONTACT_NAME           varchar(50)      NULL COMMENT '报名联系人',
    VENDOR_ATTRIBUTE       varchar(100)     NOT NULL COMMENT '供应商属性',
    PHONE                  varchar(30)      NULL COMMENT '报名联系手机号',
    SOURCE_DESCRIPTION     varchar(500)     NULL COMMENT '来源说明',
    FEEDBACK_STATUS        varchar(50)      NULL COMMENT '反馈状态',
    REJECT_DESCRIPTION     varchar(500)     NULL COMMENT '驳回说明',
    IS_SELECTED            varchar(2)       NULL COMMENT '是否入围（Y是，N否）',
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
    COMMENT '标前交流反馈-技术交流供应商表';

