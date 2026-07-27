package com.midea.cloud.srm.model.contract.enums;

/**
 * @author 100014336 ganyh19
 */
public enum ContractOperationType {
    /**
     * 暂存
     */
    SAVE_TEMP("SAVE_TEMP","暂存"),
    /**
     * 供应商驳回
     */
    VENDOR_REJECT("VENDOR_REJECT","供应商驳回"),
    /**
     * 供应商通过
     */
    VENDOR_PASS("VENDOR_PASS","供应商通过"),
    /**
     * 提交审批流
     */
    SUBMIT("SUBMIT","提交审批流"),
    /**
     * 审批流通过
     */
    PASS("PASS","审批流通过"),
    /**
     * 审批流驳回
     */
    REJECT("REJECT","审批流驳回"),
    /**
     * 审批流撤回
     */
    WITHDRAW("WITHDRAW","审批流撤回"),
    /**
     * 审批流废弃
     */
    DESTORY("DESTORY","审批流废弃"),
    /**
     * 发起电子签章
     */
    BEGIN_SIGN("BEGIN_SIGN","发起电子签章"),
    /**
     * 电子签章完成
     */
    SIGN_FINISHED("SIGN_FINISHED","电子签章完成");



    private String code;

    private String desc;

    ContractOperationType(String code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
