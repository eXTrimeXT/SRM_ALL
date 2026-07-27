package com.midea.cloud.srm.model.contract.enums;

/**
 * @author 100014336 ganyh
 */
public enum ContractAgreementAttachmentType {
    /**
     * 其他协议
     */
    OTHER_AGREEMENT("OTHER_AGREEMENT", "其他协议"),
    /**
     * 合同正文
     */
    CONTRACT_AGREEMENT("CONTRACT_AGREEMENT", "合同正文"),
    /**
     * 双方盖章协议
     */
    SEAL_AGREEMENT("SEAL_AGREEMENT", "双方盖章协议"),
    /**
     * 解除协议
     */
    TERMINATION_AGREEMENT("TERMINATION_AGREEMENT", "解除协议"),
    /**
     * 美国成分分析表
     */
    AMERICAN_COMPOSITION("AMERICAN_COMPOSITION", "美国成分分析表"),
    /**
     * 阳光协议
     */
    SUNSHINE_PROTOCOL("SUNSHINE_PROTOCOL", "阳光协议"),
    /**
     * 保密协议
     */
    SECRECY_PROTOCOL("SECRECY_PROTOCOL", "保密协议"),
    /**
     * 安全协议
     */
    SECURITY_PROTOCOL("SECURITY_PROTOCOL", "安全协议");

    private final String code;

    private final String desc;

    ContractAgreementAttachmentType(String code, String desc) {
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
