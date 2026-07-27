-- 认证信息
alter table scc_sup_company_info
    add SUNSHINE_FILE_ID      bigint(20)     null comment '阳光协议文件ID',
    add SUNSHINE_FILE_NAME    varchar(200)   null comment '阳光协议文件名',
    add GROUP_COUNTRY         VARCHAR(50)    null comment '集团所属国家',
    add TOTAL_ASSETS          decimal(20, 2) null comment '总资产(万元)',
    add CURRENT_ASSETS        decimal(20, 2) null comment '流动资产(万元)',
    add FIXED_ASSETS          decimal(20, 2) null comment '固定资产(万元)',
    add AVG_ANNUAL_OUTPUT     decimal(20, 2) null comment '近三年年均产值(万元)',
    add AVG_ANNUAL_PROFIT     decimal(20, 2) null comment '近三年年均净利润(万元)',
    add FOCUS_FLAG            VARCHAR(10)    null comment '是否重点关注',
    add POSITION_LIMIT_FLAG   VARCHAR(10)    null comment '是否单位受限',
    add CATEGORY_LIMIT_FLAG   VARCHAR(10)    null comment '是否品类受限',
    add TIME_LIMIT_FLAG       VARCHAR(10)    null comment '是否时间受限',
    add LIMIT_DATE            date           null comment '限制日期',
    add CONTRACT_VERIFICATION VARCHAR(10)    null comment '锲约验证',
    add BIDDING_FLAG          VARCHAR(10)    null comment '是否竞价',
    add KEY_SUPERVISION_FLAG  VARCHAR(10)    null comment '是否重点监督',
    add ACCOUNT_GROUP         VARCHAR(10)    null comment '账户组',
    add PARTNER               VARCHAR(10)    null comment '贸易伙伴';

-- 认证信息
alter table scc_sup_company_info_change
    add SUNSHINE_FILE_ID      bigint(20)     null comment '阳光协议文件ID',
    add SUNSHINE_FILE_NAME    varchar(200)   null comment '阳光协议文件名',
    add GROUP_COUNTRY         VARCHAR(50)    null comment '集团所属国家',
    add TOTAL_ASSETS          decimal(20, 2) null comment '总资产(万元)',
    add CURRENT_ASSETS        decimal(20, 2) null comment '流动资产(万元)',
    add FIXED_ASSETS          decimal(20, 2) null comment '固定资产(万元)',
    add AVG_ANNUAL_OUTPUT     decimal(20, 2) null comment '近三年年均产值(万元)',
    add AVG_ANNUAL_PROFIT     decimal(20, 2) null comment '近三年年均净利润(万元)',
    add FOCUS_FLAG            VARCHAR(10)    null comment '是否重点关注',
    add POSITION_LIMIT_FLAG   VARCHAR(10)    null comment '是否单位受限',
    add CATEGORY_LIMIT_FLAG   VARCHAR(10)    null comment '是否品类受限',
    add TIME_LIMIT_FLAG       VARCHAR(10)    null comment '是否时间受限',
    add LIMIT_DATE            date           null comment '限制日期',
    add CONTRACT_VERIFICATION VARCHAR(10)    null comment '锲约验证',
    add BIDDING_FLAG          VARCHAR(10)    null comment '是否竞价',
    add KEY_SUPERVISION_FLAG  VARCHAR(10)    null comment '是否重点监督',
    add ACCOUNT_GROUP         VARCHAR(10)    null comment '账户组',
    add PARTNER               VARCHAR(10)    null comment '贸易伙伴';

-- 联系人
alter table scc_sup_contact_info
    add SOCIAL_SECURITY_CERTIFICATE_FILE_ID DECIMAL(20) null comment '社保证明文件id';

alter table scc_sup_contact_info
    add SOCIAL_SECURITY_CERTIFICATE_FILE_NAME VARCHAR(100) null comment '社保证明文件名称';

alter table scc_sup_contact_info_change
    add SOCIAL_SECURITY_CERTIFICATE_FILE_ID DECIMAL(20) null comment '社保证明文件id';

alter table scc_sup_contact_info_change
    add SOCIAL_SECURITY_CERTIFICATE_FILE_NAME VARCHAR(100) null comment '社保证明文件名称';

alter table ceea_sup_auth_cate_journal
    add CONTACT_ID   bigint(20)   null comment '联系人ID',
    add CONTACT_NAME varchar(200) null comment '联系人名称';

-- 公司规模
create table scc_npm_company_size
(
    COMPANY_SIZE_ID           bigint(20)           not null comment 'ID'
        primary key,
    COMPANY_ID                bigint(20)           null comment '供应商ID',
    TOTAL_NUMBER              varchar(50)          null comment '总人数',
    SOCIAL_SECURITY_NUMBER    varchar(50)          null comment '社保人数',
    MANAGEMENT_NUMBER         varchar(50)          null comment '管理人员',
    DEVELOPER_NUMBER          varchar(50)          null comment '研发人员',
    PRODUCTION_NUMBER         varchar(50)          null comment '生产人员',
    OVER_UNDERGRADUATE_NUMBER varchar(50)          null comment '本科学历及以上',
    CREATED_ID                bigint(20)           not null comment '创建人ID',
    CREATED_BY                varchar(50)          not null comment '创建人',
    CREATION_DATE             datetime             not null comment '创建时间',
    CREATED_BY_IP             varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME         varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID           bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY           varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE          datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP        varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME    varchar(100)         null comment '最后更新人姓名',
    TENANT_ID                 varchar(30)          null comment '租户ID',
    VERSION                   bigint(20) default 0 null comment '版本号'
)
    comment '公司规模';

create table scc_npm_company_size_change
(
    COMPANY_SIZE_CAHNGE_ID    bigint(20)           not null comment 'ID'
        primary key,
    CHANGE_ID                 bigint(20)           null comment '变更ID',
    COMPANY_SIZE_ID           bigint(20)           null comment '公司规模ID',
    COMPANY_ID                bigint(20)           null comment '供应商ID',
    TOTAL_NUMBER              varchar(50)          null comment '总人数',
    SOCIAL_SECURITY_NUMBER    varchar(50)          null comment '社保人数',
    MANAGEMENT_NUMBER         varchar(50)          null comment '管理人员',
    DEVELOPER_NUMBER          varchar(50)          null comment '研发人员',
    PRODUCTION_NUMBER         varchar(50)          null comment '生产人员',
    OVER_UNDERGRADUATE_NUMBER varchar(50)          null comment '本科学历及以上',
    CREATED_ID                bigint(20)           not null comment '创建人ID',
    CREATED_BY                varchar(50)          not null comment '创建人',
    CREATION_DATE             datetime             not null comment '创建时间',
    CREATED_BY_IP             varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME         varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID           bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY           varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE          datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP        varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME    varchar(100)         null comment '最后更新人姓名',
    TENANT_ID                 varchar(30)          null comment '租户ID',
    VERSION                   bigint(20) default 0 null comment '版本号'
)
    comment '公司规模-变更表';


-- auto-generated definition
create table scc_npm_auth_cate_journal_change
(
    CATEGORY_JOURNAL_CHANGE_ID bigint(20)           not null comment 'ID'
        primary key,
    CATEGORY_JOURNAL_ID        bigint(20)           null comment '品类日志表ID',
    FORM_TYPE                  varchar(50)          null comment '品类日志单据类型',
    FORM_ID                    bigint(20)           null comment '品类日志单据ID',
    VENDOR_ID                  bigint(20)           null comment '供应商ID',
    CATEGORY_NAME              varchar(50)          null comment '品类名称',
    CATEGORY_CODE              varchar(50)          null comment '品类编码',
    CATEGORY_ID                bigint(20)           null comment '品类ID',
    CATEGORY_SERVICE_STATUS    varchar(50)          null comment '品类服务状态',
    CATEGORY_FULL_ID           varchar(800)         null comment '品类全路径ID',
    CATEGORY_FULL_NAME         varchar(800)         null comment '品类全路径名称',
    THIS_YEAR_AMOUNT           decimal(30, 8)       null comment '品类本年度采购金额',
    START_DATE                 date                 null comment '生效时间',
    END_DATE                   date                 null comment '失效时间',
    CONTACT_ID                 bigint(20)           null comment '联系人ID',
    CONTACT_NAME               varchar(200)         null comment '联系人名称',
    CREATED_ID                 bigint(20)           not null comment '创建人ID',
    CREATED_BY                 varchar(50)          not null comment '创建人',
    CREATION_DATE              datetime             not null comment '创建时间',
    CREATED_BY_IP              varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME          varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID            bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY            varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE           datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP         varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME     varchar(100)         null comment '最后更新人姓名',
    TENANT_ID                  varchar(30)          null comment '租户ID',
    VERSION                    bigint(20) default 0 null comment '版本号',
    SUPPLIER_COUNT_LIMIT_FLAG  varchar(5)           null comment '是否强控品类供应商上限（Y：是，N：否，默认N）',
    SUPPLIER_COUNT_LIMIT       int(5)               null comment '品类供应商上限',
    EXIST_COUNT_OF_COMPANY     int(5)               null comment '存在的绿牌供应商数量',
    REQ_HEAD_ID                bigint(20)           null comment '寻源单ID'
)
    comment '品类日志表-变更表';

-- auto-generated definition
create table scc_npm_finance_report
(
    FINANCE_REPORT_ID      bigint(20)           not null comment 'ID'
        primary key,
    FILE_ID                bigint(20)           null comment '文件id',
    FILE_NAME              varchar(250)         null comment '文件名',
    COMPANY_ID             bigint(20)           null comment '供应商ID',
    YEAR                   varchar(50)          null comment '年份',
    REMARK                 varchar(300)         null comment '备注',
    CREATED_ID             bigint(20)           not null comment '创建人ID',
    CREATED_BY             varchar(50)          not null comment '创建人',
    CREATION_DATE          datetime             not null comment '创建时间',
    CREATED_BY_IP          varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME      varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID        bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY        varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE       datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP     varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME varchar(100)         null comment '最后更新人姓名',
    TENANT_ID              varchar(30)          null comment '租户ID',
    VERSION                bigint(20) default 0 null comment '版本号'
)
    comment '三大报表附件';

-- auto-generated definition
create table scc_npm_finance_report_change
(
    FINANCE_REPORT_CHANGE_ID bigint(20)           not null comment 'ID'
        primary key,
    CHANGE_ID                bigint(20)           null comment '变更ID',
    FINANCE_REPORT_ID        bigint(20)           null comment 'reportId',
    FILE_ID                  bigint(20)           null comment '文件id',
    FILE_NAME                varchar(250)         null comment '文件名',
    COMPANY_ID               bigint(20)           null comment '供应商ID',
    YEAR                     varchar(50)          null comment '年份',
    REMARK                   varchar(300)         null comment '备注',
    CREATED_ID               bigint(20)           not null comment '创建人ID',
    CREATED_BY               varchar(50)          not null comment '创建人',
    CREATION_DATE            datetime             not null comment '创建时间',
    CREATED_BY_IP            varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME        varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID          bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE         datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)         null comment '最后更新人姓名',
    TENANT_ID                varchar(30)          null comment '租户ID',
    VERSION                  bigint(20) default 0 null comment '版本号'
)
    comment '三大报表附件-变更表';

-- auto-generated definition
create table scc_npm_sercice_custom
(
    SERCICE_CUSTOM_ID      bigint(20)           not null comment 'ID'
        primary key,
    CATEGORY_JOURNAL_ID    bigint(20)           null comment '品类日志id',
    FILE_ID                bigint(20)           null comment '文件id',
    FILE_NAME              varchar(250)         null comment '文件名',
    COMPANY_ID             bigint(20)           null comment '供应商ID',
    MAIN_CUSTOM            varchar(250)         null comment '主要客户',
    PERFORMANCE_AMOUNT     decimal(20, 2)       null comment '业绩额(万元)',
    CREATED_ID             bigint(20)           not null comment '创建人ID',
    CREATED_BY             varchar(50)          not null comment '创建人',
    CREATION_DATE          datetime             not null comment '创建时间',
    CREATED_BY_IP          varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME      varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID        bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY        varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE       datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP     varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME varchar(100)         null comment '最后更新人姓名',
    TENANT_ID              varchar(30)          null comment '租户ID',
    VERSION                bigint(20) default 0 null comment '版本号'
)
    comment '供应商-服务范围明细';

-- auto-generated definition
create table scc_npm_sercice_custom_change
(
    SERCICE_CUSTOM_CHANGE_ID bigint(20)           not null comment 'ID'
        primary key,
    CHANGE_ID                bigint(20)           null comment '变更ID',
    SERCICE_CUSTOM_ID        bigint(20)           null comment '服务范围明细id',
    CATEGORY_JOURNAL_ID      bigint(20)           null comment '品类日志id',
    FILE_ID                  bigint(20)           null comment '文件id',
    FILE_NAME                varchar(250)         null comment '文件名',
    COMPANY_ID               bigint(20)           null comment '供应商ID',
    MAIN_CUSTOM              varchar(250)         null comment '主要客户',
    PERFORMANCE_AMOUNT       decimal(20, 2)       null comment '业绩额(万元)',
    CREATED_ID               bigint(20)           not null comment '创建人ID',
    CREATED_BY               varchar(50)          not null comment '创建人',
    CREATION_DATE            datetime             not null comment '创建时间',
    CREATED_BY_IP            varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME        varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID          bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY          varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE         datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP       varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME   varchar(100)         null comment '最后更新人姓名',
    TENANT_ID                varchar(30)          null comment '租户ID',
    VERSION                  bigint(20) default 0 null comment '版本号'
)
    comment '供应商-服务范围明细-变更表';

-- 认证信息
alter table ceea_sup_management_attach
    add CATEGORY_ID   bigint(20)   null comment '品类ID',
    add CATEGORY_CODE varchar(50)  null comment '品类ID',
    add CATEGORY_NAME varchar(100) null comment '品类名称';

-- 认证信息变更
alter table scc_sup_management_attach_change
    add CATEGORY_ID   bigint(20)   null comment '品类ID',
    add CATEGORY_CODE varchar(50)  null comment '品类ID',
    add CATEGORY_NAME varchar(100) null comment '品类名称';

-- auto-generated definition
create table scc_npm_company_exception_info
(
    EXCEPTION_INFO_ID      bigint(20)           not null comment 'ID'
        primary key,
    COMPANY_ID             bigint(20)           null comment '供应商ID',
    EXCEPTION_TYPE         varchar(100)         null comment '异常类型',
    EXCEPTION_INFO         varchar(250)         null comment '组织/品类异常信息',
    EXCEPTION_REMARK       varchar(250)         null comment '异常备注',
    CREATED_ID             bigint(20)           not null comment '创建人ID',
    CREATED_BY             varchar(50)          not null comment '创建人',
    CREATION_DATE          datetime             not null comment '创建时间',
    CREATED_BY_IP          varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME      varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID        bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY        varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE       datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP     varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME varchar(100)         null comment '最后更新人姓名',
    TENANT_ID              varchar(30)          null comment '租户ID',
    VERSION                bigint(20) default 0 null comment '版本号'
)
    comment '供应商-异常信息';

alter table scc_base_black_company
    add SHAREHOLDER VARCHAR(250) null comment '股东',
	add DATA_SOURCE VARCHAR(50) null comment '数据来源',
	add REASON VARCHAR(250) null comment '原因';





