-- auto-generated definition
CREATE TABLE scc_npm_bid_data_submit
(
    DATA_SUBMIT_ID              bigint           NOT NULL COMMENT '主键'
        PRIMARY KEY,
    DATA_SUBMIT_NO              varchar(250)     NOT NULL COMMENT '招标资料递交单号',
    ORG_BU_ID                   bigint           NULL COMMENT '板块ID',
    ORG_BU_CODE                 varchar(32)      NULL COMMENT '板块编码',
    ORG_BU_NAME                 varchar(250)     NULL COMMENT '板块名称',
    ORG_ID                      bigint           NULL COMMENT '公司ID(对应产品的业务实体id)',
    ORG_CODE                    varchar(32)      NULL COMMENT '公司编码(对应产品的业务实体编码)',
    ORG_NAME                    varchar(250)     NULL COMMENT '公司名称(对应产品的业务实体名称)',
    CEEA_DEPARTMENT_ID          varchar(50)      NULL COMMENT '部门ID',
    CEEA_DEPARTMENT_CODE        varchar(100)     NULL COMMENT '部门编码',
    CEEA_DEPARTMENT_NAME        varchar(400)     NULL COMMENT '部门名称',
    STATUS                      varchar(50)      NOT NULL COMMENT '单据状态',
    REQ_USER_ID                 bigint           NULL COMMENT '需求人ID',
    REQ_USER_NAME               varchar(50)      NULL COMMENT '需求人名称',
    SOU_PERSON_ID               bigint           NULL COMMENT '招标负责人ID',
    SOU_PERSON_NAME             varchar(50)      NULL COMMENT '招标负责人名称',
    REQUIREMENT_HEAD_NUM        varchar(32)      NULL COMMENT '申请单号',
    PROJECT_NAME                varchar(80)      NULL COMMENT '项目名称',
    SOURCE_FROM                 varchar(250)     NULL COMMENT '需求来源',
    TOTAL_BUDGET                decimal(30, 8)   NULL COMMENT '预算总金额',
    CATEGORY_ID                 bigint           NULL COMMENT '品类ID',
    CATEGORY_CODE               varchar(100)     NULL COMMENT '品类编码',
    CATEGORY_NAME               varchar(256)     NULL COMMENT '品类',
    REQUIRE_QUANTITY            int              NULL COMMENT '规模数量',
    DEPOSIT_AMOUNT              decimal(24, 8)   NULL COMMENT '意向金缴纳金额(元)',
    INVEST_NO                   varchar(250)     NOT NULL COMMENT '投资编号',
    BID_FLOW                    varchar(50)      NOT NULL COMMENT '招标流程',
    BID_EVAL_LEADER_ID          bigint           NULL COMMENT '评标组长用户ID',
    BID_EVAL_LEADER_NAME        varchar(50)      NULL COMMENT '评标组长名称',
    BID_EVAL_DEPUTY_LEADER_ID   bigint           NULL COMMENT '评标副组长用户ID',
    BID_EVAL_DEPUTY_LEADER_NAME varchar(50)      NULL COMMENT '评标副组长名称',
    TECH_PRINCIPAL              varchar(50)      NULL COMMENT '技术负责人',
    PHONE                       varchar(30)      NULL COMMENT '联系方式',
    WORK_YEARS                  decimal(8, 3)    NULL COMMENT '工作年限',
    COMPETE_FILE_ID             bigint           NULL COMMENT '竞争性谈判签批附件ID',
    COMPETE_FILE_NAME           varchar(255)     NULL COMMENT '竞争性谈判签批附件名称',
    CONTRACT_SIGN_UNIT          varchar(1000)    NULL COMMENT '合同签订单位',
    NOT_MONTHLY_PLAN_REASON     text             NULL COMMENT '未提报月度计划原因',
    REMARK                      text             NULL COMMENT '备注',
    IS_APPOINT_EVALUATOR        varchar(2)       NULL COMMENT '是否指定评标人(Y是，N否)',
    BID_EVALUATOR_NUM           int              NULL COMMENT '评标总人数',
    ASK_SENIOR_EXPERT_NUM       int              NULL COMMENT '要求高级专家人数',
    APPOINT_EVALUATOR_REASON    text             NULL COMMENT '指定评标人原因',
    PUBLISH_TIME                datetime         NULL COMMENT '发布日期',
    CREATED_ID                  bigint           NOT NULL COMMENT '创建人ID',
    CREATED_BY                  varchar(50)      NOT NULL COMMENT '创建人',
    CREATION_DATE               datetime         NOT NULL COMMENT '创建时间',
    CREATED_BY_IP               varchar(150)     NOT NULL COMMENT '创建人IP',
    CREATED_FULL_NAME           varchar(100)     NULL COMMENT '创建人姓名',
    LAST_UPDATED_ID             bigint           NULL COMMENT '最后更新人ID',
    LAST_UPDATED_BY             varchar(50)      NULL COMMENT '更新人',
    LAST_UPDATE_DATE            datetime         NULL COMMENT '最后更新时间',
    LAST_UPDATED_BY_IP          varchar(150)     NULL COMMENT '最后更新人IP',
    LAST_UPDATED_FULL_NAME      varchar(100)     NULL COMMENT '最后更新人姓名',
    TENANT_ID                   varchar(30)      NULL COMMENT '租户ID',
    VERSION                     bigint DEFAULT 0 NULL COMMENT '版本号'
)
    COMMENT '招标资料递交头表';


-- auto-generated definition
CREATE TABLE scc_npm_bid_data_submit_details
(
    SUBMIT_DETAILS_ID      bigint           NOT NULL COMMENT 'ID'
        PRIMARY KEY,
    DATA_SUBMIT_ID         bigint           NOT NULL COMMENT '招标资料递ID',
    MATERIAL_NAME          varchar(250)     NOT NULL COMMENT '物资名称',
    COMBINATION            varchar(250)     NOT NULL COMMENT '组合',
    AFFILIATED_UNIT        varchar(250)     NOT NULL COMMENT '所属单位',
    PERFORM_DEPOSIT        decimal(24, 8)   NULL COMMENT '履约保证金(元)',
    ADVANCE_AMOUNT         decimal(24, 8)   NULL COMMENT '预付款(元)',
    MONTH_PRODUCTION       decimal(24, 2)   NULL COMMENT '月约产量',
    METERING_UNIT          varchar(250)     NOT NULL COMMENT '计量单位',
    START_BID_PRICE        decimal(24, 8)   NULL COMMENT '起拍价(元)',
    ECHELON_BID_PRICE      decimal(24, 8)   NULL COMMENT '梯次价(元)',
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
    COMMENT '招标资料递交竞价明细表';

-- auto-generated definition
CREATE TABLE scc_npm_bid_data_submit_evaluator
(
    SUBMIT_EVALUATOR_ID    bigint           NOT NULL COMMENT 'ID'
        PRIMARY KEY,
    DATA_SUBMIT_ID         bigint           NOT NULL COMMENT '招标资料递ID',
    CEEA_EMP_NO            varchar(100)     NULL COMMENT '员工工号',
    EVALUATOR_NAME         varchar(100)     NULL COMMENT '姓名',
    PHONE                  varchar(30)      NULL COMMENT '手机号',
    EMAIL                  varchar(50)      NULL COMMENT '邮箱',
    WORK_YEARS             decimal(8, 3)    NULL COMMENT '工作年限',
    EXPERT_LEVEL           varchar(50)      NULL COMMENT '专家等级',
    EVALUATOR_ROLE         varchar(50)      NULL COMMENT '角色',
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
    COMMENT '招标资料递交指定评标人表';

