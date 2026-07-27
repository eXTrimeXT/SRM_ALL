package com.midea.cloud.srm.perf.scoreproject.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.perf.indicators.IndicatorsDimensionEnum;
import com.midea.cloud.common.enums.perf.indicators.IndicatorsEvaluetionEnum;
import com.midea.cloud.common.enums.perf.scoreproject.ScoreItemsProjectStatusEnum;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.perf.level.entity.PerfLevel;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckDTO;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckQueryDTO;
import com.midea.cloud.srm.model.perf.ordercheck.entity.PerfScoreItemsOrderCheckDetail;
import com.midea.cloud.srm.model.perf.orderscoreman.entity.PjOrderScoreManScoringV1;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderCheckDetailStatusEnum;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderCheckStatusEnum;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderScoreManScoreStatusEnum;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsOrderCheck;
import com.midea.cloud.srm.model.perf.scoring.PerfIndDimScoreDetail;
import com.midea.cloud.srm.model.perf.scoring.PerfIndicatorDimScore;
import com.midea.cloud.srm.model.perf.scoring.PerfOverallScore;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateHeader;
import com.midea.cloud.srm.perf.level.service.IPjPerfLevelService;
import com.midea.cloud.srm.perf.ordercheck.service.IPerfScoreItemsOrderCheckDetailService;
import com.midea.cloud.srm.perf.orderscoreman.service.IPjOrderScoreManScoringV1Service;
import com.midea.cloud.srm.perf.scoreproject.mapper.PerfScoreItemsOrderCheckMapper;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsOrderCheckService;
import com.midea.cloud.srm.perf.scoring.constants.VendorFeedbackStatus;
import com.midea.cloud.srm.perf.scoring.service.IPerfIndDimScoreDetailService;
import com.midea.cloud.srm.perf.scoring.service.IPerfIndicatorDimScoreService;
import com.midea.cloud.srm.perf.scoring.service.IPerfOverallScoreService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateHeaderService;
import com.mideacloud.common.id.IdGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <pre>
 *  绩效评分项目评分人表 服务实现类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:36
 *  修改内容:
 * </pre>
 */
@Service
public class PerfScoreItemsOrderCheckServiceImpl extends BaseServiceImpl<PerfScoreItemsOrderCheckMapper, PerfScoreItemsOrderCheck> implements IPerfScoreItemsOrderCheckService {

    @Autowired
    private IPerfScoreItemsOrderCheckDetailService checkDetailService;

    @Autowired
    private IPjOrderScoreManScoringV1Service pjOrderScoreManScoringV1Service;

    @Autowired
    private IPjPerfLevelService pjPerfLevelService;

    @Autowired
    private IPerfOverallScoreService perfOverallScoreService;

    @Autowired
    private IPerfIndicatorDimScoreService perfIndicatorDimScoreService;

    @Autowired
    private IPerfIndDimScoreDetailService perfIndDimScoreDetailService;

    @Autowired
    private IPerfTemplateHeaderService perfTemplateHeaderService;

    @Override
    public void batchSaveOrderCheckList(List<PerfScoreItemsOrderCheckDTO> perfScoreItemsOrderCheckDTOList) {
        List<PerfScoreItemsOrderCheck> perfScoreItemsOrderCheckList = BeanCopyUtil.copyListProperties(perfScoreItemsOrderCheckDTOList, PerfScoreItemsOrderCheck.class);
        List<PerfScoreItemsOrderCheckDetail> detailList = new ArrayList<>();
        for (PerfScoreItemsOrderCheckDTO perfScoreItemsOrderCheckDTO : perfScoreItemsOrderCheckDTOList) {
            detailList.addAll(perfScoreItemsOrderCheckDTO.getDetailList());
        }
        this.saveBatch(perfScoreItemsOrderCheckList);
        checkDetailService.saveBatch(detailList);
    }

    @Override
    public List<PerfScoreItemsOrderCheck> listPage(PerfScoreItemsOrderCheckQueryDTO queryDTO) {
        LambdaQueryWrapper<PerfScoreItemsOrderCheck> wrapper = Wrappers.lambdaQuery(PerfScoreItemsOrderCheck.class);
        if (null != queryDTO) {
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getProjectName()), PerfScoreItemsOrderCheck::getProjectName, queryDTO.getProjectName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getOrganizationName()), PerfScoreItemsOrderCheck::getOrganizationName, queryDTO.getOrganizationName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getCompanyName()), PerfScoreItemsOrderCheck::getCompanyName, queryDTO.getCompanyName());
            wrapper.eq(queryDTO.getCreatedId() != null, PerfScoreItemsOrderCheck::getCreatedId, queryDTO.getCreatedId());
            wrapper.eq(StringUtils.isNotEmpty(queryDTO.getStatus()), PerfScoreItemsOrderCheck::getStatus, queryDTO.getStatus());
            if (queryDTO.getCreateDateStart() != null && queryDTO.getCreateDateEnd() != null) {
                wrapper.between(PerfScoreItemsOrderCheck::getCreationDate, queryDTO.getCreateDateStart(), queryDTO.getCreateDateEnd());
            }
        }
        wrapper.orderByDesc(PerfScoreItemsOrderCheck::getLastUpdateDate);
        return this.list(wrapper);
    }

    @Override
    public PerfScoreItemsOrderCheckDTO getDetailById(Long orderCheckId) {
        PerfScoreItemsOrderCheckDTO result = new PerfScoreItemsOrderCheckDTO();
        PerfScoreItemsOrderCheck orderCheck = this.getById(orderCheckId);
        BeanCopyUtil.copyProperties(result, orderCheck);
        List<PerfScoreItemsOrderCheckDetail> list = checkDetailService.list(Wrappers.lambdaQuery(PerfScoreItemsOrderCheckDetail.class)
                .eq(PerfScoreItemsOrderCheckDetail::getOrderCheckId, orderCheckId));
        result.setDetailList(list);
        return result;
    }

    @Override
    public void reject(PerfScoreItemsOrderCheckDTO dto) {
        List<PerfScoreItemsOrderCheckDetail> detailList = dto.getDetailList();
        Assert.isTrue(CollectionUtils.isNotEmpty(detailList), "请勾选要驳回的记录");
        //驳回逻辑
        // 1.复核明细修改状态
        detailList.stream().forEach(item -> item.setStatus(OrderCheckDetailStatusEnum.REJECT.name()));
        checkDetailService.updateBatchById(detailList);
        // 2.绩效评分根据业务主键驳回状态,驳回原因
        String rejectRemark = dto.getRejectRemark();
        //2.1 根据项目+供应商获取评分信息
        List<PjOrderScoreManScoringV1> list = pjOrderScoreManScoringV1Service.list(Wrappers.lambdaQuery(PjOrderScoreManScoringV1.class)
                .eq(PjOrderScoreManScoringV1::getScoreItemsId, dto.getScoreItemsId())
                .eq(PjOrderScoreManScoringV1::getCompanyId, dto.getCompanyId()));

        Map<String, PerfScoreItemsOrderCheckDetail> rejectMap = detailList.stream().collect(Collectors.toMap(item -> item.getScoreUserName() + "-" + item.getCategoryId(), Function.identity(), (k1, k2) -> k2));
        // 2.2获取需要驳回的评分明细
        List<PjOrderScoreManScoringV1> rejectList = list.stream().filter(item -> rejectMap.containsKey(item.getScoreUserName() + "-" + item.getCategoryId())).collect(Collectors.toList());
        rejectList.stream().forEach(item -> {
            item.setIfScored(YesOrNo.NO.getValue());
            item.setScoringStatus(OrderScoreManScoreStatusEnum.REJECT.name());
            item.setRejectRemark(rejectRemark);
        });
        // 2.3 更新
        pjOrderScoreManScoringV1Service.updateBatchById(rejectList);
    }

    @Override
    public void calcScore(Long checkId) {
        // 1.校验全部明细都是已计算得分
        // 2.按项目+供应商维度先计算,全部按100来减
        PerfScoreItemsOrderCheck orderCheck = this.getById(checkId);
        // 校验,如果计算了得分则不能再计算
        Assert.isTrue(!OrderCheckStatusEnum.CALCULATED_SCORE.name().equals(orderCheck.getStatus()),"已计算得分,不可重复计算");
        List<PerfScoreItemsOrderCheckDetail> checkDetailList = checkDetailService.list(Wrappers.lambdaQuery(new PerfScoreItemsOrderCheckDetail().setOrderCheckId(checkId)));
        Long scoreItemsId = orderCheck.getScoreItemsId();
        List<PjOrderScoreManScoringV1> manScoringV1List = pjOrderScoreManScoringV1Service.list(Wrappers.lambdaQuery(PjOrderScoreManScoringV1.class)
                .eq(PjOrderScoreManScoringV1::getScoreItemsId, scoreItemsId)
                .eq(PjOrderScoreManScoringV1::getCompanyId, orderCheck.getCompanyId()));
        // 3.1 按品类,维度,指标分组计算,完后维度得分再按品类,维度汇总计算,总得分按品类汇总
        List<PerfIndDimScoreDetail> dimScoreDetailList = calcDimScoreDetailList(manScoringV1List);
        // 3.2 完后维度得分再按品类,维度汇总计算
        List<PerfIndicatorDimScore> dimScoreList = calcDimScoreList(dimScoreDetailList);
        // 3.3 总得分按品类汇总
        List<PerfOverallScore> overallScoreList = calcOverallScoreList(dimScoreList);
        perfOverallScoreService.saveBatch(overallScoreList);
        perfIndicatorDimScoreService.saveBatch(dimScoreList);
        perfIndDimScoreDetailService.saveBatch(dimScoreDetailList);

        // 3.4 每次计算都按项目维度排名
        calcRank(scoreItemsId);
        // 3.5 更新复核头,明细,评分人状态为已计算得分 ,按项目+供应商维度更新
        updateStatus(orderCheck,checkDetailList,manScoringV1List);
    }

    private void calcRank(Long scoreItemsId){
        List<PerfOverallScore> overallScoreTotalList = perfOverallScoreService.list(Wrappers.lambdaQuery(PerfOverallScore.class)
                .eq(PerfOverallScore::getScoreItemsId, scoreItemsId));
        Map<String, List<PerfOverallScore>> groupMap = overallScoreTotalList.stream().collect(Collectors.groupingBy(item -> item.getScoreItemsId() + "-" + item.getCategoryId()));
        for (String key : groupMap.keySet()) {
            List<PerfOverallScore> tempList = groupMap.get(key);
            // 按照score字段进行排序
            tempList.sort(Comparator.comparing(PerfOverallScore::getScore).reversed());
            // 使用IntStream生成排名
            IntStream.rangeClosed(1, tempList.size())
                    .forEach(i -> tempList.get(i - 1).setRank(Long.valueOf(i)));
            // 设置indicatorCount属性
            tempList.forEach(score -> score.setIndicatorCount(tempList.size()));
        }
        perfOverallScoreService.updateBatchById(overallScoreTotalList);
    }


    /**
     * 更新状态
     * @param orderCheck
     * @param checkDetailList
     * @param manScoringV1List
     */
    private void  updateStatus(PerfScoreItemsOrderCheck orderCheck,List<PerfScoreItemsOrderCheckDetail> checkDetailList,List<PjOrderScoreManScoringV1> manScoringV1List){
        orderCheck.setStatus(OrderCheckStatusEnum.CALCULATED_SCORE.name());
        this.updateById(orderCheck);
        // 更新明细
        checkDetailList.stream().forEach(item -> item.setStatus(OrderCheckDetailStatusEnum.CALCULATED_SCORE.name()));
        checkDetailService.updateBatchById(checkDetailList);
        // 更新评分明细
        manScoringV1List.stream().forEach(item -> {
            item.setScoringStatus(OrderScoreManScoreStatusEnum.CALCULATED_SCORE.name());
            item.setIfEndScored(YesOrNo.YES.getValue());
        });
        pjOrderScoreManScoringV1Service.updateBatchById(manScoringV1List);
    }

    @Override
    public PerfLevel getLevelByScore(BigDecimal overallScoreDecimal, List<PerfLevel> levelList) {
        for (PerfLevel perfLevel : levelList) {
            if (overallScoreDecimal.compareTo(perfLevel.getScoreStart()) >= 0
                    && overallScoreDecimal.compareTo(perfLevel.getScoreEnd()) < 0) {
                return perfLevel;
            }
        }
        return new PerfLevel();
    }

    private List<PerfOverallScore> calcOverallScoreList(List<PerfIndicatorDimScore> dimScoreList) {
        Long templateHeadId = dimScoreList.get(0).getTemplateHeadId();
        PerfTemplateHeader templateHeader = perfTemplateHeaderService.getById(templateHeadId);

        // 等级规则获取
        List<PerfLevel> levelList = pjPerfLevelService.list(Wrappers.lambdaQuery(new PerfLevel().setStatus(Enable.Y)));

        List<PerfOverallScore> overallScoreList = new ArrayList<>();
        Map<String, List<PerfIndicatorDimScore>> dimScoreGroupMap = dimScoreList.stream().collect(Collectors.groupingBy(item -> item.getScoreItemsId()
                + "-" + item.getCompanyId()
                + "-" + item.getCategoryId()));
        for (String key : dimScoreGroupMap.keySet()) {
            List<PerfIndicatorDimScore> perfIndicatorDimScores = dimScoreGroupMap.get(key);
            PerfIndicatorDimScore dimScore = perfIndicatorDimScores.get(0);
            BigDecimal overallScoreDecimal = new BigDecimal("100");
            BigDecimal totalScore = perfIndicatorDimScores.stream().map(PerfIndicatorDimScore::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, BigDecimal> dimScoreMap = perfIndicatorDimScores.stream().collect(Collectors.toMap(PerfIndicatorDimScore::getIndicatorDimensionType, PerfIndicatorDimScore::getScore, (k1, k2) -> k2));
            overallScoreDecimal = overallScoreDecimal.add(totalScore);
            PerfLevel level = getLevelByScore(overallScoreDecimal, levelList);
            PerfOverallScore overallScore = new PerfOverallScore();
            long id = IdGenerator.generate();
            perfIndicatorDimScores.stream().forEach(item -> item.setOverallScoreId(id));
            overallScore.setOverallScoreId(id);
            overallScore.setScoreItemsId(dimScore.getScoreItemsId());
            overallScore.setTemplateHeadId(dimScore.getTemplateHeadId());
            overallScore.setTemplateName(templateHeader.getTemplateName());
            overallScore.setProjectName(dimScore.getProjectName());
            overallScore.setStatus(ScoreItemsProjectStatusEnum.RESULT_PUBLISHED.getValue());
            overallScore.setEvaluationPeriod(dimScore.getEvaluationPeriod());
            overallScore.setPerStartMonth(dimScore.getPerStartMonth());
            overallScore.setPerEndMonth(dimScore.getPerEndMonth());
            overallScore.setOrganizationId(dimScore.getOrganizationId());
            overallScore.setFullPathId(dimScore.getFullPathId());
            overallScore.setOrganizationName(dimScore.getOrganizationName());
            overallScore.setCategoryId(dimScore.getCategoryId());
            overallScore.setCategoryCode(dimScore.getCategoryCode());
            overallScore.setCategoryName(dimScore.getCategoryName());
            overallScore.setCategoryFullName(dimScore.getCategoryFullName());
            overallScore.setCompanyId(dimScore.getCompanyId());
            overallScore.setCompanyCode(dimScore.getCompanyCode());
            overallScore.setCompanyName(dimScore.getCompanyName());
            overallScore.setCompanyEnName(dimScore.getCompanyEnName());
            overallScore.setIndicatorType(dimScore.getIndicatorType());
            overallScore.setScore(overallScoreDecimal);
            overallScore.setLevelId(level.getLevelId());
            overallScore.setLevelName(level.getLevelName());
            overallScore.setVendorFeedbackStatus(VendorFeedbackStatus.NOT_CONFIRMED.getValue());
            overallScore.setScoreAttribute1(String.valueOf(dimScoreMap.get(IndicatorsDimensionEnum.QUALITY.name())));
            overallScore.setScoreAttribute2(String.valueOf(dimScoreMap.get(IndicatorsDimensionEnum.COST.name())));
            overallScore.setScoreAttribute3(String.valueOf(dimScoreMap.get(IndicatorsDimensionEnum.DELIVER.name())));
            overallScore.setScoreAttribute4(String.valueOf(dimScoreMap.get(IndicatorsDimensionEnum.SERVICE.name())));
            overallScore.setScoreAttribute5(String.valueOf(dimScoreMap.get(IndicatorsDimensionEnum.TECHNOLOGY.name())));
            overallScoreList.add(overallScore);
        }
        return overallScoreList;
    }

    private List<PerfIndicatorDimScore> calcDimScoreList(List<PerfIndDimScoreDetail> dimScoreDetailList) {
        List<PerfIndicatorDimScore> dimScoreList = new ArrayList<>();
        Map<String, List<PerfIndDimScoreDetail>> dimScoreGroupMap = dimScoreDetailList.stream().collect(Collectors.groupingBy(item -> item.getScoreItemsId()
                + "-" + item.getCompanyId()
                + "-" + item.getCategoryId()
                + "-" + item.getIndicatorDimensionType()));
        for (String key : dimScoreGroupMap.keySet()) {
            List<PerfIndDimScoreDetail> tempDetailList = dimScoreGroupMap.get(key);
            Map<String, List<PerfIndDimScoreDetail>> evaluationGroup = tempDetailList.stream().collect(Collectors.groupingBy(PerfIndDimScoreDetail::getEvaluation));
            BigDecimal dimScoreDecimal = new BigDecimal("0");
            for (String evaluationKey : evaluationGroup.keySet()) {
                List<PerfIndDimScoreDetail> tempList = evaluationGroup.get(evaluationKey);
                BigDecimal totalScore = tempList.stream().map(PerfIndDimScoreDetail::getScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (IndicatorsEvaluetionEnum.EXTRA_MANUAL.getValue().equals(evaluationKey)) {
                    // 加分
                    dimScoreDecimal = dimScoreDecimal.add(totalScore);
                } else if (IndicatorsEvaluetionEnum.DEDUCTION_MANUAL.getValue().equals(evaluationKey)) {
                    // 减分
                    dimScoreDecimal = dimScoreDecimal.subtract(totalScore);
                }
            }
            PerfIndicatorDimScore dimScore = new PerfIndicatorDimScore();
            long dimScoreId = IdGenerator.generate();
            tempDetailList.stream().forEach(item -> item.setIndicatorDimScoreId(dimScoreId));
            PerfIndDimScoreDetail perfIndDimScoreDetail = tempDetailList.get(0);
            dimScore.setIndicatorDimScoreId(dimScoreId);
            dimScore.setScoreItemsId(perfIndDimScoreDetail.getScoreItemsId());
            dimScore.setTemplateHeadId(perfIndDimScoreDetail.getTemplateHeadId());
            dimScore.setProjectName(perfIndDimScoreDetail.getProjectName());
            dimScore.setEvaluationPeriod(perfIndDimScoreDetail.getEvaluationPeriod());
            dimScore.setPerStartMonth(perfIndDimScoreDetail.getPerStartMonth());
            dimScore.setPerEndMonth(perfIndDimScoreDetail.getPerEndMonth());
            dimScore.setOrganizationId(perfIndDimScoreDetail.getOrganizationId());
            dimScore.setFullPathId(perfIndDimScoreDetail.getFullPathId());
            dimScore.setOrganizationName(perfIndDimScoreDetail.getOrganizationName());
            dimScore.setCategoryId(perfIndDimScoreDetail.getCategoryId());
            dimScore.setCategoryCode(perfIndDimScoreDetail.getCategoryCode());
            dimScore.setCategoryName(perfIndDimScoreDetail.getCategoryName());
            dimScore.setCategoryFullName(perfIndDimScoreDetail.getCategoryFullName());
            dimScore.setCompanyId(perfIndDimScoreDetail.getCompanyId());
            dimScore.setCompanyCode(perfIndDimScoreDetail.getCompanyCode());
            dimScore.setCompanyName(perfIndDimScoreDetail.getCompanyName());
            dimScore.setCompanyEnName(perfIndDimScoreDetail.getCompanyEnName());
            dimScore.setDimWeightId(perfIndDimScoreDetail.getDimWeightId());
            dimScore.setIndicatorType(perfIndDimScoreDetail.getIndicatorType());
            dimScore.setIndicatorDimensionType(perfIndDimScoreDetail.getIndicatorDimensionType());
            dimScore.setIndicatorDimensionWeight(perfIndDimScoreDetail.getIndicatorDimensionWeight());
            dimScore.setScore(dimScoreDecimal);
            dimScoreList.add(dimScore);
        }
        return dimScoreList;
    }

    private List<PerfIndDimScoreDetail> calcDimScoreDetailList(List<PjOrderScoreManScoringV1> manScoringV1List) {
        Map<String, List<PjOrderScoreManScoringV1>> dimScoreDetailGroupMap = manScoringV1List.stream().collect(Collectors.groupingBy(PjOrderScoreManScoringV1::calcDimScoreDetailGroupStr));
        List<PerfIndDimScoreDetail> dimScoreDetailList = new ArrayList<>();
        for (String key : dimScoreDetailGroupMap.keySet()) {
            List<PjOrderScoreManScoringV1> pjOrderScoreManScoringV1s = dimScoreDetailGroupMap.get(key);
            PjOrderScoreManScoringV1 manScoringV1 = pjOrderScoreManScoringV1s.get(0);
            BigDecimal averaegScore = pjOrderScoreManScoringV1s.stream().map(PjOrderScoreManScoringV1::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(pjOrderScoreManScoringV1s.size()), 2, RoundingMode.HALF_UP);
            PerfIndDimScoreDetail perfIndDimScoreDetail = new PerfIndDimScoreDetail();
            perfIndDimScoreDetail.setIndDimScoreDetailId(IdGenerator.generate());
            perfIndDimScoreDetail.setScoreManScoringId(IdGenerator.generate());
            perfIndDimScoreDetail.setScoreItemsId(manScoringV1.getScoreItemsId());
            perfIndDimScoreDetail.setTemplateHeadId(manScoringV1.getTemplateHeadId());
            perfIndDimScoreDetail.setProjectName(manScoringV1.getProjectName());
            perfIndDimScoreDetail.setEvaluationPeriod(manScoringV1.getEvaluationPeriod());
            perfIndDimScoreDetail.setPerStartMonth(manScoringV1.getPerStartMonth());
            perfIndDimScoreDetail.setPerEndMonth(manScoringV1.getPerEndMonth());
            perfIndDimScoreDetail.setOrganizationId(manScoringV1.getOrganizationId());
            perfIndDimScoreDetail.setFullPathId(manScoringV1.getFullPathId());
            perfIndDimScoreDetail.setOrganizationName(manScoringV1.getOrganizationName());
            perfIndDimScoreDetail.setCategoryId(manScoringV1.getCategoryId());
            perfIndDimScoreDetail.setCategoryCode(manScoringV1.getCategoryCode());
            perfIndDimScoreDetail.setCategoryName(manScoringV1.getCategoryName());
            perfIndDimScoreDetail.setCategoryFullName(manScoringV1.getCategoryFullName());
            perfIndDimScoreDetail.setCompanyId(manScoringV1.getCompanyId());
            perfIndDimScoreDetail.setCompanyCode(manScoringV1.getCompanyCode());
            perfIndDimScoreDetail.setCompanyName(manScoringV1.getCompanyName());
            perfIndDimScoreDetail.setCompanyEnName(manScoringV1.getCompanyEnName());
            perfIndDimScoreDetail.setDimWeightId(manScoringV1.getDimWeightId());
            perfIndDimScoreDetail.setIndicatorType(manScoringV1.getIndicatorType());
            perfIndDimScoreDetail.setIndicatorDimensionType(manScoringV1.getIndicatorDimensionType());
            perfIndDimScoreDetail.setIndicatorDimensionWeight(manScoringV1.getIndicatorDimensionWeight());
            perfIndDimScoreDetail.setTemplateLineId(manScoringV1.getTemplateLineId());
            perfIndDimScoreDetail.setIndicatorName(manScoringV1.getIndicatorName());
            perfIndDimScoreDetail.setDimensionWeight(manScoringV1.getDimensionWeight());
            perfIndDimScoreDetail.setScore(averaegScore);
            perfIndDimScoreDetail.setEvaluation(manScoringV1.getEvaluation());
            dimScoreDetailList.add(perfIndDimScoreDetail);
        }
        return dimScoreDetailList;
    }
}
