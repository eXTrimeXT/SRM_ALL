create table scc_npm_project_score_header
(
    SCORE_HEADER_ID            bigint(20)           not null comment '主键ID'
        primary key,
    PROJECT_SCORE_ITEMS_ID     bigint(20)           null comment '评分项目ID',
    PROJECT_NAME               varchar(250)         null comment '评分项目名称',
    CONTRACT_NO                varchar(30)          null comment '合同编号',
    CONTRACT_NAME              varchar(250)         null comment '合同名称',
    CATEGORY_ID                bigint(20)           null comment ' 品类ID ',
    CATEGORY_CODE              varchar(80)          null comment ' 品类编码 ',
    CATEGORY_NAME              varchar(255)         null comment ' 品类名称 ',
    PERFORMANCE_CODE           varchar(50)          null comment '履约阶段',
    OU_ORGANIZATION_ID         bigint(20)           null comment '公司id',
    OU_ORGANIZATION_CODE       varchar(100)         null comment '公司编码',
    OU_ORGANIZATION_NAME       varchar(150)         null comment '公司名称',
    BU_ORGANIZATION_ID         bigint(20)           null comment '板块id',
    BU_ORGANIZATION_CODE       varchar(100)         null comment '板块编码',
    BU_ORGANIZATION_NAME       varchar(150)         null comment '板块名称',
    COMPANY_ID                 bigint(20)           null comment '供应商ID',
    COMPANY_CODE               varchar(50)          null comment '供应商编码',
    COMPANY_NAME               varchar(500)         null comment '供应商名称',
    BID_CODE                   varchar(50)          null comment '招标编号',
    BID_END_DATE               date                 null comment '招标结束时间',
    BID_MANAGER                varchar(50)          null comment '招标负责人',
    BID_MANAGER_FULL_PATH      varchar(500)         null comment '招标负责人部门全路径',
    CONTRACT_MANAGER           varchar(50)          null comment '合同经办人',
    CONTRACT_MANAGER_FULL_PATH varchar(500)         null comment '合同经办人部门全路径',
    PER_START_MONTH            date                 null comment '绩效开始月份(2020-01)',
    PER_END_MONTH              date                 null comment '绩效结束月份(2020-02)',
    SCORE                      DECIMAL(20, 2)       null comment '成绩',
    LEVEL_NAME                 varchar(100)         null comment '等级',
    PROJECT_STATUS             varchar(20)          null comment '整体评分状态',
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
    comment '项目化绩效评分结果-头表';

create table scc_npm_project_score_dim
(
    SCORE_DIM_ID               bigint(20)           not null comment '主键ID'
        primary key,
    SCORE_HEADER_ID            bigint(20)           null comment '绩效评分ID',
    INDICATOR_DIMENSION_WEIGHT varchar(150)         null comment '维度权重(百分比)',
    SCORE                      DECIMAL(20, 2)       null comment '维度成绩',
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
    comment '项目化绩效评分结果-维度表';


create table scc_npm_project_score_ind
(
    SCORE_IND_ID           bigint(20)           not null comment '主键ID'
        primary key,
    SCORE_HEADER_ID        bigint(20)           null comment '绩效评分ID',
    SCORE_DIM_ID           bigint(20)           null comment '维度ID',
    INDICATOR_NAME         varchar(80)          null comment '绩效模型-指标名称',
    EVALUATION             varchar(50)          null comment '评价方式(SCORING_SYSTEM_VALUE:评分-系统取值,DEDUCTION_SYSTEM_VALUE:扣分-系统取值,SCORING_MANUAL:评分-手工,DEDUCTION_MANUAL:扣分-手工)',
    SCORE                  DECIMAL(20, 2)       null comment '指标得分',
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
    comment '项目化绩效评分结果-指标表';