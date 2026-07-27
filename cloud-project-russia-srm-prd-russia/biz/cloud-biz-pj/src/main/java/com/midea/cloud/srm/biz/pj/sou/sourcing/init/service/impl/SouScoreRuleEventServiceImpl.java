package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.judge.AbstractSouScoreRuleJudge;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouScoreRuleEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.validator.scorerule.AbstractSouScoreRuleValidator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.validator.scorerule.SouScoreRuleEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 寻源 - 评分规则 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/21
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouScoreRuleEventServiceImpl implements SouScoreRuleEventService {

    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;

    /**
     * 采购商端: 编辑/提交评分规则信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long/* scoreRuleId */ editScoreRule(ApiSouScoreRuleDTO param, boolean isTempSave) {
        param.setSouType(StringUtils.trimToNull(param.getSouType()));
        AssertUtils.notNull(param.getSouType(), "缺少souType参数");
        AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(param.getSouType()), "souType不能为DEFAULT");
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(param.getSouType(), AbstractSouScoreRuleJudge.class)
                .judgeEditScoreRuleAuth(param.getScoreRuleId());
        // 2: 入参校验及数据转换
        SouScoreRuleEditPO po = SouActiveBeanUtils.getActiveBean(param.getSouType(), AbstractSouScoreRuleValidator.class)
                .formatValidateAndConvert(param, isTempSave);
        // 3: 保存数据
        souScoreRuleDao.saveOrUpdate(po.getScoreRule());
        souScoreRuleLineDao.saveOrUpdate(po.getScoreRule().getScoreRuleId(), po.getScoreRuleLineList(), SouScoreRuleLine::getScoreRuleId);

        return po.getScoreRule().getScoreRuleId();
    }

    /**
     * 生效评分规则
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void validScoreRule(long scoreRuleId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, AbstractSouScoreRuleJudge.class)
                .judgeValidScoreRuleAuth(scoreRuleId);
        // 2: 生效状态
        souScoreRuleDao.lambdaUpdate()
                .set(SouScoreRule::getScoreRuleStatus, SouScoreRuleStatusEnum.VALID)
                .eq(SouScoreRule::getScoreRuleId, scoreRuleId)
                .update();
    }

    /**
     * 失效评分规则
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void invalidScoreRule(long scoreRuleId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, AbstractSouScoreRuleJudge.class).judgeInvalidScoreRuleAuth(scoreRuleId);
        // 2: 失效状态
        souScoreRuleDao.lambdaUpdate()
                .set(SouScoreRule::getScoreRuleStatus, SouScoreRuleStatusEnum.INVALID)
                .eq(SouScoreRule::getScoreRuleId, scoreRuleId)
                .in(SouScoreRule::getScoreRuleStatus, SouScoreRuleStatusEnum.VALID, SouScoreRuleStatusEnum.INVALID)
                .update();
    }

    /**
     * 删除评分规则
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeScoreRule(long scoreRuleId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, AbstractSouScoreRuleJudge.class).judgeRemoveScoreRuleAuth(scoreRuleId);
        // 2: 删除数据
        souScoreRuleDao.lambdaUpdate()
                .eq(SouScoreRule::getScoreRuleId, scoreRuleId)
                .eq(SouScoreRule::getScoreRuleStatus, SouScoreRuleStatusEnum.DRAFT)
                .remove();
    }

}
