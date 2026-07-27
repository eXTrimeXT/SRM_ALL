package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouScoreRuleVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;

import java.util.List;

/**
 * 寻源 - 评分规则 - 信息查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/21
 */
public interface SouScoreRuleQueryService {

    /**
     * 采购商端: 分页查询评分规则信息
     * @param queryParam
     * @return
     */
    List<SouScoreRule> listScoreRules(ApiSouScoreRuleQueryDTO queryParam);

    /**
     * 采购商/供应商端: 查询评分规则明细
     * @param scoreRuleId {@link SouScoreRule#getScoreRuleId}
     * @return
     */
    ApiSouScoreRuleVO getScoreRule(long scoreRuleId);

}
