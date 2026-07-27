-- 订单化绩效复核
create table scc_npm_score_item_order_check
(
    ORDER_CHECK_ID         bigint(20)           not null comment 'ID'
        primary key,
    SCORE_ITEMS_ID         bigint(20)           null comment '项目id',
    PROJECT_NAME           varchar(250)         null comment '项目名称',
    ORGANIZATION_ID        bigint(20)           null comment '组织id',
    ORGANIZATION_NAME      varchar(150)         null comment '组织名称',
    COMPANY_ID             bigint(20)           null comment '供应商ID',
    COMPANY_CODE           varchar(50)          null comment '供应商编码',
    COMPANY_NAME           varchar(500)         null comment '供应商名称',
    PER_START_MONTH        date                 null comment '绩效开始月份(2020-01)',
    PER_END_MONTH          date                 null comment '绩效结束月份(2020-02)',
    STATUS                 varchar(50)          null comment '复核状态',
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
    comment '订单化绩效复核';
-- 订单化绩效复核-明细
create table scc_npm_score_item_order_check_detail
(
    ORDER_CHECK_DETAIL_ID  bigint(20)           not null comment 'ID'
        primary key,
    ORDER_CHECK_ID         bigint(20)           null comment '复核头id',
    SCORE_USER_ID          bigint(20)           null comment '评分人账号ID',
    SCORE_USER_NAME        varchar(80)          null comment '评分人账号',
    SCORE_NICK_NAME        varchar(250)         null comment '评分人名称',
    CATEGORY_ID            bigint(20)           null comment '采购分类ID',
    CATEGORY_CODE          varchar(80)          null comment '采购分类编码',
    CATEGORY_NAME          varchar(250)         null comment '采购分类名称',
    SCORE_DATE              datetime            null comment '评分时间',
    STATUS                 varchar(50)          null comment '评分状态',
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
    comment '订单化绩效复核-明细';