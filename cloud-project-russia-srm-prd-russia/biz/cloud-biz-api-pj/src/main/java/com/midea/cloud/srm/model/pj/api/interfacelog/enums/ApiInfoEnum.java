package com.midea.cloud.srm.model.pj.api.interfacelog.enums;


/**
 * @Description: for srm接口信息枚举
 *
 * @author srm
 * @date 2024-05-18
 */
public enum ApiInfoEnum {
    /* 销售结算中心  start */
    CREATE_INVOICE("发票开具创建", "发票开具创建","HTTP","开发平台","SEND"),

    SETTLE_RESULT("结算结果查询", "结算结果查询","HTTP","开发平台","SEND"),

    SAVE_OUT_SOURCE_ONE_VO("批量付款及自动提交审批", "批量付款及自动提交审批", "HTTP", "开发平台", "SEND"),

    SETTLE_ACCOUNTING("结算记账", "结算记账", "HTTP", "开发平台", "SEND"),

    /* 销售结算中心  end */

    /** bpm start*/
    START_PROCESS_BY_CATEGOTY("发起流程", "发起流程","HTTP","BPM","SEND"),
    ROLL_BACK_TASK("退回任务", "退回任务","HTTP","BPM","SEND"),
    FORWARD_TASK("转办任务", "转办任务","HTTP","BPM","SEND"),
    COMMIT_TASK("提交待办任务", "提交待办任务","HTTP","BPM","SEND"),
    PREDICT("预执行接口", "预执行接口","HTTP","BPM","SEND"),
    CREATE_PROCESS("按流程分组发起流程", "按流程分组发起流程","HTTP","BPM","SEND"),
    ROLL_BACK_PROCESS("发起人撤回流程", "发起人撤回流程","HTTP","BPM","SEND"),
    UPDATE_PROCESS("更新流程", "更新流程","HTTP","BPM","SEND"),
    BPM_CALLBACK("BPM审批回调", "BPM审批回调","HTTP","BPM","RECEIVE"),
    RECOGNIZE_LICENCE("营业执照OCR识别调用", "营业执照OCR识别调用","HTTP","开发平台","SEND"),
    PUSH_ZBFW_TO_BPM("定标审批_推送中标范围", "定标审批_推送中标范围","HTTP","BPM","SEND"),
    RESUBMIT_PROCESS("驳回后重新提交流程", "驳回后重新提交流程","HTTP","BPM","SEND"),
    BPM_ROLE("流程角色推送BPM", "流程角色推送BPM","HTTP","BPM","SEND"),
    EAS_ROLE("流程角色推送EAS", "流程角色推送BEAS","HTTP","EAS","SEND"),

    /** OCR start */
    RECOGNIZE_ID_CARD_FRONT("身份证正面OCR识别调用", "身份证正面OCR识别调用","HTTP","开发平台","SEND"),
    RECOGNIZE_ID_CARD_BACK("身份证反面OCR识别调", "身份证反面OCR识别调","HTTP","开发平台","SEND"),

    /** 契约锁回调 */
    SIGN_CALLBACK("契约锁回调接口", "契约锁回调接口","HTTP","契约锁","RECEIVE"),
    /** 契约锁回调 */
    SIGN_NODE_CALLBACK("契约锁节点回调接口", "契约锁节点回调接口","HTTP","契约锁","RECEIVE"),
    /** 合同归档 */
    CONTRAC_FILING("合同归档接口", "合同归档接口","HTTP","开发平台","SEND"),
    /** 契约锁创建合同 */
    QYS_CONTRACT_CREATE("契约锁创建合同接口", "契约锁创建合同接口","HTTP","契约锁","SEND"),


    /** 阳光诚信平台用户同步接口*/
    SUN_HONESTY_COMPANY_SYN("阳光诚信用户同步接口","阳光诚信用户同步接口","HTTP","阳光诚信自助平台","SEND"),

    /** 开放平台银行支行信息同步接口*/
    BRANCH_BANK_SYN("银行支行信息同步接口","银行支行信息同步接口","HTTP","开放平台","SEND"),


    /* vendor authenticateted  start */
    IMPORT_SCREENING("供应商风险信息接口", "供应商风险信息接口","HTTP","开发平台","SEND"),
    EDM_DRAFT_ORDER_BACK("EDM草稿/订单信息回传接口", "EDM草稿/订单信息回传接口","HTTP","EDM","SEND"),
    RECEIVABLE_FINANCE_SCREENING("应收单财务接口", "应收单财务接口","HTTP","开发平台","SEND"),
    CLAIM_FINANCE_SCREENING("认领结果财务接口", "认领结果财务接口","HTTP","开发平台","SEND"),
    RECEIVE_FINANCE_SCREENING("撤销财务接口", "撤销财务接口","HTTP","开发平台","SEND");
    private final String billType;
    private final String serviceName;
    private final String serviceType;
    private final String targetSys;
    private final String interfaceType;

    /**
     * @param billType
     * @param serviceName
     * @param serviceType
     * @param targetSys
     * @param interfaceType
     */
    ApiInfoEnum(String billType, String serviceName,String serviceType,String targetSys,String interfaceType) {
        this.billType = billType;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.targetSys = targetSys;
        this.interfaceType = interfaceType;
    }

    /**
     * 单据类型
     * @return
     */
    public String getBillType() {
        return billType;
    }

    /**
     * 接口名称
     * @return
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 接口类型(HTTP,GET,WEBSERVICE)
     * @return
     */
    public String getServiceType() {
        return serviceType;
    }
    /**
     * 目标系统
     * @return
     */
    public String getTargetSys() {
        return targetSys;
    }
    /**
     * 发送接收类型  SEND 、RECEIVE
     * @return
     */
    public String getInterfaceType() {
        return this.interfaceType;
    }
}
