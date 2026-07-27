package com.midea.cloud.srm.model.sou.answer.enums;

/**
 * <pre>
 * 备注
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/17 11:47:58
 *  修改内容:
 * </pre>
 */
public enum AnswerStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),

    WAIT_PUBLISH("WAIT_PUBLISH", "待发布"),

    ISSUED("ISSUED", "已发布"),

    ABANDON("ABANDON", "已废弃"),

    COMFIRMED("CONFIRM","已确认");

    private String code;
    private String name;

    AnswerStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
