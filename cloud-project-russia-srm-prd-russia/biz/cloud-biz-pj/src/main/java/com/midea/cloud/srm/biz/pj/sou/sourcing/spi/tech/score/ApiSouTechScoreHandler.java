package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.score;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouGroupDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadDAOImpl;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechScoreDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechScoreLineDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 技术评分服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouTechScoreHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;
    @Autowired
    private SouTechScoreHeadDAOImpl souTechScoreHeadDao;

    /**
     * 供应商报价数据处理
     */
    public SouTechEditPO formatValidateAndConvert(ApiSouTechScoreDTO param) {
        SouTechEditPO po = new SouTechEditPO();

        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouTechDtoContext.setContextHolder(this.buildContextData(param));
        try {
            // 2: 数据过滤及校验处理
            this.formatAndValidateScoreHead(param, param.isTempSave());
            this.formatAndValidateScoreLines(param, param.isTempSave());
            // 3: 数据转换
            this.convertScoreHead(po, param, param.isTempSave());
            this.convertScoreLines(po, param, param.isTempSave());

            return po;
        } finally {
            // 清除业务上下文
            SouTechDtoContext.remove();
        }
    }

    protected SouTechDtoContext buildContextData(ApiSouTechScoreDTO param) {
        // 1: 查询寻源单
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        // 2: 查询评委信息
        SouGroup group = souGroupDao.getById(param.getGroupId());
        // 3: 查询报价单信息
        SouOrder order = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, param.getProjectId())
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .eq(SouOrder::getVendorId, param.getVendorId())
                .one();
        // 4: 查询评分规则
        SouScoreRule scoreRule = souScoreRuleDao.getById(souProject.getScoreTemplateId());
        List<SouScoreRuleLine> scoreRuleLineList = souScoreRuleLineDao.lambdaQuery()
                .eq(SouScoreRuleLine::getScoreRuleId, souProject.getScoreTemplateId())
                .eq(SouScoreRuleLine::getDimension, SouScoreRuleDimensionEnum.TECHNOLOGY)
                .orderByAsc(SouScoreRuleLine::getSortIndex)
                .list();
        return SouTechDtoContext.builder()
                .souProject(souProject)
                .group(group)
                .order(order)
                .scoreRule(scoreRule)
                .techScoreRuleLineList(scoreRuleLineList)
                .build();
    }

    protected void formatAndValidateScoreHead(ApiSouTechScoreDTO param, boolean isTempSave) {
        // 技术评分意见
        param.setTechComments(StringUtils.trimToNull(param.getTechComments()));
        if (param.getTechComments() != null) {
            AssertUtils.isTrue(param.getTechComments().length() <= 300, "技术评分意见的输入长度不能超过300");
        }
    }

    protected void formatAndValidateScoreLines(ApiSouTechScoreDTO param, boolean isTempSave) {
        if (CollectionUtils.isEmpty(param.getTechScoreDetails())) {
            AssertUtils.isTrue(isTempSave, "请进行技术评分");
            return;
        }
        Map<Long/* scoreRuleLineId */, SouScoreRuleLine> scoreRuleLineMap = SouTechDtoContext.getContextHolder()
                .getTechScoreRuleLineList().stream()
                .collect(Collectors.toMap(SouScoreRuleLine::getScoreRuleLineId, Function.identity()));

        int index = 0;
        for (ApiSouTechScoreLineDTO scoreLine : param.getTechScoreDetails()) {
            index++;
            // 1: 评分详情ID(略)
            // 2: 评分规则模板行ID
            AssertUtils.notNull(scoreLine.getScoreRuleLineId(), "缺少scoreRuleLineId参数");
            SouScoreRuleLine scoreRuleLine = scoreRuleLineMap.get(scoreLine.getScoreRuleLineId());
            AssertUtils.notNull(scoreRuleLine, LocaleHandler.getLocaleMsg("评分规则明细")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), scoreLine.getScoreRuleLineId());
            // 3: 评分
            if (scoreLine.getScore() == null) {
                AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("评分列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请打分"), index);
            } else {
                scoreLine.setScore(scoreLine.getScore()
                        .setScale(SouTechDtoContext.getContextHolder().getScoreRule().getScorePrecision(), RoundingMode.HALF_UP)
                        .stripTrailingZeros());
                AssertUtils.isTrue(scoreLine.getScore().compareTo(BigDecimal.ZERO) >= 0, LocaleHandler.getLocaleMsg("评分列表第")+"{0}"+LocaleHandler.getLocaleMsg("行打分不能小于0"),
                        index);
                AssertUtils.isTrue(scoreLine.getScore().compareTo(scoreRuleLine.getTotalScore()) <= 0,
                        "评分列表第{0}行打分不能大于满分值{1}", index, scoreRuleLine.getTotalScore());
            }
        }

        Set<Long> scoreRuleLineIds = param.getTechScoreDetails().stream().map(ApiSouTechScoreLineDTO::getScoreRuleLineId)
                .collect(Collectors.toSet());
        for (SouScoreRuleLine ruleLine : scoreRuleLineMap.values()) {
            if (!scoreRuleLineIds.contains(ruleLine.getScoreRuleLineId())) {
                AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("缺少")+"[{0}]"+LocaleHandler.getLocaleMsg("评分项的评分"), ruleLine.getScoreItem());
            }
        }
    }

    protected void convertScoreHead(SouTechEditPO po, ApiSouTechScoreDTO param, boolean isTempSave) {
        SouTechScoreHead entity = souTechScoreHeadDao.lambdaQuery()
                .eq(SouTechScoreHead::getProjectId, param.getProjectId())
                .eq(SouTechScoreHead::getGroupId, param.getGroupId())
                .eq(SouTechScoreHead::getVendorId, param.getVendorId())
                .one();
        if (entity == null) {
            entity = new SouTechScoreHead();
            // ID
            entity.setTechScoreHeadId(IdGenrator.generate());
            // 寻源单ID
            entity.setProjectId(param.getProjectId());
            // 报价单ID
            entity.setOrderId(SouTechDtoContext.getContextHolder().getOrder().getOrderId());
            // 评委ID
            entity.setGroupId(param.getGroupId());
            // 供应商ID
            entity.setVendorId(param.getVendorId());
        }
        // 评分状态
        entity.setScoreStatus(isTempSave ? SouTechScoreStatusEnum.UNFINISHED : SouTechScoreStatusEnum.FINISHED);
        // 技术总分(暂时略过，后面处理)
        entity.setTotalScore(BigDecimal.ZERO);
        // 技术评分意见
        entity.setTechComments(param.getTechComments());
        // 是否代理评分
        entity.setIsProxy(param.isProxyScore() ? Enable.Y : Enable.N);

        BeanUtils.copyProperties(entity, param);
        po.setScoreHead(entity);
    }

    protected void convertScoreLines(SouTechEditPO po, ApiSouTechScoreDTO param, boolean isTempSave) {
        if (CollectionUtils.isEmpty(param.getTechScoreDetails())) { return; }

        Map<Long/* scoreRuleLineId */, SouScoreRuleLine> scoreRuleLineMap = SouTechDtoContext.getContextHolder()
                .getTechScoreRuleLineList().stream()
                .collect(Collectors.toMap(SouScoreRuleLine::getScoreRuleLineId, Function.identity()));

        List<SouTechScoreLine> scoreLineList = new ArrayList<>(param.getTechScoreDetails().size());
        for (ApiSouTechScoreLineDTO detail : param.getTechScoreDetails()) {
            SouTechScoreLine scoreLine = new SouTechScoreLine();
            scoreLineList.add(scoreLine);
            BeanUtils.copyProperties(detail, scoreLine);

            SouScoreRuleLine ruleLine = scoreRuleLineMap.get(detail.getScoreRuleLineId());
            BeanUtils.copyProperties(ruleLine, scoreLine);

            // ID
            scoreLine.setTechScoreLineId(IdGenrator.generate());
            // 寻源单ID
            scoreLine.setProjectId(param.getProjectId());
            // 技术评分头ID
            scoreLine.setTechScoreHeadId(po.getScoreHead().getTechScoreHeadId());
            // 报价单ID
            scoreLine.setOrderId(po.getScoreHead().getOrderId());
            scoreLine.setGroupId(param.getGroupId());
            scoreLine.setVendorId(param.getVendorId());

            po.getScoreHead().setTotalScore(po.getScoreHead().getTotalScore().add(scoreLine.getScore()
                    .multiply(ruleLine.getScoreWeight().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
        }

        po.setScoreLineList(scoreLineList);
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
