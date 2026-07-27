package com.midea.cloud.srm.model.pj.sou.score.enums;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评分维度编码
 * PS: 字典 SCC_SOU_SCORE_DIMENSION_CODE
 *
 * @author hesl41
 * @since 2022/10/19 11:41
 */
@Getter
@AllArgsConstructor
public enum SouScoreDimensionCodeEnum {

    /** 产品：技术维度 */
    SOU_TECH(SouTypeEnum.DEFAULT.name());

    private final String souType;

    public static String getDictCode() {
        return "SCC_SOU_SCORE_DIMENSION_CODE";
    }

}
