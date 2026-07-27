package com.midea.cloud.srm.perf.orderscoreitem.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.perf.scoreproject.ScoreItemsProjectStatusEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckDTO;
import com.midea.cloud.srm.model.perf.ordercheck.entity.PerfScoreItemsOrderCheckDetail;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderCheckDetailStatusEnum;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderCheckStatusEnum;
import com.midea.cloud.srm.model.perf.scoreproject.dto.PerfScoreItemsDTO;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemManSupInd;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItems;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsMan;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsSup;
import com.midea.cloud.srm.model.perf.scoring.ScoreManScoringV1;
import com.midea.cloud.srm.model.perf.template.dto.PerfTemplateDTO;
import com.midea.cloud.srm.model.perf.template.dto.PerfTemplateDimWeightDTO;
import com.midea.cloud.srm.model.perf.template.dto.PerfTemplateLineDTO;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateCategory;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateDimWeight;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateHeader;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateLine;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.perf.scoreproject.service.*;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsOrderCheckService;
import com.midea.cloud.srm.perf.scoring.service.IScoreManScoringV1Service;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateCategoryService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateHeaderService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.mideacloud.common.id.IdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 * 订单化绩效评分项目主信息 前端控制器</pre>
 * @author huangbf3
 * <pre>
 */
@Api(value = "PjOrderPerfScoreItemsController", tags = {"订单化绩效评分项目主信息"})
@RestController
@RequestMapping("/pj/scoreproject/scoreItems")
@Slf4j
public class PjOrderPerfScoreItemsController extends BaseController {
    /**
     * 绩效评分项目Service
     */
    @Autowired
    private IPerfScoreItemsService iPerfScoreItemsService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private IPerfScoreItemsOrderCheckService perfScoreItemsOrderCheckService;

    @Autowired
    private IPerfTemplateHeaderService perfTemplateHeaderService;

    @Autowired
    private IPerfTemplateCategoryService perfTemplateCategoryService;

    @Autowired
    private IPerfScoreItemsManService perfScoreItemsManService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IPerfScoreItemsSupService perfScoreItemsSupService;

    @Autowired
    private IPerfScoreItemManSupIndService perfScoreItemManSupIndService;

    @Autowired
    private IScoreManScoringV1Service scoreManScoringV1Service;

    @ApiOperation(value = "新增/更新订单化绩效评分项目和子表信息", notes = "新增/更新订单化绩效评分项目和子表信息", httpMethod = "POST")
    @PostMapping("/saveOrUpdatePerfScoreItems")
    public Long saveOrUpdatePerfScoreItems(@RequestBody PerfScoreItemsDTO perfScoreItemsDTO) {
        if (perfScoreItemsDTO.getScoreItemsId() == null) {
            try {
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                HrUserOrgnizationDto hrUserOrgnizationByUsername = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
                if (hrUserOrgnizationByUsername != null) {
                    log.info("hrUserOrgnizationByUsername返回信息:" + JSONObject.toJSONString(hrUserOrgnizationByUsername));
                    List<Organization> orgList = new ArrayList<>();
                    Organization buOrganization = hrUserOrgnizationByUsername.getBuOrganization();
                    Organization ouOrganization = hrUserOrgnizationByUsername.getOuOrganization();
                    Organization departmentOrganization = hrUserOrgnizationByUsername.getDepartmentOrganization();
                    orgList.add(buOrganization);
                    orgList.add(ouOrganization);
                    orgList.add(departmentOrganization);
                    List<String> orgNameList = orgList.stream().filter(item -> item != null).map(Organization::getOrganizationName).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(orgNameList)) {
                        String fullPath = String.join("-", orgNameList);
                        perfScoreItemsDTO.setFullPathId(fullPath);
                    }
                }
            } catch (BaseException e) {
                throw new BaseException("调用根据用户获取组织信息接口失败,请重试");
            }
        }
        Long id = iPerfScoreItemsService.saveOrUpdatePerfScoreItems(perfScoreItemsDTO);
        return id;
    }

    @ApiOperation(value = "通知评分人(项目状态修改为通知评分人, 并生成评分人绩效评分表)", notes = "通知评分人(项目状态修改为通知评分人, 并生成评分人绩效评分表)", httpMethod = "POST")
    @PostMapping("/notifyScorers")
    public String notifyScorers(@RequestBody PerfScoreItems scoreItems) throws Exception {
        // 后置生成订单复核数据
        Long scoreItemsId = scoreItems.getScoreItemsId();
        PerfScoreItems dbScoreItems = iPerfScoreItemsService.getById(scoreItemsId);
        // 判断状态
        Assert.isTrue(!ScoreItemsProjectStatusEnum.SCORE_NOTIFIED.getValue().equals(dbScoreItems.getProjectStatus()),"已通知评分人,不可重复生成数据");
        List<PerfScoreItemsMan> man = perfScoreItemsManService.list(Wrappers.lambdaQuery(PerfScoreItemsMan.class)
                .eq(PerfScoreItemsMan::getScoreItemsId, scoreItemsId));
        // 1.创建评分人信息
        List<ScoreManScoringV1> saveList = createScoreManInfo(dbScoreItems, man);
        // 2.生成复核数据
        saveOrderCheckNew(saveList, dbScoreItems);
        // 3.更新绩效项目的主表信息,状态,评分人
        iPerfScoreItemsService.update(Wrappers.lambdaUpdate(PerfScoreItems.class)
                .eq(PerfScoreItems::getScoreItemsId,scoreItemsId)
                .set(PerfScoreItems::getProjectStatus,ScoreItemsProjectStatusEnum.SCORE_NOTIFIED.getValue())
                .set(PerfScoreItems::getScorePeopleCount,man.size())
        );
        // 发送钉钉
        // 4. 发送钉钉给评分人-有集约化绩效需要您评分，请及时处理
        try {

            List<String> accountList = man.stream().map(PerfScoreItemsMan::getScoreUserName).collect(Collectors.toList());
            pjProjectExtClient.workNotices("有集约化绩效需要您评分，请及时处理", accountList);
        } catch (Exception e) {
            log.error("订单化绩效,钉钉通知评分人失败");
            log.error(e.getMessage());
            log.error("" + e);
        }
        return null;
    }

    /**
     * 创建评分人信息
     *
     * @return
     */
    private List<ScoreManScoringV1> createScoreManInfo(PerfScoreItems dbScoreItems, List<PerfScoreItemsMan> man) {
        // 模型信息
        PerfTemplateDTO templateDTO = perfTemplateHeaderService.findPerTemplateByTemplateHeadId(dbScoreItems.getTemplateHeadId());

        // 获取符合条件的入库退货明细,品类+公司维度
        Map<String, WarehousingReturnDetail> enableScoreMap = getEnableScoreMap(dbScoreItems, templateDTO);
        // 根据人员,指标信息,获取需要生成的评分人信息
        List<ScoreManScoringV1> saveList = getSaveList(enableScoreMap, dbScoreItems, templateDTO, man);
        //保存
        scoreManScoringV1Service.saveBatch(saveList);
        //返回,后续生成复核数据用
        return saveList;
    }

    private List<ScoreManScoringV1> getSaveList(Map<String, WarehousingReturnDetail> enableScoreMap, PerfScoreItems dbScoreItems, PerfTemplateDTO templateDTO, List<PerfScoreItemsMan> man) {
        List<PerfTemplateDimWeightDTO> dimWeightDTOList = templateDTO.getPerfTemplateDimWeightList();
        List<PerfTemplateCategory> categoryList = templateDTO.getPerfTemplateCategoryList();
        // 模型的指标新消息
        List<PerfTemplateLine> totalList = new ArrayList<>();
        dimWeightDTOList.forEach(item -> {
            List<PerfTemplateLineDTO> perfTemplateLineList = item.getPerfTemplateLineList();
            for (PerfTemplateLineDTO perfTemplateLineDTO : perfTemplateLineList) {
                totalList.add(perfTemplateLineDTO.getPerfTemplateLine());
            }
        });
        List<PerfTemplateDimWeight> perfTemplateDimWeights = dimWeightDTOList.stream().map(item -> item.getPerfTemplateDimWeight()).collect(Collectors.toList());
        Map<Long, PerfTemplateCategory> categoryIdMap = categoryList.stream().collect(Collectors.toMap(PerfTemplateCategory::getCategoryId, Function.identity(), (k1, k2) -> k2));
        Map<Long, PerfTemplateDimWeight> dimWeightMap = perfTemplateDimWeights.stream().collect(Collectors.toMap(PerfTemplateDimWeight::getDimWeightId, Function.identity(), (k1, k2) -> k2));
        Map<Long, PerfTemplateLine> templateLineMap = totalList.stream().collect(Collectors.toMap(PerfTemplateLine::getTemplateLineId, Function.identity(), (k1, k2) -> k1));

        // 勾选的评分项目 供应商+人+指标维度
        List<PerfScoreItemManSupInd> supManIndList = perfScoreItemManSupIndService.list(Wrappers.lambdaQuery(PerfScoreItemManSupInd.class)
                .eq(PerfScoreItemManSupInd::getScoreItemsId, dbScoreItems.getScoreItemsId()));

        // 校验模板和当前分配内容做对比,没分配的要提示
        checkIfFinishToPerson(totalList,supManIndList);
        Map<String, PerfScoreItemManSupInd> manSupIndMap = supManIndList.stream().collect(Collectors.toMap(item -> item.getScoreItemsManId() + "-" + item.getIndicatorName(), Function.identity(), (k1, k2) -> k2));

        // 将人+人对应的任务,生成评分数据 saveList
        List<ScoreManScoringV1> saveList = new ArrayList<>();
        Map<Long, PerfScoreItemsMan> manIdMap = man.stream().collect(Collectors.toMap(PerfScoreItemsMan::getScoreItemsManId, Function.identity()));
        for (String key : enableScoreMap.keySet()) {
            WarehousingReturnDetail warehousingReturnDetail = enableScoreMap.get(key);
            for (String manIndKey : manSupIndMap.keySet()) {
                PerfScoreItemManSupInd manInd = manSupIndMap.get(manIndKey);
                PerfTemplateDimWeight dimWeight = dimWeightMap.get(manInd.getTemplateDimWeightId());
                PerfTemplateLine perfTemplateLine = templateLineMap.get(manInd.getTemplateLineId());
                PerfScoreItemsMan perfScoreItemsMan = manIdMap.get(manInd.getScoreItemsManId());
                ScoreManScoringV1 scoreManScoringV1 = new ScoreManScoringV1();
                scoreManScoringV1.setScoreManScoringId(IdGenerator.generate());
                scoreManScoringV1.setStatus(ScoreItemsProjectStatusEnum.SCORE_DRAFT.getValue());
                scoreManScoringV1.setScoreItemsId(dbScoreItems.getScoreItemsId());
                scoreManScoringV1.setTemplateHeadId(dbScoreItems.getTemplateHeadId());
                scoreManScoringV1.setTemplateName(dbScoreItems.getTemplateName());
                scoreManScoringV1.setProjectName(dbScoreItems.getProjectName());
                scoreManScoringV1.setEvaluationPeriod(dbScoreItems.getEvaluationPeriod());
                scoreManScoringV1.setPerStartMonth(dbScoreItems.getPerStartMonth());
                scoreManScoringV1.setPerEndMonth(dbScoreItems.getPerEndMonth());
                scoreManScoringV1.setOrganizationId(dbScoreItems.getOrganizationId());
                scoreManScoringV1.setOrganizationName(dbScoreItems.getOrganizationName());
                scoreManScoringV1.setScoreItemsCreatedUsername(dbScoreItems.getCreatedBy());
                scoreManScoringV1.setScoreItemsCreatedNickname(dbScoreItems.getCreatedFullName());
                scoreManScoringV1.setCategoryId(warehousingReturnDetail.getCategoryId());
                scoreManScoringV1.setCategoryCode(warehousingReturnDetail.getCategoryCode());
                scoreManScoringV1.setCategoryName(warehousingReturnDetail.getCategoryName());
                scoreManScoringV1.setCategoryFullName(categoryIdMap.get(warehousingReturnDetail.getCategoryId()).getCategoryFullName());
                scoreManScoringV1.setCompanyId(warehousingReturnDetail.getVendorId());
                scoreManScoringV1.setCompanyCode(warehousingReturnDetail.getVendorCode());
                scoreManScoringV1.setCompanyName(warehousingReturnDetail.getVendorName());
                scoreManScoringV1.setDimWeightId(manInd.getTemplateDimWeightId());
                scoreManScoringV1.setIndicatorType(dimWeight.getIndicatorType());
                scoreManScoringV1.setIndicatorDimensionType(dimWeight.getIndicatorDimensionType());
                scoreManScoringV1.setIndicatorDimensionWeight(dimWeight.getIndicatorDimensionWeight());
                scoreManScoringV1.setTemplateLineId(manInd.getTemplateLineId());
                scoreManScoringV1.setEvaluation(perfTemplateLine.getEvaluation());
                scoreManScoringV1.setIndicatorName(perfTemplateLine.getIndicatorName());
                scoreManScoringV1.setIndicatorLineType(perfTemplateLine.getIndicatorLineType());
                scoreManScoringV1.setQuoteMode(perfTemplateLine.getQuoteMode());
                scoreManScoringV1.setDimensionWeight(new BigDecimal(perfTemplateLine.getDimensionWeight() == null ? "0" : perfTemplateLine.getDimensionWeight()));
                scoreManScoringV1.setIfScored(YesOrNo.NO.getValue());
                scoreManScoringV1.setScoreUserName(perfScoreItemsMan.getScoreUserName());
                scoreManScoringV1.setScoreNickName(perfScoreItemsMan.getScoreNickName());
                scoreManScoringV1.setIndicatorLogic(perfTemplateLine.getIndicatorLogic());
                saveList.add(scoreManScoringV1);
            }
        }
        return saveList;
    }

    /**
     * 检测是否完全分配到人
     */
    private void checkIfFinishToPerson(List<PerfTemplateLine> totalList,List<PerfScoreItemManSupInd> supManIndList){
        Set<String> nameList = totalList.stream().map(PerfTemplateLine::getIndicatorName).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(supManIndList)) {
            boolean finishToPersonFlag = true;
            Map<String, PerfScoreItemManSupInd> supIndMap = supManIndList.stream().collect(Collectors.toMap(PerfScoreItemManSupInd::getIndicatorName, Function.identity(), (k1, k2) -> k2));
            StringBuilder result = new StringBuilder("存在着没分配的指标，不允许提交:<br>");
            for (String s : nameList) {
                if (!supIndMap.containsKey(s)) {
                    result.append(s).append("<br>");
                    finishToPersonFlag = false;
                }
            }
            Assert.isTrue(finishToPersonFlag, result.toString());
        } else {
            // 报错 存在着没分配的指标，不允许提交
            StringBuilder result = new StringBuilder("存在着没分配的指标，不允许提交:<br>");
            for (String s : nameList) {
                result.append(s).append("<br>");
            }
            Assert.isTrue(false, result.toString());
        }
    }

    private Map<String, WarehousingReturnDetail> getEnableScoreMap(PerfScoreItems dbScoreItems, PerfTemplateDTO templateDTO) {
        // 根据入库退货明细,获取品类信息,如果没有则提示 StorageReturn
        List<PerfTemplateCategory> categoryList = templateDTO.getPerfTemplateCategoryList();
        LocalDate endMonth = dbScoreItems.getPerEndMonth();
        List<WarehousingReturnDetail> warehousingReturnDetailList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("StorageReturn")
                .eq(WarehousingReturnDetail::getOrgId, templateDTO.getPerfTemplateHeader().getOrganizationId())
                .between(WarehousingReturnDetail::getDealDate
                        , dbScoreItems.getPerStartMonth().withDayOfMonth(1), endMonth), WarehousingReturnDetail.class);

        Assert.isTrue(CollectionUtils.isNotEmpty(warehousingReturnDetailList), "时间范围内,入库退货明细中没有合适的品类信息");
        log.info("入库退货明细信息:" + JSONArray.toJSONString(warehousingReturnDetailList));
        Map<String, WarehousingReturnDetail> vendorCategoryIdMap = warehousingReturnDetailList.stream().collect(Collectors.toMap(item -> item.getVendorId() + "-" + item.getCategoryId(), Function.identity(), (k1, k2) -> k2));
        Map<String, WarehousingReturnDetail> resultMap = new HashMap<>(16);
        List<PerfScoreItemsSup> supList = perfScoreItemsSupService.list(PerfScoreItemsSup::getScoreItemsId, dbScoreItems.getScoreItemsId());
        for (PerfScoreItemsSup perfScoreItemsSup : supList) {
            for (PerfTemplateCategory perfTemplateCategory : categoryList) {
                String key = perfScoreItemsSup.getCompanyId() + "-" + perfTemplateCategory.getCategoryId();
                if (vendorCategoryIdMap.containsKey(key)) {
                    resultMap.put(key, vendorCategoryIdMap.get(key));
                }
            }
        }
        Assert.isTrue(!resultMap.isEmpty(), "时间范围内,入库退货明细中没有合适的品类信息.");

        log.info("最终符合条件的入库退货明细:" + JSONObject.toJSONString(resultMap));
        return resultMap;
    }

    /**
     * 考虑入库退货明细生成复核数据
     *
     * @param saveList
     */
    private void saveOrderCheckNew(List<ScoreManScoringV1> saveList, PerfScoreItems dbScoreItems) {
        PerfTemplateHeader perfTemplateHeader = perfTemplateHeaderService.getById(dbScoreItems.getTemplateHeadId());
        // 1. 按供应商维度建头信息
        // 2. 根据人+品类维度建立明细
        List<PerfScoreItemsOrderCheckDTO> dtoList = new ArrayList<>();
        // 组装保存数据
        if (CollectionUtils.isNotEmpty(saveList)) {
            Map<Long, List<ScoreManScoringV1>> companyMap = saveList.stream().collect(Collectors.groupingBy(ScoreManScoringV1::getCompanyId));
            for (Long key : companyMap.keySet()) {
                List<ScoreManScoringV1> scoreManScoringV1List = companyMap.get(key);
                ScoreManScoringV1 scoreManScoringV1 = scoreManScoringV1List.get(0);
                Map<String, List<ScoreManScoringV1>> manCategoryMap = scoreManScoringV1List.stream().collect(Collectors.groupingBy(item -> item.getScoreUserName() + "-" + item.getCategoryId()));
                PerfScoreItemsOrderCheckDTO dto = new PerfScoreItemsOrderCheckDTO();
                long id = IdGenerator.generate();
                dto.setOrderCheckId(id);
                dto.setScoreItemsId(dbScoreItems.getScoreItemsId());
                dto.setProjectName(dbScoreItems.getProjectName());
                dto.setOrganizationId(perfTemplateHeader.getOrganizationId());
                dto.setOrganizationName(perfTemplateHeader.getOrganizationName());
                dto.setCompanyId(key);
                dto.setCompanyCode(scoreManScoringV1.getCompanyCode());
                dto.setCompanyName(scoreManScoringV1.getCompanyName());
                dto.setPerStartMonth(dbScoreItems.getPerStartMonth());
                dto.setPerEndMonth(dbScoreItems.getPerEndMonth());
                dto.setStatus(OrderCheckStatusEnum.TO_BE_SCORE.name());
                List<PerfScoreItemsOrderCheckDetail> detailList = new ArrayList<>();
                for (String manCategoryKey : manCategoryMap.keySet()) {
                    ScoreManScoringV1 scoreManScoring = manCategoryMap.get(manCategoryKey).get(0);
                    PerfScoreItemsOrderCheckDetail checkDetail = new PerfScoreItemsOrderCheckDetail();
                    checkDetail.setOrderCheckDetailId(IdGenerator.generate());
                    checkDetail.setOrderCheckId(id);
                    checkDetail.setScoreUserName(scoreManScoring.getScoreUserName());
                    checkDetail.setScoreNickName(scoreManScoring.getScoreNickName());
                    checkDetail.setCategoryId(scoreManScoring.getCategoryId());
                    checkDetail.setCategoryCode(scoreManScoring.getCategoryCode());
                    checkDetail.setCategoryName(scoreManScoring.getCategoryName());
                    checkDetail.setStatus(OrderCheckDetailStatusEnum.DRAFT.name());
                    detailList.add(checkDetail);
                }
                dto.setDetailList(detailList);
                dtoList.add(dto);
            }
        }
        perfScoreItemsOrderCheckService.batchSaveOrderCheckList(dtoList);
    }
}
