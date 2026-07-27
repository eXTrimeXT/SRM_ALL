package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

/**
 * 寻源 - 评分规则 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/21
 */
public interface SouScoreRuleEventService {

    /**
     * 采购商端: 编辑/提交评分规则信息
     * @param param
     * @param isTempSave
     * @return
     */
    long/* scoreRuleId */ editScoreRule(ApiSouScoreRuleDTO param, boolean isTempSave);

    /**
     * 采购商端: 生效评分规则
     * @param scoreRuleId
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void validScoreRule(long scoreRuleId, String souType);

    /**
     * 采购商端: 失效评分规则
     * @param scoreRuleId
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void invalidScoreRule(long scoreRuleId, String souType);

    /**
     * 采购商端: 删除评分规则
     * @param scoreRuleId
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void removeScoreRule(long scoreRuleId, String souType);

}
