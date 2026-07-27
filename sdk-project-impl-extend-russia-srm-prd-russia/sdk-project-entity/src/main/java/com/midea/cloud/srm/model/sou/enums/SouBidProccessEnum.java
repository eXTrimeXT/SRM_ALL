package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidProccessEnum {
    /**
     * 标准招标
     */
    STANDARD("STANDARD", "标准招标"),
    SIMPLE("SIMPLE", "简易招标"),
    COMPETE("COMPETE", "竞争性谈判"),
    INQUIRY("INQUIRY", "询比价招标")
    ;
    private String code;
    private String name;

    SouBidProccessEnum(String code, String name) {
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
