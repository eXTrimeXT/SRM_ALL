create table scc_npm_project_score_man_reject_info
(
    REJECT_INFO_ID         bigint(20)           not null comment '主键ID'
        primary key,
    PROJECT_SCORE_MAN_ID   bigint(20)           null comment '项目化绩效评分主表ID',
    SCORE_ROUND            DECIMAL(3, 0)        null comment '轮次',
    REJECT_INFO            MEDIUMTEXT           null comment '驳回说明',
    REJECT_DATE            DATE                 null comment '驳回日期',
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
    comment '项目化绩效评分-驳回信息';