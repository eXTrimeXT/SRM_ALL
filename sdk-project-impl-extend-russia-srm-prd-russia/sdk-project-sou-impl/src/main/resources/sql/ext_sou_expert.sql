create table scc_npm_sou_expert (
                                    EXPERT_ID                       bigint              not null comment 'ID' primary key,
                                    EXPERT_APPLY_ID                 bigint              not null comment '专家申请ID',
                                    EXPERT_APPLY_NO                 varchar(50)         not null comment '专家申请编码',
                                    EXPERT_LEVEL                    varchar(30)         not null comment '专家等级',
                                    EXPERT_USER_ID                  bigint              null comment '专家ID',
                                    EXPERT_USERNAME                 varchar(50)         null comment '专家账号',
                                    EXPERT_FULL_NAME                varchar(150)        null comment '专家昵称',
                                    JOB_STATUS                      varchar(10)         null comment '在职状态',
                                    APPLY_ID_FULL_PATH              varchar(80)         not null comment '申请单ID记录',
                                    HAS_QUITE                       char(1) default 'N' not null comment '是否已退出',
                                    HAS_FROZEN                      char(1) default 'N' not null comment '是否已冻结',
                                    QUITE_REASON                    text                null comment '退出原因',
                                    FROZEN_STATUS                   varchar(20)         null comment '冻结状态',
                                    FROZEN_REASON                   text                null comment '冻结原因',
                                    FROZEN_REJECT_REASON            text                null comment '拒绝说明',

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
) comment '专家库';

create table scc_npm_sou_expert_apply (
                                          EXPERT_APPLY_ID                 bigint              not null comment 'ID' primary key,
                                          EXPERT_APPLY_NO                 varchar(50)         not null comment '专家申请编号',
                                          APPLY_FROM_TYPE                 varchar(30)         not null comment '数据来源',
                                          APPLY_BY_ID                     bigint              null comment '申请人ID',
                                          APPLY_BY                        varchar(50)         null comment '申请人账号',
                                          APPLY_BY_NICKNAME               varchar(150)        null comment '申请人昵称',
                                          APPLY_TIME                      datetime            null comment '申请时间',
                                          APPLY_STATUS                    varchar(30)         null comment '申请状态',
                                          HAS_SUBMIT                      char(1) default 'N' not null comment '是否已提交',
                                          HIGHEST_DEGREE                  varchar(30)         null comment '最高学历',
                                          sex                             varchar(10)         null comment '性别',
                                          ORG_OU_ID                       bigint              null comment '所属公司ID',
                                          ORG_OU_CODE                     varchar(50)         null comment '所属公司编码',
                                          ORG_OU_NAME                     varchar(150)        null comment '所属公司名称',
                                          DEPARTMENT_ID                   varchar(30)         null comment '申请部门ID',
                                          DEPARTMENT_NAME                 varchar(150)        null comment '申请部门名称',
                                          JOB                             varchar(30)         null comment '职务',
                                          EXPERT_LEVEL                    varchar(30)         null comment '专家等级',
                                          JOB_STATUS                      varchar(10)         null comment '在职状态',
                                          PHONE                           varchar(50)         null comment '手机号码',
                                          HIRE_DATE                       date                null comment '入职时间',
                                          APPLY_LEVEL                     varchar(30)         null comment '申请等级',
                                          FROM_APPLY_ID                   varchar(80)         null comment '用于专家升级时记录来源申请单号',
                                          GREEN_REASON                    text                null comment '绿色通道原因',
                                          UPGRADE_REASON                  text                null comment '升级申请原因',

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
) comment '专家申请';

create table scc_npm_sou_expert_category (
                                             EXPERT_CATEGORY_ID              bigint              not null comment 'ID' primary key,
                                             EXPERT_APPLY_ID                 bigint              not null comment '专家申请ID',
                                             CATEGORY_ID                     bigint              null comment '品类ID',
                                             CATEGORY_CODE                   varchar(80)         null comment '品类编码',
                                             CATEGORY_NAME                   varchar(150)        null comment '品类名称',
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
) comment '专家申请适用品类';

create table scc_npm_sou_expert_apply_edu (
                                              EXPERT_EDUCATION_ID             bigint              not null comment 'ID' primary key,
                                              EXPERT_APPLY_ID                 bigint              not null comment '专家申请ID',
                                              EDUCATION                       varchar(30)         null comment '学历',
                                              STUDY_COLLEGE                   varchar(255)        null comment '就读院校',
                                              FULL_TIME_STUDY                 char(1) default 'Y' null comment '是否全日制',
                                              STUDY_DATE_FROM                 date                null comment '就读时间从',
                                              STUDY_DATE_TO                   date                null comment '就读时间到',
                                              MAJOR                           varchar(150)        null comment '主修专业',
                                              DEGREE_CERTIFY_DOC_ID           bigint              null comment '学位证书-文件ID',
                                              DEGREE_CERTIFY_FILE_NAME        varchar(150)        null comment '学位证书-文件名称',
                                              GRADUATION_CERTIFY_DOC_ID       bigint              null comment '毕业证书-文件ID',
                                              GRADUATION_CERTIFY_FILE_NAME    varchar(150)        null comment '毕业证书-文件名称',
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
) comment '专家申请学历';

create table scc_npm_sou_expert_org (
                                        EXPERT_ORG_ID                   bigint              not null comment 'ID' primary key,
                                        EXPERT_APPLY_ID                 bigint              not null comment '专家申请ID',
                                        ORG_ID                          bigint              null comment '品类ID',
                                        ORG_CODE                        varchar(50)         null comment '品类编码',
                                        ORG_NAME                        varchar(150)        null comment '品类名称',
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
) comment '专家申请适用组织';

create table scc_npm_sou_expert_work (
                                         EXPERT_WORK_ID                  bigint              not null comment 'ID' primary key,
                                         EXPERT_APPLY_ID                 bigint              not null comment '专家申请ID',
                                         WORK_UNIT                       varchar(255)        null comment '工作单位',
                                         JOB                             varchar(150)        null comment '职务',
                                         ENTRY_DATE                      date                null comment '入职时间',
                                         QUIT_DATE                       date                null comment '离职时间',
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
) comment '专家申请工作经历';

create table scc_npm_sou_expert_work_relate (
                                                EXPERT_WORK_RELATE_ID           bigint              not null comment 'ID' primary key,
                                                EXPERT_APPLY_ID                 bigint              not null comment '专家申请ID',
                                                RELATIVE_NAME                   varchar(50)         null comment '亲属名称',
                                                RELATIVE_TYPE                   varchar(30)         null comment '亲属关系',
                                                WORK_UNIT                       varchar(255)        null comment '工作单位',
                                                JOB                             varchar(150)        null comment '职务',
                                                ENTRY_DATE                      date                null comment '入职时间',
                                                QUIT_DATE                       date                null comment '离职时间',
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
) comment '专家申请亲属工作经历';

create table scc_npm_sou_expert_score (
                                          EXPERT_SCORE_ID                 bigint              not null comment 'ID' primary key,
                                          SOU_PROJECT_ID                  bigint              not null comment '寻源单ID',
                                          SOU_NO                          varchar(50)         null comment '寻源单号',
                                          SOU_NAME                        varchar(150)        null comment '寻源名称',
                                          PROJECT_ADDRESS                 varchar(300)        null comment '项目所在地',
                                          TOTAL_AMOUNT_BY_TEN_KILO        decimal(24,8)       null comment '概算金额(万元)',
                                          EXPERT_ID                       bigint              not null comment '专家库表ID',
                                          EXPERT_USER_ID                  bigint              null comment '专家ID',
                                          EXPERT_USERNAME                 varchar(80)         null comment '专家账号',
                                          EXPERT_FULL_NAME                varchar(100)        null comment '专家昵称',
                                          SCORE_STATUS                    varchar(20)         null comment '评价状态',
                                          SCORE_RESULT                    decimal(8, 4)       null comment '评价结果',
                                          SCORE_TIME                      datetime            null comment '评价时间',

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
) comment '专家评审信息';

create table scc_npm_sou_expert_score_line (
                                               EXPERT_SCORE_LINE_ID            bigint              not null comment 'ID' primary key,
                                               EXPERT_SCORE_ID                 bigint              not null comment '专家评审ID',
                                               USER_ID                         bigint              null comment '用户ID',
                                               USERNAME                        varchar(80)         null comment '用户账号',
                                               NICKNAME                        varchar(150)        null comment '用户昵称',
                                               GROUP_TYPE                      varchar(30)         null comment '用户职责',
                                               SCORE                           decimal(8, 4)       null comment '评分',

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
) comment '专家评审明细';

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_APPLY_STATUS', '专家库 - 专家申请状态', '专家库 - 专家申请状态', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：拟定
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'DRAFT',      '拟定',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：已提交
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'SUBMITTED',      '已提交',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 3 ：审批中
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'APPROVING',      '审批中',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 4 ：已驳回
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'REJECTED',      '已驳回',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 5 ：已审批
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'APPROVED',      '已审批',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 6 ：已废弃
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'ABANDONED',      '已废弃',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 7 ：已撤回
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_STATUS' and LANGUAGE = 'zh_CN'),
    'WITHDRAW',      '已撤回',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_APPLY_FROM_TYPE', '专家库 - 专家申请数据来源', '专家库 - 专家申请数据来源', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：自主申请
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_FROM_TYPE' and LANGUAGE = 'zh_CN'),
    'INDEPENDENT',      '自主申请',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：绿色通道
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_FROM_TYPE' and LANGUAGE = 'zh_CN'),
    'GREEN_CHANNEL',      '绿色通道',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 3 ：升级申请
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_APPLY_FROM_TYPE' and LANGUAGE = 'zh_CN'),
    'UPGRADE',      '升级申请',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_EDUCATION', '专家库 - 专家学历等级', '专家库 - 专家学历等级', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：博士
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    'DOCTOR',      '博士',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：硕士
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    'MASTER',      '硕士',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 3 ：本科
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    'UNDERGRADUATE',      '本科',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 4 ：专科
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    'SPECIALTY',      '专科',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 5 ：高中
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    'HIGH_SCHOOL',      '高中',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_JOB_STATUS', '专家库 - 在职状态', '专家库 - 在职状态', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：在职
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_JOB_STATUS' and LANGUAGE = 'zh_CN'),
    'OB_JOB',      '在职',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：离职
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_JOB_STATUS' and LANGUAGE = 'zh_CN'),
    'FEMALE',      '离职',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_LEVEL', '专家库 - 专家等级', '专家库 - 专家等级', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：普通
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_LEVEL' and LANGUAGE = 'zh_CN'),
    'NORMAL',      '普通',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：高级
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_LEVEL' and LANGUAGE = 'zh_CN'),
    'SENIOR',      '高级',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_SEX', '专家库 - 专家等级', '专家库 - 专家等级', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：男
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SEX' and LANGUAGE = 'zh_CN'),
    'MALE',      '男',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：女
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SEX' and LANGUAGE = 'zh_CN'),
    'FEMALE',      '女',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');


insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    (445332846927873,
     (select DICT_ID from scc_base_dict where DICT_CODE = 'ATTACHMENT_TEMPLATE_SCENE' and LANGUAGE = 'zh_CN'),
    'SCENE_EXT_SOU_EXPERT',      '寻源专家库',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
INSERT INTO scc_base_dict (DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME, DICT_ROLE, DICT_ROLE_NAME, ACTIVE_DATE, INACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION) VALUES (445694962841600, 'SCENE_EXT_SOU_EXPERT', '寻源专家库附件编码', '', 'zh_CN', '中文', '', '', '2023-10-20', null, 8123195817787648, 'super', '2023-10-20 16:35:59', '10.74.150.75', '超级管理员', null, null, '2023-10-20 16:35:59', '10.74.150.75', '超级管理员', null, 0);
INSERT INTO scc_base_dict_item (DICT_ITEM_ID, DICT_ID, DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME, ITEM_DESCRIPTION, DICT_ITEM_NO, DICT_ITEM_MARK, ACTIVE_DATE, INACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION) VALUES (445694992007168, 445694962841600, 'SCENE_EXT_SOU_EXPERT', '寻源专家库附件编码', 'zh_CN', '中文', '', null, '', '2023-10-20', null, 8123195817787648, 'super', '2023-10-20 16:36:13', '10.74.150.75', '超级管理员', null, null, '2023-10-20 16:36:13', '10.74.150.75', '超级管理员', null, 0);

insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    (8171720778907905,
     'EXT_SOU_EXPERT_FROZEN_STATUS', '专家库 - 专家冻结状态', '专家库 - 专家冻结状态', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');

-- 1 ：冻结未确认
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    (8171720778907906,
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_FROZEN_STATUS' and LANGUAGE = 'zh_CN'),
    'FROZEN_UN_CONFIRM',      '冻结未确认',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：已冻结
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    (8171720778907907,
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_FROZEN_STATUS' and LANGUAGE = 'zh_CN'),
    'FROZEN',      '已冻结',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 3 ：解冻未确认
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    (8171720778907908,
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_FROZEN_STATUS' and LANGUAGE = 'zh_CN'),
    'UNFROZEN_UN_CONFIRM',      '解冻未确认',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 4 ：已确认解冻
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    (8171720778907909,
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_FROZEN_STATUS' and LANGUAGE = 'zh_CN'),
    'UNFROZEN',      '已确认解冻',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');


-- 专家申请（单据编号）
INSERT INTO scc_base_seq_definition (SEQUENCE_ID, SEQUENCE_NAME, SEQUENCE_CODE, LENGTH, PREFIX, INITIAL_VALUE, CURRENT_VALUE, GRANULAR1, GRANULAR2, GRANULAR3, GRANULAR4, GRANULAR5, GRANULAR6, GRANULAR7, GRANULAR8, GRANULAR9, GRANULAR10, GRANULAR11, GRANULAR12, GRANULAR13, GRANULAR14, GRANULAR15, GRANULAR16, GRANULAR17, GRANULAR18, GRANULAR19, GRANULAR20, SCOPE_DEFINITION, SEQUENCE_NUM_RESET, START_DATE, END_DATE, OFFSET, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
VALUES (392217135419394, '专家申请单据号', 'SEQ_SOU_EXPERT_APPLY_NO', 5, 'PX', 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '[@df_prefix][@df_date_yyyyMMdd][@df_seq]', null, '2022-12-28 00:00:00', null, null, 8123195817787648, 'yangkn', '2022-12-22 11:12:18', '10.18.4.154', '杨明', 8123195817787648, 'super', '2023-09-21 17:57:48', '10.26.34.192', '超级管理员', null, 0);

-- 新增一个专家申请审批流
delete from srm_cbpm_template_header where TEMPLATE_HEAD_ID = 413974876491790;
INSERT INTO srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
VALUES (413974876491790, 'MQL_SOU_EXPERT_APPLY_INIT', null, 'MQL_SOU_EXPERT_APPLY_INIT', null, '/', 'com.midea.cloud.srm.feign.sou.SouClient', 'com.midea.cloud.srm.sou.ext.expert.flow.PrSouExpertPlanInitFlowServiceImpl', null, null, '', 8123195817787648, 'super', '2023-04-24 10:17:15', '10.18.4.154', '超级管理员', 8123195817787648, 'super', '2023-04-24 10:17:58', '10.18.4.154', '超级管理员', null, 0, 0, 'N', null, null, 'Push', '专家申请审批', '');

-- 新增一个专家升级审批流
delete from srm_cbpm_template_header where TEMPLATE_HEAD_ID = 413974876491791;
INSERT INTO srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
VALUES (413974876491791, 'MQL_SOU_EXPERT_APPLY_UPGRADE', null, 'MQL_SOU_EXPERT_APPLY_UPGRADE', null, '/', 'com.midea.cloud.srm.feign.sou.SouClient', 'com.midea.cloud.srm.sou.ext.expert.flow.PrSouExpertPlanUpgradeFlowServiceImpl', null, null, '', 8123195817787648, 'super', '2023-04-24 10:17:15', '10.18.4.154', '超级管理员', 8123195817787648, 'super', '2023-04-24 10:17:58', '10.18.4.154', '超级管理员', null, 0, 0, 'N', null, null, 'Push', '专家申请审批', '');

-- 新增一个专家申请绿色通道审批流
delete from srm_cbpm_template_header where TEMPLATE_HEAD_ID = 413974876491792;
INSERT INTO srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
VALUES (413974876491792, 'MQL_SOU_EXPERT_APPLY_GREEN', null, 'MQL_SOU_EXPERT_APPLY_GREEN', null, '/', 'com.midea.cloud.srm.feign.sou.SouClient', 'com.midea.cloud.srm.sou.ext.expert.flow.PrSouExpertPlanGreenFlowServiceImpl', null, null, '', 8123195817787648, 'super', '2023-04-24 10:17:15', '10.18.4.154', '超级管理员', 8123195817787648, 'super', '2023-04-24 10:17:58', '10.18.4.154', '超级管理员', null, 0, 0, 'N', null, null, 'Push', '专家申请审批', '');
