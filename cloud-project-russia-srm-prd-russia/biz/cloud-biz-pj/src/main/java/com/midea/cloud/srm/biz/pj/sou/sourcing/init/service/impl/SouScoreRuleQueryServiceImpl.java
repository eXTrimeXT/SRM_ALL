package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.judge.AbstractSouScoreRuleJudge;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouScoreRuleQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouScoreRuleVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源 - 评分规则 - 信息查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/21
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouScoreRuleQueryServiceImpl implements SouScoreRuleQueryService {

    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;

    /**
     * 采购商端: 分页查询评分规则信息
     */
    @Override
    public List<SouScoreRule> listScoreRules(ApiSouScoreRuleQueryDTO queryParam) {
        // 1: 入参格式化
        queryParam.formatParams();
        // 2: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        return souScoreRuleDao.lambdaQuery()
                // 评分规则编码
                .like(queryParam.getScoreRuleNo() != null, SouScoreRule::getScoreRuleNo, queryParam.getScoreRuleNo())
                // 评分规则名称
                .like(queryParam.getScoreRuleName() != null, SouScoreRule::getScoreRuleName, queryParam.getScoreRuleName())
                // 状态
                .eq(queryParam.getStatus() != null, SouScoreRule::getScoreRuleStatus, queryParam.getStatus())
                // 寻源类型
                .eq(queryParam.getSouType() != null, SouScoreRule::getSouType, queryParam.getSouType())
                .list();
    }

    /**
     * 采购商/供应商端: 查询评分规则明细
     * @param scoreRuleId {@link SouScoreRule#getScoreRuleId}
     */
    @Override
    public ApiSouScoreRuleVO getScoreRule(long scoreRuleId) {
        // 1: 校验操作条件/权限
        SouScoreRule rule = SouActiveBeanUtils.getActiveBean(SouTypeEnum.DEFAULT.name(), AbstractSouScoreRuleJudge.class)
                .judgeGetScoreRuleAuth(scoreRuleId);
        // 2: 查询评选详情
        List<SouScoreRuleLine> ruleLineList = souScoreRuleLineDao.lambdaQuery()
                .eq(SouScoreRuleLine::getScoreRuleId, scoreRuleId)
                .list();
        // 3: 组装数据返回
        ApiSouScoreRuleVO vo = SouObjectXUtil.convertTargetObj(rule, ApiSouScoreRuleVO.class);
        vo.setRuleLineList(ruleLineList);
        return vo;
    }

}
