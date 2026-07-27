-- auto-generated definition
create table scc_npm_sou_req_head
(
    REQ_HEAD_ID              bigint           not null comment '主键'
        primary key,
    REQ_HEAD_NO              varchar(250)     not null comment '寻源单单号',
    ORG_BU_ID                bigint           null comment '板块ID',
    ORG_BU_CODE              varchar(32)      null comment '板块编码',
    ORG_BU_NAME              varchar(250)     null comment '板块名称',
    ORG_ID                   bigint           null comment '公司ID(对应产品的业务实体id)',
    ORG_CODE                 varchar(32)      null comment '公司编码(对应产品的业务实体编码)',
    ORG_NAME                 varchar(250)     null comment '公司名称(对应产品的业务实体名称)',
    PUBCONFIG_ID             bigint           null comment '寻源公示模板ID',
    PUBCONFIG_NAME           varchar(100)     null comment '寻源公示模板名称',
    REQ_DEPARTMENT           varchar(250)     null comment '需求部门',
    REQ_USER_ID              bigint           null comment '需求人ID',
    REQ_USER_NAME            varchar(50)      null comment '需求人名称',
    RESPONSIBILITY_ID        bigint           null comment '供应商负责人ID',
    RESPONSIBILITY_NAME      varchar(50)      null comment '供应商负责人名称',
    SOU_PERSON_ID            bigint           null comment '招标负责人ID',
    SOU_PERSON_NAME          varchar(50)      null comment '招标负责人名称',
    IS_PRE_COMM              varchar(2)       null comment '是否前置交流（Y是，N否）',
    IS_PUBLIC                varchar(2)       null comment '是否公示寻源（Y是，N否）',
    CLOSE_PUBLIC_REASON      varchar(1000)    null comment '关闭公示原因',
    IS_RECOMMEND             varchar(2)       null comment '是否已经供应商推荐(Y是，N否)',
    STATUS                   varchar(50)      not null comment '单据状态',
    PROJECT_NAME             varchar(80)      null comment '项目名称',
    PUBLIC_END_TIME          datetime         null comment '公示截止时间',
    RELEASE_DATE             datetime         null comment '发布时间',
    CATEGORY_ID              bigint           null comment '所属品类ID',
    CATEGORY_CODE            varchar(50)      null comment '所属品类编码',
    CATEGORY_NAME            varchar(255)     null comment '所属品类名称',
    TOTAL_AMOUNT_BY_TEN_KILO decimal(24, 8)   null comment '概算金额(万元)',
    REQUIRE_QUANTITY         int              null comment '规模数量',
    INVITE_QUANTITY          int              null comment '已邀请供应商数量',
    REQUIREMENT_HEAD_ID      bigint           null comment '招标计划申请单ID',
    REQUIREMENT_HEAD_NO      varchar(250)     null comment '招标计划申请单号',
    REQUIREMENT_HEAD_NO_LIST varchar(1000)    null comment '招标计划申请单号',
    REQUIRE_FROM             varchar(30)      null comment '需求来源',
    PROJECT_SCOPE            text             null comment '项目概况与招标范围',
    VENDOR_QUAL_REQ          text             null comment '供应商资质要求',
    TECHNICAL_REQ            text             null comment '技术要求',
    PERFORMANCE_REQ          text             null comment '业绩要求',
    PROJECT_ADDRESS          varchar(255)     null comment '项目所在地',
    CONTACT_NAME             varchar(100)     null comment '联系人',
    PHONE                    varchar(30)      null comment '电话',
    OFFICE_PHONE             varchar(50)      null comment '办公电话',
    IS_NEED_DEPOSIT          varchar(2)       null comment '是否需缴纳意向金（Y是，N否）',
    DEPOSIT_AMOUNT           decimal(24, 8)   null comment '意向金缴纳金额(元)',
    BANK_NAME                varchar(300)     null comment '开户银行',
    BANK_NUMBER              varchar(200)     null comment '开户行号',
    BANK_ACCOUNT             varchar(200)     null comment '开户账号',
    BANK_ACCOUNT_NAME        varchar(100)     null comment '开户户名',
    PROJECT_VIEWS_COUNT      int              null comment '项目已阅数量',
    CREATED_ID               bigint           not null comment '创建人ID',
    CREATED_BY               varchar(50)      not null comment '创建人',
    CREATION_DATE            datetime         not null comment '创建时间',
    CREATED_BY_IP            varchar(150)     not null comment '创建人IP',
    CREATED_FULL_NAME        varchar(100)     null comment '创建人姓名',
    LAST_UPDATED_ID          bigint           null comment '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)      null comment '更新人',
    LAST_UPDATE_DATE         datetime         null comment '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)     null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)     null comment '最后更新人姓名',
    TENANT_ID                varchar(30)      null comment '租户ID',
    VERSION                  bigint default 0 null comment '版本号'
)
    comment '寻源需求单头表';

create index scc_npm_sou_req_head_ORG_BU_ID_index
    on scc_npm_sou_req_head (ORG_BU_ID);

create index scc_npm_sou_req_head_REQ_HEAD_NO_index
    on scc_npm_sou_req_head (REQ_HEAD_NO);



-- auto-generated definition
CREATE TABLE scc_npm_sou_req_apply
(
    APPLY_ID                 bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    REQ_HEAD_ID              bigint           NOT NULL COMMENT '寻源单ID',
    VENDOR_ID                bigint           NOT NULL COMMENT '供应商id',
    VENDOR_CODE              varchar(30)      NOT NULL COMMENT '供应商编码/企业标识',
    VENDOR_NAME              varchar(240)     NOT NULL COMMENT '供应商名称',
    IS_INTERNAL_VENDOR       varchar(2)       NULL COMMENT '是否内部供应商（Y是，N否）',
    IS_RECOMM                varchar(2)       NULL COMMENT '是否被推荐（Y是，N否）',
    APPLY_STATUS             varchar(20)      NULL COMMENT '报名状态',
    DEPOSIT_REFUND_STATUS    varchar(10)      NULL COMMENT '意向金退款状态（值对应字典SOU_DEPOSIT_REFUND_STATUS）',
    APPLY_CONTACT_NAME       varchar(50)      NULL COMMENT '报名联系人',
    APPLY_PHONE              varchar(30)      NULL COMMENT '报名联系手机号',
    APPLY_EMAIL              varchar(50)      NULL COMMENT '报名联系邮箱',
    DEPOSIT_FILE_ID          bigint           NULL COMMENT '意向金缴纳凭证附件ID',
    DEPOSIT_FILE_NAME        varchar(255)     NULL COMMENT '意向金缴纳凭证附件名称',
    DEPOSIT_STATUS           varchar(20)      NULL COMMENT '意向金缴纳状态',
    VENDOR_BANK_ACCOUNT      varchar(200)     NULL COMMENT '供应商缴纳账户',
    VENDOR_BANK_ACCOUNT_NAME varchar(100)     NULL COMMENT '缴纳户名',
    VENDOR_BANK_NAME         varchar(300)     NULL COMMENT '缴纳银行',
    VENDOR_BANK_NUMBER       varchar(200)     NULL COMMENT '银行联行号',
    IS_AGENT                 varchar(2)       NULL COMMENT '是否代理（Y是，N否）',
    AGENT_BRAND              varchar(500)     NULL COMMENT '代理品牌',
    APPLY_FAIL_REASON        text             NULL COMMENT '报名失败原因',
    APPLY_HANDLE_TYPE        varchar(50)      null comment '报名处理方式',
    APPLY_HANDLE_REASON      varchar(1000)    null comment '报名处理原因',
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
    COMMENT '寻源需求单报名表';

CREATE INDEX scc_npm_sou_req_apply_REQ_HEAD_ID_index
    ON scc_npm_sou_req_apply (REQ_HEAD_ID);


-- auto-generated definition
CREATE TABLE scc_npm_sou_info_history
(
    INFO_HISTORY_ID           bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    REQ_HEAD_ID               bigint           NOT NULL COMMENT '寻源单ID',
    BEFORE_PROJECT_NAME       varchar(80)      NULL COMMENT '项目名称(调整前)',
    BEFORE_PROJECT_SCOPE      text             NULL COMMENT '项目概况与招标范围(调整前)',
    BEFORE_VENDOR_QUAL_REQ    text             NULL COMMENT '供应商资质要求(调整前)',
    BEFORE_TECHNICAL_REQ      text             NULL COMMENT '技术要求(调整前)',
    BEFORE_PERFORMANCE_REQ    text             NULL COMMENT '业绩要求(调整前)',
    AFTER_PROJECT_NAME        varchar(80)      NULL COMMENT '项目名称(调整后)',
    AFTER_PROJECT_SCOPE       text             NULL COMMENT '项目概况与招标范围(调整后)',
    AFTER_VENDOR_QUAL_REQ     text             NULL COMMENT '供应商资质要求(调整后)',
    AFTER_TECHNICAL_REQ       text             NULL COMMENT '技术要求(调整后)',
    AFTER_PERFORMANCE_REQ     text             NULL COMMENT '业绩要求(调整后)',
    UPDATE_REASON             text             NOT NULL COMMENT '归档原因',
    ARCHIVE_FILE_ID           bigint           NULL COMMENT '归档附件ID',
    ARCHIVE_FILE_NAME         varchar(255)     NULL COMMENT '归档附件名称',
    CREATED_ID                bigint           NOT NULL COMMENT '创建人ID',
    CREATED_BY                varchar(50)      NOT NULL COMMENT '创建人',
    CREATION_DATE             datetime         NOT NULL COMMENT '创建时间',
    CREATED_BY_IP             varchar(150)     NOT NULL COMMENT '创建人IP',
    CREATED_FULL_NAME         varchar(100)     NULL COMMENT '创建人姓名',
    LAST_UPDATED_ID           bigint           NULL COMMENT '最后更新人ID',
    LAST_UPDATED_BY           varchar(50)      NULL COMMENT '更新人',
    LAST_UPDATE_DATE          datetime         NULL COMMENT '最后更新时间',
    LAST_UPDATED_BY_IP        varchar(150)     NULL COMMENT '最后更新人IP',
    LAST_UPDATED_FULL_NAME    varchar(100)     NULL COMMENT '最后更新人姓名',
    TENANT_ID                 varchar(30)      NULL COMMENT '租户ID',
    VERSION                   bigint DEFAULT 0 NULL COMMENT '版本号'
)
    COMMENT '寻源单公示信息修改历史表';

CREATE INDEX scc_npm_sou_info_history_REQ_HEAD_ID_index
    ON scc_npm_sou_info_history (REQ_HEAD_ID);


-- auto-generated definition
create table scc_npm_sou_intention_deposit_refund
(
    REFUND_ID                bigint           not null comment '主键'
        primary key,
    REQ_HEAD_ID              bigint           not null comment '寻源单ID',
    APPLY_ID                 bigint           not null comment '寻源需求单报名ID',
    VENDOR_ID                bigint           not null comment '供应商id',
    VENDOR_CODE              varchar(30)      not null comment '供应商编码/企业标识',
    VENDOR_NAME              varchar(240)     not null comment '供应商名称',
    REFUND_BANK_ACCOUNT      varchar(200)     null comment '退款账户',
    REFUND_BANK_ACCOUNT_NAME varchar(100)     null comment '退款户名',
    REFUND_BANK_NAME         varchar(300)     null comment '退款银行',
    REFUND_BANK_NUMBER       varchar(200)     null comment '退款银行联行号',
    REFUND_AMOUNT            decimal(24, 8)   null comment '退款金额',
    REFUND_REASON            varchar(1000)    not null comment '退款原因',
    REFUND_REMARK            varchar(1000)    not null comment '退款备注',
    REFUND_FILE_ID           bigint           null comment '退款附件ID',
    REFUND_FILE_NAME         varchar(255)     null comment '退款附件名称',
    REFUND_TIME              datetime         null comment '退款时间',
    CREATED_ID               bigint           not null comment '创建人ID',
    CREATED_BY               varchar(50)      not null comment '创建人',
    CREATION_DATE            datetime         not null comment '创建时间',
    CREATED_BY_IP            varchar(150)     not null comment '创建人IP',
    CREATED_FULL_NAME        varchar(100)     null comment '创建人姓名',
    LAST_UPDATED_ID          bigint           null comment '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)      null comment '更新人',
    LAST_UPDATE_DATE         datetime         null comment '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)     null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)     null comment '最后更新人姓名',
    TENANT_ID                varchar(30)      null comment '租户ID',
    VERSION                  bigint default 0 null comment '版本号'
)
    comment '寻源单意向金退款表';

create index scc_npm_sou_deposit_refund_APPLY_ID_index
    on scc_npm_sou_intention_deposit_refund (APPLY_ID);

create index scc_npm_sou_deposit_refund_REQ_HEAD_ID_index
    on scc_npm_sou_intention_deposit_refund (REQ_HEAD_ID);



-- auto-generated definition
create table scc_npm_sou_intention_deposit_invoice
(
    INVOICE_ID                    bigint           not null comment '主键'
        primary key,
    INVOICE_NO                    varchar(250)     not null comment '意向金开票单号',
    REQ_HEAD_ID                   bigint           not null comment '寻源单ID',
    APPLY_ID                      bigint           not null comment '寻源需求单报名ID',
    FROM_DEPOSIT_INVOICE_ID       bigint           null comment '红字发票对应的发票ID主键',
    STATUS                        varchar(50)      not null comment '单据状态',
    PROJECT_NAME                  varchar(80)      null comment '项目名称',
    INVOICE_TYPE                  varchar(50)      null comment '发票类型(发票,红字发票)',
    INVOICE_COMPANY               varchar(240)     not null comment '开票公司',
    TAX_PAYER                     varchar(200)     null comment '纳税人识别号',
    BANK_NAME                     varchar(300)     null comment '开户银行',
    BANK_ACCOUNT                  varchar(200)     null comment '开户账号',
    ENTRUST_PAY_VOUCHER_FILE_ID   bigint           null comment '委托代付凭证附件ID',
    ENTRUST_PAY_VOUCHER_FILE_NAME varchar(255)     null comment '委托代付凭证附件名称',
    ADDRESS                       varchar(1000)    null comment '地址',
    INVOICE_RECEIVER_EMAIL        varchar(50)      null comment '发票接收邮箱',
    INVOICE_QUANTITY              int    default 1 not null comment '开票数量',
    PRICE                         decimal(24, 8)   null comment '单价(元)',
    VENDOR_ID                     bigint           not null comment '供应商id',
    VENDOR_CODE                   varchar(30)      not null comment '供应商编码/企业标识',
    VENDOR_NAME                   varchar(240)     not null comment '供应商名称',
    RED_INVOICE_REASON            varchar(1000)    not null comment '开具红字发票原因',
    APPLY_INVOICE_FAIL_REASON     varchar(1000)    not null comment '开具发票失败原因',
    CREATED_ID                    bigint           not null comment '创建人ID',
    CREATED_BY                    varchar(50)      not null comment '创建人',
    CREATION_DATE                 datetime         not null comment '创建时间',
    CREATED_BY_IP                 varchar(150)     not null comment '创建人IP',
    CREATED_FULL_NAME             varchar(100)     null comment '创建人姓名',
    LAST_UPDATED_ID               bigint           null comment '最后更新人ID',
    LAST_UPDATED_BY               varchar(50)      null comment '更新人',
    LAST_UPDATE_DATE              datetime         null comment '最后更新时间',
    LAST_UPDATED_BY_IP            varchar(150)     null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME        varchar(100)     null comment '最后更新人姓名',
    TENANT_ID                     varchar(30)      null comment '租户ID',
    VERSION                       bigint default 0 null comment '版本号'
)
    comment '寻源单意向金开票表';

create index scc_npm_sou_deposit_invoice_APPLY_ID_index
    on scc_npm_sou_intention_deposit_invoice (APPLY_ID);

create index scc_npm_sou_deposit_invoice_DEPOSIT_INVOICE_NO_index
    on scc_npm_sou_intention_deposit_invoice (INVOICE_NO);

create index scc_npm_sou_deposit_invoice_REQ_HEAD_ID_index
    on scc_npm_sou_intention_deposit_invoice (REQ_HEAD_ID);



create table scc_npm_sou_invoice_info
(
    INVOICE_INFO_ID        bigint           not null comment '主键'
        primary key,
    STATUS                 varchar(50)      not null comment '单据状态',
    VENDOR_ID              bigint           not null comment '供应商id',
    VENDOR_CODE            varchar(30)      not null comment '供应商编码/企业标识',
    VENDOR_NAME            varchar(240)     not null comment '供应商名称',
    TAX_PAYER              varchar(200)     null comment '纳税人识别号',
    BANK_NAME              varchar(300)     null comment '开户银行',
    BANK_ACCOUNT           varchar(200)     null comment '开户账号',
    PHONE                  varchar(30)      null comment '手机号',
    EMAIL                  varchar(50)      null comment '发票接收邮箱',
    ADDRESS                varchar(1000)    null comment '地址',
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
    VERSION                bigint default 0 null comment '版本号'
)
    comment '开票信息表';
