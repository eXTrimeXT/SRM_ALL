-- 项目化绩效项目
create table scc_npm_project_score_items
(
    PROJECT_SCORE_ITEMS_ID     bigint(20)           not null comment 'ID'
        primary key,
    PROJECT_NAME               varchar(250)         null comment '评分项目名称',
    PROJECT_STATUS             varchar(20)          null comment '评分状态',
    CONTRACT_NO                varchar(30)          null comment '合同编号',
    CONTRACT_NAME              varchar(250)         null comment '合同名称',
    PERFORMANCE_CODE           varchar(50)          null comment '履约阶段',
    OU_ORGANIZATION_ID         bigint(20)           null comment '公司id',
    OU_ORGANIZATION_CODE       varchar(100)         not null comment '公司编码',
    OU_ORGANIZATION_NAME       varchar(150)         null comment '公司名称',
    BU_ORGANIZATION_ID         bigint(20)           null comment '板块id',
    BU_ORGANIZATION_CODE       varchar(100)         not null comment '板块编码',
    BU_ORGANIZATION_NAME       varchar(150)         null comment '板块名称',
    COMPANY_ID                 bigint(20)           null comment '供应商ID',
    COMPANY_CODE               varchar(50)          null comment '供应商编码',
    COMPANY_NAME               varchar(500)         null comment '供应商名称',
    BID_CODE                   varchar(50)          null comment '招标编号',
    BID_END_DATE               date                 null comment '招标结束时间',
    BID_MANAGER                varchar(50)          not null comment '招标负责人',
    BID_MANAGER_FULL_PATH      varchar(500)         null comment '招标负责人部门全路径',
    CONTRACT_MANAGER           varchar(50)          not null comment '合同经办人',
    CONTRACT_MANAGER_FULL_PATH varchar(500)         null comment '合同经办人部门全路径',
    PER_START_MONTH            date                 null comment '绩效开始月份(2020-01)',
    PER_END_MONTH              date                 null comment '绩效结束月份(2020-02)',
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
    VERSION                    bigint(20) default 0 null comment '版本号'
)
    comment '项目化绩效项目主表';

-- 项目化绩效项目-评分人
create table scc_npm_project_score_items_person
(
    PROJECT_SCORE_ITEMS_PERSON_ID bigint(20)           not null comment 'ID'
        primary key,
    PROJECT_SCORE_ITEMS_ID        bigint(20)           null comment '项目id',
    CATEGORY_ID                   bigint(20)           null comment ' 品类ID ',
    CATEGORY_CODE                 varchar(80)          null comment ' 品类编码 ',
    CATEGORY_NAME                 varchar(255)         null comment ' 品类名称 ',
    TEMPLATE_HEAD_ID              bigint(20)           null comment ' 绩效模型id ',
    TEMPLATE_NAME                 varchar(250)         null comment ' 绩效模型名称 ',
    SCORE_MAN_ACCOUNT             varchar(100)         null comment '评分人账号',
    SCORE_MAN_NAME                varchar(100)         null comment '评分人姓名',
    SCORE_MAN_ID                  bigint(20)           null comment '评分人ID',
    EMAIL                         varchar(100)         null comment '邮箱',
    REMARK                        mediumtext           null comment '备注',
    CREATED_ID                    bigint(20)           not null comment '创建人ID',
    CREATED_BY                    varchar(50)          not null comment '创建人',
    CREATION_DATE                 datetime             not null comment '创建时间',
    CREATED_BY_IP                 varchar(150)         not null comment '创建人IP',
    CREATED_FULL_NAME             varchar(100)         null comment '创建人姓名',
    LAST_UPDATED_ID               bigint(20)           null comment '最后更新人ID',
    LAST_UPDATED_BY               varchar(50)          null comment '更新人',
    LAST_UPDATE_DATE              datetime             null comment '最后更新时间',
    LAST_UPDATED_BY_IP            varchar(150)         null comment '最后更新人IP',
    LAST_UPDATED_FULL_NAME        varchar(100)         null comment '最后更新人姓名',
    TENANT_ID                     varchar(30)          null comment '租户ID',
    VERSION                       bigint(20) default 0 null comment '版本号'
)
    comment '项目化绩效项目主表-评分人';
