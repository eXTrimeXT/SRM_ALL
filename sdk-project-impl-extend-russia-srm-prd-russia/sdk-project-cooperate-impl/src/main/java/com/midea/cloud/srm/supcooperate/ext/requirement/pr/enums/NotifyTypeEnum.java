package com.midea.cloud.srm.supcooperate.ext.requirement.pr.enums;

import lombok.Getter;

/**
 * 备注
 * @author huangbf3
 */
@Getter
public enum NotifyTypeEnum {
    /**
     * 备注
     */

    PURCHASER("PURCHASER", "本单位采购员"), ASSIGN("ASSIGN", "短信触发人员");

    private NotifyTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    private String value;
    private String name;

    }
