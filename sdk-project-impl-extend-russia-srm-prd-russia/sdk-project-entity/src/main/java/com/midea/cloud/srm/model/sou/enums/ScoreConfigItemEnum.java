package com.midea.cloud.srm.model.sou.enums;

import io.swagger.annotations.ApiModel;

/**
 * 打分项枚举值
 * @author huangbf3
 */
@ApiModel("打分项枚举值")
public enum ScoreConfigItemEnum {
    /**
     * 打分项枚举值
     */
    QUA_REVIEW("QUA_REVIEW", "资格评审"),
    BUS_REVIEW("BUS_REVIEW", "商务评审"),
    TEH_REVIEW("TEH_REVIEW", "技术评审"),
    COM_REVIEW("COM_REVIEW", "综合评审"),
    ;
    private String code;
    private String name;

    ScoreConfigItemEnum(String code, String name) {
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
