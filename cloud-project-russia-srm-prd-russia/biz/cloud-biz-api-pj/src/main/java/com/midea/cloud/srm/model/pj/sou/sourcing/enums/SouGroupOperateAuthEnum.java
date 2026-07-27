package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 寻源核心 - 工作小组成员操作权限
 * 字典值: SOU_GROUP_OPERATE_AUTH
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Getter
@AllArgsConstructor
public enum SouGroupOperateAuthEnum {

    /**
     * 商务开标
     */
    SOU_BUSINESS_OPEN(com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum.DEFAULT.name()),
    /**
     * 技术开标
     */
    SOU_TECH_OPEN(com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum.DEFAULT.name()),
    /**
     * 报价解密
     */
    SOU_DECRYPT_PRICE(SouTypeEnum.DEFAULT.name());

    private final String souType;

    public static String getEnumDictCode() {
        return "SOU_GROUP_OPERATE_AUTH";
    }

}
