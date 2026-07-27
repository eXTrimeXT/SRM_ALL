package com.midea.cloud.srm.sou.report.bid.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.NpmSouBidProjectNoUtils;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.perf.enums.PerformanceCodeEnum;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerStatusEnum;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectExtendDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportQueryDto;
import com.midea.cloud.srm.model.sou.report.souschedules.entity.SccNpmSouSchedule;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.report.bid.mapper.ExtReportBidScheduleMapper;
import com.midea.cloud.srm.sou.report.bid.schedule.mapper.SccNpmSouScheduleMapper;
import com.midea.cloud.srm.sou.report.bid.schedule.service.ISccNpmSouScheduleService;
import com.midea.cloud.srm.sou.report.utils.ReportUtils;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/03/12/ $
 * @Description: 招标项目进度报表接口实现类
 */
@Slf4j
@Service
public class SccNpmSouScheduleServiceImpl extends ServiceImpl<SccNpmSouScheduleMapper, SccNpmSouSchedule> implements ISccNpmSouScheduleService {

    @Autowired
    private ExtReportBidScheduleMapper extReportBidScheduleMapper;

    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouOrderService iExtSouOrderService;

    @Autowired
    private IExtNpmSouOrderService iExtNpmSouOrderService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouRoundService roundService;

    private static final String MAX_BID_DATA_SUBMIT_KEY = "maxBidDataSubmitKey";

    private static final String MAX_BID_DATA_SUBMIT_DATA = "maxBidDataSubmitData";

    private static final String TECH_VENDOR_LIST = "techVendorList";

    private static final String BUS_VENDOR_LIST = "busVendorList";

    private static final String REQUIREMENT_HEAD_NUM_RECOM = "REQUIREMENT_HEAD_NUM_RECOM";

    @Override
    public PageInfo<ExtSouProject> listPageAsChangeRecently(ScheduleReportQueryDto queryDto) {
        PageUtil.startPage(ObjectUtils.defaultIfNull(queryDto.getPageNum(), 1), ObjectUtils.defaultIfNull(queryDto.getPageSize(), 15));
        List<ExtSouProject> dataList = extReportBidScheduleMapper.listProjectAsChangeRecently(queryDto);
        return new PageInfo<>(dataList);
    }

    @Override
    public List<SccNpmSouSchedule> generateScheduleReportData(List<ExtSouProject> projectList) {
        if(CollectionUtils.isEmpty(projectList)) {
            return null;
        }
        List<ExtSouProject> souProjectList = projectService.listByIds(projectList.stream().map(ExtSouProject::getProjectId).collect(Collectors.toList()));

        List<SccNpmSouSchedule> scheduleList = convertSouSchedule(souProjectList);

        fillScheduleData(scheduleList, souProjectList);

        return scheduleList;
    }

    /**
     * 填充报表数据
     * @param scheduleList
     * @param souProjectList
     */
    private void fillScheduleData(List<SccNpmSouSchedule> scheduleList, List<ExtSouProject> souProjectList) {
        if(CollectionUtils.isEmpty(scheduleList)) {
            return;
        }

        /** 招标项目 key value */
        Map<Long, ExtSouProject> projectMap = souProjectList.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(), (k1,k2) -> k2));

        List<Long> projectIdList = new ArrayList<>(projectMap.keySet());

        StopWatch stopWatch = new StopWatch("处理招标项目进度报表数据");

        stopWatch.start("查询采购品类信息");
        Map<Long, PurchaseCategory> purchaseCategoryMap = cachePurchaseCategory(souProjectList);
        stopWatch.stop();

        stopWatch.start("查询采购申请相关信息");
        Map<String, Object> requirementCacheMap = cacheRequirementInfo(projectIdList);
        stopWatch.stop();

        stopWatch.start("查询招标工作小组");
        Map<String, ExtSouGroup> groupMap = cacheExtSouGroup(projectIdList);
        stopWatch.stop();

        stopWatch.start("查询投标供应商和招标计划和实际");
        Map<String, Object> bidOrderMap = cachePlanAsBidOrder(projectIdList);
        stopWatch.stop();

        stopWatch.start("查询供应商推荐信息");
        Map<String, Object> recommvendorMap = cacheRecommVendor(projectIdList, requirementCacheMap, bidOrderMap);
        stopWatch.stop();

        stopWatch.start("查询澄清项目次数");
        Map<String, Object> answerMap = cacheAnswer(projectIdList, groupMap);
        stopWatch.stop();

        stopWatch.start("查询绩效得分和结果");
        Map<String, Object> projectScoreMap = cachePerfProjectScore(souProjectList);
        stopWatch.stop();

        stopWatch.start("数据处理");
        scheduleList.stream().forEach(sccNpmSouSchedule -> {
            ExtSouProject souProject = projectMap.get(sccNpmSouSchedule.getProjectId());
            /** 一级品类名称*/
            fillClassification(sccNpmSouSchedule, souProject, purchaseCategoryMap);

            /** 招标负责人	供应商负责人	评标组长	技术负责人*/
            fillPrincipal(sccNpmSouSchedule, souProject, requirementCacheMap, groupMap);

            /** 申请资料计划递交时间	申请资料审核通过时间	公示截止日期	计划出表时间	实际出表时间 */
            fillDataSubmit(sccNpmSouSchedule, requirementCacheMap);

            /** 招标计划和实际 发标环节		收标环节		技术标评完时间		汇总上报环节		定标环节		出具中标通知 */
            fillDataBidPlan(sccNpmSouSchedule, bidOrderMap);

            /** 发标单位数量	推荐单位投标数量	追加单位数量	追加单位投标数量	新供应商数量	开发新单位数量	总发标单位数量	总投标单位数量*/
            fillRecommvendor(sccNpmSouSchedule, recommvendorMap);

            /** 总计划周期	总实际周期	供应商推荐延期天数	发标延期天数	发标延期占比率	收标延期天数	收标延期占比率	汇总上报延期天数	汇总上报延期占比率	中标延期天数	中标延期占比率	资料递交延期天数	评标延期天数	评标延期占比率	定标延期天数	定标延期占比率*/
            fillDataAsCalculate(sccNpmSouSchedule, souProject);

            /** 统计澄清次数 */
            fillAnswer(sccNpmSouSchedule, answerMap);

            /** 履约分数	履约结果 */
            fillProjectScore(sccNpmSouSchedule, souProject, projectScoreMap);

        });
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    /**
     * 履约分数	履约结果
     * @param data
     * @param souProject
     * @param projectScoreMap
     */
    private void fillProjectScore(SccNpmSouSchedule data, ExtSouProject souProject,  Map<String, Object> projectScoreMap) {
        String extProjectNo = souProject.getExtProjectNo();
        data.setHonourScore((BigDecimal) projectScoreMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, extProjectNo, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getHonourScore))));
        data.setHonourResult((String) projectScoreMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, extProjectNo, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getHonourResult))));
    }

    /**
     * 统计澄清次数
     * @param data
     * @param answerMap
     */
    private void fillAnswer(SccNpmSouSchedule data, Map<String, Object> answerMap) {
        data.setAnswerIssuedCount((Long) answerMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, data.getProjectId(), ExtSouBidComponent.fieldName(SccNpmSouSchedule::getAnswerIssuedCount))));
    }

    /**
     * 填充计算数据
     * 总计划周期	总实际周期	供应商推荐延期天数	发标延期天数	发标延期占比率	收标延期天数	收标延期占比率	汇总上报延期天数	汇总上报延期占比率	中标延期天数	中标延期占比率	资料递交延期天数	评标延期天数	评标延期占比率	定标延期天数	定标延期占比率
     * @param data
     * @param souProject
     */
    private void fillDataAsCalculate(SccNpmSouSchedule data, ExtSouProject souProject) {
        /** 总计划周期：计划中标通知时间-计划发标时间  PLAN_PUBLISH_WIN_LOSS_TIME - PLAN_PUBLISH_TIME*/
        data.setPlanTotalCycle(ReportUtils.dateSubtractAsDay(data.getPlanPublishTime(), data.getPlanPublishWinLossTime()));
        /** 总实际周期：实际中标通知时间-实际发标时间  ACTUAL_PUBLISH_WIN_LOSS_TIME - ACTUAL_PUBLISH_TIME*/
        data.setActualTotalCycle(ReportUtils.dateSubtractAsDay(data.getActualPublishTime(), data.getActualPublishWinLossTime()));

        /** 供应商推荐延期天数：供应商推荐实际推荐时间-计划推荐时间 也就是  实际出表时间 - 计划出表时间   ACTUAL_REQUIREMENT_TIME - PLAN_REQUIREMENT_TIME */
        data.setVendorPostponeCycle(ReportUtils.dateSubtractAsDay(data.getPlanRequirementTime(), data.getActualRequirementTime()));

        /** 发标延期天数：实际-计划 ACTUAL_PUBLISH_TIME - PLAN_PUBLISH_TIME */
        data.setPublishPostponeCycle(ReportUtils.dateSubtractAsDay(data.getPlanPublishTime(), data.getActualPublishTime()));
        /** 发标延期占比率：发标延期天数/(总实际周期-总计划周期)  PUBLISH_POSTPONE_CYCLE/(ACTUAL_TOTAL_CYCLE - PLAN_TOTAL_CYCLE)  */
        BigDecimal totalCycleDiff = ReportUtils.subtractBigDecimal(data.getActualTotalCycle(), data.getPlanTotalCycle());
        data.setPublishPostponeProportion(ReportUtils.divideBigDecimal(data.getPublishPostponeCycle(), totalCycleDiff));

        /** 收标延期天数：（实际收标-实际发标）-（计划收标-计划发标） (ACTUAL_ACCEPTANCE_BID_TIME - ACTUAL_PUBLISH_TIME) - (PLAN_ACCEPTANCE_BID_TIME - PLAN_PUBLISH_TIME) */
        data.setAcceptancePostponeCycle(ReportUtils.subtractLong(ReportUtils.dateSubtractAsDay(data.getActualPublishTime(), data.getActualAcceptanceBidTime()), ReportUtils.dateSubtractAsDay(data.getPlanPublishTime(), data.getPlanAcceptanceBidTime())));
        /** 收标延期占比率：收标延期天数/(总实际周期-总计划周期) ACCEPTANCE_POSTPONE_CYCLE /(ACTUAL_TOTAL_CYCLE - PLAN_TOTAL_CYCLE) */
        data.setAcceptancePostponeProportion(ReportUtils.divideBigDecimal(data.getAcceptancePostponeCycle(), totalCycleDiff));

        /** 汇总上报延期天数: 非询比价公式（实际上报标-实际评标）-（计划上报-计划评标）， （ACTUAL_SUM_REPORT_TIME - ACTUAL_TECH_EVALUATION_TIME） - （PLAN_SUM_REPORT_TIME - PLAN_TECH_EVALUATION_TIME）；
         * 询比价公式 （实际上报标-实际收标）-（计划上报-计划收标） , (ACTUAL_SUM_REPORT_TIME - ACTUAL_ACCEPTANCE_BID_TIME) - (PLAN_SUM_REPORT_TIME - PLAN_ACCEPTANCE_BID_TIME) */
        if(SouBidProccessEnum.INQUIRY.getCode().equals(souProject.getExtSouProcess())) {
            /** 询比价公式 实际上报标-实际收标）-（计划上报-计划收标）*/
            data.setSumReportPostponeCycle(ReportUtils.subtractLong(ReportUtils.dateSubtractAsDay(data.getActualAcceptanceBidTime(), data.getActualSumReportTime()),
                    ReportUtils.dateSubtractAsDay(data.getPlanAcceptanceBidTime(), data.getPlanSumReportTime())));
        } else {
            /** 非询比价公式 （实际上报标-实际评标）-（计划上报-计划评标）*/
            data.setSumReportPostponeCycle(ReportUtils.subtractLong(ReportUtils.dateSubtractAsDay(data.getActualTechEvaluationTime(), data.getActualSumReportTime()),
                    ReportUtils.dateSubtractAsDay(data.getPlanTechEvaluationTime(), data.getPlanSumReportTime())));
        }
        /** 汇总上报延期占比率：汇总延期天数/(总实际周期-总计划周期) SUM_REPORT_POSTPONE_CYCLE /(ACTUAL_TOTAL_CYCLE - PLAN_TOTAL_CYCLE) */
        data.setSumReportPostponeProportion(ReportUtils.divideBigDecimal(data.getSumReportPostponeCycle(), totalCycleDiff));

        /** 中标延期天数：(实际中标-实际定标）-（计划中标-计划定标） (ACTUAL_PUBLISH_WIN_LOSS_TIME - ACTUAL_PICKETAGE_TIME) - (PLAN_PUBLISH_WIN_LOSS_TIME - PLAN_PICKETAGE_TIME) */
        data.setWinPostponeCycle(ReportUtils.subtractLong(ReportUtils.dateSubtractAsDay(data.getActualPicketageTime(), data.getActualPublishWinLossTime()), ReportUtils.dateSubtractAsDay(data.getPlanPicketageTime(), data.getPlanPublishWinLossTime())));
        /** 收标延期占比率：中标延期天数/(总实际周期-总计划周期) WIN_POSTPONE_CYCLE /(ACTUAL_TOTAL_CYCLE - PLAN_TOTAL_CYCLE) */
        data.setWinPostponeProportion(ReportUtils.divideBigDecimal(data.getWinPostponeCycle(), totalCycleDiff));

        /** 资料递交延期天数：实际资料递交时间-计划资料递交时间 也就是 招标资料提交审核通过时间（实际出表） - 申请资料计划递交时间  ExtPrSouRequirementHead::getApprovalPassTime - ExtPrSouRequirementHead::getSendSouProfileEndDate */
        data.setDataSubmitPostponeCycle(ReportUtils.dateSubtractAsDay(data.getSendSouProfileEndDate(), data.getApprovalPassTime()));

        /** 评标延期天数：（实际评标-实际收标）-（计划评标-计划收标）  （ACTUAL_TECH_EVALUATION_TIME-ACTUAL_ACCEPTANCE_BID_TIME）-（PLAN_TECH_EVALUATION_TIME-PLAN_ACCEPTANCE_BID_TIME） */
        data.setEvaluationPostponeCycle(ReportUtils.subtractLong(ReportUtils.dateSubtractAsDay(data.getActualAcceptanceBidTime(), data.getActualTechEvaluationTime()),
                ReportUtils.dateSubtractAsDay(data.getPlanAcceptanceBidTime(), data.getPlanTechEvaluationTime())));
        /** 评标延期占比率：评标延期天数/(总实际周期-总计划周期) */
        data.setEvaluationPostponeProportion(ReportUtils.divideBigDecimal(data.getEvaluationPostponeCycle(), totalCycleDiff));

        /** 定标延期天数：（实际定标-汇总上报）-（计划定标-计划汇总上报）  （ACTUAL_PICKETAGE_TIME-ACTUAL_SUM_REPORT_TIME）-（PLAN_PICKETAGE_TIME-PLAN_SUM_REPORT_TIME） */
        data.setPicketagePostponeCycle(ReportUtils.subtractLong(ReportUtils.dateSubtractAsDay(data.getActualSumReportTime(), data.getActualPicketageTime()),
                ReportUtils.dateSubtractAsDay(data.getPlanSumReportTime(), data.getPlanPicketageTime())));
        /** 定标延期占比率：定标延期天数/(总实际周期-总计划周期) */
        data.setPicketagePostponeProportion(ReportUtils.divideBigDecimal(data.getPicketagePostponeCycle(), totalCycleDiff));

    }

    /**
     * 发标单位数量	推荐单位投标数量	追加单位数量	追加单位投标数量	新供应商数量	开发新单位数量	总发标单位数量	总投标单位数量
     * @param data
     * @param recommvendorMap
     */
    private void fillRecommvendor(SccNpmSouSchedule data, Map<String, Object> recommvendorMap) {
        Long projectId = data.getProjectId();
        data.setSendBidNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getSendBidNumber))));
        data.setSendBidAsSubmitNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getSendBidAsSubmitNumber))));
        data.setAddBidNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getAddBidNumber))));
        data.setAddBidAsSubmitNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getAddBidAsSubmitNumber))));
        data.setNewVendorBidNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getNewVendorBidNumber))));
        data.setNewUniteVendorBidNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getNewUniteVendorBidNumber))));
        data.setTotalBidNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getTotalBidNumber))));
        data.setTotalBidAsSubmitNumber((Long) recommvendorMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getTotalBidAsSubmitNumber))));
    }

    /**
     * 匹配实际时间，如果当前时间小于实际时间，则返回null
     * @param date
     * @return
     */
    private Date formateActualTime(Date date) {
        if(Objects.isNull(date)) {
           return null;
        }
        if(new Date().before(date)) {
            return null;
        }
        return date;
    }

    /**
     * 招标计划和实际 发标环节		收标环节		技术标评完时间		汇总上报环节		定标环节		出具中标通知
     * @param data
     * @param bidOrderMap
     */
    private void fillDataBidPlan(SccNpmSouSchedule data, Map<String, Object> bidOrderMap) {
        Long projectId = data.getProjectId();
        String planKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, SouBidPlanTypeEnum.PLAN.getCode());
        String actualKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, SouBidPlanTypeEnum.ACTUAL.getCode());

        ExtSouPlan plan = (ExtSouPlan) bidOrderMap.getOrDefault(planKey, new ExtSouPlan());
        ExtSouPlan actual = (ExtSouPlan) bidOrderMap.getOrDefault(actualKey, new ExtSouPlan());

        /** 发标环节 */
        data.setPlanPublishTime(plan.getPublishTime());
        data.setActualPublishTime(actual.getPublishTime());

        /** 收标环节 */
        data.setPlanAcceptanceBidTime(plan.getTechEndTime());

        data.setActualAcceptanceBidTime(formateActualTime(actual.getTechEndTime()));

        /** 技术标评完时间 */
        data.setPlanTechEvaluationTime(plan.getTechEvaluationTime());
        data.setActualTechEvaluationTime(actual.getTechEvaluationTime());

        /** 汇总上报环节 */
        data.setPlanSumReportTime(plan.getSumReportTime());
        data.setActualSumReportTime(actual.getSumReportTime());

        /** 定标环节 */
        data.setPlanPicketageTime(plan.getPicketageTime());
        data.setActualPicketageTime(actual.getPicketageTime());

        /** 出具中标通知 */
        data.setPlanPublishWinLossTime(plan.getPublishWinLossTime());
        data.setActualPublishWinLossTime(actual.getPublishWinLossTime());
    }

    /**
     * 填充 来源资料提交单据的一些信息	申请资料计划递交时间	申请资料审核通过时间	公示截止日期	计划出表时间	实际出表时间
     * @param data
     * @param requirementCacheMap
     */
    private void fillDataSubmit(SccNpmSouSchedule data, Map<String, Object> requirementCacheMap) {
        Long projectId = data.getProjectId();

        String maxBidDataSubmitKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, MAX_BID_DATA_SUBMIT_KEY);
        RecordDTO requirementHead = (RecordDTO) requirementCacheMap.get(maxBidDataSubmitKey);
        if(Objects.isNull(requirementHead)) {
            requirementHead = new RecordDTO();
        }

        Map<Long, RecordDTO> extRequirementMap = (Map<Long, RecordDTO>) requirementCacheMap.getOrDefault(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD, new HashMap<>(50));
        RecordDTO extRequirementHead = extRequirementMap.getOrDefault(requirementHead.get(RequirementHead::getRequirementHeadId), new RecordDTO());

        //寻源公示
        Map<Long, SouReqHead> souReqHeadMap = (Map<Long, SouReqHead>) requirementCacheMap.get(MqlType.SOU_REQ_HEAD_BUYER);
        SouReqHead souReqHead = new SouReqHead();
        if(ObjectUtils.allNotNull(souReqHeadMap, extRequirementHead) && souReqHeadMap.containsKey(extRequirementHead.get(ExtPrSouRequirementHead::getSouReqId))) {
            souReqHead = souReqHeadMap.get(extRequirementHead.get(ExtPrSouRequirementHead::getSouReqId));
        }

        //申请资料计划递交时间 取 需求提报的公示截止时间 或者 需求提报不公示的计划的递交资料时间
        Date sendSouProfileEndDate = DateUtil.localDateToDate(ExtSouBidComponent.formateLocalDate(extRequirementHead, ExtSouBidComponent.fieldName(ExtPrSouRequirementHead::getSendSouProfileEndDate)));
        //公示截止时间，取值寻源公示单的截止时间
        Date publicEndTime = souReqHead.getPublicEndTime();

        /** 计划出表时间 取 不公示：计划的递交资料时间+2天 */
        Date planRequirementTime = addDay(sendSouProfileEndDate, 2);

        //公示情况
        if(YesOrNo.YES.getValue().equals(ObjectUtils.defaultIfNull(extRequirementHead.get(ExtPrSouRequirementHead::getNeedPublic), Enable.N).name())) {
            if(!Objects.isNull(publicEndTime)) {
                sendSouProfileEndDate = publicEndTime;
                /** 计划出表时间 取 公示截止时间+1天 */
                planRequirementTime = addDay(publicEndTime, 1);
            }
        }

        /** 申请资料计划递交时间 */
        data.setSendSouProfileEndDate(sendSouProfileEndDate);

        /** 公示截止日期  取值寻源公示截止时间*/
        data.setPublicEndTime(publicEndTime);

        String maxBidDataSubmitData = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, MAX_BID_DATA_SUBMIT_DATA);
        BidDataSubmit maxBidDataSubmit = (BidDataSubmit) requirementCacheMap.getOrDefault(maxBidDataSubmitData, new BidDataSubmit());

        /** 申请资料审核通过时间---取招标资料提交审核通过时间 */
        data.setApprovalPassTime(maxBidDataSubmit.getApprovePassTime());

        /** 计划出表时间 取 公示：公示截止时间+1天 或 不公示：计划的递交资料时间+2天 */
        data.setPlanRequirementTime(planRequirementTime);

        /** 实际出表时间 取推荐单的最后更新时间--原单 */
        Map<String, RecommvendorProjectDto> requirementHeadNumRecomMap = (Map<String, RecommvendorProjectDto>) requirementCacheMap.get(REQUIREMENT_HEAD_NUM_RECOM);

        if(!Objects.isNull(requirementHeadNumRecomMap)) {
            RecommvendorProjectDto projectDto = requirementHeadNumRecomMap.get(requirementHead.get(RequirementHead::getRequirementHeadNum));
            if(!Objects.isNull(projectDto)) {

                data.setActualRequirementTime(projectDto.getLastUpdateDate());
            }
        }



        /** 计划类型 */
        data.setRequirementPlanType(extRequirementHead.get(ExtPrSouRequirementHead::getRequireFrom));
    }

    private Date addDay(Date date, Integer add) {
        if(Objects.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, add);
        return calendar.getTime();
    }

    /**
     * 填充 招标负责人	供应商负责人	评标组长	技术负责人
     * @param data
     * @param souProject
     * @param requirementCacheMap
     * @param groupMap
     */
    private void fillPrincipal(SccNpmSouSchedule data, ExtSouProject souProject, Map<String, Object> requirementCacheMap, Map<String, ExtSouGroup> groupMap) {

        /** 招标负责人 取 招标单创建人 */
        data.setSouPrincipal(souProject.getCreatedFullName());
        /** 供应商负责人 取 计划池 */
        String vendorGroupTypeKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtPrRequirementGroupTypeEnum.VENDOR.getCode());
        if(requirementCacheMap.containsKey(vendorGroupTypeKey)) {
            RecordDTO recordGroup = (RecordDTO) requirementCacheMap.get(vendorGroupTypeKey);
            data.setVendorPrincipal(recordGroup.get(ExtPrSouRequirementGroup::getFullName));
        }
        /** 评标组长 取 招标工作人员 */
        String leaderGroupRoleKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getProjectId(), ExtSouGroupRoleEnum.LEADER.getCode());
        if(groupMap.containsKey(leaderGroupRoleKey)) {
            data.setLeaderPrincipal(groupMap.get(leaderGroupRoleKey).getFullName());
        }

        data.setExtTechPrincipal(souProject.getExtTechPrincipal());

    }

    /**
     * 分类--改成一级品类名称
     * @param data
     * @param souProject
     * @param purchaseCategoryMap
     */
    private void fillClassification(SccNpmSouSchedule data, ExtSouProject souProject, Map<Long, PurchaseCategory> purchaseCategoryMap) {
        PurchaseCategory purchaseCategory = purchaseCategoryMap.getOrDefault(souProject.getExtCategoryId(), new PurchaseCategory());
        if(StringUtils.isNotBlank(purchaseCategory.getCategoryFullName())) {
            String[] categoryFullNames = purchaseCategory.getCategoryFullName().split(SrmConstant.SHORT_LINE);
            data.setClassification(categoryFullNames[0]);
        }
    }

    /**
     * 缓存绩效得分和结果
     * @param souProjectList
     * @return
     */
    private Map<String, Object> cachePerfProjectScore(List<ExtSouProject> souProjectList) {

        List<String> extProjectNoList = souProjectList.stream().map(ExtSouProject::getExtProjectNo).collect(Collectors.toList());
        Map<String, Object> projectScoreMap = new HashMap<>(50);
        List<RecordDTO> recordList = qlOpenClient.query(ContextPath.PEF, QlOpenWrappers.query(MqlType.SCC_NPM_PROJECT_SCORE_HEADER)
                .in(ProjectScoreHeader::getBidCode, extProjectNoList).eq(ProjectScoreHeader::getPerformanceType, PerformanceCodeEnum.PROJECT.getCode()));
        if(CollectionUtils.isNotEmpty(recordList)) {
            recordList.stream().forEach(score -> {
                String scoreKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, score.get(ProjectScoreHeader::getBidCode), ExtSouBidComponent.fieldName(SccNpmSouSchedule::getHonourScore));
                projectScoreMap.put(scoreKey, score.get(ProjectScoreHeader::getScore));
                String resultKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, score.get(ProjectScoreHeader::getLevelName), ExtSouBidComponent.fieldName(SccNpmSouSchedule::getHonourResult));
                projectScoreMap.put(resultKey, score.get(ProjectScoreHeader::getLevelName));
            });
        }
        return projectScoreMap;
    }


    /**
     * 缓存澄清次数 评标组长发起的该项目的次数
     * @param projectIdList
     * @param groupMap
     * @return
     */
    private Map<String, Object> cacheAnswer(List<Long> projectIdList, Map<String, ExtSouGroup> groupMap) {
        Map<String, Object> answerMap = new HashMap<>(50);
        List<AnswerDTO> answerList = qlService.queryByWrapper(QlWrappers.query(MqlType.ANSWER).in(AnswerDTO::getProjectId, projectIdList).eq(AnswerDTO::getAnswerStatus, AnswerStatusEnum.ISSUED.getCode()), AnswerDTO.class);
        Map<String, Set<Long>> answerCountMap = new HashMap<>(50);
        if(CollectionUtils.isNotEmpty(answerList)) {
            answerList.stream().forEach(answer -> {
                String bidLeaderKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, answer.getProjectId(), ExtSouGroupRoleEnum.LEADER.getCode());
                if(groupMap.containsKey(bidLeaderKey) && ObjectUtils.defaultIfNull(groupMap.get(bidLeaderKey).getUserName(), "").equals(answer.getCreatedBy())) {
                    String answerIssuedCountKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, answer.getProjectId(), ExtSouBidComponent.fieldName(SccNpmSouSchedule::getAnswerIssuedCount));
                    if(!answerCountMap.containsKey(answerIssuedCountKey)) {
                        answerCountMap.put(answerIssuedCountKey, new HashSet<>());
                    }
                    answerCountMap.get(answerIssuedCountKey).add(answer.getAnswerId());
                }
            });
        }
        for(String key : answerCountMap.keySet()) {
            answerMap.put(key, Long.valueOf(answerCountMap.get(key).size()));
        }
        return answerMap;
    }

    /**
     * 缓存供应商推荐信息
     * @param projectIdList
     * @param requirementCacheMap
     * @param bidOrderMap
     * @return
     */
    private Map<String, Object> cacheRecommVendor(List<Long> projectIdList, Map<String, Object> requirementCacheMap, Map<String, Object> bidOrderMap) {
        Map<String, Object> recommvendorMap = new HashMap<>(50);

        Map<Long, List<ExtSouDemand>> projectIdDemandGroup = (Map<Long, List<ExtSouDemand>>) requirementCacheMap.get(MqlType.NPM_SOU_DEMAND);

        if(MapUtils.isEmpty(projectIdDemandGroup)) {
            return recommvendorMap;
        }

        List<String> requirementHeadNumList = new ArrayList<>(50);
        for(List<ExtSouDemand> demandList : projectIdDemandGroup.values()) {
            requirementHeadNumList.addAll(demandList.stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList()));
        }

        List<ExtSouDemand> recommvendorDemandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).in(ExtSouDemand::getApplicantNo, requirementHeadNumList).notIn(ExtSouDemand::getProjectId, projectIdList), ExtSouDemand.class);
        Map<String, List<ExtSouDemand>> recommvendorDemandGroup = recommvendorDemandList.stream().collect(Collectors.groupingBy(r -> r.getApplicantNo()));

        Map<Long, RecommvendorProjectExtendDto> recommvendorProjectExtendDtoMap = new HashMap<>(50);
        Map<Long, List<RecommvendorDto>> recommvendorDtoMap = new HashMap<>(50);
        Map<Long, RecommvendorProjectDto> recommvendorProjectMap = new HashMap<>(50);
        if(CollectionUtils.isNotEmpty(recommvendorDemandList)) {
            List<RecommvendorProjectExtendDto> recommvendorProjectExtendDtos = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProjectExtend.name()).in(RecommvendorProjectExtendDto::getProjectId, recommvendorDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList())), RecommvendorProjectExtendDto.class);
            recommvendorProjectExtendDtoMap = recommvendorProjectExtendDtos.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(), (k1, k2)->k2));

            List<RecommvendorProjectDto> recommvendorProjectDtos = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProject.name()).in(RecommvendorProjectDto::getProjectId, recommvendorDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList())), RecommvendorProjectDto.class);
            recommvendorProjectMap = recommvendorProjectDtos.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(), (k1, k2)->k2));

            List<RecommvendorDto> recommvendorDtoList = qlService.queryByWrapper(QlWrappers.query(RecommType.Recommvendor.name()).in(RecommvendorDto::getProjectId, recommvendorDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList())), RecommvendorDto.class);
            recommvendorDtoMap = recommvendorDtoList.stream().collect(Collectors.groupingBy(r -> r.getProjectId()));
        }

        List<RecommvendorDto> vendorList = qlService.queryByWrapper(QlWrappers.query(RecommType.Recommvendor.name()).in(RecommvendorDto::getProjectId, projectIdList), RecommvendorDto.class);
        Map<Long, List<RecommvendorDto>> vendorMap = vendorList.stream().collect(Collectors.groupingBy(RecommvendorDto::getProjectId));

        /** 发标单位数量	推荐单位投标数量	追加单位数量	追加单位投标数量	新供应商数量	开发新单位数量	总发标单位数量	总投标单位数量 */
        List<ExtSouOrder> techVendorList = (List<ExtSouOrder>) bidOrderMap.getOrDefault(TECH_VENDOR_LIST, new ArrayList<>(50));
        List<ExtSouOrder> busVendorList = (List<ExtSouOrder>) bidOrderMap.getOrDefault(BUS_VENDOR_LIST, new ArrayList<>(50));

        Map<Long, List<ExtSouOrder>> techVendorGroup = techVendorList.stream().collect(Collectors.groupingBy(o -> o.getProjectId()));
        Map<Long, List<ExtSouOrder>> busVendorGroup = busVendorList.stream().collect(Collectors.groupingBy(o -> o.getProjectId()));

        /** 申请单号对应的推荐单原单 */
        Map<String, RecommvendorProjectDto> requirementHeadNumRecomMap = new HashMap<>(50);
        for(Long projectId : projectIdDemandGroup.keySet()) {
            List<ExtSouDemand> extSouDemands = projectIdDemandGroup.get(projectId);
            List<ExtSouDemand> recommDemands = new ArrayList<>(50);
            extSouDemands.stream().filter(d -> recommvendorDemandGroup.containsKey(d.getApplicantNo())).forEach(d -> {
                List<ExtSouDemand> subRecommDemands = recommvendorDemandGroup.get(d.getApplicantNo());
                recommDemands.addAll(subRecommDemands);
            });
            if(CollectionUtils.isNotEmpty(recommDemands)) {

                Map<Long, RecommvendorProjectExtendDto> finalRecommvendorProjectExtendDtoMap = recommvendorProjectExtendDtoMap;
                Map<Long, RecommvendorProjectDto> finalRecommvendorProjectMap = recommvendorProjectMap;
                recommDemands.stream().forEach(r -> {
                    RecommvendorProjectExtendDto projectExtendDto = finalRecommvendorProjectExtendDtoMap.get(r.getProjectId());
                    if(!Objects.isNull(projectExtendDto) && SouRecommvendorTypeEnum.RECOMM.getCode().equals(projectExtendDto.getRcommendType())) {
                        RecommvendorProjectDto projectDto = finalRecommvendorProjectMap.get(r.getProjectId());
                        if(!Objects.isNull(projectDto)) {
                            requirementHeadNumRecomMap.put(r.getApplicantNo(), projectDto);
                        }
                    }
                });

                Map<String, List<RecommvendorProjectExtendDto>> recommvendorGroup = recommDemands.stream().filter(d -> finalRecommvendorProjectExtendDtoMap.containsKey(d.getProjectId())).map(d -> finalRecommvendorProjectExtendDtoMap.get(d.getProjectId())).collect(Collectors.groupingBy(RecommvendorProjectExtendDto::getRcommendType));

                //原单供应商
                List<RecommvendorDto> recommTypeVendorList = new ArrayList<>(50);
                Map<Long, List<RecommvendorDto>> finalRecommvendorDtoMap = recommvendorDtoMap;
                recommvendorGroup.getOrDefault(SouRecommvendorTypeEnum.RECOMM.getCode(), new ArrayList<>(50)).stream().filter(r -> finalRecommvendorDtoMap.containsKey(r.getProjectId())).forEach(r -> recommTypeVendorList.addAll(finalRecommvendorDtoMap.get(r.getProjectId())));

                List<Long> recommTypeVendorIdList = recommTypeVendorList.stream().map(r -> r.getVendorId()).distinct().collect(Collectors.toList());

                List<RecommvendorDto> recommvendorDtos = vendorMap.getOrDefault(projectId, new ArrayList<>(50));

                //追加供应商
                List<RecommvendorDto> addTypeVendorList = new ArrayList<>(50);
                recommvendorGroup.getOrDefault(SouRecommvendorTypeEnum.ADD.getCode(), new ArrayList<>(50)).stream().filter(r -> finalRecommvendorDtoMap.containsKey(r.getProjectId())).forEach(r -> addTypeVendorList.addAll(finalRecommvendorDtoMap.get(r.getProjectId()).stream().filter(v -> !recommTypeVendorIdList.contains(v.getVendorId())).collect(Collectors.toList())));

                List<Long> addTypeVendorIdList = recommvendorDtos.stream().filter(v -> !recommTypeVendorIdList.contains(v.getVendorId())).map(v -> v.getVendorId()).distinct().collect(Collectors.toList());

                /** 发标单位数量 取 推荐供应商数量*/
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getSendBidNumber)), Long.valueOf(recommTypeVendorIdList.size()));
                /** 推荐单位投标数量*/
                List<Long> techVendorIdList = techVendorGroup.getOrDefault(projectId, new ArrayList<>(50)).stream().map(o -> o.getVendorId()).distinct().collect(Collectors.toList());
                List<Long> busVendorIdList = busVendorGroup.getOrDefault(projectId, new ArrayList<>(50)).stream().map(o -> o.getVendorId()).distinct().collect(Collectors.toList());
                List<Long> recoomTypeBidVendorIdList = recommTypeVendorIdList.stream().filter(vendorId -> techVendorIdList.contains(vendorId) || busVendorIdList.contains(vendorId)).collect(Collectors.toList());
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getSendBidAsSubmitNumber)), Long.valueOf(recoomTypeBidVendorIdList.size()));

                /** 追加单位数量*/
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getAddBidNumber)), Long.valueOf(addTypeVendorIdList.size()));
                /** 追加单位投标数量 */
                List<Long> addTypeBidVendorIdList = addTypeVendorIdList.stream().filter(vendorId -> techVendorIdList.contains(vendorId) || busVendorIdList.contains(vendorId)).collect(Collectors.toList());
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getAddBidAsSubmitNumber)), Long.valueOf(addTypeBidVendorIdList.size()));
                /** 新供应商数量 */
                List<Long> recommNewVendorIdList = recommTypeVendorList.stream().filter(v -> YesOrNo.YES.getValue().equals(v.getExtIsNewVendor())).map(v -> v.getVendorId()).collect(Collectors.toList());
                List<Long> addNewVendorIdList = addTypeVendorList.stream().filter(v -> YesOrNo.YES.getValue().equals(v.getExtIsNewVendor())).map(v -> v.getVendorId()).collect(Collectors.toList());
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getNewVendorBidNumber)), Long.valueOf(recommNewVendorIdList.size() + addNewVendorIdList.size()));
                /** 开发新单位数量 */
                List<Long> recommNewUniteVendorIdList = recommTypeVendorList.stream().filter(v -> ObjectUtils.defaultIfNull(v.getExtVendorAttr(), "").contains(SouRecommVendorNatrueEnum.NEW_UNITE.getCode())).map(v -> v.getVendorId()).collect(Collectors.toList());
                List<Long> addNewUniteVendorIdList = addTypeVendorList.stream().filter(v -> ObjectUtils.defaultIfNull(v.getExtVendorAttr(), "").contains(SouRecommVendorNatrueEnum.NEW_UNITE.getCode())).map(v -> v.getVendorId()).collect(Collectors.toList());
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getNewUniteVendorBidNumber)), Long.valueOf(recommNewUniteVendorIdList.size() + addNewUniteVendorIdList.size()));

                /** 总发标单位数量 发标单位数量+追加单位数量 */
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getTotalBidNumber)), Long.valueOf(addTypeVendorIdList.size() + recommTypeVendorIdList.size()));
                /** 总投标单位数量 追加单位投标数量+推荐单位投标数量 */
                recommvendorMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(SccNpmSouSchedule::getTotalBidAsSubmitNumber)), Long.valueOf(addTypeBidVendorIdList.size() + recoomTypeBidVendorIdList.size()));
            }
        }

        /** 缓存招标资料提交 */
        requirementCacheMap.put(REQUIREMENT_HEAD_NUM_RECOM, requirementHeadNumRecomMap);

        return recommvendorMap;
    }

    /**
     * 缓存投标供应商和招标计划和实际
     * @param projectIdList
     * @return
     */
    private Map<String, Object> cachePlanAsBidOrder(List<Long> projectIdList) {
        Map<String, Object> bidOrderMap = new HashMap<>(50);

        /** 查询招标单报价单 */
        List<ExtSouOrder> orderList = iExtSouOrderService.lambdaQuery().in(ExtSouOrder::getProjectId, projectIdList).list();
        List<ExtNpmSouOrder> extNpmSouOrders = new ArrayList<>(50);
        if(CollectionUtils.isNotEmpty(orderList)) {
            extNpmSouOrders = iExtNpmSouOrderService.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream().map(o -> o.getOrderId()).collect(Collectors.toList())).list();
        }
        Map<Long, ExtSouOrder> orderMap = orderList.stream().collect(Collectors.toMap(o -> o.getOrderId(), Function.identity(), (k1, k2)->k2));
        List<Long> techProjectIdList = extNpmSouOrders.stream().filter(e -> ExtOrderTypeEnum.TECH.getCode().equals(e.getExtOrderType())).map(e -> orderMap.get(e.getOrderId()).getProjectId()).distinct().collect(Collectors.toList());

        //已投技术标
        List<ExtSouOrder> techVendorList = extNpmSouOrders.stream().filter(e -> ExtOrderTypeEnum.TECH.getCode().equals(e.getExtOrderType())).filter(e -> SouOrderStatusEnum.SUBMISSION.name().equals(e.getOrderStatus())).map(e -> orderMap.get(e.getOrderId())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtSouOrder::getOrderId))), ArrayList::new));
        //已投技术标
        List<ExtSouOrder> busVendorList = extNpmSouOrders.stream().filter(e -> ExtOrderTypeEnum.BUS.getCode().equals(e.getExtOrderType())).filter(e -> SouOrderStatusEnum.SUBMISSION.name().equals(e.getOrderStatus())).map(e -> orderMap.get(e.getOrderId())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtSouOrder::getOrderId))), ArrayList::new));

        bidOrderMap.put(TECH_VENDOR_LIST, techVendorList);
        bidOrderMap.put(BUS_VENDOR_LIST, busVendorList);

        List<ExtSouPlan> planList = planService.lambdaQuery().in(ExtSouPlan::getProjectId, projectIdList).list();

        List<ExtSouRound> roundList = roundService.lambdaQuery().in(ExtSouRound::getProjectId, projectIdList).eq(ExtSouRound::getRound, SrmConstant.NUM_ONE).list();

        Map<String, List<ExtSouPlan>> planMap = planList.stream().collect(Collectors.groupingBy(ExtSouPlan::getPlanType));

        Map<String, ExtSouRound> extSouRoundMap = roundList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getProjectId(), k.getRound()), Function.identity(), (k1, k2)-> k2));

        if(MapUtils.isNotEmpty(planMap)) {
            if(planMap.containsKey(SouBidPlanTypeEnum.PLAN.getCode())) {
                planMap.get(SouBidPlanTypeEnum.PLAN.getCode()).stream().forEach(plan -> {
                    String planKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, plan.getProjectId(), plan.getPlanType());
                    bidOrderMap.put(planKey, plan);

                    Boolean techFlag = techProjectIdList.contains(plan.getProjectId());

                    if(!techFlag) {
                        plan.setTechEndTime(plan.getBusEndTime());
                    }
                });
            }
            if(planMap.containsKey(SouBidPlanTypeEnum.ACTUAL.getCode())) {
                planMap.get(SouBidPlanTypeEnum.ACTUAL.getCode()).stream().forEach(plan -> {
                    String actualKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, plan.getProjectId(), plan.getPlanType());
                    bidOrderMap.put(actualKey, plan);

                    String planKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, plan.getProjectId(), SouBidPlanTypeEnum.PLAN.getCode());
                    ExtSouPlan extSouPlan = (ExtSouPlan) bidOrderMap.getOrDefault(planKey, new ExtSouPlan());

                    Boolean techFlag = techProjectIdList.contains(plan.getProjectId());

                    if(techFlag) {
                        if(!Objects.isNull(extSouPlan.getTechEndFixTime())) {
                            plan.setTechEndTime(extSouPlan.getTechEndFixTime());
                        } else {
                            plan.setTechEndTime(extSouPlan.getTechEndTime());
                        }
                    } else {
                        String roundKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, plan.getProjectId(), SrmConstant.NUM_ONE);
                        ExtSouRound extSouRound = extSouRoundMap.getOrDefault(roundKey, new ExtSouRound());
                        plan.setTechEndTime(extSouRound.getOrderEndTime());
                    }
                });
            }
        }

        return bidOrderMap;
    }


    /**
     * 缓存招标工作小组
     * @param projectIdList
     * @return
     */
    private Map<String, ExtSouGroup> cacheExtSouGroup(List<Long> projectIdList) {
        List<ExtSouGroup> groupList = groupService.lambdaQuery().in(ExtSouGroup::getProjectId, projectIdList).eq(ExtSouGroup::getExtGroupFlag, YesOrNo.YES.getValue()).list();
        Map<String, ExtSouGroup> groupMap = groupList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getProjectId(), k.getGroupRole()), Function.identity(), (k1, k2)->k2));
        return groupMap;
    }

    /**
     * 缓存采购申请信息
     * @param projectIdList
     * @return
     */
    private Map<String, Object> cacheRequirementInfo(List<Long> projectIdList) {
        Map<String, Object> requirementCacheMap = new HashMap<>(16);

        List<ExtSouDemand> demandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).in(ExtSouDemand::getProjectId, projectIdList), ExtSouDemand.class);

        if(CollectionUtils.isEmpty(demandList)) {
            return requirementCacheMap;
        }

        /** 采购申请单号 */
        List<String> requirementHeadNumList = demandList.stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList());

        /** 采购申请头表 */
        List<RecordDTO> requirementHeadList = new ArrayList<>(50);
        /** 采购申请扩展表 */
        List<RecordDTO> extRequirementHeadList = new ArrayList<>(50);
        /** 采购申请负责人关联表 */
        List<RecordDTO> requirementGroupList = new ArrayList<>(50);

        /** 寻源需求管理 */
        List<SouReqHead> souReqHeadList = new ArrayList<>(50);

        requirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadNum, requirementHeadNumList));
        if(CollectionUtils.isNotEmpty(requirementHeadList)) {
            extRequirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId, requirementHeadList.stream().map(r -> r.get(RequirementHead::getRequirementHeadId)).collect(Collectors.toList())));
            requirementGroupList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_GROUP).in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadList.stream().map(r -> r.get(RequirementHead::getRequirementHeadId)).collect(Collectors.toList())));
        }

        if(CollectionUtils.isNotEmpty(extRequirementHeadList)) {
            souReqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER)
                    .in(SouReqHead::getReqHeadId, extRequirementHeadList.stream().map(r -> r.get(ExtPrSouRequirementHead::getSouReqId)).distinct().collect(Collectors.toList())), SouReqHead.class);
        }

        /** 招标资料提交 */
        List<BidDataSubmit> dataSubmitList = qlService.queryByWrapper(QlWrappers.query(MqlType.SUBMIT_BUYER).in(BidDataSubmit::getRequirementHeadNum, requirementHeadNumList).orderByDesc(BidDataSubmit::getTotalBudget), BidDataSubmit.class);

        Map<Long, List<ExtSouDemand>> demandGroup = demandList.stream().collect(Collectors.groupingBy(ExtSouDemand::getProjectId));

        Map<String, RecordDTO> requirementMap = requirementHeadList.stream().collect(Collectors.toMap(r -> r.get(RequirementHead::getRequirementHeadNum), Function.identity(), (k1, k2)->k2));
        Map<Long, RecordDTO> extRequiremnetMap = extRequirementHeadList.stream().collect(Collectors.toMap(r -> r.get(ExtPrSouRequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)-> k2));
        Map<String, RecordDTO> requirementGroupMap = requirementGroupList.stream().collect(Collectors.toMap(r -> StringUtils.joinWith(SrmConstant.UNDER_LINE, r.get(ExtPrSouRequirementGroup::getRequirementHeadId), r.get(ExtPrSouRequirementGroup::getGroupType)), Function.identity(), (k1, k2) -> k2));
        Map<String, BidDataSubmit> dataSubmitMap = dataSubmitList.stream().collect(Collectors.toMap(d -> d.getRequirementHeadNum(), Function.identity(), (k1, k2) -> k2));
        Map<Long, SouReqHead> souReqHeadMap = souReqHeadList.stream().collect(Collectors.toMap(k -> k.getReqHeadId(), Function.identity(), (k1, k2) -> k2));

        /** 缓存信息 */
        requirementCacheMap.put(MqlType.PURCHASE_REQUIREMENT_HEAD, requirementMap);
        requirementCacheMap.put(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD, extRequiremnetMap);
        requirementCacheMap.put(MqlType.NPM_SOU_DEMAND, demandGroup);
        requirementCacheMap.put(MqlType.SOU_REQ_HEAD_BUYER, souReqHeadMap);

        //按照招标单据构造数据
        for(Long projectId: demandGroup.keySet()) {
            List<ExtSouDemand> extSouDemands = demandGroup.get(projectId);
            //按预算金额从小到大
            List<BidDataSubmit> bidDataSubmits = extSouDemands.stream().filter(d -> dataSubmitMap.containsKey(d.getApplicantNo())).map(d -> dataSubmitMap.get(d.getApplicantNo())).sorted(new Comparator<BidDataSubmit>() {
                @Override
                public int compare(BidDataSubmit o1, BidDataSubmit o2) {
                    return ObjectUtils.defaultIfNull(o2.getTotalBudget(), BigDecimal.ZERO).compareTo(ObjectUtils.defaultIfNull(o1.getTotalBudget(), BigDecimal.ZERO));
                }
            }).collect(Collectors.toList());
            BidDataSubmit maxBidDataSubmit = new BidDataSubmit();
            if(CollectionUtils.isNotEmpty(bidDataSubmits)) {
                maxBidDataSubmit = bidDataSubmits.get(0);
            }

            //供应商负责人
            String vendorGroupTypeKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtPrRequirementGroupTypeEnum.VENDOR.getCode());
            String requirementHeadNum = extSouDemands.get(0).getApplicantNo();
            if(requirementMap.containsKey(maxBidDataSubmit.getRequirementHeadNum())) {
                requirementHeadNum = maxBidDataSubmit.getRequirementHeadNum();
            }

            //最大预算的申请单号
            String maxBidDataSubmitKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, MAX_BID_DATA_SUBMIT_KEY);
            requirementCacheMap.put(maxBidDataSubmitKey, requirementMap.get(requirementHeadNum));

            //最大预算的招标资料单号
            String maxBidDataSubmitData = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, MAX_BID_DATA_SUBMIT_DATA);
            requirementCacheMap.put(maxBidDataSubmitData, maxBidDataSubmit);

            //供应商负责人
            requirementCacheMap.put(vendorGroupTypeKey, requirementGroupMap.getOrDefault(StringUtils.joinWith(SrmConstant.UNDER_LINE, requirementMap.getOrDefault(requirementHeadNum, new RecordDTO()).get(RequirementHead::getRequirementHeadId), ExtPrRequirementGroupTypeEnum.VENDOR.getCode()), new RecordDTO()));

            //资料提交
            String bidDataSubmitKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, MqlType.SUBMIT_BUYER);
            requirementCacheMap.put(bidDataSubmitKey, bidDataSubmits);
        }

        return requirementCacheMap;
    }

    /**
     * 缓存品类信息
     * @param souProjectList
     * @return
     */
    private Map<Long, PurchaseCategory> cachePurchaseCategory(List<ExtSouProject> souProjectList) {
        List<Long> purchaseCategoryIds = souProjectList.stream().filter(o -> ObjectUtils.allNotNull(o.getExtCategoryId())).map(ExtSouProject::getExtCategoryId).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(purchaseCategoryIds)) {
            return new HashMap<>(8);
        }
        List<PurchaseCategory> purchaseCategoryList = baseClient.listCategoryByIds(purchaseCategoryIds);
        return purchaseCategoryList.stream().collect(Collectors.toMap(k->k.getCategoryId(), Function.identity(), (k1, k2)->k2));
    }

    /**
     * 实体类转换
     * @param souProjectList
     * @return
     */
    private List<SccNpmSouSchedule> convertSouSchedule(List<ExtSouProject> souProjectList) {
        if(CollectionUtils.isEmpty(souProjectList)) {
            return null;
        }
        List<SccNpmSouSchedule> scheduleList = new ArrayList<>(souProjectList.size());
        souProjectList.stream().forEach(project -> {
            SccNpmSouSchedule schedule = new SccNpmSouSchedule();
            schedule.setProjectId(project.getProjectId());
            /** 招标单号拆分 */
            String[] projectNoArray = NpmSouBidProjectNoUtils.resolveProjectNo(project.getExtProjectNo());
            schedule.setCompanyShortCode(projectNoArray[0]);
            schedule.setYear(projectNoArray[1]);
            schedule.setMonth(projectNoArray[2]);
            scheduleList.add(schedule);
        });
        return scheduleList;
    }

    @Override
    public List<SccNpmSouSchedule> saveOrUpdate(List<SccNpmSouSchedule> scheduleList) {
        if(CollectionUtils.isEmpty(scheduleList)) {
            return null;
        }
        /** 查询数据库 */
        List<SccNpmSouSchedule> dbList = this.lambdaQuery().in(SccNpmSouSchedule::getProjectId, scheduleList.stream().map(SccNpmSouSchedule::getProjectId).collect(Collectors.toList())).list();
        Map<Long, SccNpmSouSchedule> dbMap = dbList.stream().collect(Collectors.toMap(SccNpmSouSchedule::getProjectId, Function.identity(), (k1, k2)->k2));

        /** 保存列表 */
        List<SccNpmSouSchedule> insertList = new ArrayList<>(50);
        /** 更新列表 */
        List<SccNpmSouSchedule> updateList = new ArrayList<>(50);

        /** 数据处理 */
        scheduleList.stream().forEach(sccNpmSouSchedule -> {
            if(dbMap.containsKey(sccNpmSouSchedule.getProjectId())) {
                sccNpmSouSchedule.setScheduleId(dbMap.get(sccNpmSouSchedule.getProjectId()).getScheduleId());
                updateList.add(sccNpmSouSchedule);
            } else {
                sccNpmSouSchedule.setScheduleId(IdGenrator.generate());
                sccNpmSouSchedule.setVersion(SrmConstant.LONG_ZERO);
                insertList.add(sccNpmSouSchedule);
            }
        });

        /** 数据更新 */
        if(CollectionUtils.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }

        /** 数据插入 */
        if(CollectionUtils.isNotEmpty(insertList)) {
            this.saveBatch(insertList);
        }

        return scheduleList;
    }

    @Override
    public PageInfo<ExtSouProject> timerJobToRefreshReport(ScheduleReportQueryDto queryDto) {
        /** 分页获取刷新数据范围*/
        PageInfo<ExtSouProject> pageInfo = this.listPageAsChangeRecently(queryDto);

        /** 生成报表数据*/
        List<SccNpmSouSchedule> scheduleList = this.generateScheduleReportData(pageInfo.getList());

        /** 报表数据持久化*/
        this.saveOrUpdate(scheduleList);

        return pageInfo;
    }
}
