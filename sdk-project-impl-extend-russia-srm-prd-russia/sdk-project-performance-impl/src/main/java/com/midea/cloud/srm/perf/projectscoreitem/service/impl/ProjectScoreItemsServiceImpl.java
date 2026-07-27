package com.midea.cloud.srm.perf.projectscoreitem.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.contract.ContractType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.perf.level.entity.PerfLevel;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreDim;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreInd;
import com.midea.cloud.srm.model.perf.projectscore.enums.ProjectScorePerformanceTypeEnum;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsQueryDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItemsPerson;
import com.midea.cloud.srm.model.perf.projectscoreitem.enums.ProjectScoreHeaderStatusEnum;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManDetail;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManRejectInfo;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreItemCheckStatusEnum;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreItemStatusEnum;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreManStatusEnum;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateDimWeight;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateLine;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.perf.level.service.IPjPerfLevelService;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreDimService;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreHeaderService;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreIndService;
import com.midea.cloud.srm.perf.projectscoreitem.mapper.ProjectScoreItemsMapper;
import com.midea.cloud.srm.perf.projectscoreitem.service.ProjectScoreItemsPersonService;
import com.midea.cloud.srm.perf.projectscoreitem.service.ProjectScoreItemsService;
import com.midea.cloud.srm.perf.projectscoreman.mapper.ProjectScoreManMapper;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManDetailService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManRejectInfoService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManService;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsOrderCheckService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateDimWeightService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateHeaderService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateLineService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.mideacloud.common.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 100014337
 */
@Slf4j
@Service
public class ProjectScoreItemsServiceImpl extends BaseServiceImpl<ProjectScoreItemsMapper, ProjectScoreItems> implements ProjectScoreItemsService {

    @Autowired
    private ProjectScoreItemsPersonService projectScoreItemsPersonService;

    @Autowired
    private IPerfTemplateHeaderService perfTemplateHeaderService;

    @Autowired
    private IPerfTemplateLineService perfTemplateLineService;

    @Autowired
    private IPerfTemplateDimWeightService perfTemplateDimWeightService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private ProjectScoreManMapper projectScoreManMapper;

    @Autowired
    private ProjectScoreManService projectScoreManService;

    @Autowired
    private ProjectScoreManRejectInfoService projectScoreManRejectInfoService;

    @Autowired
    private ProjectScoreManDetailService projectScoreManDetailService;

    @Autowired
    private IPjPerfLevelService pjPerfLevelService;

    @Autowired
    private IPerfScoreItemsOrderCheckService perfScoreItemsOrderCheckService;

    @Autowired
    private ProjectScoreHeaderService projectScoreHeaderService;

    @Autowired
    private ProjectScoreDimService projectScoreDimService;

    @Autowired
    private ProjectScoreIndService projectScoreIndService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private BaseClient baseClient;

    private final String PERF_REMIND_LIST = "PERF_REMIND_LIST";

    @Override
    public List<ProjectScoreItems> listPage(ProjectScoreItemsQueryDTO queryDTO) {
        LambdaQueryWrapper<ProjectScoreItems> wrapper = Wrappers.lambdaQuery(ProjectScoreItems.class);
        if (queryDTO != null) {
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getContractName()), ProjectScoreItems::getContractName, queryDTO.getContractName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getBidCode()), ProjectScoreItems::getBidCode, queryDTO.getBidCode());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getPerformanceCode()), ProjectScoreItems::getPerformanceCode, queryDTO.getPerformanceCode());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getProjectName()), ProjectScoreItems::getProjectName, queryDTO.getProjectName());
            wrapper.like(StringUtils.isNotEmpty(queryDTO.getBuOrganizationName()), ProjectScoreItems::getBuOrganizationName, queryDTO.getBuOrganizationName());
            wrapper.eq(StringUtils.isNotEmpty(queryDTO.getProjectStatus()), ProjectScoreItems::getProjectStatus, queryDTO.getProjectStatus());
            wrapper.eq(StringUtils.isNotEmpty(queryDTO.getCheckStatus()), ProjectScoreItems::getCheckStatus, queryDTO.getCheckStatus());
            wrapper.in(CollectionUtils.isNotEmpty(queryDTO.getProjectStatusList()), ProjectScoreItems::getProjectStatus, queryDTO.getProjectStatusList());
            if (queryDTO.getCreateStartDate() != null) {
                wrapper.ge(ProjectScoreItems::getCreationDate, queryDTO.getCreateStartDate());
            }
            if (queryDTO.getCreateEndDate() != null) {
                wrapper.le(ProjectScoreItems::getCreationDate, queryDTO.getCreateEndDate());
            }
        }
        wrapper.orderByDesc(ProjectScoreItems::getLastUpdateDate);
        return this.list(wrapper);
    }

    @Override
    public ProjectScoreItemsDTO getDetailById(Long projectScoreItemsId) {
        ProjectScoreItemsDTO dto = new ProjectScoreItemsDTO();
        ProjectScoreItems byId = this.getById(projectScoreItemsId);
        BeanUtils.copyProperties(byId, dto);
        List<ProjectScoreItemsPerson> list = projectScoreItemsPersonService.list(Wrappers.lambdaQuery(ProjectScoreItemsPerson.class).eq(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreItemsId));
        dto.setPersonList(list);
        return dto;
    }

    @Override
    public Long saveOrUpdateDetail(ProjectScoreItemsDTO dto) {
        List<ProjectScoreItemsPerson> personList = dto.getPersonList();
        ProjectScoreItems projectScoreItems = new ProjectScoreItems();
        BeanUtils.copyProperties(dto, projectScoreItems);
        if (projectScoreItems.getProjectScoreItemsId() == null) {
            // 校验合同+履约节点是否已存在
            if (StringUtils.isNotEmpty(projectScoreItems.getContractNo())
                    && StringUtils.isNotEmpty(projectScoreItems.getPerformanceCode())) {
                ProjectScoreItems dbItem = this.selectFirst(Wrappers.lambdaQuery(ProjectScoreItems.class)
                        .eq(ProjectScoreItems::getContractNo, projectScoreItems.getContractNo())
                        .eq(ProjectScoreItems::getPerformanceCode, projectScoreItems.getPerformanceCode()));
                if (dbItem != null) {
                    throw new BaseException("合同+履约节点已存在项目化绩效项目,请勿重复新建");
                }
            }
            long id = IdGenerator.generate();
            projectScoreItems.setProjectScoreItemsId(id);
            if (CollectionUtils.isNotEmpty(personList)) {
                personList.stream().forEach(item -> item.setProjectScoreItemsId(id));
            }
        }else{
            if (CollectionUtils.isNotEmpty(personList)) {
                personList.stream().forEach(item -> item.setProjectScoreItemsId(projectScoreItems.getProjectScoreItemsId()));
            }
        }
        projectScoreItems.setProjectStatus(ProjectScoreItemStatusEnum.DRAFT.name());
        this.saveOrUpdate(projectScoreItems);
        projectScoreItemsPersonService.saveOrUpdate(projectScoreItems.getProjectScoreItemsId(), personList, ProjectScoreItemsPerson::getProjectScoreItemsId);
        return projectScoreItems.getProjectScoreItemsId();
    }

    private void checkBeforeNotifyScorers(Long projectScoreItemsId) {
        Long countPerson = projectScoreItemsPersonService.lambdaQuery().eq(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreItemsId).count();
        if(ObjectUtils.defaultIfNull(countPerson, SrmConstant.LONG_ZERO).compareTo(new Long(SrmConstant.NUM_TWO)) == -1) {
            throw new BaseException(MessageFormat.format("要求评分人大于等于两人，当前评分人数为{0}不满足条件", countPerson));
        }
    }

    @Override
    public void notifyScorers(Long projectScoreItemsId) {
        checkBeforeNotifyScorers(projectScoreItemsId);
        // 校验当前项目是否已通知评分
        List<ProjectScoreMan> dbList = projectScoreManService.list(ProjectScoreMan::getProjectScoreItemsId, projectScoreItemsId);
        Assert.isTrue(CollectionUtils.isEmpty(dbList), "当前项目已通知评分,请勿重复操作");
        //1. 生成项目化绩效评分人数据
        saveScoreManList(projectScoreItemsId);
        //2. 修改绩效项目自己的状态,与复核同表,更新复核状态待评分
        this.update(Wrappers.lambdaUpdate(ProjectScoreItems.class)
                .set(ProjectScoreItems::getProjectStatus, ProjectScoreItemStatusEnum.SCORE_NOTIFIED.name())
                .set(ProjectScoreItems::getCheckStatus, ProjectScoreItemCheckStatusEnum.DRAFT.name())
                .eq(ProjectScoreItems::getProjectScoreItemsId, projectScoreItemsId));
        //3.更新复核明细状态,跟绩效评分人的状态通用
        projectScoreItemsPersonService.update(Wrappers.lambdaUpdate(ProjectScoreItemsPerson.class)
                .set(ProjectScoreItemsPerson::getApproveStatus, ProjectScoreManStatusEnum.DRAFT.name())
                .eq(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreItemsId)
        );
        // 4. 发送钉钉给评分人-有项目化履约绩效需要您评分，请及时处理
        try {
            List<ProjectScoreItemsPerson> personList = projectScoreItemsPersonService.list(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreItemsId);
            List<String> accountList = personList.stream().map(ProjectScoreItemsPerson::getScoreManAccount).collect(Collectors.toList());
            pjProjectExtClient.workNotices("有项目化履约绩效需要您评分，请及时处理", accountList);
        } catch (Exception e) {
            log.error("项目化绩效,钉钉通知评分人失败");
            log.error(e.getMessage());
            log.error("" + e);
        }
    }

    @Override
    public void reject(ProjectScoreItemsDTO dto) {
        List<ProjectScoreItemsPerson> personList = dto.getPersonList();
        Assert.isTrue(CollectionUtils.isNotEmpty(personList), "请勾选要驳回的记录");
        // 1. 按维度,把供应商评分的驳回,评分的主表状态修改
        Long projectScoreItemsId = personList.get(0).getProjectScoreItemsId();
        ProjectScoreItems projectScoreItems = this.getById(projectScoreItemsId);
        projectScoreItems.setCheckStatus(ProjectScoreItemCheckStatusEnum.DRAFT.name());
        personList.stream().forEach(item -> item.setApproveStatus(ProjectScoreManStatusEnum.CHECK_REJECT.name()));
        projectScoreItemsPersonService.updateBatchById(personList);
        // 2. 修改复核主表状态
        this.updateById(projectScoreItems);
        // 3. 评分人表状态驳回,驳回轮次
        List<ProjectScoreMan> queryList = new ArrayList<>();
        for (ProjectScoreItemsPerson projectScoreItemsPerson : personList) {
            ProjectScoreMan queryDto = new ProjectScoreMan();
            queryDto.setProjectScoreItemsId(projectScoreItemsId);
            queryDto.setScoreManAccount(projectScoreItemsPerson.getScoreManAccount());
            queryDto.setCategoryId(projectScoreItemsPerson.getCategoryId());
            queryList.add(queryDto);
        }
        List<ProjectScoreManRejectInfo> rejectInfoList = new ArrayList<>();
        List<ProjectScoreMan> scoreManList = projectScoreManMapper.listByGroupList(queryList);
        // 4. 更新评分主子表的id,重新走流程
        Map<Long, Long> idMap = new HashMap<>(16);
        scoreManList.stream().forEach(item -> idMap.put(item.getProjectScoreManId(), IdGenerator.generate()));

        scoreManList.forEach(item -> {
            item.setApproveStatus(ProjectScoreManStatusEnum.CHECK_REJECT.name());
            BigDecimal currentRound = item.getScoreRound();
            BigDecimal newRound = currentRound.add(BigDecimal.ONE);
            item.setScoreRound(newRound);
            // 3. 增加驳回信息
            ProjectScoreManRejectInfo rejectInfo = new ProjectScoreManRejectInfo();
            rejectInfo.setRejectInfoId(IdGenerator.generate());
            rejectInfo.setProjectScoreManId(item.getProjectScoreManId());
            rejectInfo.setScoreRound(currentRound);
            rejectInfo.setRejectInfo(dto.getRejectRemark());
            rejectInfo.setRejectDate(LocalDate.now());
            rejectInfoList.add(rejectInfo);
        });
        projectScoreManService.updateBatchById(scoreManList);
        projectScoreManRejectInfoService.saveBatch(rejectInfoList);

        for (Long oldId : idMap.keySet()) {
            projectScoreManService.update(Wrappers.lambdaUpdate(ProjectScoreMan.class)
                    .eq(ProjectScoreMan::getProjectScoreManId, oldId)
                    .set(ProjectScoreMan::getProjectScoreManId, idMap.get(oldId)));
            projectScoreManDetailService.update(Wrappers.lambdaUpdate(ProjectScoreManDetail.class)
                    .eq(ProjectScoreManDetail::getProjectScoreManId, oldId)
                    .set(ProjectScoreManDetail::getProjectScoreManId, idMap.get(oldId)));
            projectScoreManRejectInfoService.update(Wrappers.lambdaUpdate(ProjectScoreManRejectInfo.class)
                    .eq(ProjectScoreManRejectInfo::getProjectScoreManId, oldId)
                    .set(ProjectScoreManRejectInfo::getProjectScoreManId, idMap.get(oldId)));
        }
        // 发送钉钉给绩效评分人
        List<String> scoreManAccountList = personList.stream().map(ProjectScoreItemsPerson::getScoreManAccount).distinct().collect(Collectors.toList());
        for (String scoreManAccount : scoreManAccountList) {
            try {
                pjProjectExtClient.workNotices("您好，" + projectScoreItems.getContractName() + "项目，履约评价评分已驳回，请登录SRM平台修改", Arrays.asList(scoreManAccount));
            } catch (Exception e) {
                log.error("驳回评分发送钉钉异常");
                log.error("驳回评分发送钉钉异常" + e);
                log.error("驳回评分发送钉钉异常" + e.getMessage());
            }
        }
    }

    @Override
    public void calcScore(Long projectScoreItemsId) {
        // todo 校验是否全部提交
        ProjectScoreItems projectScoreItems = this.getById(projectScoreItemsId);
        Assert.isTrue(!ProjectScoreItemCheckStatusEnum.CALCULATED_SCORE.name().equals(projectScoreItems.getCheckStatus()), "已计算得分,不可重复计算");
        List<ProjectScoreMan> scoreManList = projectScoreManService.list(ProjectScoreMan::getProjectScoreItemsId, projectScoreItemsId);
        List<Long> scoreManIdList = scoreManList.stream().map(ProjectScoreMan::getProjectScoreManId).collect(Collectors.toList());
        List<ProjectScoreManDetail> detailList = projectScoreManDetailService.listIn(ProjectScoreManDetail::getProjectScoreManId, scoreManIdList);
        Map<String, List<ProjectScoreMan>> groupMap = scoreManList.stream().collect(Collectors.groupingBy(item -> item.getProjectName() + "-" + item.getCategoryId()));

        //组装绩效成绩数据,保存入库
        List<ProjectScoreHeader> headerList = savePorjectScore(groupMap, detailList);
        List<ProjectScoreHeader> dbHeaderList = saveProjectTypeHeader(headerList);
        // 根据dbHeaderList的信息,低于60分发送钉钉提醒
        sendDingDing(dbHeaderList);

        // 更新复核状态
        this.update(Wrappers.lambdaUpdate(ProjectScoreItems.class)
                .eq(ProjectScoreItems::getProjectScoreItemsId, projectScoreItemsId)
                .set(ProjectScoreItems::getCheckStatus, ProjectScoreItemCheckStatusEnum.CALCULATED_SCORE.name()));
    }

    private void sendDingDing(List<ProjectScoreHeader> dbHeaderList) {
        // 定于60分发送到系统配置的账户
        if (CollectionUtils.isNotEmpty(dbHeaderList)) {
            // 获取字典,获取配置的账户信息
            Map<String, String> dictItemMap = baseClient.getDictItmeMapByDictCode("MILESTONE_SCHEDULE");
            SystemConfigureDTO systemConfigure = baseClient.getSystemConfigure(PERF_REMIND_LIST);
            String paramValue = systemConfigure.getParamValue();
            List<String> noticeManList = Arrays.asList(paramValue.split(","));
            dbHeaderList.stream().filter(item -> item.getScore().compareTo(BigDecimal.valueOf(60)) < 0 && StringUtils.isNotEmpty(item.getPerformanceType())).forEach(item -> {
                try {
                    pjProjectExtClient.workNotices("您好，" + item.getContractName() + "项目，" + item.getCompanyName() + "供应商" + dictItemMap.get(item.getPerformanceType()) + "节点得分小于60分，请跟踪确认", noticeManList);
                } catch (Exception e) {
                    log.error("分数低于60分发送钉钉异常");
                    log.error("分数低于60分发送钉钉异常" + e);
                }
            });
        }
    }

    private List<ProjectScoreHeader> saveProjectTypeHeader(List<ProjectScoreHeader> headerList) {
        // 增加汇总的header记录,保存/更新 需要合同履约数据 PerPlan PerPlanMilestone
        if (CollectionUtils.isNotEmpty(headerList)) {
            String contractNo = headerList.get(0).getContractNo();
            // 获取合同履约的所有节点,判断和dbHeader的节点是否全部都有,
            List<Record> perPlanRecords = qlOpenClient.query(ContextPath.CM, QlOpenWrappers.query("PerPlan")
                    .eq("contractNo", contractNo), Record.class);
            List<String> milestoneTypeList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(perPlanRecords)) {
                Long perPlanId = perPlanRecords.get(0).getLong("perPlanId");
                List<Record> perPlanMilestoneRecords = qlOpenClient.query(ContextPath.CM, QlOpenWrappers.query("PerPlanMilestone")
                        .eq("perPlanId", perPlanId), Record.class);
                milestoneTypeList = perPlanMilestoneRecords.stream().map(item -> item.getString("milestoneType")).collect(Collectors.toList());
            }

            List<PerfLevel> levelList = pjPerfLevelService.list(Wrappers.lambdaQuery(new PerfLevel().setStatus(Enable.Y)));
            List<ProjectScoreHeader> dbHeaderList = projectScoreHeaderService.list(Wrappers.lambdaQuery(ProjectScoreHeader.class)
                    .eq(ProjectScoreHeader::getContractNo, contractNo));
            // 获取整体评分状态
            Map<String, String> dbCodeMap = dbHeaderList.stream()
                    .filter(header -> !header.getPerformanceType().equals(ProjectScorePerformanceTypeEnum.PROJECT.name()))
                    .collect(Collectors.toMap(ProjectScoreHeader::getPerformanceCode, ProjectScoreHeader::getPerformanceCode, (k1, k2) -> k2));
            boolean finishedFlag = true;
            for (String milestoneType : milestoneTypeList) {
                if (!dbCodeMap.containsKey(milestoneType)) {
                    finishedFlag = false;
                }
            }
            // 有就更新平均值,没有新建
            BigDecimal averageScore = dbHeaderList.stream()
                    .filter(header -> !header.getPerformanceType().equals(ProjectScorePerformanceTypeEnum.PROJECT.name()))
                    .map(ProjectScoreHeader::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(dbHeaderList.size() > 1 ? dbHeaderList.size() - 1 : 1), 2, RoundingMode.HALF_UP);
            // 过滤掉performanceType属性为PROJECT字符串的对象，获取第一个剩余对象
            if (dbHeaderList.stream().anyMatch(header -> header.getPerformanceType().equals(ProjectScorePerformanceTypeEnum.PROJECT.name()))) {
                dbHeaderList.stream()
                        .filter(header -> header.getPerformanceType().equals(ProjectScorePerformanceTypeEnum.PROJECT.name()))
                        .forEach(header -> {
                            header.setScore(averageScore);
                            header.setLevelName(perfScoreItemsOrderCheckService.getLevelByScore(averageScore, levelList).getLevelName());
                        });
            } else {
                ProjectScoreHeader projectScoreHeader = dbHeaderList.get(0);
                ProjectScoreHeader newHeader = new ProjectScoreHeader();
                BeanUtils.copyProperties(projectScoreHeader, newHeader);
                newHeader.setScoreHeaderId(IdGenerator.generate());
                newHeader.setPerformanceType(ProjectScorePerformanceTypeEnum.PROJECT.name());
                newHeader.setPerformanceCode(null);
                newHeader.setProjectScoreItemsId(null);
                newHeader.setPerformanceCode(null);
                newHeader.setProjectName(null);
                newHeader.setCalcDate(LocalDate.now());
                newHeader.setScore(averageScore);
                newHeader.setLevelName(perfScoreItemsOrderCheckService.getLevelByScore(averageScore, levelList).getLevelName());
                dbHeaderList.add(newHeader);
            }
            if (finishedFlag) {
                for (ProjectScoreHeader projectScoreHeader : dbHeaderList) {
                    projectScoreHeader.setProjectStatus(finishedFlag ? ProjectScoreHeaderStatusEnum.FINISHED.name() : ProjectScoreHeaderStatusEnum.ON_GOING.name());
                }
            }
            projectScoreHeaderService.saveOrUpdateBatch(dbHeaderList);
            return dbHeaderList;
        }
        return new ArrayList<>();
    }

    @Override
    public List<ProjectScoreManDetail> listScoreManDetailList(ProjectScoreMan projectScoreMan) {
        List<ProjectScoreMan> list = projectScoreManService.list(Wrappers.lambdaQuery(ProjectScoreMan.class)
                .eq(ProjectScoreMan::getProjectScoreItemsId, projectScoreMan.getProjectScoreItemsId())
                .eq(ProjectScoreMan::getScoreManAccount, projectScoreMan.getScoreManAccount())
                .eq(ProjectScoreMan::getCategoryId, projectScoreMan.getCategoryId()));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> manIdList = list.stream().map(ProjectScoreMan::getProjectScoreManId).collect(Collectors.toList());
            return projectScoreManDetailService.listIn(ProjectScoreManDetail::getProjectScoreManId, manIdList);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ProjectScoreItems getInfoByContractNo(ContractHead contractHead) {
        List<Record> contractHeadList = qlOpenClient.query(ContextPath.CM, QlOpenWrappers.query(ContractHead.class)
                        .eq(ContractHead::getContractNo, contractHead.getContractNo())
                        .eq(ContractHead::getContractType, ContractType.MIAN_CONTRACT_ADD.name())
                , Record.class);
        Record dbContractHead = contractHeadList.get(0);
        ProjectScoreItems result = new ProjectScoreItems();
        result.setProjectStatus(ProjectScoreItemStatusEnum.DRAFT.name());
        result.setContractNo(dbContractHead.getString("contractNo"));
        result.setContractName(dbContractHead.getString("contractName"));
        result.setCompanyId(dbContractHead.getLong("vendorId"));
        result.setCompanyCode(dbContractHead.getString("vendorCode"));
        result.setCompanyName(dbContractHead.getString("vendorName"));
        result.setBidCode(dbContractHead.getString("sourceNumber"));
        result.setBidEndDate(dbContractHead.getLocalDate("extInviteFinishDate"));
        result.setExtInvestNo(dbContractHead.getString("extInvestNo"));
        Long bidManagerId = dbContractHead.getLong("extInviteHeadId");
        Long contractManagerId = dbContractHead.getLong("extContractHandlerId");
        // 先找根据id找人,找获取上面的信息
        if (bidManagerId != null) {
            User bidManager = rbacClient.getUserByIdAnon(bidManagerId);
            HrUserOrgnizationDto hrUserOrgnizationByUsername = pjProjectExtClient.getHrUserOrgnizationByUsername(bidManager.getUsername());
            if (hrUserOrgnizationByUsername != null) {
                log.info("招标负责人-hrUserOrgnizationByUsername返回信息:" + JSONObject.toJSONString(hrUserOrgnizationByUsername));
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
                    result.setBidManager(bidManager.getNickname() + "(" + bidManager.getUsername() + ")");
                    result.setBidManagerFullPath(fullPath);
                    if (ouOrganization != null) {
                        result.setOuOrganizationId(ouOrganization.getOrganizationId());
                        result.setOuOrganizationCode(ouOrganization.getOrganizationCode());
                        result.setOuOrganizationName(ouOrganization.getOrganizationName());
                    }
                    if (buOrganization != null) {
                        result.setBuOrganizationId(buOrganization.getOrganizationId());
                        result.setBuOrganizationCode(buOrganization.getOrganizationCode());
                        result.setBuOrganizationName(buOrganization.getOrganizationName());
                    }
                }
            }
        }

        if (contractManagerId != null) {
            User contractManager = rbacClient.getUserByIdAnon(contractManagerId);
            HrUserOrgnizationDto hrUserOrgnizationByUsername = pjProjectExtClient.getHrUserOrgnizationByUsername(contractManager.getUsername());
            if (hrUserOrgnizationByUsername != null) {
                log.info("合同经办人-hrUserOrgnizationByUsername返回信息:" + JSONObject.toJSONString(hrUserOrgnizationByUsername));
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
                    result.setContractManager(contractManager.getNickname() + "(" + contractManager.getUsername() + ")");
                    result.setContractManagerFullPath(fullPath);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProjectScoreManRejectInfo> queryProjectScoreManRejectInfo(ProjectScoreItemsPerson projectScoreItemsPerson) {
        return projectScoreManRejectInfoService.lambdaQuery().eq(ProjectScoreManRejectInfo::getProjectScoreManId,projectScoreItemsPerson.getScoreManId()).list();
    }

    private List<ProjectScoreHeader> savePorjectScore(Map<String, List<ProjectScoreMan>> groupMap, List<ProjectScoreManDetail> detailList) {
        List<ProjectScoreHeader> headerList = new ArrayList<>();
        List<ProjectScoreDim> dimList = new ArrayList<>();
        List<ProjectScoreInd> indList = new ArrayList<>();
        List<PerfLevel> levelList = pjPerfLevelService.list(Wrappers.lambdaQuery(new PerfLevel().setStatus(Enable.Y)));
        for (String key : groupMap.keySet()) {
            List<ProjectScoreMan> scoreManTempList = groupMap.get(key);
            ProjectScoreMan projectScoreMan = scoreManTempList.get(0);
            int size = scoreManTempList.size();
            Map<Long, ProjectScoreMan> scoreManMap = scoreManTempList.stream().collect(Collectors.toMap(ProjectScoreMan::getProjectScoreManId, Function.identity()));
            List<ProjectScoreManDetail> detailTempList = detailList.stream().filter(item -> scoreManMap.containsKey(item.getProjectScoreManId())).collect(Collectors.toList());
            ProjectScoreHeader projectScoreHeader = new ProjectScoreHeader();
            long headId = IdGenerator.generate();
            projectScoreHeader.setScoreHeaderId(headId);
            projectScoreHeader.setCalcDate(LocalDate.now());
            projectScoreHeader.setPerformanceType(ProjectScorePerformanceTypeEnum.NODE.name());
            projectScoreHeader.setProjectScoreItemsId(projectScoreMan.getProjectScoreItemsId());
            projectScoreHeader.setProjectName(projectScoreMan.getProjectName());
            projectScoreHeader.setContractNo(projectScoreMan.getContractNo());
            projectScoreHeader.setContractName(projectScoreMan.getContractName());
            projectScoreHeader.setCategoryId(projectScoreMan.getCategoryId());
            projectScoreHeader.setCategoryCode(projectScoreMan.getCategoryCode());
            projectScoreHeader.setCategoryName(projectScoreMan.getCategoryName());
            projectScoreHeader.setPerformanceCode(projectScoreMan.getPerformanceCode());
            projectScoreHeader.setOuOrganizationId(projectScoreMan.getOuOrganizationId());
            projectScoreHeader.setOuOrganizationCode(projectScoreMan.getOuOrganizationCode());
            projectScoreHeader.setOuOrganizationName(projectScoreMan.getOuOrganizationName());
            projectScoreHeader.setBuOrganizationId(projectScoreMan.getBuOrganizationId());
            projectScoreHeader.setBuOrganizationCode(projectScoreMan.getBuOrganizationCode());
            projectScoreHeader.setBuOrganizationName(projectScoreMan.getBuOrganizationName());
            projectScoreHeader.setCompanyId(projectScoreMan.getCompanyId());
            projectScoreHeader.setCompanyCode(projectScoreMan.getCompanyCode());
            projectScoreHeader.setCompanyName(projectScoreMan.getCompanyName());
            projectScoreHeader.setBidCode(projectScoreMan.getBidCode());
            projectScoreHeader.setBidEndDate(projectScoreMan.getBidEndDate());
            projectScoreHeader.setBidManager(projectScoreMan.getBidManager());
            projectScoreHeader.setBidManagerFullPath(projectScoreMan.getBidManagerFullPath());
            projectScoreHeader.setContractManager(projectScoreMan.getContractManager());
            projectScoreHeader.setContractManagerFullPath(projectScoreMan.getContractManagerFullPath());
            projectScoreHeader.setPerStartMonth(projectScoreMan.getPerStartMonth());
            projectScoreHeader.setPerEndMonth(projectScoreMan.getPerEndMonth());
            projectScoreHeader.setProjectStatus(ProjectScoreHeaderStatusEnum.ON_GOING.name());
            BigDecimal dimScoreTotal = new BigDecimal("0");
            Map<String, List<ProjectScoreManDetail>> dimGroupMap = detailTempList.stream().collect(Collectors.groupingBy(ProjectScoreManDetail::getIndicatorDimensionType));
            for (String dimKey : dimGroupMap.keySet()) {
                List<ProjectScoreManDetail> dimDetailTempList = dimGroupMap.get(dimKey);
                ProjectScoreManDetail scoreManDetail = dimDetailTempList.get(0);
                ProjectScoreDim projectScoreDim = new ProjectScoreDim();
                long dimId = IdGenerator.generate();
                projectScoreDim.setScoreDimId(dimId);
                projectScoreDim.setScoreHeaderId(headId);
                projectScoreDim.setIndicatorDimensionWeight(projectScoreDim.getIndicatorDimensionWeight());
                projectScoreDim.setIndicatorDimensionType(projectScoreDim.getIndicatorDimensionType());
                // 按指标分组dimDetailTempList
                Map<String, List<ProjectScoreManDetail>> indGroupMap = dimDetailTempList.stream().collect(Collectors.groupingBy(ProjectScoreManDetail::getIndicatorName));
                BigDecimal indScoreTotal = new BigDecimal("0");
                for (String indKey : indGroupMap.keySet()) {
                    List<ProjectScoreManDetail> indDetailList = indGroupMap.get(indKey);
                    BigDecimal totalIndScore = indDetailList.stream().map(ProjectScoreManDetail::getScore).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal indScore = totalIndScore.divide(new BigDecimal(size), 2, BigDecimal.ROUND_CEILING);
                    ProjectScoreInd projectScoreInd = new ProjectScoreInd();
                    projectScoreInd.setScoreIndId(IdGenerator.generate());
                    projectScoreInd.setScoreHeaderId(headId);
                    projectScoreInd.setScoreDimId(dimId);
                    projectScoreInd.setIndicatorName(indDetailList.get(0).getIndicatorName());
                    // 评价方式
                    projectScoreInd.setEvaluation(indDetailList.get(0).getEvaluation());
                    projectScoreInd.setScore(indScore);
                    indList.add(projectScoreInd);
                    indScoreTotal = indScoreTotal.add(indScore);
                }
                projectScoreDim.setScore(indScoreTotal);
                dimScoreTotal = dimScoreTotal.add(indScoreTotal);
                dimList.add(projectScoreDim);
            }
            projectScoreHeader.setScore(dimScoreTotal);
            projectScoreHeader.setLevelName(perfScoreItemsOrderCheckService.getLevelByScore(dimScoreTotal, levelList).getLevelName());
            projectScoreHeader.setExtInvestNo(projectScoreMan.getExtInvestNo());
            headerList.add(projectScoreHeader);
        }
        if (CollectionUtils.isNotEmpty(headerList)) {
            projectScoreHeaderService.saveBatch(headerList);
        }
        if (CollectionUtils.isNotEmpty(dimList)) {
            projectScoreDimService.saveBatch(dimList);
        }
        if (CollectionUtils.isNotEmpty(indList)) {
            projectScoreIndService.saveBatch(indList);
        }
        return headerList;
    }

    private void saveScoreManList(Long projectScoreItemsId) {
        List<ProjectScoreMan> scoreManList = new ArrayList<>();
        List<ProjectScoreManDetail> scoreManDetailList = new ArrayList<>();
        // 1.抓取所有的模型,及模型明细数据
        ProjectScoreItems scoreItems = this.getById(projectScoreItemsId);
        List<ProjectScoreItemsPerson> personList = projectScoreItemsPersonService.list(ProjectScoreItemsPerson::getProjectScoreItemsId, projectScoreItemsId);
        List<Long> templateHeadIdList = personList.stream().map(ProjectScoreItemsPerson::getTemplateHeadId).collect(Collectors.toList());
        Long templateHeadId = templateHeadIdList.get(0);
        // 模型维度信息
        List<PerfTemplateDimWeight> dimWeightList = perfTemplateDimWeightService.list(PerfTemplateDimWeight::getTemplateHeadId, templateHeadId);
        Map<Long, PerfTemplateDimWeight> dimWeightMap = dimWeightList.stream().collect(Collectors.toMap(PerfTemplateDimWeight::getDimWeightId, Function.identity()));
        List<Long> dimWeightIdList = dimWeightList.stream().map(PerfTemplateDimWeight::getDimWeightId).collect(Collectors.toList());
        // 模型指标信息
        List<PerfTemplateLine> templateLineList = perfTemplateLineService.listIn(PerfTemplateLine::getTemplateDimWeightId, dimWeightIdList);
        // 2.绩效项目笛卡尔积生成评分人主表数据
        for (ProjectScoreItemsPerson scoreItemsPerson : personList) {
            ProjectScoreMan scoreMan = new ProjectScoreMan();
            BeanUtils.copyProperties(scoreItems, scoreMan);
            scoreMan.setProjectScoreManId(IdGenerator.generate());
            scoreMan.setApproveStatus(ProjectScoreManStatusEnum.DRAFT.name());
            scoreMan.setCategoryId(scoreItemsPerson.getCategoryId());
            scoreMan.setCategoryCode(scoreItemsPerson.getCategoryCode());
            scoreMan.setCategoryName(scoreItemsPerson.getCategoryName());
            scoreMan.setScoreManAccount(scoreItemsPerson.getScoreManAccount());
            scoreMan.setScoreManName(scoreItemsPerson.getScoreManName());
            scoreMan.setScoreManId(scoreItemsPerson.getScoreManId());
            scoreMan.setScoreRound(BigDecimal.ONE);
            scoreMan.setExtInvestNo(scoreItems.getExtInvestNo());
            scoreManList.add(scoreMan);
        }
        // 3.评分人主表数据再与绩效指标笛卡尔积,获取明细数据
        for (ProjectScoreMan scoreMan : scoreManList) {
            for (PerfTemplateLine perfTemplateLine : templateLineList) {
                PerfTemplateDimWeight dimWeight = dimWeightMap.get(perfTemplateLine.getTemplateDimWeightId());
                ProjectScoreManDetail scoreManDetail = new ProjectScoreManDetail();
                scoreManDetail.setScoreManDetailId(IdGenerator.generate());
                scoreManDetail.setProjectScoreManId(scoreMan.getProjectScoreManId());
                scoreManDetail.setTemplateLineId(perfTemplateLine.getTemplateLineId());
                scoreManDetail.setEvaluation(perfTemplateLine.getEvaluation());
                scoreManDetail.setIndicatorDimensionType(dimWeight.getIndicatorDimensionType());
                scoreManDetail.setIndicatorDimensionWeight(dimWeight.getIndicatorDimensionWeight());
                scoreManDetail.setIndicatorName(perfTemplateLine.getIndicatorName());
                scoreManDetail.setIndicatorLineType(perfTemplateLine.getIndicatorLineType());
                scoreManDetail.setQuoteMode(perfTemplateLine.getQuoteMode());
                scoreManDetail.setIndicatorLogic(perfTemplateLine.getIndicatorLogic());
                scoreManDetail.setDimensionWeight(new BigDecimal(perfTemplateLine.getDimensionWeight()));
                scoreManDetailList.add(scoreManDetail);
            }
        }
        projectScoreManService.saveBatch(scoreManList);
        projectScoreManDetailService.saveBatch(scoreManDetailList);
    }
}
