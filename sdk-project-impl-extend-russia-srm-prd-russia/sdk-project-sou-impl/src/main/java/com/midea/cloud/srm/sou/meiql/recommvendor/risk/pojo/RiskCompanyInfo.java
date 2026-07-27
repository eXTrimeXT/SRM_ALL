package com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo;

import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Data
public class RiskCompanyInfo {

    /**
     * 时间受限
     */
    private String timeLimitFlag;
    /**
     * 重点关注
     */
    private String focusFlag;
    /**
     * 是否单位受限
     */
    private String positionLimitFlag;
    /**
     * 是否品类受限
     */
    private String categoryLimitFlag;
    /**
     * 是否重点监督
     */
    private String keySupervisionFlag;

}
