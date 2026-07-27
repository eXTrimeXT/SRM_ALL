package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidAttachmentTypeEnum {
    /**
     * 申请资料
     */
    APPLY("APPLY", "申请资料"),
    BID("BID", "招标文件"),
    VENDOR("VENDOR", "推荐供应商"),
    TALK("TALK", "谈判资料"),
    ARCHIVE("ARCHIVE", "项目归档")
            ;
    private String code;
    private String name;

    SouBidAttachmentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
