create table scc_npm_pr_require_head (
    REQUIREMENT_HEAD_ID             bigint              not null comment 'ID' primary key,
    ORG_BU_ID                       bigint              null comment '所属板块ID',
    ORG_BU_CODE                     varchar(50)         null comment '所属板块编码',
    ORG_BU_NAME                     varchar(255)        null comment '所属板块名称',
    REQUIRE_FROM                    varchar(30)         null comment '需求来源',
    NO_REPORT_MONTH_PLAN_REASON     varchar(300)        null comment '未报月度计划原因',
    PROJECT_NAME                    varchar(80)         null comment '项目名称',
    PROJECT_MONTH                   int                 null comment '月份',
    INVEST_NO                       varchar(50)         null comment '投资编号',
    REQUIRE_QUANTITY                varchar(50)         null comment '数量/规模',
    TOTAL_AMOUNT_BY_TEN_KILO        decimal(24,8)       null comment '概算金额(万元)',
    NEED_PUBLIC                     char(1)             not null comment '是否公示',
    NO_PUBLIC_REASON                varchar(300)        null comment '不公示理由',
    PUBLIC_END_TIME                 date                null comment '公示截止时间',
    PROJECT_ADDRESS                 varchar(255)        null comment '项目所在地',
    PREFIX_TECH_DISCUSSION          char(1)             not null comment '前置技术交流意向',
    SEND_SOU_PROFILE_END_DATE       date                null comment '递交招标资料时间',
    PROJECT_PLAN_ID                 bigint              null comment '项目计划ID',
    PLAN_NO                         varchar(50)         null comment '项目计划编号',
    SPECIAL_SOU_TYPE                varchar(30)         null comment '特殊招标类型',
    SPECIAL_REASON                  varchar(300)        null comment '特殊原因',
    REQUIRE_PRODUCT_DATE            date                null comment '需求产生时间',
    REQUIRE_PRODUCT_FILE_ID         bigint              null comment '需求产生时间附件ID',
    REQUIRE_PRODUCT_FILE_NAME       varchar(150)        null comment '需求产生时间附件名称',
    REMAINING_DAY                   decimal(12,4)       null comment '剩余时间',
    DELIVERY_DAY                    decimal(12,4)       null comment '工期交货期',
    DELIVERY_DAY_FILE_ID            bigint              null comment '工期交货期附件ID',
    DELIVERY_DAY_FILE_NAME          varchar(150)        null comment '工期交货期附件名称',
    SIGN_CONTRACT_DAY               decimal(12,4)       null comment '签合同用时',
    PUT_INTO_USE_DATE               date                null comment '投入使用时间',
    PUT_INTO_USE_DATE_FILE_ID       bigint              null comment '投入使用时间附件ID',
    PUT_INTO_USE_DATE_FILE_NAME     varchar(150)        null comment '投入使用时间附件名称',
    OTHER_SPECIAL_REASON            varchar(300)        null comment '其他特殊原因补充',
    PROJECT_OVERVIEW                text                null comment '项目概况及范围',
    TECH_REQUIRE                    text                null comment '技术要求',
    PERFORMANCE_REQUIRE             text                null comment '业绩要求',
    VENDOR_QUALIFICATION_REQUIRE    text                null comment '供应商资质要求',
    HAS_ASSIGNED                    char(1) default 'N' not null comment '是否已分配(招标+供应商负责人)',
    HAS_SEND_SOU_PROFILE            char(1) default 'N' not null comment '是否已递交招标资料',
    HAS_CREATE_SOU                  char(1) default 'N' not null comment '是否已创建寻源',
    HAS_CREATE_SOU_REQ              char(1) default 'N' not null comment '是否已创建寻源需求',
    HAS_CREATE_VENDOR_RECOMMEND     char(1) default 'N' not null comment '是否已创建供应商推荐',
    HAS_SUBMIT                      char(1) default 'N' not null comment '是否已提交',
    EARNEST_MONEY                   decimal(24,8)       null comment '意向金金额(万元)',
    APPROVAL_PASS_TIME              datetime            null comment '需求审批通过时间',
    SOU_REQ_STATUS                  varchar(30)         null comment '需求状态',
    SEND_SOU_PROFILE_STATUS         varchar(30)         null comment '招标资料状态',
    AFTER_TOTAL_AMOUNT_BY_TEN_KILO  decimal(24,8)       null comment '变更后概算金额(万元)',
    CHANGE_REASON                   text                null comment '变更原因',
    CHANGE_REQUIREMENT_HEAD_ID      bigint              null comment '变更来源计划ID',
    CHANGE_REQUIREMENT_HEAD_NUM     varchar(50)         null comment '变更来源计划单号',
    REQ_CANCEL_REASON               text                null comment '需求取消原因',
    SOU_TYPE                        varchar(10)         null comment '关联寻源单类型',
    SOU_PROJECT_ID                  bigint              null comment '关联寻源单ID',
    SOU_NO                          varchar(50)         null comment '关联寻源单号',
    SOU_NAME                        varchar(255)        null comment '关联寻源单名称',
    SOU_REQ_ID                      bigint              null comment '关联寻源需求ID',
    SOU_REQ_NO                      varchar(50)         null comment '关联寻源需求编号',
    RECOMMEND_VENDOR_BILL_ID        bigint              null comment '关联推荐供应商单据ID',
    RECOMMEND_VENDOR_BILL_NO        varchar(50)         null comment '关联推荐供应商单据编码',

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
) comment '招标计划拓展表';


create table scc_npm_pr_require_group (
    REQUIREMENT_GROUP_ID            bigint              not null comment 'ID' primary key,
    REQUIREMENT_HEAD_ID             bigint              not null comment '招标计划ID',
    USER_ID                         bigint              null comment '用户ID',
    USERNAME                        varchar(50)         null comment '用户账号',
    FULL_NAME                       varchar(255)        null comment '用户昵称',
    GROUP_TYPE                      varchar(80)         null comment '职责类型',
    PHONE                           varchar(50)         null comment '联系方式',
    EMAIL                           varchar(200)        null comment '邮箱',
    SORT_INDEX                      int                 not null comment '排序',

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
) comment '招标计划工作成员表';


create table scc_npm_pr_require_vendor (
    REQUIREMENT_VENDOR_ID           bigint              not null comment 'ID' primary key,
    REQUIREMENT_HEAD_ID             bigint              not null comment '招标计划ID',
    VENDOR_ID                       bigint              null comment '供应商ID',
    VENDOR_CODE                     varchar(50)         null comment '供应商账号',
    VENDOR_NAME                     varchar(255)        null comment '供应商昵称',
    PHONE                           varchar(50)         null comment '联系方式',
    EMAIL                           varchar(200)        null comment '邮箱',
    RECOMMEND_FROM                  varchar(30)         null comment '推荐来源',
    SORT_INDEX                      int                 not null comment '排序',

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
) comment '招标计划推荐供应商表';


create table scc_npm_pr_require_attach (
    REQUIREMENT_ATTACH_ID           bigint              not null comment 'ID' primary key,
    REQUIREMENT_HEAD_ID             bigint              not null comment '招标计划ID',
    FILE_TYPE                       varchar(30)         null comment '文件类型',
    FILE_ID                         bigint              null comment '文件ID',
    FILE_NAME                       varchar(255)        null comment '文件名称',
    UPDATE_DATE                     date                null comment '上传时间',
    SORT_INDEX                      int                 not null comment '排序',

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
) comment '招标计划附件表';

create table scc_npm_pr_project_plan (
    PROJECT_PLAN_ID                 bigint              not null comment 'ID' primary key,
    PROJECT_NAME                    varchar(80)         not null comment '项目名称',
    SCENE_TYPE                      varchar(30)         not null comment '应用场景',
    INIT_DATE                       date                null comment '立项时间',
    DEPARTMENT_ID                   varchar(30)         null comment '投资部门ID',
    DEPARTMENT_NAME                 varchar(255)        null comment '投资部门名称',
    INIT_AMOUNT                     decimal(24,8)       null comment '立项金额',
    PLAN_NO                         varchar(80)         null comment '计划编号',
    PLAN_ADDRESS                    varchar(255)        null comment '投资地点',
    PLAN_LEVEL                      varchar(30)         null comment '项目级别',
    PLAN_STATUS                     varchar(30)         null comment '项目状态',
    REQUIREMENT_HEAD_ID             bigint(20)          null comment '被引用的招标计划ID',

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
) comment '招标计划项目计划表';


create table scc_npm_pr_require_cancel (
    REQUIREMENT_CANCEL_ID           bigint              not null comment 'ID' primary key,
    REQUIREMENT_CANCEL_NO           varchar(50)         not null comment '计划取消编号',
    CANCEL_STATUS                   varchar(30)         not null comment '计划取消状态',
    DEPARTMENT_ID                   varchar(50)         null comment '申请部门ID',
    DEPARTMENT_NAME                 varchar(150)        null comment '申请部门名称',
    APPLY_DATE                      date                null comment '申请日期',
    APPLY_BY_ID                     bigint              null comment '申请人ID',
    APPLY_BY                        varchar(50)         null comment '申请人账号',
    APPLY_BY_NICKNAME               varchar(150)        null comment '申请人昵称',
    CANCEL_REASON                   text                null comment '取消原因',

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
) comment '招标计划 - 计划取消';

create table scc_npm_pr_require_cancel_file (
    REQUIREMENT_CANCEL_ATTACH_ID    bigint              not null comment 'ID' primary key,
    REQUIREMENT_CANCEL_ID           bigint              not null comment '计划取消ID',
    FILE_ID                         bigint              null comment '文件ID',
    FILE_NAME                       varchar(150)        null comment '文件名称',
    UPLOAD_TIME                     datetime            null comment '上传时间',
    SORT_INDEX                      int                 not null comment '排序',

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
) comment '招标计划 - 计划取消附件';

create table scc_npm_pr_require_cancel_line (
    REQUIREMENT_CANCEL_LINE_ID      bigint              not null comment 'ID' primary key,
    REQUIREMENT_CANCEL_ID           bigint              not null comment '计划取消ID',
    REQUIREMENT_HEAD_ID             bigint              null comment '招标计划ID',
    SORT_INDEX                      int                 not null comment '排序',

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
) comment '招标计划 - 计划取消明细';

-- 计划取消（单据编号）
INSERT INTO scc_base_seq_definition (SEQUENCE_ID, SEQUENCE_NAME, SEQUENCE_CODE, LENGTH, PREFIX, INITIAL_VALUE, CURRENT_VALUE, GRANULAR1, GRANULAR2, GRANULAR3, GRANULAR4, GRANULAR5, GRANULAR6, GRANULAR7, GRANULAR8, GRANULAR9, GRANULAR10, GRANULAR11, GRANULAR12, GRANULAR13, GRANULAR14, GRANULAR15, GRANULAR16, GRANULAR17, GRANULAR18, GRANULAR19, GRANULAR20, SCOPE_DEFINITION, SEQUENCE_NUM_RESET, START_DATE, END_DATE, OFFSET, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
VALUES (392217135419393, '招标计划-取消单据', 'SEQ_PR_REQ_SOU_CANCEL', 5, 'C', 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '[@df_prefix][@df_date_yyyyMMdd][@df_seq]', null, '2022-12-28 00:00:00', null, null, 8123195817787648, 'yangkn', '2022-12-22 11:12:18', '10.18.4.154', '杨明', 8123195817787648, 'super', '2023-09-21 17:57:48', '10.26.34.192', '超级管理员', null, 0);

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_PROJECT_PLAN_TYPE', '招标计划 - 项目计划状态', '招标计划 - 项目计划状态', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');

insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_PROJECT_PLAN_TYPE' and LANGUAGE = 'zh_CN'),
 'NORMAL',      '正常',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');

insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_PROJECT_PLAN_TYPE' and LANGUAGE = 'zh_CN'),
 'FINISH',      '完结',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');

insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_PROJECT_PLAN_TYPE' and LANGUAGE = 'zh_CN'),
 'CANCEL',      '取消',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');


-- 新增一个基于mql的采购申请审批流
delete from srm_cbpm_template_header where TEMPLATE_HEAD_ID = 413974876491788;
INSERT INTO srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
VALUES (413974876491788, 'MQL_PR_SOU_REQUIREMENT_INIT', null, 'MQL_PR_SOU_REQUIREMENT_INIT', null, '/', 'com.midea.cloud.srm.feign.supcooperate.SupcooperateClient', 'com.midea.cloud.srm.supcooperate.ext.requirement.souplan.flow.PrSouRequirementFlowServiceImpl', null, null, '', 8123195817787648, 'super', '2023-04-24 10:17:15', '10.18.4.154', '超级管理员', 8123195817787648, 'super', '2023-04-24 10:17:58', '10.18.4.154', '超级管理员', null, 0, 0, 'N', null, null, 'Push', '招标计划立项审批', '');

-- 新增一个取消单的审批流
delete from srm_cbpm_template_header where TEMPLATE_HEAD_ID = 413974876491789;
INSERT INTO srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
VALUES (413974876491789, 'MQL_PR_SOU_REQ_CANCEL_INIT', null, 'MQL_PR_SOU_REQ_CANCEL_INIT', null, '/', 'com.midea.cloud.srm.feign.supcooperate.SupcooperateClient', 'com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.flow.PrSouRequirementCancelFlowServiceImpl', null, null, '', 8123195817787648, 'super', '2023-04-24 10:17:15', '10.18.4.154', '超级管理员', 8123195817787648, 'super', '2023-04-24 10:17:58', '10.18.4.154', '超级管理员', null, 0, 0, 'N', null, null, 'Push', '招标计划取消单审批', '');

-- 新增一个变更单的审批流
delete from srm_cbpm_template_header where TEMPLATE_HEAD_ID = 413974876491791;
INSERT INTO srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
VALUES (413974876491791, 'MQL_PR_SOU_REQ_CHANGE', null, 'MQL_PR_SOU_REQ_CHANGE', null, '/', 'com.midea.cloud.srm.feign.supcooperate.SupcooperateClient', 'com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.flow.PrSouRequirementChangeFlowServiceImpl', null, null, '', 8123195817787648, 'super', '2023-04-24 10:17:15', '10.18.4.154', '超级管理员', 8123195817787648, 'super', '2023-04-24 10:17:58', '10.18.4.154', '超级管理员', null, 0, 0, 'N', null, null, 'IdeFlow', '招标计划取消单审批', '');

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_REQUIREMENT_FROM', '招标计划 - 需求来源', '招标计划 - 需求来源', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');

insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FROM' and LANGUAGE = 'zh_CN'),
 'YEAR',      '年度',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FROM' and LANGUAGE = 'zh_CN'),
 'MONTH',      '月度',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FROM' and LANGUAGE = 'zh_CN'),
 'WITHOUT_PLAN',      '计划外',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FROM' and LANGUAGE = 'zh_CN'),
 'SPECIAL_SOU',      '特殊招标',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');


insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_REQUIREMENT_FILE_TYPE', '招标计划 - 文件类型', '招标计划 - 文件类型', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');

insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FILE_TYPE' and LANGUAGE = 'zh_CN'),
 'OTHER',      '其他',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FILE_TYPE' and LANGUAGE = 'zh_CN'),
 'RESTRICT_VENDOR',      '是否限制供应商',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_FILE_TYPE' and LANGUAGE = 'zh_CN'),
 'RESTRICT_BRAND',      '是否限制品类',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');


insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_REQUIREMENT_SPECIAL_REASON', '招标计划 - 特殊原因', '招标计划 - 特殊原因', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_REASON' and LANGUAGE = 'zh_CN'),
 'MONOPOLY',      '垄断',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_REASON' and LANGUAGE = 'zh_CN'),
 'GOVERNMENT',      '政府定制',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_REASON' and LANGUAGE = 'zh_CN'),
 'FACTORY_COOP',      '原厂合作',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_REASON' and LANGUAGE = 'zh_CN'),
 'OTHER',      '其他',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');


insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_REQUIREMENT_SPECIAL_TYPE', '招标计划 - 特殊招标类型', '招标计划 - 特殊招标类型', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_TYPE' and LANGUAGE = 'zh_CN'),
 'SPECIAL_VENDOR_ONE',      '特定原因使得供应商唯一',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_TYPE' and LANGUAGE = 'zh_CN'),
 'TIME_URGENT',      '时间紧急',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_TYPE' and LANGUAGE = 'zh_CN'),
 'OTHER',      '其他',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');



insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_REQUIREMENT_STATUS', '招标计划池 - 需求状态', '招标计划池 - 需求状态', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_STATUS' and LANGUAGE = 'zh_CN'),
 'EXECUTING',      '进行中',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_STATUS' and LANGUAGE = 'zh_CN'),
 'FINISH',      '已完成',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_STATUS' and LANGUAGE = 'zh_CN'),
 'CANCEL',      '已取消',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');


insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 'PR_SOU_REQUIREMENT_SEND_PROFILE_STATUS', '招标计划池 - 招标资料状态', '招标计划池 - 招标资料状态', 'zh_CN', '中文',
 '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
 (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SEND_PROFILE_STATUS' and LANGUAGE = 'zh_CN'),
 'APPROVED',      '已审批',          'zh_CN',       '中文',
 current_date, '1',       'admin',    now(),         '127.0.0.1',
 '1',             'admin',         '127.0.0.1');


-- 项目计划快查
INSERT INTO scc_base_quicksearch_config (QUICKSEARCH_CONFIG_ID, NAME, QUERY_DATASOURCE, QUERY_MODULE, QUERY_LANGUAGE_TYPE, QUERY_MATCH_OPERATOR, QUERY_TABLE, QUERY_MAX_SIZE, QUERY_MODE, CODE_ATTR, VALUE_ATTR, QUERY_LANGUAGE, QUERY_LANGUAGE_CUSTOM, DIALOG_ENABLED, DIALOG_QUERY_TYPE, DIALOG_QUERY_TABLE, DIALOG_QUERY_MAX_SIZE, DIALOG_QUERY_LANGUAGE, DIALOG_QUERY_LANGUAGE_CUSTOM, DESCRIPTION, SELECT_MODE, REFLECT_INPUT_CLASS_NAME, REFLECT_CLASS_NAME, REFLECT_INPUT_METHOD_NAME, REFLECT_METHOD_NAME, REFLECT_INPUT_PROPERTY_NAME, REFLECT_PROPERTY_NAME, SELECT_INPUT_MODE, HTTP_URL, INPUT_HTTP_URL, COMPANY_VIEW_FLAG, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION) VALUES (443698235834368, 'scc_npm_pr_project_plan_query', null, 'SUPCOOPERATE_READ', null, 'allMatch', 'scc_npm_pr_project_plan', 15, null, null, 'PROJECT_PLAN_ID', 'select t.* from scc_npm_pr_project_plan t where 1=1 and t.PLAN_STATUS = ''NORMAL'' and (t.PROJECT_NAME like :query or t.PLAN_NO like :query) order by t.CREATION_DATE desc', null, null, null, null, null, 'select t.* from scc_npm_pr_project_plan t where 1=1 and t.PLAN_STATUS = ''NORMAL''', null, '招标计划-项目计划查询', null, '', '', '', '', '', '', null, '', '', 'N', 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0);

INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967488, 443698235834368, 't', 'PROJECT_PLAN_ID', 'ID', 'BIGINT', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967489, 443698235834368, 't', 'PROJECT_NAME', '项目名称', 'VARCHAR', null, null, 'Y', 'Y', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967490, 443698235834368, 't', 'SCENE_TYPE', '应用场景', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967491, 443698235834368, 't', 'INIT_DATE', '立项时间', 'DATE', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967492, 443698235834368, 't', 'DEPARTMENT_ID', '投资部门ID', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967493, 443698235834368, 't', 'DEPARTMENT_NAME', '投资部门名称', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967494, 443698235834368, 't', 'INIT_AMOUNT', '立项金额', 'DECIMAL', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967495, 443698235834368, 't', 'PLAN_NO', '计划编号', 'VARCHAR', null, null, 'Y', 'Y', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:34', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967496, 443698235834368, 't', 'PLAN_ADDRESS', '投资地点', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967497, 443698235834368, 't', 'PLAN_LEVEL', '项目级别', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967498, 443698235834368, 't', 'PLAN_STATUS', '项目状态', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967499, 443698235834368, 't', 'REQUIREMENT_HEAD_ID', '被引用的招标计划ID', 'BIGINT', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967500, 443698235834368, 't', 'CREATED_ID', '创建人ID', 'BIGINT', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967501, 443698235834368, 't', 'CREATED_BY', '创建人', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967502, 443698235834368, 't', 'CREATION_DATE', '创建时间', 'TIMESTAMP', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967503, 443698235834368, 't', 'CREATED_BY_IP', '创建人IP', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967504, 443698235834368, 't', 'CREATED_FULL_NAME', '创建人姓名', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967505, 443698235834368, 't', 'LAST_UPDATED_ID', '最后更新人ID', 'BIGINT', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967506, 443698235834368, 't', 'LAST_UPDATED_BY', '更新人', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967507, 443698235834368, 't', 'LAST_UPDATE_DATE', '最后更新时间', 'TIMESTAMP', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967508, 443698235834368, 't', 'LAST_UPDATED_BY_IP', '最后更新人IP', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967509, 443698235834368, 't', 'LAST_UPDATED_FULL_NAME', '最后更新人姓名', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967510, 443698235834368, 't', 'TENANT_ID', '租户ID', 'VARCHAR', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);
INSERT INTO scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY) VALUES (443698235967511, 443698235834368, 't', 'VERSION', '版本号', 'BIGINT', null, null, 'N', 'N', null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, null, '2023-10-09 09:46:35', '10.74.150.65', '超级管理员', null, 0, null, null, null);