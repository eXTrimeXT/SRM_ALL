package com.midea.cloud.srm.perf.projectscoreman.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItemsPerson;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManDetail;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManRejectInfo;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreItemCheckStatusEnum;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreManStatusEnum;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.perf.projectscoreitem.service.ProjectScoreItemsPersonService;
import com.midea.cloud.srm.perf.projectscoreitem.service.ProjectScoreItemsService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManDetailService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManRejectInfoService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManService;
import com.mideacloud.common.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 备注
 *
 * @author huangbf3
 */
@Service
@Slf4j
public class ProjectScoreManFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private ProjectScoreManService projectScoreManService;

    @Autowired
    private ProjectScoreItemsPersonService projectScoreItemsPersonService;

    @Autowired
    private ProjectScoreItemsService projectScoreItemsService;

    @Autowired
    private ProjectScoreManRejectInfoService rejectInfoService;

    @Autowired
    private ProjectScoreManDetailService projectScoreManDetailService;

    @Autowired
    private ProjectScoreManRejectInfoService projectScoreManRejectInfoService;

    @Value("${bpm.jixiao.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private PjProjectExtClient projectExtClient;

    @Autowired
    private BaseClient baseClient;

    private void updateStatus(Long businessId, String status) {
        projectScoreManService.update(Wrappers.lambdaUpdate(ProjectScoreMan.class)
                .set(ProjectScoreMan::getApproveStatus, status)
                .eq(ProjectScoreMan::getProjectScoreManId, businessId));
        // 更新复核子表信息
        ProjectScoreMan projectScoreMan = projectScoreManService.getById(businessId);
        Long projectScoreItemsId = projectScoreMan.getProjectScoreItemsId();
        String scoreManAccount = projectScoreMan.getScoreManAccount();
        Long categoryId = projectScoreMan.getCategoryId();
        List<ProjectScoreItemsPerson> list = projectScoreItemsPersonService.list(Wrappers.lambdaQuery(ProjectScoreItemsPerson.class)
                .eq(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreItemsId)
                .eq(ProjectScoreItemsPerson::getScoreManAccount, scoreManAccount)
                .eq(ProjectScoreItemsPerson::getCategoryId, categoryId));
        list.stream().forEach(item -> item.setApproveStatus(status));
        projectScoreItemsPersonService.updateBatchById(list);
    }


    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ProjectScoreManStatusEnum.SUBMITTED.name());
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ProjectScoreManStatusEnum.APPROVED.name());
        ProjectScoreMan projectScoreMan = projectScoreManService.getById(businessId);
        List<ProjectScoreItemsPerson> list = projectScoreItemsPersonService.list(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreMan.getProjectScoreItemsId());
        Map<String, List<ProjectScoreItemsPerson>> groupMap = list.stream().collect(Collectors.groupingBy(ProjectScoreItemsPerson::getApproveStatus));
        if (groupMap.size() == 1 && groupMap.containsKey(ProjectScoreManStatusEnum.APPROVED.name())) {
            // 只有全部评分审批通过，才是待复核
            projectScoreItemsService.update(Wrappers.lambdaUpdate(ProjectScoreItems.class)
                    .eq(ProjectScoreItems::getProjectScoreItemsId, projectScoreMan.getProjectScoreItemsId())
                    .set(ProjectScoreItems::getCheckStatus, ProjectScoreItemCheckStatusEnum.WITHOUT_CHECK.name()));
            try {
                // 发送钉钉提醒招标负责人
                ProjectScoreItems scoreItems = projectScoreItemsService.getById(projectScoreMan.getProjectScoreItemsId());
                Pattern pattern = Pattern.compile("\\((.*?)\\)");
                String bidManager = projectScoreMan.getBidManager();
                Matcher matcher = pattern.matcher(bidManager);
                if (matcher.find()) {
                    String bidManagerStr = matcher.group(1);
                    projectExtClient.workNotices("您好，" + scoreItems.getContractName() + "项目，履约评价评分已完毕，请进行复核", Arrays.asList(bidManagerStr));
                } else {
                    log.info("待复核提醒失败,招标负责人信息获取失败");
                }
            } catch (Exception e) {
                    log.error("待复核提醒失败,招标负责人信息获取失败:"+e);
                    log.error("待复核提醒失败,招标负责人信息获取失败:"+e.getMessage());
            }
        }
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ProjectScoreManStatusEnum.FLOW_REJECT.name());
        // 轮次+1
        ProjectScoreMan projectScoreMan = projectScoreManService.getById(businessId);
        BigDecimal currentRound = new BigDecimal(String.valueOf(projectScoreMan.getScoreRound()));
        BigDecimal newRound = currentRound.add(BigDecimal.ONE);
        projectScoreMan.setScoreRound(newRound);
        projectScoreManService.updateById(projectScoreMan);
        // 增加驳回新下
        ProjectScoreManRejectInfo rejectInfo = new ProjectScoreManRejectInfo();
        rejectInfo.setRejectInfoId(IdGenerator.generate());
        rejectInfo.setProjectScoreManId(businessId);
        rejectInfo.setScoreRound(currentRound);
        rejectInfo.setRejectInfo("审批流程驳回");
        rejectInfo.setRejectDate(LocalDate.now());
        rejectInfoService.save(rejectInfo);
        // 更新复核主表
        projectScoreItemsService.update(Wrappers.lambdaUpdate(ProjectScoreItems.class)
                .eq(ProjectScoreItems::getProjectScoreItemsId, projectScoreMan.getProjectScoreItemsId())
                .set(ProjectScoreItems::getCheckStatus, ProjectScoreItemCheckStatusEnum.DRAFT.name()));
        // 更新主键ID,后续发起新流程
        long newId = IdGenerator.generate();
        projectScoreManService.update(Wrappers.lambdaUpdate(ProjectScoreMan.class)
                .set(ProjectScoreMan::getProjectScoreManId, newId)
                .eq(ProjectScoreMan::getProjectScoreManId, businessId));
        projectScoreManDetailService.update(Wrappers.lambdaUpdate(ProjectScoreManDetail.class)
                .set(ProjectScoreManDetail::getProjectScoreManId, newId)
                .eq(ProjectScoreManDetail::getProjectScoreManId, businessId));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ProjectScoreManStatusEnum.WITHDRAW.name());
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ProjectScoreManStatusEnum.ABANDONED.name());
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------绩效getDataPushFlow-----------businessId:{}---param:{}", businessId, param);
        ProjectScoreMan projectScoreMan = projectScoreManService.getById(businessId);
        // 1.主表
        String mainTable = "BO_EU_PFXX";
        Map<String, Object> projectScoreManMap = new HashMap<>(16);
        // 评分项目名称
        projectScoreManMap.put("PFXMMC", projectScoreMan.getProjectName());
        // 合同编码
        projectScoreManMap.put("HTBH", projectScoreMan.getContractNo());
        // 合同名称
        projectScoreManMap.put("HTMC", projectScoreMan.getContractName());
        // 板块
        projectScoreManMap.put("BK", projectScoreMan.getBuOrganizationName());
        // 公司
        projectScoreManMap.put("GS", projectScoreMan.getBuOrganizationName());
        // 供应商编码
        projectScoreManMap.put("GYSBM", projectScoreMan.getCompanyCode());
        // 供应商名称
        projectScoreManMap.put("GYSMC", projectScoreMan.getCompanyName());
        // 招标编号
        projectScoreManMap.put("ZBBH", projectScoreMan.getBidCode());
        // 招标结束时间
        projectScoreManMap.put("ZBJSSS", projectScoreMan.getBidEndDate());
        // 履约阶段
        projectScoreManMap.put("LXJD", getDictName("MILESTONE_SCHEDULE", projectScoreMan.getPerformanceCode()));
        // 品类
        projectScoreManMap.put("PL", projectScoreMan.getCategoryName());
        // 审核状态
        projectScoreManMap.put("SHZT", getDictName("PROJECT_SCORE_MAN_STATUS", projectScoreMan.getApproveStatus()));
        // 评分人
        projectScoreManMap.put("PFR", projectScoreMan.getScoreManName());
        // 评分时间
        projectScoreManMap.put("PFSJ", projectScoreMan.getScoreDate());

        // 2.评分详情子表
        List<Object> itemDataList = new ArrayList<>();
        List<ProjectScoreManDetail> manDetailList = projectScoreManDetailService.list(ProjectScoreManDetail::getProjectScoreManId, businessId);
        Map<String, String> fileMap = baseClient.getDictItmeMapByDictCode("INDICATORS_DIM");
        for (ProjectScoreManDetail scoreManDetail : manDetailList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_PFXQ");
            // 指标维度
            map.put("ZBWD", fileMap.get(scoreManDetail.getIndicatorDimensionType()));
            // 指标名称
            map.put("ZBMC", scoreManDetail.getIndicatorName());
            // 打分逻辑
            map.put("DFLJ", scoreManDetail.getIndicatorLogic());
            // 百分制打分
            map.put("BFZDF", scoreManDetail.getPefScore());
            // 绩效得分
            map.put("JXDF", scoreManDetail.getScore());
            // 打分说明
            map.put("DFSM", scoreManDetail.getComments());
            // 相关附件
            if (scoreManDetail.getFileId() != null) {
                map.put("XGFJ", BpmResult.getFileList(addressPath, scoreManDetail.getFileName(), scoreManDetail.getFileId()));
            } else {
                map.put("XGFJ", new ArrayList<>());
            }
            itemDataList.add(map);
        }

        // 3.驳回信息
        List<ProjectScoreManRejectInfo> rejectInfoList = projectScoreManRejectInfoService.list(ProjectScoreManRejectInfo::getProjectScoreManId, businessId);
        for (ProjectScoreManRejectInfo rejectInfo : rejectInfoList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_FHBHXX");
            // 轮次
            map.put("LC", rejectInfo.getScoreRound());
            // 招标驳回说明
            map.put("ZBBHSM", rejectInfo.getRejectInfo());
            // 驳回时间
            map.put("BHSJ", rejectInfo.getRejectDate());

            itemDataList.add(map);
        }

        String processTitle = "绩效评分递交-" + projectScoreMan.getProjectName();
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
//        String createUser = loginAppUser.getUsername();
        String createUser = "GW00244106";
        Long orgId = projectScoreMan.getOuOrganizationId();
        SccPjOrganization pjOrg = projectExtClient.getHrOrganizationInfo(orgId);
        String createOrgId = null;
        if (pjOrg == null || pjOrg.getId() == null) {
            SccPjUser sccPjUser = projectExtClient.getSccUserByPersonnelNo(createUser);
            if (sccPjUser != null && sccPjUser.getGroupId() != null) {
                createOrgId = String.valueOf(sccPjUser.getGroupId());
            }
        } else {
            createOrgId = String.valueOf(pjOrg.getId());
        }
        SccPjUser sccPjUser = projectExtClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        List<String> tableList = new ArrayList<>();
        tableList.add("BO_EU_PFXQ");
        tableList.add("BO_EU_FHBHXX");
        Map<String, Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_PFXQ", BpmResult.getFileField("XGFJ"));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, projectScoreManMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        log.info("绩效推送bpmjson:" + JSONObject.toJSONString(dataPushFlowJsn));
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public String getDictName(String dictCode, String va) {
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode(dictCode);
        for (DictItemDTO e : gyqyList) {
            if (e.getDictItemCode().equals(va)) {
                return e.getDictItemName();
            }
        }
        return null;
    }
}
