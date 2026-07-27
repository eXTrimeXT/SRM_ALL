package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.*;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.aihelper.BidReviewResDto;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.dto.ExtReplayFileDTO;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayFileDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerConfirmStatusEnum;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.analysis.ScoreAnalysisDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.analysis.ScoreAnalysisDynamicFormDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.analysis.ScoreAnalysisVendorDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RiskItemType;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.bid.enums.ReviewFileTypeEnum;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.bid.process.service.BidSouProcessQueryWebService;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.factory.VendorRiskFactory;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.req.vo.ProjectVO;
import com.midea.cloud.srm.sou.sourcing.expert.mapper.ExtSouNpmExpertMapper;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorMapper;
import com.midea.cloud.srm.sou.sourcing.init.dto.ApiExtSouTechScoreLineReviewDto;
import com.midea.cloud.srm.sou.sourcing.init.excel.writer.NpmSouBidCellWriteHandler;
import com.midea.cloud.srm.sou.sourcing.init.excel.writer.NpmSouBidMergeStrategy;
import com.midea.cloud.srm.sou.sourcing.init.mapper.*;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouInitProjectInfoQueryHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouInitQueryHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouOrderItemQueryHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouTechManagementQueryHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editmargins.ExtSouMarginRecordPo;
import com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores.ExtSouTechScorePO;
import com.midea.cloud.srm.sou.sourcing.spi.init.queryinvitesuppliers.ApiExtSouInitInviteSupplierQueryHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ExtSouInitQueryServiceImpl implements ExtSouInitQueryService {
    @Autowired
    private ExtTempMapper extTempMapper;
    @Resource
    private ExtSouProjectMapper projectMapper;

    @Autowired
    private ExtSouDemandMapper demandMapper;

    @Autowired
    private ExtSouGroupMapper groupMapper;

    @Autowired
    private ExtSouFileMapper souFileMapper;

    @Autowired
    private ExtSouPlanMapper planMapper;

    @Autowired
    private ExtSouItemMapper itemMapper;

    @Autowired
    private ExtScoreRuleMapper scoreRuleMapper;

    @Autowired
    private SouVendorMapper souVendorMapper;

    @Autowired
    private IExtSouMarginService souMarginService;

    @Autowired
    private IExtSouMarginRecordService souMarginRecordService;

    @Autowired
    private SouOrderDAO souOrderDAO;

    @Autowired
    private SouOrderItemDAO souOrderItemDAO;

    @Autowired
    private SouRoundDAO souRoundDAO;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private ExtSouTechScoreHeadMapper techScoreHeadMapper;

    @Autowired
    private IExtSouTechScoreLineService techScoreLineService;

    @Autowired
    private IExtSccSouTechScoreHistoryService techScoreHistoryService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private BidSouProcessQueryWebService bidSouProcessQueryService;

    @Autowired
    private IExtSouProcessConfigService souProcessConfigService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouRoundService souRoundService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouExpertRecordService expertRecordService;

    @Autowired
    private IExtSouExpertRiskService expertRiskService;

    @Autowired
    private ExtBidSouInitQueryWebService bidSouInitQueryWebService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private ExtNpmSouExpertService extNpmSouExpertService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouProjectService iExtSouProjectService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private ExtSouNpmExpertMapper extSouNpmExpertMapper;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private IExtNpmSouAjustTimeService iExtNpmSouAjustTimeService;

    public static final String PR_SOU_REQUIREMENT_POOL_FOR_BUYER = "PrSouRequirementPoolForBuyer";

    @Autowired
    private ExtSouTechScoreFileMapper extSouTechScoreFileMapper;

    @Autowired
    private ExtSouVendorMapper extSouVendorMapper;
    @Autowired
    private PjSouClient pjSouClient;

    private static final int NUM2 = 2;

    @Override
    public PageInfo listProjects(ApiExtSouProjectQueryDTO souProjectQuery, String souType) {
        //查询前置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitQueryHandler.class).doHandlerBeforePageProjects(souProjectQuery, souType);
        //格式化请求参数
        souProjectQuery.formatParams();
        //分页参数
        if (!Objects.isNull(souProjectQuery.getPageNum()) && !Objects.isNull(souProjectQuery.getPageSize())) {
            PageUtil.startPage(souProjectQuery.getPageNum(), souProjectQuery.getPageSize());
        }
        //查询条件
        LambdaQueryWrapper<ExtSouProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouProject::getSouType, souType);
        //招标单号
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getSouNo()), ExtSouProject::getSouNo, souProjectQuery.getSouNo());
        //项目编号
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getExtProjectNo()), ExtSouProject::getExtProjectNo, souProjectQuery.getExtProjectNo());
        //招标单状态
        queryWrapper.eq(StringUtils.isNotBlank(souProjectQuery.getProjectStatus()), ExtSouProject::getProjectStatus, souProjectQuery.getProjectStatus());
        //创建人
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getCreatedFullName()), ExtSouProject::getCreatedFullName, souProjectQuery.getCreatedFullName());
        //发布日期从
        queryWrapper.gt(!Objects.isNull(souProjectQuery.getPublishTimeFrom()), ExtSouProject::getPublishTime, souProjectQuery.getPublishTimeFrom());
        //发布日期至
        queryWrapper.lt(!Objects.isNull(souProjectQuery.getPublishTimeTo()), ExtSouProject::getPublishTime, souProjectQuery.getPublishTimeTo());
        //创建日期从
        queryWrapper.gt(!Objects.isNull(souProjectQuery.getCreationDateFrom()), ExtSouProject::getCreationDate, souProjectQuery.getCreationDateFrom());
        //创建日期至
        queryWrapper.lt(!Objects.isNull(souProjectQuery.getCreationDateTo()), ExtSouProject::getCreationDate, souProjectQuery.getCreationDateTo());
        queryWrapper.notIn("proStatusBidFlagNoArchiveDone".equals(souProjectQuery.getProjectStatusFlag()), ExtSouProject::getProjectStatus, Arrays.asList(SouBiddingProStatusEnum.ARCHIVE_DONE.getCode(), SouBiddingProStatusEnum.ABANDON.getCode()));
        //项目名称
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getSouName()), ExtSouProject::getSouName, souProjectQuery.getSouName());
        //审核状态
        queryWrapper.eq(StringUtils.isNotBlank(souProjectQuery.getCreateApprovalStatus()), ExtSouProject::getCreateApprovalStatus, souProjectQuery.getCreateApprovalStatus());

        queryWrapper.orderByDesc(ExtSouProject::getProjectId);
        List<ExtSouProject> souProjectList = projectMapper.selectList(queryWrapper);
        PageInfo pageInfo = new PageInfo(souProjectList);
        List<ExtSouProjectDto> extSouProjectDtoList = JSON.parseArray(JSON.toJSONString(souProjectList), ExtSouProjectDto.class);
        // 行业包额外处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitQueryHandler.class).doHandlerAfterPageProjects(souProjectQuery, souType, extSouProjectDtoList);
        pageInfo.setList(extSouProjectDtoList);

        return pageInfo;
    }

    @Override
    public ApiExtSouProjectInfoDTO getProjectInfo(Long projectId) {
        return getProjectInfo(projectId, SouTypeEnum.bid.name());
    }

    @Override
    public ApiExtSouProjectInfoDTO getProjectInfo(Long projectId, String souType) {

        AssertUtils.notNull(projectId, "请求参数不允许为空");
        ApiExtSouProjectInfoDTO projectInfoDTO = new ApiExtSouProjectInfoDTO();

        //修改单据截止态
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitQueryHandler.class).doHandlerBeforeGetProjectInfo(projectId, souType);

        //查询头信息
        ExtSouProjectDto extSouProjectDto = listExtSouProjectDto(projectId);
        if (Objects.isNull(extSouProjectDto)) {
            return null;
        }
        projectInfoDTO.setProject(extSouProjectDto);
        //查询工作小组
        projectInfoDTO.setGroupList(listExtSouGroup(projectId));
        //查询招标文件
        listSouFile(projectInfoDTO, projectId);
        //查询招标计划
        projectInfoDTO.setPlanList(listSouPlan(projectId));
        //修正截止时间
        fixActualEndTime(projectInfoDTO.getProject(), projectInfoDTO.getPlanList());

        //查询节点信息
        ApiExtSouProcessConfigVo configVo = souProcessConfigService.listSouProcessConfig(extSouProjectDto.getProcessConfigId());
        extSouProjectDto.setProcessConfig(configVo);

        //查询流程节点
        List<ApiSouProcessNodeVO> nodeVOList = bidSouProcessQueryService.listProcessNodes(projectId);
        extSouProjectDto.setProcessNodeList(nodeVOList);

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectInfoQueryHandler.class).doHandlerAfterQueryProjectInfo(projectInfoDTO, souType);

        return projectInfoDTO;
    }

    private void fixActualEndTime(ExtSouProjectDto projectDto, List<ExtSouPlan> planList) {
        //技术标实际截止时间
        AtomicReference<Date> techEndDate = new AtomicReference<>(null);
        //商务标实际截止时间
        Date busEndDate = null;

        List<ExtSouRound> roundList = souRoundService.lambdaQuery().eq(ExtSouRound::getProjectId, projectDto.getProjectId())
                .eq(ExtSouRound::getRound, SrmConstant.NUM_ONE).list();
        if(CollectionUtils.isNotEmpty(roundList)) {
            busEndDate = roundList.get(0).getOrderEndTime();
        }
        planList.stream().filter(p -> SouBidPlanTypeEnum.PLAN.getCode().equals(p.getPlanType())).forEach(plan -> {
            if(Objects.isNull(plan.getTechEndFixTime())) {
                techEndDate.set(plan.getTechEndTime());
            } else {
                techEndDate.set(plan.getTechEndFixTime());
            }
        });

        Date currentDate = new Date();
        if(ObjectUtils.anyNull(techEndDate.get()) || currentDate.before(techEndDate.get())) {
            techEndDate.set(null);
        }
        if(ObjectUtils.anyNull(busEndDate) || currentDate.before(busEndDate) || Arrays.asList(SouBiddingProStatusEnum.DRAW_UP.getCode(), SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode(), SouBiddingProStatusEnum.TECH_BID_OPEN.getCode(), SouBiddingProStatusEnum.TECH_BID_EVA.getCode(), SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode()).contains(projectDto.getProjectStatus())) {
            busEndDate = null;
        }
        Date finalBusEndDate = busEndDate;
        planList.stream().filter(p -> SouBidPlanTypeEnum.ACTUAL.getCode().equals(p.getPlanType())).forEach(plan -> {
            plan.setTechEndTime(techEndDate.get());
            plan.setBusEndTime(finalBusEndDate);

        });
    }

    private List<ExtSouPlan> listSouPlan(Long projectId) {
        LambdaQueryWrapper<ExtSouPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouPlan::getProjectId, projectId);
        queryWrapper.in(ExtSouPlan::getPlanType, Arrays.asList(SouBidPlanTypeEnum.PLAN.getCode(), SouBidPlanTypeEnum.ACTUAL.getCode()));

        List<ExtSouPlan> planList = planMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(planList)) {
            ExtSouPlan plan = new ExtSouPlan();
            plan.setProjectId(projectId);
            plan.setPlanType(SouBidPlanTypeEnum.PLAN.getCode());
            planList.add(plan);
            ExtSouPlan actual = new ExtSouPlan();
            actual.setProjectId(projectId);
            actual.setPlanType(SouBidPlanTypeEnum.ACTUAL.getCode());
            planList.add(actual);
            return planList;
        }

        planList = planList.stream().sorted(new Comparator<ExtSouPlan>() {
            @Override
            public int compare(ExtSouPlan o1, ExtSouPlan o2) {
                Integer c1 = SouBidPlanTypeEnum.PLAN.getCode().equals(o1.getPlanType()) ? -1 : 1;
                Integer c2 = SouBidPlanTypeEnum.ACTUAL.getCode().equals(o1.getPlanType()) ? -1 : 1;
                return c1.compareTo(c2);
            }
        }).collect(Collectors.toList());

        return planList;
    }

    private void listSouFile(ApiExtSouProjectInfoDTO projectInfoDTO, Long projectId) {
        LambdaQueryWrapper<ExtSouFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouFile::getProjectId, projectId);
        queryWrapper.in(ExtSouFile::getFileType, Arrays.asList(SouBidAttachmentTypeEnum.APPLY.getCode(), SouBidAttachmentTypeEnum.BID.getCode()));
        queryWrapper.orderByAsc(ExtSouFile::getSouFileId);
        List<ExtSouFile> bidAttachmentList = souFileMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(bidAttachmentList)) {
            Map<String, List<ExtSouFile>> bidAttachmentMap = bidAttachmentList.stream().collect(Collectors.groupingBy(ExtSouFile::getFileType));
            projectInfoDTO.setApplyFileList(bidAttachmentMap.getOrDefault(SouBidAttachmentTypeEnum.APPLY.getCode(), new ArrayList<>()));
            projectInfoDTO.setBidFileList(bidAttachmentMap.getOrDefault(SouBidAttachmentTypeEnum.BID.getCode(), new ArrayList<>()));
        }
    }

    private List<ExtSouGroup> listExtSouGroup(Long projectId) {
        LambdaQueryWrapper<ExtSouGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouGroup::getProjectId, projectId);
        queryWrapper.eq(ExtSouGroup::getExtGroupFlag, YesOrNo.YES.getValue());
        queryWrapper.orderByAsc(ExtSouGroup::getGroupId);
        List<ExtSouGroup> groupList = groupMapper.selectList(queryWrapper);
        return groupList;
    }

    private ExtSouProjectDto listExtSouProjectDto(Long projectId) {
        ExtSouProjectDto extSouProjectDto = new ExtSouProjectDto();
        ExtSouProject extSouProject = projectMapper.selectById(projectId);
        if (Objects.isNull(extSouProject)) {
            return null;
        }
        BeanCopyUtil.copyProperties(extSouProjectDto, extSouProject);
        //合并需求
        extSouProjectDto.setApplicantNo(getApplicantNo(projectId));
        extSouProjectDto.setApplicantId(getApplicantId(extSouProjectDto.getApplicantNo()));
        extSouProjectDto.setPartCancle(partCancle(projectId));
        String text = ";";
        if (StringUtils.isNotBlank(extSouProjectDto.getApplicantNo()) && extSouProjectDto.getApplicantNo().contains(text)) {
            extSouProjectDto.setMergeFlag(true);
        } else {
            extSouProjectDto.setMergeFlag(false);
        }
        return extSouProjectDto;
    }

    @Override
    public String getApplicantId(String applicantNo) {
        if (StringUtils.isNotBlank(applicantNo)) {
            List<String> applicantNoList = Arrays.stream(applicantNo.split(";")).collect(Collectors.toList());
            QlOpenQueryWrapper qlOpenWrappers = QlOpenWrappers.query(PR_SOU_REQUIREMENT_POOL_FOR_BUYER).in("requirementHeadNum", applicantNoList);

            List<RecordDTO> recordDtos = qlOpenClient.query(ContextPath.SUP_CE, qlOpenWrappers, RecordDTO.class);
            if (CollectionUtils.isEmpty(recordDtos)) {
                return "";
            }

            Map<String, Long> applicantMap = new HashMap<>(50);

            recordDtos.forEach(recordDTO -> {
                applicantMap.put(recordDTO.getString("requirementHeadNum"), recordDTO.getLong("requirementHeadId"));
            });
            List<Long> applicantIdList = new ArrayList<>();
            applicantNoList.stream().forEach(no -> {
                applicantIdList.add(applicantMap.getOrDefault(no, -1L));
            });
            return applicantIdList.stream().map(l -> l.toString()).collect(Collectors.joining(";"));
        }
        return "";
    }

    @Override
    public String getApplicantNo(Long projectId) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, projectId);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        queryWrapper.select(ExtSouDemand::getApplicantNo);
        queryWrapper.orderByAsc(ExtSouDemand::getSortIndex);
        List<ExtSouDemand> demandList = demandMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(demandList)) {
            return demandList.stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.joining(";"));
        }
        return null;
    }

    @Override
    public String partCancle(Long projectId) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, projectId);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ONE);
        queryWrapper.select(ExtSouDemand::getApplicantNo);
        Long count = demandService.count(queryWrapper);
        if(!Objects.isNull(count) && Long.compare(count, SrmConstant.LONG_ZERO) == 1) {
            return YesOrNo.YES.getValue();
        }
        return YesOrNo.NO.getValue();
    }

    @Override
    public List<ExtSouItem> getRequireInfo(Long projectId) {

        LambdaQueryWrapper<ExtSouItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouItem::getProjectId, projectId);
        queryWrapper.orderByAsc(ExtSouItem::getSouItemId);
        List<ExtSouItem> souItemList = itemMapper.selectList(queryWrapper);
        return souItemList;
    }

    @Override
    public List<ExtScoreRule> getScoreRule(Long projectId) {

        LambdaQueryWrapper<ExtScoreRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtScoreRule::getProjectId, projectId);
        queryWrapper.orderByAsc(ExtScoreRule::getScoreRuleId);
        List<ExtScoreRule> scoreRuleList = scoreRuleMapper.selectList(queryWrapper);
        return scoreRuleList;
    }

    @Override
    public List<ExtSouVendor> getInviteSupplier(Long projectId, String souType) {

        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, projectId);
        queryWrapper.orderByAsc(ExtSouVendor::getSouVendorId);
        List<ExtSouVendor> vendorList = vendorService.list(queryWrapper);

        ApiExtSouProjectInfoDTO projectInfoDTO = this.getProjectInfo(projectId, souType);

        //后置处理
        vendorList = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitInviteSupplierQueryHandler.class).doHandlerAfterGetInviteSupplier(projectId, souType, projectInfoDTO, vendorList);
        return vendorList;
    }

    @Override
    public List<ExtSouMargin> getSouMargin(Long projectId) {
        List<ExtSouMargin> souMarginList = queryMargin(projectId);
        souMarginService.copyYearMarginInfo(souMarginList);
        return souMarginList;
    }

    private List<ExtSouMargin> queryMargin(Long projectId) {
        LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouMargin::getProjectId, projectId);
        queryWrapper.orderByAsc(ExtSouMargin::getMarginId);
        List<ExtSouMargin> souMarginList = souMarginService.list(queryWrapper);
        return souMarginList;
    }

    @Override
    public List<ExtSouMarginRecordDto> getSouMarginRecord(Long projectId, String type) {

        ExtSouMarginRecordPo po = new ExtSouMarginRecordPo();

        List<ExtSouMargin> souMarginList = queryMargin(projectId);

        if(CollectionUtils.isEmpty(souMarginList)) {
            return new ArrayList<>(16);
        }

        AtomicReference<Boolean> yearMarginFlag = new AtomicReference<>(false);
        List<Long> relYearMarginIdList = new ArrayList<>(16);
        souMarginList.stream().forEach(souMargin -> {
            if(YesOrNo.YES.getValue().equals(souMargin.getYearFlag())) {
                yearMarginFlag.set(true);
                relYearMarginIdList.add(souMargin.getRelYearMarginId());
            }
        });

        LambdaQueryWrapper<ExtSouMarginRecord> queryWrapper = new LambdaQueryWrapper<>();

        if(yearMarginFlag.get()) {
            queryWrapper.in(ExtSouMarginRecord::getMarginId, relYearMarginIdList);
        } else {
            queryWrapper.eq(ExtSouMarginRecord::getProjectId, projectId);
        }
        queryWrapper.eq(ExtSouMarginRecord::getType, type);
        queryWrapper.orderByDesc(ExtSouMarginRecord::getRecordId);
        List<ExtSouMarginRecord> recordList = souMarginRecordService.list(queryWrapper);

        po.setMarginRecordList(recordList);
        return po.getMarginRecordDtoList();
    }

    private List<Long> queryOrderIdWithRound(Long projectId, Integer round) {
        LambdaQueryWrapper<ExtSouOrderItem> query = new LambdaQueryWrapper<>();
        query.eq(ExtSouOrderItem::getProjectId, projectId);
        query.eq(!Objects.isNull(round), ExtSouOrderItem::getRound, round);

        query.select(ExtSouOrderItem::getOrderId).groupBy(ExtSouOrderItem::getOrderId);
        return orderItemService.list(query).stream().map(o -> o.getOrderId()).collect(Collectors.toList());
    }

    @Override
    public ExtSouProjectControlDto getProjectControl(Long projectId) {

        ExtSouProjectControlDto controlDto = new ExtSouProjectControlDto();

        //查询项目基本信息
        ExtSouProject project = projectMapper.selectById(projectId);
        AssertUtils.notNull(project, "项目信息不存在！");

        //查询最新轮次
        PageUtil.startPage(1, 1);
        LambdaQueryWrapper<SouRound> queryRoundWrapper = new LambdaQueryWrapper<>();
        queryRoundWrapper.eq(SouRound::getProjectId, projectId);
        queryRoundWrapper.orderByDesc(SouRound::getCreationDate);
        List<SouRound> roundList = souRoundDAO.list(queryRoundWrapper);
        SouRound currentRound = new SouRound();
        if (CollectionUtils.isNotEmpty(roundList)) {
            currentRound = roundList.get(0);
        }

        //招标计划
        ExtSouPlan plan = new ExtSouPlan();
        List<ExtSouPlan> extSouPlans = planService.lambdaQuery().eq(ExtSouPlan::getProjectId, project.getProjectId())
                .eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.PLAN.getCode()).list();
        if (CollectionUtils.isNotEmpty(extSouPlans)) {
            plan = extSouPlans.get(0);
        }

        //查询投标头表
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.eq(ExtSouOrder::getProjectId, projectId);
        queryOrderWrapper.eq(!Objects.isNull(currentRound.getRound()), ExtSouOrder::getRound, currentRound.getRound());
        queryOrderWrapper.groupBy(ExtSouOrder::getVendorId);

        List<ExtSouOrder> orderList = orderService.list(queryOrderWrapper);
        //修正投标数据
        //是否包含商务报价
        Boolean busFlag = orderList.stream().filter(o -> YesOrNo.YES.getValue().equals(o.getExtTechFlag()) && ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).findAny().isPresent();
        if (busFlag) {
            orderList = orderList.stream().filter(o -> YesOrNo.YES.getValue().equals(o.getExtTechFlag()) && ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).collect(Collectors.toList());
        }

        Long haveTenderNum = orderList.stream().filter(o -> SouOrderStatusEnum.SUBMISSION.equals(o.getOrderStatus())).count();
        if (!Objects.isNull(haveTenderNum)) {
            controlDto.setHaveTenderNum(haveTenderNum.intValue());
        }
        controlDto.setNeedTenderNum(orderList.size());

        //当前轮次是技术投标还是商务标
        Boolean cureentBusFlag = orderList.stream().filter(o -> ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).findAny().isPresent();
        if (!cureentBusFlag) {
            controlDto.setOrderEndTime(Objects.isNull(plan.getTechEndFixTime()) ? plan.getTechEndTime() : plan.getTechEndFixTime());
        } else {
            controlDto.setOrderEndTime(currentRound.getOrderEndTime());
        }

        return controlDto;
    }

    @Override
    public List<ApiExtSouOrderDto> getExtSouOrder(Long projectId) {
        //查询项目基本信息
        ExtSouProject project = projectMapper.selectById(projectId);
        AssertUtils.notNull(project, "项目信息不存在！");
        //查询投标头表
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.eq(SouOrder::getProjectId, projectId);
        queryOrderWrapper.orderByDesc(SouOrder::getRound);
        queryOrderWrapper.orderByDesc(SouOrder::getVendorId);

        List<ExtSouOrder> orderList = orderService.list(queryOrderWrapper);

        List<ApiExtSouOrderDto> souOrderDtoList = JSON.parseArray(JSON.toJSONString(orderList), ApiExtSouOrderDto.class);

        if(CollectionUtils.isEmpty(souOrderDtoList)) {
            return new ArrayList<>();
        }

        //查询投标行表
        LambdaQueryWrapper<ExtSouOrderItem> queryOrderItemWrapper = new LambdaQueryWrapper<>();
        queryOrderItemWrapper.eq(SouOrderItem::getProjectId, projectId);
        List<ExtSouOrderItem> orderItemList = orderItemService.list(queryOrderItemWrapper);
//轮次-供应商-投标明细记录
        Map<String, ExtSouOrderItem> roundVendorItemMap = new HashMap<>(50);
        //轮次记录表
        Set<Integer> roundSet = new HashSet<>();
        roundSet.add(project.getCurrentRound());
        Map<Long, List<ExtSouOrderItem>> orderItemMap = orderItemList.stream().peek(item -> {
            String roundVendorKey = StringUtils.joinWith("_", item.getRound(), item.getVendorId());
            roundVendorItemMap.put(roundVendorKey, item);
            roundSet.add(item.getRound());
        }).collect(Collectors.groupingBy(s -> s.getOrderId()));

        //查询需求
        LambdaQueryWrapper<ExtSouItem> querySouItemWrapper = new LambdaQueryWrapper<>();
        querySouItemWrapper.eq(ExtSouItem::getProjectId, projectId);
        List<ExtSouItem> souItemList = itemMapper.selectList(querySouItemWrapper);
        Map<Long, ExtSouItem> souItemMap = souItemList.stream().collect(Collectors.toMap(s -> s.getSouItemId(), Function.identity()));

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> querySouVendorWrapper = new LambdaQueryWrapper<>();
        querySouVendorWrapper.eq(ExtSouVendor::getProjectId, projectId);
        List<ExtSouVendor> souVendorList = vendorService.list(querySouVendorWrapper);
        Map<Long, ExtSouVendor> souVendorMap = souVendorList.stream().collect(Collectors.toMap(s -> s.getVendorId(), Function.identity(), (k1, k2) -> k2));

        //查询供应商主数据
        Map<Long, RecordDTO> supplierMap = new HashMap<>(50);
        if(MapUtils.isNotEmpty(souVendorMap)) {
            List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER).in("companyId", new ArrayList<>(souVendorMap.keySet())), RecordDTO.class);
            supplierMap = recordDTOList.stream().collect(Collectors.toMap(k -> k.getLong("companyId"), Function.identity(), (k1, k2) -> k2));
        }

        List<ApiExtSouOrderDto> allRoundOrderList = getApiExtSouOrderDtos(project, orderList, souOrderDtoList, orderItemMap, souItemMap, souVendorMap, supplierMap);

        return allRoundOrderList;
    }

    /**
     * 组装数据
     * @param project 参数
     * @param orderList 参数
     * @param souOrderDtoList 参数
     * @param orderItemMap 参数
     * @param souItemMap 参数
     * @param souVendorMap 参数
     * @param supplierMap 参数
     * @return 返回
     */
    @NotNull
    private List<ApiExtSouOrderDto> getApiExtSouOrderDtos(ExtSouProject project, List<ExtSouOrder> orderList, List<ApiExtSouOrderDto> souOrderDtoList, Map<Long, List<ExtSouOrderItem>> orderItemMap, Map<Long, ExtSouItem> souItemMap, Map<Long, ExtSouVendor> souVendorMap, Map<Long, RecordDTO> supplierMap) {
        //是否存在技术标
        AtomicReference<Boolean> techFlag = new AtomicReference<>(false);

        //处理包名
        souOrderDtoList.stream().forEach(order -> {
            if (YesOrNo.YES.getValue().equals(order.getExtTechFlag())) {
                techFlag.set(true);
            }
            List<ExtSouOrderItem> souOrderItemList = orderItemMap.get(order.getOrderId());
            if (CollectionUtils.isNotEmpty(souOrderItemList)) {
                Set<String> packageNameSet = new HashSet<>();
                souOrderItemList.stream().forEach(orderItem -> {
                    if (souItemMap.containsKey(orderItem.getSouItemId()) && StringUtils.isNotBlank(souItemMap.get(orderItem.getSouItemId()).getExtPackageName())) {
                        packageNameSet.add(souItemMap.get(orderItem.getSouItemId()).getExtPackageName());
                    }
                });
                order.setTenderPackageName(packageNameSet.stream().sorted(Comparator.comparing(s -> s)).collect(Collectors.joining("; ")));

                //是否查询标书
                ExtSouVendor vendor = souVendorMap.getOrDefault(order.getVendorId(), new ExtSouVendor());
//                order.setReadBidFileFlag(vendor.getExtReadBidFlag());
                //供应商编码、名称
                order.setVendorCode(vendor.getVendorCode());
                order.setVendorName(vendor.getVendorName());
                //供应商属性
                order.setExtVendorAttr(vendor.getExtVendorAttr());
            }

        });

        List<ApiExtSouOrderDto> allRoundOrderList = new ArrayList<>();

        List<ExtNpmSouOrder> extNpmSouOrders = extNpmSouOrderService.lambdaQuery()
                .in(ExtNpmSouOrder::getOrderId, orderList.stream().map(o -> o.getOrderId()).collect(Collectors.toList()))
                .orderByDesc(ExtNpmSouOrder::getRound)
                .orderByAsc(ExtNpmSouOrder::getExtOrderType)
                .list();
        Map<Long, ApiExtSouOrderDto> souOrderDtoMap = souOrderDtoList.stream().collect(Collectors.toMap(o -> o.getOrderId(), Function.identity(), (k1, k2)->k2));
        Map<Long, RecordDTO> finalSupplierMap = supplierMap;
        extNpmSouOrders.stream().forEach(extNpmSouOrder -> {
            ApiExtSouOrderDto apiExtSouOrderDto = new ApiExtSouOrderDto();
            ApiExtSouOrderDto order = souOrderDtoMap.getOrDefault(extNpmSouOrder.getOrderId(), new ApiExtSouOrderDto());
            BeanCopyUtil.copyProperties(apiExtSouOrderDto, order);
            //赋予扩展字段
            apiExtSouOrderDto.setOrderStatus(SouOrderStatusEnum.valueOf(StringUtils.defaultIfBlank(extNpmSouOrder.getOrderStatus(), SouOrderStatusEnum.DRAFT.name())));//报价状态
            apiExtSouOrderDto.setExtOrderType(extNpmSouOrder.getExtOrderType());//投标类型
            apiExtSouOrderDto.setSubmitTime(extNpmSouOrder.getSubmitTime());//提交时间
            apiExtSouOrderDto.setWithdrawReason(extNpmSouOrder.getWithdrawReason());//撤回报价原因
            apiExtSouOrderDto.setExtNotjoinReason(extNpmSouOrder.getExtNotjoinReason());//不参与原因
            apiExtSouOrderDto.setWithdrawTime(extNpmSouOrder.getWithdrawTime());//撤回报价时间
            apiExtSouOrderDto.setRejectReason(extNpmSouOrder.getRejectReason());//作废报价原因
            apiExtSouOrderDto.setRejectTime(extNpmSouOrder.getRejectTime());//作废报价时间
            apiExtSouOrderDto.setExtTechFlag(extNpmSouOrder.getExtTechFlag());//技术投标标识
            apiExtSouOrderDto.setExtDownBidFileTime(extNpmSouOrder.getExtDownBidFileTime());//下载标书时间
            apiExtSouOrderDto.setReadBidFileFlag(extNpmSouOrder.getExtReadBidFlag());//是否查阅标书
            apiExtSouOrderDto.setRound(extNpmSouOrder.getRound());//轮次
            apiExtSouOrderDto.setOrderId(extNpmSouOrder.getExtOrderId());//修正投标ID

            //供应商主数据契约认证
            RecordDTO supplier = finalSupplierMap.get(apiExtSouOrderDto.getVendorId());
            if(ObjectUtils.allNotNull(supplier)) {
                apiExtSouOrderDto.setContractVerification(supplier.getString("contractVerification"));
            }

            allRoundOrderList.add(apiExtSouOrderDto);
        });
        return allRoundOrderList;
    }


    @Override
    public List<ExtSouGroup> getEvaGroup(Long projectId) {
        LambdaQueryWrapper<ExtSouGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouGroup::getProjectId, projectId);
        queryWrapper.eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue());
        queryWrapper.eq(ExtSouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH.getCode());
        queryWrapper.orderByDesc(ExtSouGroup::getSortIndex);
        List<ExtSouGroup> groupList = groupMapper.selectList(queryWrapper);
        return groupList;
    }

    @Override
    public PageInfo<ExtSouTechScoreHeadDto> getTechScore(ApiExtSouTechScoreQueryDTO query, String souType) {
        //格式化请求参数
        query.formatParams();
        //分页参数
        if (!Objects.isNull(query.getPageNum()) && !Objects.isNull(query.getPageSize())) {
            PageUtil.startPage(query.getPageNum(), query.getPageSize());
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("p.sou_type", souType);
        queryWrapper.like(StringUtils.isNotBlank(query.getSouNo()), "p.sou_no", query.getSouNo());
        queryWrapper.like(StringUtils.isNotBlank(query.getExtProjectNo()), "p.ext_project_no", query.getExtProjectNo());
        queryWrapper.eq(StringUtils.isNotBlank(query.getProjectStatus()), "p.project_status", query.getProjectStatus());
        queryWrapper.like(StringUtils.isNotBlank(query.getSouName()), "p.sou_name", query.getSouName());
        queryWrapper.like(StringUtils.isNotBlank(query.getFullName()), "g.full_name", query.getFullName());
        queryWrapper.ge(!Objects.isNull(query.getCreationDateFrom()), "h.CREATION_DATE", query.getCreationDateFrom());
        queryWrapper.le(!Objects.isNull(query.getCreationDateTo()), "h.CREATION_DATE", query.getCreationDateTo());
        queryWrapper.eq("g.user_name", AppUserUtil.getUserName());


        List<ExtSouTechScoreHeadDto> scoreHeadDtoList = techScoreHeadMapper.listExtSouTechScoreHead(queryWrapper);

        return new PageInfo<>(scoreHeadDtoList);
    }

    @Override
    public List<ApiExtScoreRuleDto> getExtScoreRule(ApiExtSouTechScoreLineQueryDTO query) {
        //查询项目信息
        ExtSouProject project = projectMapper.selectById(query.getProjectId());
        AssertUtils.notNull(project, "项目信息不存在！");

        //查询打分项
        LambdaQueryWrapper<ExtScoreRule> queryRuleWrapper = new LambdaQueryWrapper<>();
        queryRuleWrapper.eq(ExtScoreRule::getProjectId, query.getProjectId());
        queryRuleWrapper.orderByAsc(ExtScoreRule::getScoreRuleId);

        List<ExtScoreRule> extScoreRuleList = scoreRuleMapper.selectList(queryRuleWrapper);

        List<ApiExtScoreRuleDto> scoreRuleDtoList = JSON.parseArray(JSON.toJSONString(extScoreRuleList), ApiExtScoreRuleDto.class);

        //查询打分项 助手结果
        Table<Long, Long, List<BidReviewResDto.AnswerAndQuotation>> extScoreRuleTableMap = bidReviewItem(query);

        //查询供应商
        List<ExtSouVendor> vendorList = this.queryTechScoreVendory(query, project);

        //根据评委反差评分头ID
        if(Objects.isNull(query.getTechScoreHeadId())) {
            List<ExtSouTechScoreHead> techScoreHeadList = techScoreHeadService.lambdaQuery().eq(ExtSouTechScoreHead::getProjectId, query.getProjectId())
                    .eq(ExtSouTechScoreHead::getGroupId, query.getGroupId()).list();
            if(CollectionUtils.isNotEmpty(techScoreHeadList)) {
                query.setTechScoreHeadId(techScoreHeadList.get(0).getTechScoreHeadId());
            }
        }

        //查询供应商得分
        LambdaQueryWrapper<ExtSouTechScoreLine> queryScoreLineWrapper = new LambdaQueryWrapper<>();
        queryScoreLineWrapper.eq(ExtSouTechScoreLine::getProjectId, query.getProjectId());
        queryScoreLineWrapper.eq(ExtSouTechScoreLine::getGroupId, query.getGroupId());
        queryScoreLineWrapper.eq(ExtSouTechScoreLine::getTechScoreHeadId, query.getTechScoreHeadId());

        List<ExtSouTechScoreLine> techScoreLineList = techScoreLineService.list(queryScoreLineWrapper);

        Map<String, ExtSouTechScoreLine> techScoreLineMap = techScoreLineList.stream().collect(Collectors.toMap(l -> StringUtils.joinWith("_", l.getScoreRuleLineId(), l.getVendorId(), l.getGroupId()), Function.identity(), (k1, k2) -> k2));

        //记录脱敏供应商
        query.setDesensitizeVendorMap(new HashMap<>(50));

        //组装数据
        for (ApiExtScoreRuleDto ruleDto : scoreRuleDtoList) {
            ruleDto.setVendorScoreList(new ArrayList<>());

            for (ExtSouVendor vendor : vendorList) {
                ApiExtSouTechScoreLineReviewDto techScoreLineDto = new ApiExtSouTechScoreLineReviewDto();
                String key = StringUtils.joinWith("_", ruleDto.getScoreRuleId(), vendor.getVendorId(), query.getGroupId());
                ExtSouTechScoreLine techScoreLine = techScoreLineMap.getOrDefault(key, new ApiExtSouTechScoreLineDto());
                //复制属性值
                BeanCopyUtil.copyProperties(techScoreLineDto, techScoreLine);
                techScoreLineDto.setVendorId(vendor.getVendorId());
                techScoreLineDto.setTechScoreHeadId(query.getTechScoreHeadId());
                techScoreLineDto.setGroupId(query.getGroupId());
                techScoreLineDto.setVendorCode(vendor.getVendorCode());
                techScoreLineDto.setVendorName(vendor.getVendorName());

                techScoreLineDto.setAnswerAndQuotationList(extScoreRuleTableMap.get(ruleDto.getScoreRuleId(),vendor.getVendorId()));

                query.getDesensitizeVendorMap().put(vendor.getVendorId(), techScoreLineDto.getVendorName());

                ruleDto.getVendorScoreList().add(techScoreLineDto);
            }

        }

        //计算平均分
        Map<Long, BigDecimal> averageMap = caculateAverageScore(project.getProjectId());
        //计算总分
        caculateScore(scoreRuleDtoList, averageMap);

        return scoreRuleDtoList;
    }

    private Table<Long, Long, List<BidReviewResDto.AnswerAndQuotation>>  bidReviewItem(ApiExtSouTechScoreLineQueryDTO query) {
        Table<Long, Long, List<BidReviewResDto.AnswerAndQuotation>> extScoreRuleTableMap = HashBasedTable.create();
        if(Objects.equals(YesOrNo.YES.getValue(), query.getExtendReview())){
            BidReviewResDto bidReviewResDto = pjSouClient.itemsQuotation(query.getProjectId());
            if(Objects.nonNull(bidReviewResDto) && Objects.nonNull(bidReviewResDto.getReviewItemList())){
                bidReviewResDto.getReviewItemList().forEach(reviewItem -> {
                    reviewItem.getCompanyList().forEach(company -> {
                        extScoreRuleTableMap.put(reviewItem.getItemId(), company.getCompanyId(), company.getAnswerAndQuotationList());
                    });
                });
            }
        }
        return extScoreRuleTableMap;
    }

    @Override
    public Map<Long, List<ApiExtScoreRuleDto>> getExtScoreRuleBatchAsAllGroup(ApiExtSouTechScoreLineQueryDTO query) {

        Map<Long, List<ApiExtScoreRuleDto>> scoreMap = new HashMap<>(50);

        //查询项目信息
        ExtSouProject project = projectMapper.selectById(query.getProjectId());
        AssertUtils.notNull(project, "项目信息不存在！");

        //查询打分项
        LambdaQueryWrapper<ExtScoreRule> queryRuleWrapper = new LambdaQueryWrapper<>();
        queryRuleWrapper.eq(ExtScoreRule::getProjectId, query.getProjectId());
        queryRuleWrapper.orderByAsc(ExtScoreRule::getScoreRuleId);

        List<ExtScoreRule> extScoreRuleList = scoreRuleMapper.selectList(queryRuleWrapper);

        //查询供应商
        List<ExtSouVendor> vendorList = this.queryTechScoreVendory(query, project);

        //查询评委评分头
        List<ExtSouTechScoreHead> techScoreHeadList = techScoreHeadService.lambdaQuery().eq(ExtSouTechScoreHead::getProjectId, query.getProjectId()).list();
        if(CollectionUtils.isNotEmpty(techScoreHeadList)) {
            Map<Long, ExtSouTechScoreHead> groupTechMap = techScoreHeadList.stream().collect(Collectors.toMap(t -> t.getGroupId(), Function.identity(), (k1, k2)->k2));
            //查询供应商得分
            LambdaQueryWrapper<ExtSouTechScoreLine> queryScoreLineWrapper = new LambdaQueryWrapper<>();
            queryScoreLineWrapper.eq(ExtSouTechScoreLine::getProjectId, query.getProjectId());

            List<ExtSouTechScoreLine> techScoreLineList = techScoreLineService.list(queryScoreLineWrapper);

            Map<String, ExtSouTechScoreLine> techScoreLineMap = techScoreLineList.stream().collect(Collectors.toMap(l -> StringUtils.joinWith("_", l.getScoreRuleLineId(), l.getVendorId(), l.getGroupId()), Function.identity(), (k1, k2) -> k2));

            //记录脱敏供应商
            query.setDesensitizeVendorMap(new HashMap<>(50));

            //计算平均分
            Map<Long, BigDecimal> averageMap = caculateAverageScore(project.getProjectId());

            //查询评委
            List<ExtSouGroup> groupList = groupService.listByIds(techScoreHeadList.stream().map(ExtSouTechScoreHead::getGroupId).collect(Collectors.toList()));
            Map<Long, ExtSouGroup> groupMap = groupList.stream().collect(Collectors.toMap(k -> k.getGroupId(), Function.identity()));

            for(Long groupId: groupTechMap.keySet()) {
                List<ApiExtScoreRuleDto> scoreRuleDtoList = JSON.parseArray(JSON.toJSONString(extScoreRuleList), ApiExtScoreRuleDto.class);
                //组装数据
                for (ApiExtScoreRuleDto ruleDto : scoreRuleDtoList) {
                    ruleDto.setVendorScoreList(new ArrayList<>());
                    ExtSouGroup group = groupMap.getOrDefault(groupId, new ExtSouGroup());
                    ruleDto.setUserName(group.getUserName());
                    ruleDto.setFullName(group.getFullName());
                    ruleDto.setExtProjectNo(project.getExtProjectNo());
                    ruleDto.setSouName(project.getSouName());

                    for (ExtSouVendor vendor : vendorList) {
                        ApiExtSouTechScoreLineDto techScoreLineDto = new ApiExtSouTechScoreLineDto();
                        String key = StringUtils.joinWith("_", ruleDto.getScoreRuleId(), vendor.getVendorId(), groupId);
                        ExtSouTechScoreLine techScoreLine = techScoreLineMap.getOrDefault(key, new ApiExtSouTechScoreLineDto());
                        //复制属性值
                        BeanCopyUtil.copyProperties(techScoreLineDto, techScoreLine);
                        techScoreLineDto.setVendorId(vendor.getVendorId());
                        techScoreLineDto.setTechScoreHeadId(groupTechMap.get(groupId).getTechScoreHeadId());
                        techScoreLineDto.setGroupId(groupId);
                        techScoreLineDto.setVendorCode(vendor.getVendorCode());
                        techScoreLineDto.setVendorName(vendor.getVendorName());

                        query.getDesensitizeVendorMap().put(vendor.getVendorId(), techScoreLineDto.getVendorName());

                        ruleDto.getVendorScoreList().add(techScoreLineDto);
                    }

                }
                //计算总分
                caculateScore(scoreRuleDtoList, averageMap);

                scoreMap.put(groupId, scoreRuleDtoList);
            }
        }


        return scoreMap;
    }

    /**
     * 查询技术投标供应商范围
     *
     * @param projectId
     * @return
     */
    private List<Long> queryTechTenderVendorList(Long projectId) {
        //查看已投标供应商
        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, projectId).list();
        if(CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        }
        List<ExtNpmSouOrder> extNpmSouOrders = extNpmSouOrderService.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream().map(ExtSouOrder::getOrderId).distinct().collect(Collectors.toSet()))
                .eq(ExtNpmSouOrder::getRound, SrmConstant.NUM_ONE).eq(ExtNpmSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name()).list();

        if(CollectionUtils.isEmpty(extNpmSouOrders)) {
            return new ArrayList<>();
        }
        List<Long> targetOrderIdList = extNpmSouOrders.stream().map(ExtNpmSouOrder::getOrderId).distinct().collect(Collectors.toList());;
        List<Long> tenderVendorIdList = orderList.stream().filter(o -> targetOrderIdList.contains(o.getOrderId())).map(ExtSouOrder::getVendorId).distinct().collect(Collectors.toList());
        return tenderVendorIdList;
    }

    @Override
    public ApiExtSouTechEvaDetailDto getExtScoreDetail(ApiExtSouTechScoreLineQueryDTO query) {

        ApiExtSouTechEvaDetailDto detailDto = new ApiExtSouTechEvaDetailDto();
        detailDto.setProjectId(query.getProjectId());
        detailDto.setTechScoreHeadId(query.getTechScoreHeadId());
        detailDto.setGroupId(query.getGroupId());

        //查询项目信息
        ExtSouProject souProject = iExtSouProjectService.getById(query.getProjectId());
        if(YesOrNo.YES.getValue().equals(souProject.getExtHideKeyInfo())) {
            query.setExtendReview(YesOrNo.NO.getValue());
        }

        //查询已投标的供应商范围
        List<Long> techVendorIdList = queryTechTenderVendorList(query.getProjectId());
        query.setTechVendorIdList(techVendorIdList);
        //查询打分项
        detailDto.setScoreRuleList(this.getExtScoreRule(query));

        //查询技术文件
        List<ExtSouOrderFileDto> techFileDtoList = null;
        if(YesOrNo.YES.getValue().equals(souProject.getExtHideKeyInfo())) {
            techFileDtoList = orderFileService.getSecretFileList(query.getProjectId());
        } else {
            List<ExtSouOrderFile> techFileList = orderFileService.getScoreTechOrderFile(query.getProjectId());
            techFileDtoList = JSON.parseArray(JSON.toJSONString(techFileList), ExtSouOrderFileDto.class);
        }

        ExtSouOrderFileDto fileDto = techFileDtoList.stream().max(Comparator.comparing(ExtSouOrderFileDto::getRound)).get();
        Integer round = Objects.isNull(fileDto) ? 1 : fileDto.getRound();
        techFileDtoList = techFileDtoList.stream().filter(t -> techVendorIdList.contains(t.getVendorId()) && round.compareTo(t.getRound()) == 0).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(techFileDtoList)) {
            List<Long> vendorIdList = techFileDtoList.stream().map(f -> f.getVendorId()).distinct().collect(Collectors.toList());

            List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, query.getProjectId())
                    .in(ExtSouVendor::getVendorId, vendorIdList).list();
            Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2) -> k2));
            techFileDtoList.stream().forEach(f -> {
                ExtSouVendor vendor = vendorMap.getOrDefault(f.getVendorId(), new ExtSouVendor());
                f.setVendorCode(vendor.getVendorCode());
                f.setVendorName(ObjectUtils.defaultIfNull(query.getDesensitizeVendorMap(), new HashMap<Long, String>(50)).getOrDefault(f.getVendorId(), vendor.getVendorName()));
            });
        }

        List<ExtReplayFileDTO> replayFileDtoList = new ArrayList<>();
        //查询澄清回复文件
        //查询扫描件
        bidReviewScan(query, replayFileDtoList, techFileDtoList);

        detailDto.setReplayFileList(replayFileDtoList);
        detailDto.setTechFileList(techFileDtoList);
        detailDto.setMergeFlag(this.isMergeApplitionNum(query.getProjectId()));
        detailDto.setFileList(extSouTechScoreFileMapper.selectList(new LambdaQueryWrapper<ExtSouTechScoreFile>().eq(ExtSouTechScoreFile::getTechScoreHeadId, query.getTechScoreHeadId())));
        return detailDto;
    }

    private void bidReviewScan(ApiExtSouTechScoreLineQueryDTO query, List<ExtReplayFileDTO> replayFileDtoList, List<ExtSouOrderFileDto> techFileDtoList) {
        if(cn.hutool.core.util.ObjectUtil.notEqual(YesOrNo.YES.getValue(), query.getExtendReview())){
            return;
        }

        List<Long> scanFileList = pjSouClient.scanFileList(query.getProjectId()) ;
        techFileDtoList.forEach(e -> {
            if(scanFileList.contains(e.getOrderDocId())){
                e.setOrderFileName("*"+e.getOrderFileName());
            }else if(!ReviewFileTypeEnum.isValidReviewFileType(e.getOrderFileName())){
                e.setOrderFileName("*"+e.getOrderFileName());
            }
        });

        List<AnswerDTO> answerDTOS = qlService.queryByWrapper(QlWrappers.query(MqlType.ANSWER)
                .eq(AnswerDTO::getProjectId, query.getProjectId())
                .eq(AnswerDTO::getAnswerStatus, AnswerConfirmStatusEnum.COMFIRMED.getCode()), AnswerDTO.class);
        if(CollectionUtils.isEmpty(answerDTOS)){
            return;
        }
        List<AnswerVendorDTO> answerVendorDTOS = qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                .in(AnswerVendorDTO::getAnswerId,answerDTOS.stream().map(AnswerDTO::getAnswerId).collect(Collectors.toList()))
                .eq(AnswerVendorDTO::getConfirmStatus,AnswerConfirmStatusEnum.COMFIRMED.getCode())
                .isNotNull(AnswerVendorDTO::getReplayId),AnswerVendorDTO.class);
        if(CollectionUtils.isEmpty(answerVendorDTOS)){
            return;
        }
        List<ReplayFileDTO> replayFileDtos = qlService.queryByWrapper(QlWrappers.query(TypeEnum.ReplayFile.getCode())
                .in(ReplayFileDTO::getReplayId,answerVendorDTOS.stream().map(AnswerVendorDTO::getReplayId).collect(Collectors.toList()))
                .eq(ReplayFileDTO::getIsDelete, YesOrNo.NO.getValue()),ReplayFileDTO.class);
        if(CollectionUtils.isEmpty(replayFileDtos)){
            return;
        }
        Map<Long, List<AnswerVendorDTO>> replayVendorMap = answerVendorDTOS.stream()
                .collect(Collectors.groupingBy(AnswerVendorDTO::getReplayId, Collectors.toList()));

        replayFileDtos.stream()
                .filter(e -> replayVendorMap.containsKey(e.getReplayId()))
                .forEach(e -> {
                    ExtReplayFileDTO extReplayFileDTO = new ExtReplayFileDTO();
                    extReplayFileDTO.setVendorId(replayVendorMap.get(e.getReplayId()).get(0).getVendorId());
                    extReplayFileDTO.setVendorCode(replayVendorMap.get(e.getReplayId()).get(0).getVendorCode());
                    extReplayFileDTO.setVendorName(replayVendorMap.get(e.getReplayId()).get(0).getVendorName());
                    extReplayFileDTO.setFileId(e.getFileId());
                    extReplayFileDTO.setFileName(e.getFileName());

                    replayFileDtoList.add(extReplayFileDTO);
                });

        replayFileDtoList.forEach(e -> {
            if(scanFileList.contains(e.getFileName())){
                e.setFileName("*"+e.getFileName());
            }else if(!ReviewFileTypeEnum.isValidReviewFileType(e.getFileName())){
                e.setFileName("*"+e.getFileName());
            }
        });
    }

    public List<ExtSouOrderFile> getSubmitOrderScoreTechOrderFile(Long projectId, List<ExtSouOrderFile> techFileList) {
        List<ExtSouOrderFile> submitTechFileList = new ArrayList<>();

        ExtSouProject extSouProject = iExtSouProjectService.getById(projectId);

        List<ExtSouOrder> extSouOrders = orderService.lambdaQuery()
                .eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name())
                .eq(ExtSouOrder::getProjectId, projectId)
                .eq(ExtSouOrder::getRound, extSouProject.getCurrentRound())
                .list();
        if (extSouOrders != null && extSouOrders.size() > 0) {
            Set<Long> orderIdSet = extSouOrders.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toSet());
            for (ExtSouOrderFile souOrderFile : techFileList) {
                if (orderIdSet.contains(souOrderFile.getOrderId())) {
                    submitTechFileList.add(souOrderFile);
                }
            }
        }
        return submitTechFileList;
    }

    private Integer sortGroupRole(String groupRole) {
        Integer sort = Integer.MAX_VALUE;
        if(ExtSouGroupRoleEnum.LEADER.getCode().equals(groupRole)) {
            return 0;
        }
        if(ExtSouGroupRoleEnum.PRINCIPAL.getCode().equals(groupRole)) {
            return 1;
        }
        if(ExtSouGroupRoleEnum.MEMBER.getCode().equals(groupRole)) {
            return 3;
        }
        return sort;
    }

    /**
     * 查询技术评委
     * @param query
     * @return
     */
    private List<ExtSouGroup> queryTechScoreGroup(ApiExtSouTechScoreLineQueryDTO query) {
        LambdaQueryWrapper<ExtSouGroup> queryGroupWrapper = new LambdaQueryWrapper<>();
        queryGroupWrapper.eq(ExtSouGroup::getProjectId, query.getProjectId());
        queryGroupWrapper.eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue());
        queryGroupWrapper.eq(ExtSouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH.getCode());
        List<ExtSouGroup> groupList = groupMapper.selectList(queryGroupWrapper);
        groupList = groupList.stream().sorted(new Comparator<ExtSouGroup>() {
            @Override
            public int compare(ExtSouGroup o1, ExtSouGroup o2) {

                return sortGroupRole(o1.getGroupRole()).compareTo(sortGroupRole(o2.getGroupRole()));
            }
        }).collect(Collectors.toList());
        return new ArrayList<>(groupList);
    }

    /**
     * 查询技术评分供应商
     * @param query
     * @return
     */
    private List<ExtSouVendor> queryTechScoreVendory(ApiExtSouTechScoreLineQueryDTO query, ExtSouProject project) {
        //获取供应商技术投标范围ID
        List<Long> techVendorList = queryTechTenderVendorList(query.getProjectId());

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> queryVendorWrapper = new LambdaQueryWrapper<>();
        queryVendorWrapper.eq(ExtSouVendor::getProjectId, query.getProjectId());
        queryVendorWrapper.in(CollectionUtils.isNotEmpty(techVendorList), ExtSouVendor::getVendorId, techVendorList);
        queryVendorWrapper.orderByAsc(ExtSouVendor::getVendorId);

        List<ExtSouVendor> vendorInfoList = vendorService.listVendorInfoAsShieldVendorName(project);

        List<ExtSouVendor> vendorList = new ArrayList<>(16);
        //扩展供应商
        List<Long> extendVendorIdList = extendAbandonVendor(techVendorList, query);

        //返回供应商数据
        vendorInfoList.stream().forEach(vendor -> {
            if(techVendorList.contains(vendor.getVendorId())) {
                vendorList.add(vendor);
                return;
            }
            if(CollectionUtils.isNotEmpty(extendVendorIdList) && extendVendorIdList.contains(vendor.getVendorId())) {
                vendor.getExtensions().put(ExtSouBidComponent.fieldName(ApiExtSouTechScoreLineQueryDTO::getExtendAbandon), YesOrNo.YES.getValue());
                vendorList.add(vendor);
            }
        });

        return vendorList;
    }

    private List<Long> extendAbandonVendor(List<Long> vendorIdList, ApiExtSouTechScoreLineQueryDTO query) {
        //是否扩展已评分的供应商
        if(CollectionUtils.isEmpty(vendorIdList)) {
            return null;
        }
        if(!YesOrNo.YES.getValue().equals(query.getExtendAbandon())) {
            return null;
        }
        LambdaQueryWrapper<ExtSouTechScoreLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouTechScoreLine::getProjectId, query.getProjectId());
        queryWrapper.notIn(ExtSouTechScoreLine::getVendorId, vendorIdList);
        queryWrapper.select(ExtSouTechScoreLine::getVendorId, ExtSouTechScoreLine::getProjectId);
        queryWrapper.groupBy(ExtSouTechScoreLine::getVendorId);

        List<ExtSouTechScoreLine> lineList = techScoreLineService.list(queryWrapper);
        return lineList.stream().map(ExtSouTechScoreLine::getVendorId).distinct().collect(Collectors.toList());
    }

    /**
     * 查询技术打分项
     * @param query
     * @return
     */
    private List<ApiExtScoreRuleDto> queryTechScoreRule(ApiExtSouTechScoreLineQueryDTO query) {
        LambdaQueryWrapper<ExtScoreRule> queryRuleWrapper = new LambdaQueryWrapper<>();
        queryRuleWrapper.eq(ExtScoreRule::getProjectId, query.getProjectId());

        List<ExtScoreRule> extScoreRuleList = scoreRuleMapper.selectList(queryRuleWrapper);

        List<ApiExtScoreRuleDto> scoreRuleDtoList = JSON.parseArray(JSON.toJSONString(extScoreRuleList), ApiExtScoreRuleDto.class);
        return scoreRuleDtoList;
    }

    /**
     * 查询得分头
     * @param query
     * @return
     */
    private List<ExtSouTechScoreHead> queryTechScoreHead(ApiExtSouTechScoreLineQueryDTO query, List<ExtSouGroup> groupList) {
        LambdaQueryWrapper<ExtSouTechScoreHead> queryScoreHeadWrapper = new LambdaQueryWrapper<>();
        queryScoreHeadWrapper.eq(ExtSouTechScoreHead::getProjectId, query.getProjectId());
        queryScoreHeadWrapper.in(CollectionUtils.isNotEmpty(groupList), ExtSouTechScoreHead::getGroupId, groupList.stream().map(t -> t.getGroupId()).distinct().collect(Collectors.toList()));
        queryScoreHeadWrapper.groupBy(ExtSouTechScoreHead::getGroupId);
        List<ExtSouTechScoreHead> techScoreHeadList = techScoreHeadService.list(queryScoreHeadWrapper);
        return techScoreHeadList;
    }

    /**
     * 查询得分明细
     * @param query
     * @return
     */
    private List<ExtSouTechScoreLine> queryTechScoreLine(ApiExtSouTechScoreLineQueryDTO query, List<ExtSouTechScoreHead> techScoreHeadList) {
        LambdaQueryWrapper<ExtSouTechScoreLine> queryScoreLineWrapper = new LambdaQueryWrapper<>();
        queryScoreLineWrapper.eq(ExtSouTechScoreLine::getProjectId, query.getProjectId());
        queryScoreLineWrapper.in(CollectionUtils.isNotEmpty(techScoreHeadList), ExtSouTechScoreLine::getTechScoreHeadId, techScoreHeadList.stream().map(ExtSouTechScoreHead::getTechScoreHeadId).collect(Collectors.toList()));

        List<ExtSouTechScoreLine> techScoreLineList = techScoreLineService.list(queryScoreLineWrapper);
        techScoreLineList = techScoreLineList.stream().sorted(Comparator.comparingLong(ExtSouTechScoreLine::getTechScoreLineId).reversed()).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StringUtils.joinWith("_", o.getTechScoreHeadId(), o.getScoreRuleLineId(), o.getVendorId(), o.getGroupId())))), ArrayList::new));

        return techScoreLineList;
    }

    private List<String> queryTechScoreHistSubimteDate(ApiExtSouTechScoreLineQueryDTO query, List<ExtSouTechScoreHead> techScoreHeadList) {
        LambdaQueryWrapper<ExtSccSouTechScoreHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ExtSccSouTechScoreHistory::getSubmiteDate);
        queryWrapper.eq(ExtSccSouTechScoreHistory::getProjectId, query.getProjectId());
        queryWrapper.in(CollectionUtils.isNotEmpty(techScoreHeadList), ExtSccSouTechScoreHistory::getTechScoreHeadId, techScoreHeadList.stream().map(ExtSouTechScoreHead::getTechScoreHeadId).collect(Collectors.toList()));
        queryWrapper.orderByDesc(ExtSccSouTechScoreHistory::getHistoryNum);
        queryWrapper.groupBy(ExtSccSouTechScoreHistory::getHistoryNum);
        List<ExtSccSouTechScoreHistory> historyList = techScoreHistoryService.list(queryWrapper);
        List<String> submiteDateList = new ArrayList<>();
        historyList.stream().forEach(h -> {
            submiteDateList.add(DateUtil.format(h.getSubmiteDate(), DateUtil.DATE_FORMAT_19));
        });
        return submiteDateList;
    }

    /**
     * 查询历史得分明细
     * @param query
     * @return
     */
    @SneakyThrows(value = {Exception.class})
    private List<ExtSouTechScoreLine> queryTechScoreHistoryLine(ApiExtSouTechScoreLineQueryDTO query, List<ExtSouTechScoreHead> techScoreHeadList) {

        Date submiteDate = null;
        if(StringUtils.isNotBlank(query.getSubmiteDate())) {
            submiteDate = DateUtil.parseDate(query.getSubmiteDate(), DateUtil.DATE_FORMAT_19);
        }
        LambdaQueryWrapper<ExtSccSouTechScoreHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSccSouTechScoreHistory::getProjectId, query.getProjectId());
        queryWrapper.eq(ExtSccSouTechScoreHistory::getSubmiteDate, submiteDate);
        queryWrapper.in(CollectionUtils.isNotEmpty(techScoreHeadList), ExtSccSouTechScoreHistory::getTechScoreHeadId, techScoreHeadList.stream().map(ExtSouTechScoreHead::getTechScoreHeadId).collect(Collectors.toList()));

        List<ExtSccSouTechScoreHistory> historyList = techScoreHistoryService.list(queryWrapper);
        historyList = historyList.stream().sorted(Comparator.comparingLong(ExtSccSouTechScoreHistory::getTechScoreLineId).reversed()).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StringUtils.joinWith("_", o.getTechScoreHeadId(), o.getScoreRuleLineId(), o.getVendorId(), o.getGroupId())))), ArrayList::new));

        List<ExtSouTechScoreLine> techScoreLineList = new ArrayList<>(historyList.size());
        historyList.stream().forEach(h -> {
            ExtSouTechScoreLine line = new ExtSouTechScoreLine();
            BeanCopyUtil.copyProperties(line, h);
            techScoreLineList.add(line);
        });
        return techScoreLineList;
    }

    private List<ApiExtScoreRuleDto> calculateThenAssembleScore(ApiExtSouTechScoreLineQueryDTO query,ExtSouProject project, List<ExtSouTechScoreHead> techScoreHeadList, List<ApiExtScoreRuleDto> scoreRuleDtoList, Map<String, ExtSouTechScoreLine> techScoreLineMap, List<ExtSouVendor> vendorList) {
        //组装数据
        List<ApiExtScoreRuleDto> allScoreRuleList = new ArrayList<>();

        Map<Long, Set<Long>> headVendorMap = new HashMap<>(50);
        for(ExtSouTechScoreLine line : techScoreLineMap.values()) {
            if(!headVendorMap.containsKey(line.getTechScoreHeadId())) {
                headVendorMap.put(line.getTechScoreHeadId(), new HashSet<>(16));
            }
            headVendorMap.get(line.getTechScoreHeadId()).add(line.getVendorId());
        }

        //脱敏供应商名称
        for (ExtSouTechScoreHead scoreHead : techScoreHeadList) {
            for (ApiExtScoreRuleDto scoreRuleDto : scoreRuleDtoList) {
                ApiExtScoreRuleDto ruleDto = new ApiExtScoreRuleDto();
                BeanCopyUtil.copyProperties(ruleDto, scoreRuleDto);
                allScoreRuleList.add(ruleDto);

                ruleDto.setVendorScoreList(new ArrayList<>());
                //根据每一个评分人组装数据
                for (ExtSouVendor vendor : vendorList) {
                    if(YesOrNo.YES.getValue().equals(query.getExtendAbandon()) && YesOrNo.YES.getValue().equals(vendor.getExtensions().getOrDefault(ExtSouBidComponent.fieldName(ApiExtSouTechScoreLineQueryDTO::getExtendAbandon), YesOrNo.NO.getValue()))) {
                        //如果评分明细不包含供应商明细，则直接返回
                        if(!headVendorMap.getOrDefault(scoreHead.getTechScoreHeadId(), new HashSet<>(16)).contains(vendor.getVendorId())) {
                            continue;
                        }
                    }

                    ApiExtSouTechScoreLineDto techScoreLineDto = new ApiExtSouTechScoreLineDto();
                    String key = StringUtils.joinWith("_", ruleDto.getScoreRuleId(), vendor.getVendorId(), scoreHead.getGroupId());
                    ExtSouTechScoreLine techScoreLine = techScoreLineMap.getOrDefault(key, new ApiExtSouTechScoreLineDto());
                    //复制属性值
                    BeanCopyUtil.copyProperties(techScoreLineDto, techScoreLine);
                    techScoreLineDto.setVendorId(vendor.getVendorId());
                    techScoreLineDto.setVendorCode(vendor.getVendorCode());
                    techScoreLineDto.setVendorName(vendor.getVendorName());
                    techScoreLineDto.setTechScoreHeadId(techScoreLine.getTechScoreHeadId());
                    techScoreLineDto.setGroupId(query.getGroupId());
                    techScoreLineDto.getExtensions().put(ExtSouBidComponent.fieldName(ApiExtSouTechScoreLineQueryDTO::getExtendAbandon), vendor.getExtensions().get(ExtSouBidComponent.fieldName(ApiExtSouTechScoreLineQueryDTO::getExtendAbandon)));

                    ruleDto.getVendorScoreList().add(techScoreLineDto);
                    ruleDto.setGroupId(scoreHead.getGroupId());
                }
            }
        }

        //计算平均分
        Map<Long, BigDecimal> averageMap = caculateAverageScore(project.getProjectId());
        //计算总分
        caculateScore(allScoreRuleList, averageMap);
        return allScoreRuleList;
    }

    @Override
    public ApiExtScoreRuleRespDto getExtScore(ApiExtSouTechScoreLineQueryDTO query) {
        //查询项目信息
        ExtSouProject project = projectMapper.selectById(query.getProjectId());
        AssertUtils.notNull(project, "项目信息不存在！");

        //查询打分项
        List<ApiExtScoreRuleDto> scoreRuleDtoList = this.queryTechScoreRule(query);

        //查询技术评分供应商
        List<ExtSouVendor> vendorList = this.queryTechScoreVendory(query, project);

        //查询评委
        List<ExtSouGroup> groupList = this.queryTechScoreGroup(query);

        //查询技术分头表
        List<ExtSouTechScoreHead> techScoreHeadList = this.queryTechScoreHead(query, groupList);

        //查询供应商得分
        List<ExtSouTechScoreLine> techScoreLineList = this.queryTechScoreLine(query, techScoreHeadList);

        Map<String, ExtSouTechScoreLine> techScoreLineMap = techScoreLineList.stream().collect(Collectors.toMap(l -> StringUtils.joinWith("_", l.getScoreRuleLineId(), l.getVendorId(), l.getGroupId()), Function.identity(), (k1, k2) -> k2));


        //组装数据
        List<ApiExtScoreRuleDto> allScoreRuleList = this.calculateThenAssembleScore(query, project, techScoreHeadList, scoreRuleDtoList, techScoreLineMap, vendorList);

        ApiExtScoreRuleRespDto apiExtScoreRuleRespDto = new ApiExtScoreRuleRespDto();
        Map<Long, List<ApiExtScoreRuleDto>> scoreMap = allScoreRuleList.stream().filter(o -> !Objects.isNull(o.getGroupId())).collect(Collectors.groupingBy(ApiExtScoreRuleDto::getGroupId));
        //补充未评分人数据
        groupList.stream().filter(g -> !scoreMap.containsKey(g.getGroupId())).forEach(g -> scoreMap.put(g.getGroupId(), new ArrayList<>()));

        apiExtScoreRuleRespDto.setVendorList(vendorList);
        apiExtScoreRuleRespDto.setGroupList(groupList);
        apiExtScoreRuleRespDto.setScoreMap(scoreMap);
        apiExtScoreRuleRespDto.setMergeFlag(isMergeApplitionNum(project.getProjectId()));
        if (scoreMap != null) {
            apiExtScoreRuleRespDto.setScoreMapFile(dealScoreMapFile(scoreMap));
        }

        //技术评分分析
        scoreAnalysis(apiExtScoreRuleRespDto);

        return apiExtScoreRuleRespDto;
    }

    private Map<Long, List<ExtSouTechScoreFile>> dealScoreMapFile(Map<Long, List<ApiExtScoreRuleDto>> sMap) {
        Map<Long, List<ExtSouTechScoreFile>> reMap = new HashMap<>(15);
        Set<Long> keys = sMap.keySet();
        keys.forEach(e -> {
            LambdaQueryWrapper<ExtSouTechScoreHead> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ExtSouTechScoreHead::getGroupId, e);
            List<ExtSouTechScoreHead> shList = techScoreHeadService.list(lqw);
            if (CollectionUtils.isNotEmpty(shList)) {
                LambdaQueryWrapper<ExtSouTechScoreFile> tsQw = new LambdaQueryWrapper<>();
                tsQw.eq(ExtSouTechScoreFile::getTechScoreHeadId, shList.get(0).getTechScoreHeadId());
                reMap.put(e, extSouTechScoreFileMapper.selectList(tsQw));
            }
        });
        return reMap;
    }

    protected void scoreAnalysis(ApiExtScoreRuleRespDto apiExtScoreRuleRespDto) {
        apiExtScoreRuleRespDto.setScoreAnalysisDynamicForm(new ScoreAnalysisDynamicFormDto());
        /** 供应商数据 */
        if(CollectionUtils.isNotEmpty(apiExtScoreRuleRespDto.getGroupList()) && CollectionUtils.isNotEmpty(apiExtScoreRuleRespDto.getVendorList())) {
            List<ScoreAnalysisVendorDto> vendorDtoList = new ArrayList<>();
            apiExtScoreRuleRespDto.getVendorList().forEach(v -> {
                ScoreAnalysisVendorDto vendor = new ScoreAnalysisVendorDto();
                vendor.setVendorCode(v.getVendorCode());
                vendor.setVendorName(v.getVendorName());
                vendorDtoList.add(vendor);
            });
            apiExtScoreRuleRespDto.getScoreAnalysisDynamicForm().setDynamicTitleList(vendorDtoList);

            apiExtScoreRuleRespDto.getScoreAnalysisDynamicForm().setFormDataList(new ArrayList<>(apiExtScoreRuleRespDto.getGroupList().size()));
            apiExtScoreRuleRespDto.getGroupList().stream().filter(g -> apiExtScoreRuleRespDto.getScoreMap().containsKey(g.getGroupId())).forEach(group -> {
                ScoreAnalysisDto scoreAnalysisDto = new ScoreAnalysisDto();
                scoreAnalysisDto.setFullName(group.getFullName());
                scoreAnalysisDto.setUserName(group.getUserName());
                scoreAnalysisDto.setScoreAnalysisVendorDtoList(new ArrayList<>(vendorDtoList.size()));
                apiExtScoreRuleRespDto.getScoreAnalysisDynamicForm().getFormDataList().add(scoreAnalysisDto);

                List<ApiExtScoreRuleDto> scoreRuleDtoList = apiExtScoreRuleRespDto.getScoreMap().get(group.getGroupId());
                ApiExtScoreRuleDto scoreRuleDto = scoreRuleDtoList.get(0);
                if(CollectionUtils.isNotEmpty(scoreRuleDto.getVendorScoreList())) {
                    scoreRuleDto.getVendorScoreList().stream().forEach(score -> {
                        ScoreAnalysisVendorDto vendorDto = new ScoreAnalysisVendorDto();
                        vendorDto.setVendorCode(score.getVendorCode());
                        vendorDto.setVendorName(score.getVendorName());
                        vendorDto.setTotalScore(score.getTotalScore());
                        vendorDto.setAverageScore(score.getAverageScore());
                        if(BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(score.getAverageScore(), BigDecimal.ZERO)) != 0) {
                            BigDecimal diff = ObjectUtils.defaultIfNull(vendorDto.getTotalScore(), BigDecimal.ZERO).subtract(vendorDto.getAverageScore());
                            vendorDto.setDifferenceRatio(diff.divide(vendorDto.getAverageScore(), 4, RoundingMode.HALF_UP));
                        }
                        scoreAnalysisDto.getScoreAnalysisVendorDtoList().add(vendorDto);
                    });
                }
            });

        }
    }

    @Override
    public ApiExtScoreRuleRespDto getExtScoreHistory(ApiExtSouTechScoreLineQueryDTO query) {
        //查询项目信息
        ExtSouProject project = projectMapper.selectById(query.getProjectId());
        AssertUtils.notNull(project, "项目信息不存在！");

        //查询打分项
        List<ApiExtScoreRuleDto> scoreRuleDtoList = this.queryTechScoreRule(query);

        //查询技术评分供应商
        List<ExtSouVendor> vendorList = this.queryTechScoreVendory(query, project);

        //查询评委
        List<ExtSouGroup> groupList = this.queryTechScoreGroup(query);

        ExtSouGroup group = new ExtSouGroup();
        if(CollectionUtils.isNotEmpty(groupList)) {
            group = Objects.isNull(query.getGroupId()) ? groupList.get(0) : groupList.stream().filter(g -> g.getGroupId().compareTo(query.getGroupId()) == 0).findFirst().orElseGet(ExtSouGroup::new);
        }

        //查询技术分头表
        List<ExtSouTechScoreHead> techScoreHeadList = this.queryTechScoreHead(query, Collections.singletonList(group));

        //查询历史得分提交日期
        List<String> historySubmiteDateList = this.queryTechScoreHistSubimteDate(query, techScoreHeadList);
        if(StringUtils.isBlank(query.getSubmiteDate()) && CollectionUtils.isNotEmpty(historySubmiteDateList)) {
            query.setSubmiteDate(historySubmiteDateList.get(0));
        }

        //查询供应商得分
        List<ExtSouTechScoreLine> techScoreLineList = this.queryTechScoreHistoryLine(query, techScoreHeadList);

        Map<String, ExtSouTechScoreLine> techScoreLineMap = techScoreLineList.stream().collect(Collectors.toMap(l -> StringUtils.joinWith("_", l.getScoreRuleLineId(), l.getVendorId(), l.getGroupId()), Function.identity(), (k1, k2) -> k2));


        //组装数据
        List<ApiExtScoreRuleDto> allScoreRuleList = this.calculateThenAssembleScore(query, project, techScoreHeadList, scoreRuleDtoList, techScoreLineMap, vendorList);

        ApiExtScoreRuleRespDto apiExtScoreRuleRespDto = new ApiExtScoreRuleRespDto();
        Map<Long, List<ApiExtScoreRuleDto>> scoreMap = allScoreRuleList.stream().filter(o -> !Objects.isNull(o.getGroupId())).collect(Collectors.groupingBy(ApiExtScoreRuleDto::getGroupId));
        //补充未评分人数据
        groupList.stream().filter(g -> !scoreMap.containsKey(g.getGroupId())).forEach(g -> scoreMap.put(g.getGroupId(), new ArrayList<>()));

        Map<Long, List<ApiExtScoreRuleDto>> scoreMapNew = JSON.parseObject(JSON.toJSONString(scoreMap), Map.class);
        apiExtScoreRuleRespDto.setVendorList(vendorList);
        apiExtScoreRuleRespDto.setGroupList(groupList);
        apiExtScoreRuleRespDto.setScoreMap(scoreMapNew);
        if (scoreMap != null) {
            apiExtScoreRuleRespDto.setScoreMapFile(dealScoreMapFile(query.getGroupId()));
        }
        apiExtScoreRuleRespDto.setMergeFlag(isMergeApplitionNum(project.getProjectId()));
        apiExtScoreRuleRespDto.setSubmiteDateList(historySubmiteDateList);
        apiExtScoreRuleRespDto.setScoreHistoryList(allScoreRuleList);

        return apiExtScoreRuleRespDto;
    }

    private Map<Long, List<ExtSouTechScoreFile>> dealScoreMapFile(Long groupId) {
        Map<Long, List<ExtSouTechScoreFile>> reMap = new HashMap<>(15);
        LambdaQueryWrapper<ExtSouTechScoreHead> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ExtSouTechScoreHead::getGroupId, groupId);
        List<ExtSouTechScoreHead> shList = techScoreHeadService.list(lqw);
        if (CollectionUtils.isNotEmpty(shList)) {
            LambdaQueryWrapper<ExtSouTechScoreFile> tsQw = new LambdaQueryWrapper<>();
            tsQw.eq(ExtSouTechScoreFile::getTechScoreHeadId, shList.get(0).getTechScoreHeadId());
            reMap.put(groupId, extSouTechScoreFileMapper.selectList(tsQw));
        }
        return reMap;
    }

    protected boolean isMergeApplitionNum(Long projectId) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, projectId);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        int count = (int) demandService.count(queryWrapper);
        if (Integer.compare(count, 1) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 查询有技术评分的评委ID
     *
     * @param projectId
     * @return
     */
    private List<Long> queryEvaGroupIdList(Long projectId) {
        LambdaQueryWrapper<ExtSouGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouGroup::getProjectId, projectId);
        queryWrapper.eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue());
        queryWrapper.eq(ExtSouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH.getCode());
        return groupMapper.selectList(queryWrapper).stream().map(ExtSouGroup::getGroupId).collect(Collectors.toList());
    }

    @Override
    public Map<Long, BigDecimal> caculateAverageScore(Long projectId) {

        //评分项目
        ExtSouProject project = iExtSouProjectService.getById(projectId);

        //评委ID
        List<Long> groupIdList = queryEvaGroupIdList(projectId);

        //查询评分规则-技术打分项
        LambdaQueryWrapper<ExtScoreRule> queryRuleWrapper = new LambdaQueryWrapper<>();
        queryRuleWrapper.eq(ExtScoreRule::getProjectId, projectId);
        queryRuleWrapper.eq(ExtScoreRule::getScoreItem, ScoreConfigItemEnum.TEH_REVIEW);
        queryRuleWrapper.orderByAsc(ExtScoreRule::getScoreRuleId);

        List<ExtScoreRule> extScoreRuleList = scoreRuleMapper.selectList(queryRuleWrapper);
        if (CollectionUtils.isEmpty(extScoreRuleList)) {
            return new HashMap<>(50);
        }

        LambdaQueryWrapper<ExtSouTechScoreHead> techHeadQuery = new LambdaQueryWrapper<>();
        techHeadQuery.eq(ExtSouTechScoreHead::getProjectId, projectId);
        techHeadQuery.eq(!SouBiddingProStatusEnum.TECH_BID_EVA.getCode().equals(project.getProjectStatus()), ExtSouTechScoreHead::getScoreStatus, TechScoreStatusEnum.FINISHED.getCode());
        techHeadQuery.in(CollectionUtils.isNotEmpty(groupIdList), ExtSouTechScoreHead::getGroupId, groupIdList);
        //查询技术分头表
        List<ExtSouTechScoreHead> headList = techScoreHeadService.list(techHeadQuery);
        if (CollectionUtils.isEmpty(headList)) {
            return new HashMap<>(50);
        }

        //技术投标的供应商范围
        List<Long> techVendorIdList = queryTechTenderVendorList(projectId);

        //查询供应商得分
        LambdaQueryWrapper<ExtSouTechScoreLine> queryScoreLineWrapper = new LambdaQueryWrapper<>();
        queryScoreLineWrapper.eq(ExtSouTechScoreLine::getProjectId, projectId);
        queryScoreLineWrapper.in(ExtSouTechScoreLine::getScoreRuleLineId, extScoreRuleList.stream().map(ExtScoreRule::getScoreRuleId).collect(Collectors.toList()));
        queryScoreLineWrapper.in(CollectionUtils.isNotEmpty(techVendorIdList), ExtSouTechScoreLine::getVendorId, techVendorIdList);
        queryScoreLineWrapper.in(ExtSouTechScoreLine::getTechScoreHeadId, headList.stream().map(ExtSouTechScoreHead::getTechScoreHeadId).collect(Collectors.toList()));

        List<ExtSouTechScoreLine> techScoreLineList = techScoreLineService.list(queryScoreLineWrapper);

        //统计每个供应商打分人个数
        Map<String, Set<Long>> countGroupMap = new HashMap<>(50);

        //统计每个供应商总得分(多个打分人-按头表计算)
        Map<String, BigDecimal> vendorTotalScoreMap = new HashMap<>(50);
        techScoreLineList.stream().forEach(line -> {
            addScore(vendorTotalScoreMap, line.getVendorId().toString(), line.getScore());
            if (!countGroupMap.containsKey(line.getVendorId().toString())) {
                countGroupMap.put(line.getVendorId().toString(), new HashSet<>());
            }
            countGroupMap.get(line.getVendorId().toString()).add(line.getTechScoreHeadId());
        });


        Map<Long, BigDecimal> averageMap = new HashMap<>(50);

        for (String vendorIdStr : vendorTotalScoreMap.keySet()) {
            Long vendorId = Long.valueOf(vendorIdStr);
            BigDecimal totalScore = vendorTotalScoreMap.get(vendorIdStr);
            BigDecimal averageScore = totalScore.divide(new BigDecimal(countGroupMap.get(vendorIdStr).size()), 2, BigDecimal.ROUND_UP);
            averageMap.put(vendorId, averageScore);
        }

        return averageMap;
    }


    private void caculateScore(List<ApiExtScoreRuleDto> scoreRuleDtoList, Map<Long, BigDecimal> averageMap) {
        Map<String, List<ApiExtScoreRuleDto>> scoreRuleDtoGroup = scoreRuleDtoList.stream().collect(Collectors.groupingBy(ApiExtScoreRuleDto::getScoreItem));

        List<ApiExtScoreRuleDto> techList = scoreRuleDtoGroup.getOrDefault(ScoreConfigItemEnum.TEH_REVIEW.getCode(), new ArrayList<>());

        //按供应商+评分人+行
        Map<String, BigDecimal> techScoreMap = new HashMap<>(50);

        //累计总分
        for (ApiExtScoreRuleDto ruleDto : techList) {
            for (ApiExtSouTechScoreLineDto lineDto : ruleDto.getVendorScoreList()) {
                String key = StringUtils.joinWith("_", lineDto.getVendorId(), lineDto.getGroupId(), lineDto.getTechScoreHeadId());
                addScore(techScoreMap, key, lineDto.getScore());
            }
        }

        //赋值总分 和 平均分
        for (ApiExtScoreRuleDto ruleDto : scoreRuleDtoList) {
            ruleDto.setTotalScoreList(new ArrayList<>(ruleDto.getVendorScoreList().size()));
            for (ApiExtSouTechScoreLineDto lineDto : ruleDto.getVendorScoreList()) {
                String key = StringUtils.joinWith("_", lineDto.getVendorId(), lineDto.getGroupId(), lineDto.getTechScoreHeadId());
                lineDto.setTotalScore(techScoreMap.get(key));
                lineDto.setAverageScore(averageMap.get(lineDto.getVendorId()));
                ruleDto.getTotalScoreList().add(lineDto.getTotalScore());
            }
        }

    }

    private void addScore(Map<String, BigDecimal> techScoreMap, String key, BigDecimal score) {
        if (!techScoreMap.containsKey(key)) {
            techScoreMap.put(key, BigDecimal.ZERO);
        }
        BigDecimal add = Objects.isNull(score) ? BigDecimal.ZERO : score;
        techScoreMap.put(key, techScoreMap.get(key).add(add));
    }

    @Override
    public void exportScoreExcelTemplate(ApiExtSouTechScoreLineQueryDTO query, HttpServletResponse response) throws Exception {
        List<ApiExtScoreRuleDto> scoreRuleDtoList = this.getExtScoreRule(query);

        List<List<String>> headList = ExtSouTechScorePO.getHeadList(scoreRuleDtoList);

        List<List<Object>> dataList = new ArrayList<>();

        //查询字典
        List<DictItem> dictItems = baseClient.listDictItemByDictCode(DictCodeEnum.SOU_SCORE_CONFIG_ITEM.getCode());
        Map<String, String> scoreItemMap = dictItems.stream().collect(Collectors.toMap(DictItem::getDictItemCode, DictItem::getDictItemName, (k1, k2) -> k2));

        String matchReviewItem = "与长城集团合作经历及评价";
        AtomicReference<Boolean> match = new AtomicReference<>(false);
        List<Long> vendorIdList = scoreRuleDtoList.get(0).getVendorScoreList().stream().map(l -> l.getVendorId()).distinct().collect(Collectors.toList());
        scoreRuleDtoList.stream().forEach(apiExtScoreRuleDto -> {
            if(matchReviewItem.equals(apiExtScoreRuleDto.getReviewItem())) {
                match.set(true);
            }
        });

        if(match.get() && CollectionUtils.isNotEmpty(vendorIdList)) {
            Map<String, Object> params = new HashMap<>(15);
            params.put("vendorIdList", vendorIdList);
            List<ProjectVO> projectVOList = extTempMapper.queryScoreInfo(params);
            Map<Long, List<ProjectVO>> vendorScoreMap = projectVOList.stream().collect(Collectors.groupingBy(o -> o.getCompanyId()));
            //限时最多五个这里定义五个字符串标号
            LinkedList<String> sortNumberList = new LinkedList<>();
            sortNumberList.add("①");
            sortNumberList.add("②");
            sortNumberList.add("③");
            sortNumberList.add("④");
            sortNumberList.add("⑤");
            scoreRuleDtoList.stream().forEach(apiExtScoreRuleDto -> {
                if(matchReviewItem.equals(apiExtScoreRuleDto.getReviewItem())) {
                    apiExtScoreRuleDto.getVendorScoreList().stream().filter(v -> vendorScoreMap.containsKey(v.getVendorId())).forEach(score -> {
                        if(!(score.getExtDescription()!=null&&score.getExtDescription().length()>0)) {
                            List<ProjectVO> voList = vendorScoreMap.get(score.getVendorId());
                            if(CollectionUtils.isNotEmpty(voList)){
                                for (int i = 0; i < voList.size(); i++) {
                                    voList.get(i).setSortNumber(sortNumberList.get(i));
                                }
                                score.setExtDescription(voList.stream().map(v -> v.formateResult()).collect(Collectors.joining(SrmConstant.SIG_3)));
                            }
                        }
                    });
                }
            });
        }


        for (ApiExtScoreRuleDto ruleDto : scoreRuleDtoList) {
            List<Object> data = new ArrayList<>();
            data.add(scoreItemMap.getOrDefault(ruleDto.getScoreItem(), ruleDto.getScoreItem()));
            data.add(ruleDto.getReviewItem());
            data.add(ruleDto.getMaxScore());
            data.add(ruleDto.getScoreDesc());

            if (CollectionUtils.isNotEmpty(ruleDto.getVendorScoreList())) {
                for (ApiExtSouTechScoreLineDto lineDto : ruleDto.getVendorScoreList()) {
                    data.add(lineDto.getExtDescription());
                    data.add(lineDto.getScore());
                }
            }
            dataList.add(data);
        }

        // 获取输出流
        OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "评分模板");
        EasyExcel.write(outputStream).head(headList).sheet(0).sheetName("sheetName").registerWriteHandler(new NpmSouBidCellWriteHandler()).doWrite(dataList);

    }

    @Override
    public List<Map<String, Object>> exportScoreExcelForArchivist(ApiExtSouTechScoreLineQueryDTO query) {
        Map<Long, List<ApiExtScoreRuleDto>> scoreRuleMap = this.getExtScoreRuleBatchAsAllGroup(query);
        //查询字典
        List<DictItem> dictItems = baseClient.listDictItemByDictCode(DictCodeEnum.SOU_SCORE_CONFIG_ITEM.getCode());
        Map<String, String> scoreItemMap = dictItems.stream().collect(Collectors.toMap(DictItem::getDictItemCode, DictItem::getDictItemName, (k1, k2) -> k2));

        List<Map<String, Object>> archivistList = new ArrayList<>(16);

        for(List<ApiExtScoreRuleDto> scoreRuleDtoList : scoreRuleMap.values()) {

            Map<String, Object> archivistMap = new HashMap<>(16);
            archivistList.add(archivistMap);

            List<List<String>> headList = ExtSouTechScorePO.getHeadList(scoreRuleDtoList);

            List<List<Object>> dataList = new ArrayList<>();

            String groupName = null;
            List<ApiExtSouTechScoreLineDto> lineList = null;
            String souName = null;
            String dateStr = null;

            for (ApiExtScoreRuleDto ruleDto : scoreRuleDtoList) {
                if(StringUtils.isBlank(groupName)) {
                    groupName = ruleDto.getFullName();
                    souName = ruleDto.getSouName();
                    if(CollectionUtils.isNotEmpty(ruleDto.getVendorScoreList())) {
                        dateStr = DateUtil.format(ruleDto.getVendorScoreList().get(0).getLastUpdateDate(), DateUtil.DATE_FORMAT_7);
                    } else {
                        dateStr = DateUtil.format(new Date(), DateUtil.DATE_FORMAT_7);
                    }

                }
                List<Object> data = new ArrayList<>();
                data.add(scoreItemMap.getOrDefault(ruleDto.getScoreItem(), ruleDto.getScoreItem()));
                data.add(ruleDto.getReviewItem());
                data.add(ruleDto.getMaxScore());
                data.add(ruleDto.getScoreDesc());
                if (CollectionUtils.isNotEmpty(ruleDto.getVendorScoreList())) {
                    if(Objects.isNull(lineList)) {
                        lineList = ruleDto.getVendorScoreList();
                    }
                    for (ApiExtSouTechScoreLineDto lineDto : ruleDto.getVendorScoreList()) {
                        data.add(lineDto.getExtDescription());
                        data.add(lineDto.getScore());
                    }
                }
                dataList.add(data);
            }

            String excelName = StringUtils.join("评标结果-", groupName, "-", souName, "-", dateStr, ".xlsx");

            archivistMap.put("headTitle", headList);
            archivistMap.put("dataList", dataList);
            archivistMap.put("excelName", excelName);

        }

        return archivistList;
    }

    @Override
    public void exportScoreExcelBatch(ApiExtSouTechScoreLineQueryDTO query, HttpServletResponse response) throws Exception {

        Map<Long, List<ApiExtScoreRuleDto>> scoreRuleMap = this.getExtScoreRuleBatchAsAllGroup(query);

        // 获取输出流
        OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "招标评分");

        //查询字典
        List<DictItem> dictItems = baseClient.listDictItemByDictCode(DictCodeEnum.SOU_SCORE_CONFIG_ITEM.getCode());
        Map<String, String> scoreItemMap = dictItems.stream().collect(Collectors.toMap(DictItem::getDictItemCode, DictItem::getDictItemName, (k1, k2) -> k2));


        ExcelWriter excelWriter = EasyExcel.write(outputStream).build();

        for(List<ApiExtScoreRuleDto> scoreRuleDtoList : scoreRuleMap.values()) {

            //合并单元格
            NpmSouBidMergeStrategy mergeStrategy = new NpmSouBidMergeStrategy();

            List<List<String>> headList = ExtSouTechScorePO.getHeadList(scoreRuleDtoList);

            List<List<Object>> dataList = new ArrayList<>();

            String groupName = null;
            List<ApiExtSouTechScoreLineDto> lineList = null;
            Integer rowIndex = 1;
            for (ApiExtScoreRuleDto ruleDto : scoreRuleDtoList) {
                rowIndex++;
                if(StringUtils.isBlank(groupName)) {
                    groupName = StringUtils.joinWith(SrmConstant.SHORT_LINE, ruleDto.getUserName(), ruleDto.getFullName());
                }
                List<Object> data = new ArrayList<>();
                data.add(scoreItemMap.getOrDefault(ruleDto.getScoreItem(), ruleDto.getScoreItem()));
                data.add(ruleDto.getReviewItem());
                data.add(ruleDto.getMaxScore());
                data.add(ruleDto.getScoreDesc());
                if (CollectionUtils.isNotEmpty(ruleDto.getVendorScoreList())) {
                    if(Objects.isNull(lineList)) {
                        lineList = ruleDto.getVendorScoreList();
                    }
                    for (ApiExtSouTechScoreLineDto lineDto : ruleDto.getVendorScoreList()) {
                        data.add(lineDto.getExtDescription());
                        data.add(lineDto.getScore());
                    }
                }
                dataList.add(data);
            }

            if(CollectionUtils.isNotEmpty(lineList)) {
                for(int i = 0; i < NUM2; i++) {
                    rowIndex++;
                    String dataValue =  "总得分";
                    if( Integer.compare(SrmConstant.NUM_ZERO, i) == SrmConstant.NUM_MINUS_ONE) {
                        dataValue =  "平均分";
                    }

                    List<Object> data = new ArrayList<>();
                    data.add(dataValue);
                    data.add(dataValue);
                    data.add(dataValue);
                    data.add(dataValue);

                    Integer colIndex = 3;
                    mergeStrategy.addMergeRange(rowIndex, rowIndex, 0, colIndex);

                    for (ApiExtSouTechScoreLineDto scoreDto : lineList) {
                        BigDecimal dataScore = scoreDto.getTotalScore();
                        if( Integer.compare(SrmConstant.NUM_ZERO, i) == SrmConstant.NUM_MINUS_ONE) {
                            dataScore = scoreDto.getAverageScore();
                        }
                        data.add(dataScore);
                        data.add(dataScore);
                        mergeStrategy.addMergeRange(rowIndex, rowIndex, colIndex+1, colIndex+2);
                        colIndex = colIndex+2;
                    }
                    dataList.add(data);
                }
            }


            excelWriter.write(dataList, EasyExcel.writerSheet().sheetName(groupName).registerWriteHandler(mergeStrategy).registerWriteHandler(new NpmSouBidCellWriteHandler())
                    .head(headList).build());

        }

        excelWriter.finish();

    }

    @Override
    public List<ExtSouTechScoreHeadDto> getTechScoreHead(Long projectId) {

        LambdaQueryWrapper<ExtSouGroup> groupQuery = new LambdaQueryWrapper<>();
        groupQuery.eq(ExtSouGroup::getProjectId, projectId);
        groupQuery.eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue());
        groupQuery.eq(ExtSouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH.getCode());
        List<ExtSouGroup> groupList = groupMapper.selectList(groupQuery);

        LambdaQueryWrapper<ExtSouTechScoreHead> query = new LambdaQueryWrapper<>();
        query.eq(ExtSouTechScoreHead::getProjectId, projectId);
        query.in(CollectionUtils.isNotEmpty(groupList), ExtSouTechScoreHead::getGroupId, groupList.stream().map(ExtSouGroup::getGroupId).collect(Collectors.toList()));
        List<ExtSouTechScoreHead> headList = techScoreHeadService.list(query);

        List<ExtSouTechScoreHeadDto> headDtoList = JSON.parseArray(JSON.toJSONString(headList), ExtSouTechScoreHeadDto.class);
        //查询招标工作组
        if (CollectionUtils.isNotEmpty(headDtoList)) {

            Map<Long, ExtSouGroup> groupMap = groupList.stream().collect(Collectors.toMap(s -> s.getGroupId(), Function.identity()));
            headDtoList.stream().forEach(h -> {
                ExtSouGroup group = groupMap.getOrDefault(h.getGroupId(), new ExtSouGroup());
                h.setExtExpertLevel(group.getExtExpertLevel());
                h.setGroupRole(group.getGroupRole());
                h.setUserId(group.getUserId());
                h.setUserName(group.getUserName());
                h.setFullName(group.getFullName());
                h.setPhone(group.getPhone());
            });
        }
        return headDtoList;
    }

    @Override
    public ApiExtSouBusManageQueryRespDto getBusOrderMangeInfo(Long projectId) {
        ApiExtSouBusManageQueryRespDto respDto = new ApiExtSouBusManageQueryRespDto();
        respDto.setOrderList(this.getSouOrder(projectId));
        respDto.setTalkFileList(this.getTalkFile(projectId));
        respDto.setOpenUserList(this.getSouOrderBusOpenUser(projectId));
        return respDto;
    }

    @Override
    public List<ExtSouOrderDto> getSouOrder(Long projectId) {

        List<ExtSouOrder> souOrderList = extNpmSouOrderService.queryNewestBusSubmissionOrder(projectId);

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        Map<Long, ExtSouVendor> vendorMap = vendorService.list(vendorQuery).stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2) -> k2));

        //查询轮次表
        LambdaQueryWrapper<ExtSouRound> roundQuery = new LambdaQueryWrapper<>();
        roundQuery.eq(ExtSouRound::getProjectId, projectId);
        Map<Integer, ExtSouRound> roundMap = souRoundService.list(roundQuery).stream().collect(Collectors.toMap(r -> r.getRound(), Function.identity(), (k1, k2) -> k2));

        ApiExtComparePriceRespDto respDto = this.getComparePrice(projectId);

        List<ApiExtComparePriceDto> comparePriceList = respDto.getComparePriceList();
        Map<Long,BigDecimal> taxAmountMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(comparePriceList)){
            ApiExtComparePriceDto priceDto = comparePriceList.get(0);
            List<ApiExtCompareVendorPriceDto> priceList = priceDto.getPriceList();
            if(CollectionUtils.isNotEmpty(priceList)){
                taxAmountMap = priceList.stream().collect(Collectors.toMap(ApiExtCompareVendorPriceDto::getVendorId,ApiExtCompareVendorPriceDto::getExtTotalProvPriceSumTax));
            }
        }

        List<ExtSouOrderDto> souOrderDtoList = JSON.parseArray(JSON.toJSONString(souOrderList), ExtSouOrderDto.class);

        for(ExtSouOrderDto o:souOrderDtoList){
            ExtSouVendor vendor = vendorMap.getOrDefault(o.getVendorId(), new ExtSouVendor());
            o.setVendorCode(vendor.getVendorCode());
            o.setVendorName(vendor.getVendorName());
            o.setExtTaxAmount(taxAmountMap.get(o.getVendorId()));
            ExtSouRound round = roundMap.getOrDefault(o.getRound(), new ExtSouRound());
            o.setExtOrderReason(round.getExtOrderReason());
        }

        return souOrderDtoList;
    }

    @Override
    public List<ExtNpmSouOpenBidRecordDto> getSouOrderBusOpenUser(Long projectId) {
        ExtSouProject project = iExtSouProjectService.getById(projectId);
        if(!Objects.isNull(project)) {
            return openBidRecordService.queryBusOpenRecord(projectId, project.getCurrentRound());
        }
        return new ArrayList<>();
    }

    @Override
    public List<ExtSouFile> getTalkFile(Long projectId) {
        LambdaQueryWrapper<ExtSouFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouFile::getProjectId, projectId);
        queryWrapper.in(ExtSouFile::getFileType, Arrays.asList(SouBidAttachmentTypeEnum.TALK.getCode()));
        queryWrapper.orderByAsc(ExtSouFile::getSouFileId);
        return souFileMapper.selectList(queryWrapper);
    }

    @Override
    public ApiExtComparePriceRespDto getComparePrice(Long projectId) {
        //查询报价信息
        LambdaQueryWrapper<ExtSouItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(ExtSouItem::getProjectId, projectId);
        itemQuery.orderByAsc(ExtSouItem::getExtPackageName, ExtSouItem::getSortIndex);

        List<ExtSouItem> itemList = itemMapper.selectList(itemQuery);

        AssertUtils.isTrue(CollectionUtils.isNotEmpty(itemList), "报价信息不存在！");

        //查询供应商表
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        vendorQuery.orderByAsc(ExtSouVendor::getVendorId);
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);

        //查询已投标的投标单据 SouOrderStatusEnum
        List<ExtSouOrder> orderList = extNpmSouOrderService.queryNewestBusSubmissionOrder(projectId);

        if (CollectionUtils.isEmpty(orderList)) {
            return new ApiExtComparePriceRespDto();
        }

        List<Long> filterVendorIdList = orderList.stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList());
        vendorList = vendorList.stream().filter(v -> filterVendorIdList.contains(v.getVendorId())).collect(Collectors.toList());

        //查询报价模板
        ApiExtSouPriceTemplateDto templateDto = bidSouInitQueryWebService.listPriceTemplate(projectId);
        //取含税报价标识
        AtomicBoolean extPriceTaxFlag = new AtomicBoolean(templateDto.getSelectedList().stream().anyMatch(f -> "extPriceTax".equals(f.getColumnCode())));
        //报价模板是否包含数量/工程量
        AtomicBoolean extQuantityFlag = new AtomicBoolean(templateDto.getSelectedList().stream().anyMatch(f -> ExtSouBidComponent.fieldName(ExtSouItem::getExtQuantity).equals(f.getColumnCode())));
        //查询报价明细
        LambdaQueryWrapper<ExtSouOrderItem> orderItemQuery = new LambdaQueryWrapper<>();
        orderItemQuery.in(ExtSouOrderItem::getOrderId, orderList.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toList()));

        Map<Long, ExtSouOrder> orderMap = orderList.stream().collect(Collectors.toMap(o->o.getOrderId(), Function.identity(), (k1, k2)->k2));

        List<ExtSouOrderItem> souOrderItemList = orderItemService.list(orderItemQuery);

        //取供应商最新轮次的数据
        souOrderItemList = souOrderItemList.stream().filter(orderItem -> {
            return Integer.compare(ObjectUtils.defaultIfNull(orderMap.getOrDefault(orderItem.getOrderId(), new ExtSouOrder()).getRound(), 1), ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0;
        }).collect(Collectors.toList());

        Map<String, ExtSouOrderItem> orderItemMap = souOrderItemList.stream().collect(Collectors.toMap(s -> StringUtils.joinWith("_", s.getVendorId(), s.getSouItemId()), Function.identity(), (k1, k2) -> k2));

        ApiExtComparePriceRespDto respDto = new ApiExtComparePriceRespDto();

        List<ApiExtComparePriceDto> comparePriceList = new ArrayList<>();

        //暂定未税总价
        Map<String, BigDecimal> extProvPriceSumNoTaxMap = new HashMap<>(50);
        //暂定含税总价
        Map<String, BigDecimal> extProvPriceSumTaxMap = new HashMap<>(50);

        //暂定未税总价-按包名小计
        Map<String, BigDecimal> extPacknameProvPriceSumNoTaxMap = new HashMap<>(50);
        //暂定含税总价-按包名小计
        Map<String, BigDecimal> extPacknameProvPriceSumTaxMap = new HashMap<>(50);

        //构造数据
        extracted(itemList, vendorList, extPriceTaxFlag, extQuantityFlag, orderItemMap, comparePriceList, extProvPriceSumNoTaxMap, extProvPriceSumTaxMap, extPacknameProvPriceSumNoTaxMap, extPacknameProvPriceSumTaxMap, filterVendorIdList);

        List<String> extInvoiceTypeList = new ArrayList<>();

        List<BigDecimal> extProvPriceSumNoTaxList = new ArrayList<>();

        List<BigDecimal> extProvPriceSumTaxList = new ArrayList<>();

        Map<Long, ApiExtCompareVendorPriceDto> priceMap = new HashMap<>(50);

        //未税暂定总价-按包名小计 转列表
        Map<String, List<BigDecimal>> extPacknameProvPriceSumNoTaxMapToList = new HashMap<>(50);
        //含税暂定总价-按包名小计
        Map<String, List<BigDecimal>> extPacknameProvPriceSumTaxMapToList = new HashMap<>(50);

        //设置值
        AtomicBoolean firstLine = new AtomicBoolean(true);
        Map<String, Boolean> packFirstLine = new HashMap<>(50);
        comparePriceList.forEach(comparePrice -> {
            boolean isFirstLine = firstLine.getAndSet(false);
            boolean isPackFirstLine = packFirstLine.getOrDefault(comparePrice.getExtPackageName(), true);
            packFirstLine.put(comparePrice.getExtPackageName(), false);
            comparePrice.getPriceList().forEach(price -> {
                price.setExtTotalProvPriceSumNoTax(extProvPriceSumNoTaxMap.get(StringUtils.joinWith("_", price.getVendorId())));
                price.setExtTotalProvPriceSumTax(extProvPriceSumTaxMap.get(StringUtils.joinWith("_", price.getVendorId())));
                price.setExtPacknameProvPriceSumNoTax(extPacknameProvPriceSumNoTaxMap.get(StringUtils.joinWith("_", price.getVendorId(), comparePrice.getExtPackageName())));
                price.setExtPacknameProvPriceSumTax(extPacknameProvPriceSumTaxMap.get(StringUtils.joinWith("_", price.getVendorId(), comparePrice.getExtPackageName())));


                if (isFirstLine) {
                    extInvoiceTypeList.add(price.getExtInvoiceType());
                    extProvPriceSumNoTaxList.add(price.getExtTotalProvPriceSumNoTax());
                    extProvPriceSumTaxList.add(price.getExtTotalProvPriceSumTax());
                    priceMap.put(price.getVendorId(), price);
                }

                if (isPackFirstLine) {
                    //按包小计
                    String packnameKey = StringUtils.joinWith("_", price.getVendorId(), comparePrice.getExtPackageName());

                    if (!extPacknameProvPriceSumNoTaxMapToList.containsKey(comparePrice.getExtPackageName())) {
                        extPacknameProvPriceSumNoTaxMapToList.put(comparePrice.getExtPackageName(), new ArrayList<>());
                    }
                    extPacknameProvPriceSumNoTaxMapToList.get(comparePrice.getExtPackageName()).add(extPacknameProvPriceSumNoTaxMap.get(packnameKey));

                    if (!extPacknameProvPriceSumTaxMapToList.containsKey(comparePrice.getExtPackageName())) {
                        extPacknameProvPriceSumTaxMapToList.put(comparePrice.getExtPackageName(), new ArrayList<>());
                    }
                    extPacknameProvPriceSumTaxMapToList.get(comparePrice.getExtPackageName()).add(extPacknameProvPriceSumTaxMap.get(packnameKey));

                }

            });
        });

        respDto.setComparePriceList(comparePriceList);
        respDto.setExtInvoiceTypeList(extInvoiceTypeList);
        respDto.setExtProvPriceSumNoTaxList(extProvPriceSumNoTaxList);
        respDto.setExtProvPriceSumTaxList(extProvPriceSumTaxList);
        respDto.setExtPacknameProvPriceSumNoTaxMap(extPacknameProvPriceSumNoTaxMapToList);
        respDto.setExtPacknameProvPriceSumTaxMap(extPacknameProvPriceSumTaxMapToList);
        respDto.setPriceMap(priceMap);
        respDto.setMergeFlag(this.isMergeApplitionNum(projectId));
        return respDto;
    }

    /**
     * 构造数据
     * @param itemList 参数
     * @param vendorList 参数
     * @param extPriceTaxFlag 参数
     * @param extQuantityFlag 参数
     * @param orderItemMap 参数
     * @param comparePriceList 参数
     * @param extProvPriceSumNoTaxMap 参数
     * @param extProvPriceSumTaxMap 参数
     * @param extPacknameProvPriceSumNoTaxMap 参数
     * @param extPacknameProvPriceSumTaxMap 参数
     * @param filterVendorIdList 参数
     */
    private void extracted(List<ExtSouItem> itemList, List<ExtSouVendor> vendorList, AtomicBoolean extPriceTaxFlag,AtomicBoolean extQuantityFlag, Map<String, ExtSouOrderItem> orderItemMap, List<ApiExtComparePriceDto> comparePriceList, Map<String, BigDecimal> extProvPriceSumNoTaxMap, Map<String, BigDecimal> extProvPriceSumTaxMap, Map<String, BigDecimal> extPacknameProvPriceSumNoTaxMap, Map<String, BigDecimal> extPacknameProvPriceSumTaxMap, List<Long> filterVendorIdList) {
        itemList.stream().forEach(item -> {
            ApiExtComparePriceDto comparePriceDto = new ApiExtComparePriceDto();
            BeanCopyUtil.copyProperties(comparePriceDto, item);
            //构造供应商数据
            List<ApiExtCompareVendorPriceDto> priceList = new ArrayList<>(vendorList.size());
            vendorList.stream().forEach(vendor -> {
                //筛选供应商
                if(!filterVendorIdList.contains(vendor.getVendorId())) {
                    return;
                }

                String key = StringUtils.joinWith("_", vendor.getVendorId(), item.getSouItemId());
                ExtSouOrderItem orderItem = orderItemMap.getOrDefault(key, new ApiExtSouOrderItemDto());

                ApiExtSouOrderItemDto orderItemDto = new ApiExtSouOrderItemDto();
                BeanCopyUtil.copyProperties(orderItemDto, orderItem);
                //将报价信息转换成报价信息表的字段
                orderItemDto.coverItemFields();
                //按汇率转换成CNY的价格
                orderItemDto.convertExchangeRateAsItemFields();
                //特殊处理，将报价信息填充到含税报价和固定含税单价
                if (extPriceTaxFlag.get()) {
                    orderItemDto.setExtFixedPriceNoTax(orderItemDto.getExtPriceNoTax());
                    orderItemDto.setExtFixedPriceTax(orderItemDto.getExtPriceTax());
                } else {
                    orderItemDto.setExtPriceNoTax(orderItemDto.getExtFixedPriceNoTax());
                    orderItemDto.setExtPriceTax(orderItemDto.getExtFixedPriceTax());
                }
                /** 特殊处理，如果模板不包含数量/工程数量，那么就用暂定数量/工程数量来代替 */
                if(!extQuantityFlag.get() && ObjectUtils.allNotNull(item.getRequireQuantity())) {
                    comparePriceDto.setExtQuantity(item.getRequireQuantity());
                }

                //计算总价
                orderItemDto.setExtProvPriceSumNoTax(multiplyPrice(comparePriceDto.getExtQuantity(), orderItemDto.getExtFixedPriceNoTax()));
                orderItemDto.setExtProvPriceSumTax(multiplyPrice(comparePriceDto.getExtQuantity(), orderItemDto.getExtFixedPriceTax()));
                orderItemDto.setExtPriceSumNoTax(multiplyPrice(comparePriceDto.getExtQuantity(), orderItemDto.getExtPriceNoTax()));
                orderItemDto.setExtPriceSumTax(multiplyPrice(comparePriceDto.getExtQuantity(), orderItemDto.getExtPriceTax()));

                ApiExtCompareVendorPriceDto vendorPriceDto = new ApiExtCompareVendorPriceDto();
                BeanCopyUtil.copyProperties(vendorPriceDto, orderItemDto);
                vendorPriceDto.setVendorId(vendor.getVendorId());
                vendorPriceDto.setVendorName(vendor.getVendorName());

                //累加未税总价--特殊处理：若报价行为增值税普通发票或者商业发票，则计算未税合计时，选择该发票类型的未税用含税计算
                Boolean addTaxFlag = Arrays.asList(SouBidpriceInvoiceTypeEnum.VAT_ORDINARY.getCode(), SouBidpriceInvoiceTypeEnum.OFFICIAL.getCode()).contains(ObjectUtils.defaultIfNull(orderItemDto.getExtInvoiceType(), "")) ? true : false;
                BigDecimal addSumNoTax = orderItemDto.getExtPriceSumNoTax();
                if(addTaxFlag) {
                    addSumNoTax = orderItemDto.getExtPriceSumTax();
                }
                addPrice(extProvPriceSumNoTaxMap, StringUtils.joinWith("_", vendor.getVendorId()), addSumNoTax);
                //累加含税总价
                addPrice(extProvPriceSumTaxMap, StringUtils.joinWith("_", vendor.getVendorId()), orderItemDto.getExtPriceSumTax());
                //累加未税总价-按包名小计--特殊处理：若报价行为增值税普通发票或者商业发票，则计算未税合计时，选择该发票类型的未税用含税计算
                addPrice(extPacknameProvPriceSumNoTaxMap, StringUtils.joinWith("_", vendor.getVendorId(), comparePriceDto.getExtPackageName()), addSumNoTax);
                //累加含税单价-按包名小计
                addPrice(extPacknameProvPriceSumTaxMap, StringUtils.joinWith("_", vendor.getVendorId(), comparePriceDto.getExtPackageName()), orderItemDto.getExtPriceSumTax());

                priceList.add(vendorPriceDto);

            });

            comparePriceDto.setPriceList(priceList);
            comparePriceList.add(comparePriceDto);
        });
    }

    private BigDecimal multiplyPrice(BigDecimal amount, BigDecimal price) {
        if (ObjectUtils.anyNull(amount, price)) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(price);
    }

    private void addPrice(Map<String, BigDecimal> map, String vendorId, BigDecimal price) {
        if (!map.containsKey(vendorId)) {
            map.put(vendorId, BigDecimal.ZERO);
        }
        if (Objects.isNull(price)) {
            return;
        }
        map.put(vendorId, map.get(vendorId).add(price));
    }

    @Override
    public List<ApiExtSouOrderItemDto> getOrderItem(ApiExtSouOrderItemQueryDto query, String souType) {


        ExtSouProject souProject = projectMapper.selectById(query.getProjectId());
        AssertUtils.notNull(souProject, "单据信息不存在");

        //商务报价中、商务报价已截止不允许查看
        if(Arrays.asList(SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode()).contains(souProject.getProjectStatus())) {
            return new ArrayList<>();
        }

        //报价信息
        LambdaQueryWrapper<ExtSouOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrderItem::getProjectId, query.getProjectId());
        queryWrapper.eq(!Objects.isNull(query.getRound()), ExtSouOrderItem::getRound, query.getRound());
        queryWrapper.eq(!Objects.isNull(query.getVendorId()), ExtSouOrderItem::getVendorId, query.getVendorId());
        queryWrapper.orderByDesc(ExtSouOrderItem::getRound, ExtSouOrderItem::getCreationDate);
        List<ExtSouOrderItem> orderItemList = orderItemService.list(queryWrapper);

        //查询报价字段
        LambdaQueryWrapper<ExtSouItem> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(ExtSouItem::getProjectId, query.getProjectId());
        List<ExtSouItem> souItemList = itemMapper.selectList(itemQueryWrapper);

        Map<Long, ExtSouItem> souItemMap = souItemList.stream().collect(Collectors.toMap(ExtSouItem::getSouItemId, Function.identity()));

        //查询供应商名称
        Map<Long, ExtSouVendor> vendorMap = new HashMap<>(50);
        if (CollectionUtils.isNotEmpty(orderItemList)) {
            LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
            vendorQuery.eq(ExtSouVendor::getProjectId, query.getProjectId());
            vendorQuery.in(ExtSouVendor::getVendorId, orderItemList.stream().map(ExtSouOrderItem::getVendorId).distinct().collect(Collectors.toList()));
            vendorMap = vendorService.list(vendorQuery).stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2) -> k2));
        }

        /** 组织报价原因 */
        List<ExtSouRound> roundList = souRoundService.lambdaQuery().eq(ExtSouRound::getProjectId, query.getProjectId()).list();
        Map<Integer, ExtSouRound> roundMap = roundList.stream().collect(Collectors.toMap(ExtSouRound::getRound, Function.identity(), (k1, k2)->k2));

        List<ApiExtSouOrderItemDto> orderItemDtoList = JSON.parseArray(JSON.toJSONString(orderItemList), ApiExtSouOrderItemDto.class);
        Map<Long, ExtSouVendor> finalVendorMap = vendorMap;
        orderItemDtoList.stream().forEach(itemDto -> {
            try {
                BeanCopyUtil.copyProperties(itemDto, souItemMap.getOrDefault(itemDto.getSouItemId(), new ExtSouItem()), true);
                itemDto.setExtOrgBuId(souProject.getExtOrgBuId());
                itemDto.setExtOrgBuCode(souProject.getExtOrgBuCode());
                itemDto.setExtOrgBuName(souProject.getExtOrgBuName());
                itemDto.setExtOrgOuId(souProject.getExtOrgOuId());
                itemDto.setExtOrgOuCode(souProject.getExtOrgOuCode());
                itemDto.setExtOrgOuName(souProject.getExtOrgOuName());

                ExtSouVendor vendor = finalVendorMap.getOrDefault(itemDto.getVendorId(), new ExtSouVendor());
                itemDto.setVendorCode(vendor.getVendorCode());
                itemDto.setVendorName(vendor.getVendorName());
                /** 设置组织报价原因 */
                itemDto.setExtOrderReason(roundMap.getOrDefault(itemDto.getRound(), new ExtSouRound()).getExtOrderReason());
            } catch (Exception e) {
                log.error("getOrderItem Exception", e);
            }
            itemDto.coverItemFields();
        });

        //查询后置处理
        orderItemDtoList = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemQueryHandler.class).doHandlerAfterOrderItem(query, souType, orderItemDtoList);
        return orderItemDtoList;
    }

    @Override
    public List<ExtSouOrderFile> getBusOrderFile(Long orderId) {
        ExtSouOrder souOrder = orderService.getById(orderId);
        AssertUtils.notNull(souOrder, "报价单不存在");

        //查询招标单据
        ExtSouProject project = iExtSouProjectService.getById(souOrder.getProjectId());

        //商务报价中、商务报价已截止不允许查看
        if(Arrays.asList(SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode()).contains(project.getProjectStatus())) {
            return new ArrayList<>();
        }

        return orderFileService.getBusOrderFile(souOrder.getProjectId(), souOrder.getOrderId());
    }

    @Override
    public List<CaSelectionResultDTO> queryCaResult(Long projectId) {

        List<CaDTO> caDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.Ca.getCode())
                .eq(CaDTO::getProjectId, projectId).eq(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode()).eq(CaDTO::getType, CaTypeEnum.APPLY.getCode()).orderByDesc(CaDTO::getCreationDate), CaDTO.class);

        if (CollectionUtils.isNotEmpty(caDTOList)) {
            List<CaSelectionResultDTO> caSelectionResultDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                    .in(CaSelectionResultDTO::getCaId, caDTOList.stream().map(CaDTO::getCaId).collect(Collectors.toList())), CaSelectionResultDTO.class);
            Map<Long, List<CaSelectionResultDTO>> catResultGroup = caSelectionResultDTOList.stream().collect(Collectors.groupingBy(CaSelectionResultDTO::getCaId));
            caDTOList.stream().forEach(caDTO -> {
                caDTO.setCaSelectionResults(catResultGroup.getOrDefault(caDTO.getCaId(), new ArrayList<>()));
            });

            return caDTOList.get(0).getCaSelectionResults();
        }


        return new ArrayList<>();
    }

    @Override
    public List<ApiExtSouOrderItemDto> getEditSouResult(Long projectId, Long vendorId) {
        //查询最新轮次
        LambdaQueryWrapper<ExtSouRound> roundQuery = new LambdaQueryWrapper<>();
        roundQuery.eq(ExtSouRound::getProjectId, projectId);
        roundQuery.orderByDesc(ExtSouRound::getRound);
        PageUtil.startPage(1, 1);
        List<ExtSouRound> roundList = souRoundService.list(roundQuery);

        if (CollectionUtils.isEmpty(roundList)) {
            return new ArrayList<>();
        }

        ExtSouRound round = roundList.get(0);

        //查询投标信息
        LambdaQueryWrapper<ExtSouOrder> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(ExtSouOrder::getProjectId, projectId);
        orderQuery.eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());
        orderQuery.eq(ExtSouOrder::getRound, round.getRound());
        orderQuery.eq(!Objects.isNull(vendorId), ExtSouOrder::getVendorId, vendorId);

        List<ExtSouOrder> orderList = orderService.list(orderQuery);
        if (CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        }

        //查询投标明细
        LambdaQueryWrapper<ExtSouOrderItem> orderItemQuery = new LambdaQueryWrapper<>();
        orderItemQuery.eq(ExtSouOrderItem::getProjectId, projectId);
        orderItemQuery.in(ExtSouOrderItem::getOrderId, orderList.stream().map(o -> o.getOrderId()).collect(Collectors.toList()));
        orderItemQuery.orderByDesc(ExtSouOrderItem::getVendorId, ExtSouOrderItem::getSouItemId);
        List<ExtSouOrderItem> orderItemList = orderItemService.list(orderItemQuery);
        if (CollectionUtils.isEmpty(orderItemList)) {
            return new ArrayList<>();
        }

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        vendorQuery.in(ExtSouVendor::getVendorId, orderList.stream().map(ExtSouOrder::getVendorId).distinct().collect(Collectors.toList()));
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2) -> k2));

        //查询报价信息
        LambdaQueryWrapper<ExtSouItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(ExtSouItem::getProjectId, projectId);
        itemQuery.in(ExtSouItem::getSouItemId, orderItemList.stream().map(ExtSouOrderItem::getSouItemId).distinct().collect(Collectors.toList()));
        List<ExtSouItem> itemList = itemMapper.selectList(itemQuery);
        Map<Long, ExtSouItem> itemMap = itemList.stream().collect(Collectors.toMap(i -> i.getSouItemId(), Function.identity(), (k1, k2) -> k2));

        List<ApiExtSouOrderItemDto> itemDtoList = JSON.parseArray(JSON.toJSONString(orderItemList), ApiExtSouOrderItemDto.class);

        itemDtoList.stream().forEach(itemDto -> {
            ExtSouVendor vendor = vendorMap.getOrDefault(itemDto.getVendorId(), new ExtSouVendor());
            itemDto.setVendorCode(vendor.getVendorCode());
            itemDto.setVendorName(vendor.getVendorName());

            ExtSouItem item = itemMap.getOrDefault(itemDto.getSouItemId(), new ExtSouItem());
            itemDto.setExtBidSection(item.getExtBidSection());
            itemDto.setExtQuantity(item.getExtQuantity());
            itemDto.setUnit(item.getUnit());
            itemDto.setExtCurrency(item.getExtCurrency());

            itemDto.coverItemFields();
            if (Objects.isNull(itemDto.getExtPriceNoTax())) {
                itemDto.setExtPriceOrFixedNoTax(itemDto.getExtFixedPriceNoTax());
            } else {
                itemDto.setExtPriceOrFixedNoTax(itemDto.getExtPriceNoTax());
            }
            if (SouWinStatusEnum.D.equals(itemDto.getWinStatus())) {
                itemDto.setWinStatus(SouWinStatusEnum.N);
            }
        });

        return itemDtoList;
    }

    @Override
    public List<ExtSouVendor> getEditSouResultVendor(Long projectId) {
        //查询最新轮次
        LambdaQueryWrapper<ExtSouRound> roundQuery = new LambdaQueryWrapper<>();
        roundQuery.eq(ExtSouRound::getProjectId, projectId);
        roundQuery.orderByDesc(ExtSouRound::getRound);
        PageUtil.startPage(1, 1);
        List<ExtSouRound> roundList = souRoundService.list(roundQuery);

        if (CollectionUtils.isEmpty(roundList)) {
            return new ArrayList<>();
        }

        ExtSouRound round = roundList.get(0);

        //查询投标信息
        LambdaQueryWrapper<ExtSouOrder> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(ExtSouOrder::getProjectId, projectId);
        orderQuery.eq(ExtSouOrder::getRound, round.getRound());
        orderQuery.eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());

        List<ExtSouOrder> orderList = orderService.list(orderQuery);
        if (CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        }
        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        vendorQuery.in(ExtSouVendor::getVendorId, orderList.stream().map(ExtSouOrder::getVendorId).distinct().collect(Collectors.toList()));
        vendorQuery.orderByAsc(ExtSouVendor::getVendorId);
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);

        return vendorList;
    }

    @Override
    public ApiExtSouOrderEditResultDto getEditSouResultInfo(ApiExtSouOrderQueryResultDto param, String souType) {
        ApiExtSouOrderEditResultDto resultDto = new ApiExtSouOrderEditResultDto();
        resultDto.setSelectionResultList(this.queryCaResult(param.getProjectId()));
        resultDto.setOrderItemResultList(this.getEditSouResult(param.getProjectId(), param.getVendorId()));
        resultDto.setVendorResultList(this.getEditSouResultVendor(param.getProjectId()));
        return resultDto;
    }

    @Override
    public ApiExtRoundDto getStartPrice(Long projectId) {

        ApiExtRoundDto apiExtRoundDto = new ApiExtRoundDto();

        //查询供应商报价单
        LambdaQueryWrapper<ExtSouOrder> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(ExtSouOrder::getProjectId, projectId);
        orderQuery.exists(MessageFormat.format("select 1 from ext_scc_sou_order ext where ext.order_id = scc_sou_order.order_id and ext.round = {0} and ext.order_status = ''{1}''", 1, SouOrderStatusEnum.SUBMISSION.name()));
        List<ExtSouOrder> orderList = orderService.list(orderQuery);

        //查询供应商
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        vendorQuery.in(CollectionUtils.isNotEmpty(orderList), ExtSouVendor::getVendorId, orderList.stream().map(o -> o.getVendorId()).distinct().collect(Collectors.toList()));
        vendorQuery.orderByAsc(ExtSouVendor::getVendorId);

        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);

        apiExtRoundDto.setVendorList(vendorList);

        //查询物料
        LambdaQueryWrapper<ExtSouItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(ExtSouItem::getProjectId, projectId);
        itemQuery.orderByAsc(ExtSouItem::getSouItemId);

        List<ExtSouItem> itemList = itemMapper.selectList(itemQuery);

        apiExtRoundDto.setItemList(itemList);


        return apiExtRoundDto;
    }

    @Override
    public ApiExtSouWinLossNoticeDto getWinLossNotice(Long projectId) {
        ExtSouProject souProject = projectMapper.selectById(projectId);
        AssertUtils.notNull(souProject, "项目信息为空");

        ApiExtSouWinLossNoticeDto noticeDto = new ApiExtSouWinLossNoticeDto();

        //查询中落表通知头表
        List<BidNoticeDTO> noticeDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode())
                        .eq(BidNoticeDTO::getProjectId, projectId)
                        .eq(BidNoticeDTO::getStatus, BidNoticeStatusEnum.APPROVED.getCode())
                        .eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode())
                        .orderByDesc(BidNoticeDTO::getCreationDate)
                , BidNoticeDTO.class);

        if (CollectionUtils.isEmpty(noticeDTOList)) {
            return noticeDto;
        }

        BidNoticeDTO bidNoticeDTO = noticeDTOList.get(0);

        //查看中/落标通知
        List<BidNoticeDetailDTO> noticeDetailDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
                        .eq(BidNoticeDetailDTO::getBidNoticeId, bidNoticeDTO.getBidNoticeId())
                        .orderByAsc(BidNoticeDetailDTO::getVendorId)
                , BidNoticeDetailDTO.class);

        //查看内部通知
        List<BidNoticeInternalDTO> noticeInternalDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeInternal.getCode())
                        .eq(BidNoticeInternalDTO::getBidNoticeId, bidNoticeDTO.getBidNoticeId())
                        .orderByAsc(BidNoticeInternalDTO::getInternalId)
                , BidNoticeInternalDTO.class);

        noticeDto.setNoticeDetailList(noticeDetailDTOList);
        noticeDto.setNoticeInternalList(noticeInternalDTOList);
        return noticeDto;
    }

    @Override
    public ApiExtSouArchiveFileEditDto getArchiveFile(Long projectId) {
        LambdaQueryWrapper<ExtSouFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouFile::getProjectId, projectId);
        queryWrapper.in(ExtSouFile::getFileType, Arrays.asList(SouBidAttachmentTypeEnum.ARCHIVE.getCode()));
        queryWrapper.orderByAsc(ExtSouFile::getSouFileId);
        List<ExtSouFile> souFileList = souFileMapper.selectList(queryWrapper);

        ApiExtSouArchiveFileEditDto editDto = new ApiExtSouArchiveFileEditDto();
        editDto.setProjectId(projectId);
        editDto.setArchiveFileList(souFileList);
        return editDto;
    }

    @Override
    public ApiExtSouTechManageQueryRespDto getTechManagement(Long projectId, String souType) {

        ExtSouProject souProject = projectMapper.selectById(projectId);
        AssertUtils.notNull(souProject, "项目信息不存在！");

        ApiExtSouTechManageQueryRespDto respDto = new ApiExtSouTechManageQueryRespDto();
        respDto.setExtExpertRange(souProject.getExtExpertRange());
        respDto.setExtAskSeniorExpertNum(souProject.getExtAskSeniorExpertNum());
        respDto.setExtBidEvaluatorNum(souProject.getExtBidEvaluatorNum());

        //评标小组
        respDto.setEvaGroupList(this.getEvaGroup(projectId));
        //专家组
        ApiExtSouExpertRandomExtractDto extSouExpertRandomExtractDto = new ApiExtSouExpertRandomExtractDto();
        extSouExpertRandomExtractDto.setProjectId(projectId);
        extSouExpertRandomExtractDto.setExtExpertRange(StringUtils.defaultIfBlank(souProject.getExtExpertRange(), SouExpertRangeEnum.GROUP.getCode()));
        List<ExtSouExpertRecord> expertList = extNpmSouExpertService.queryExpert(extSouExpertRandomExtractDto, souProject, null);
        if (CollectionUtils.isNotEmpty(expertList)) {
            Map<String, List<ExtSouExpertRecord>> group = expertList.stream().collect(Collectors.groupingBy(ExtSouExpertRecord::getExpertLevel));
            respDto.setExpertNum(group.getOrDefault(SouBidExpertLevelEnum.SENIOR.getCode(), new ArrayList<>()).size());
            respDto.setCommonNum(group.getOrDefault(SouBidExpertLevelEnum.NORMAL.getCode(), new ArrayList<>()).size());
        } else {
            respDto.setExpertNum(0);
            respDto.setCommonNum(0);
        }


        //投标进度跟踪
        respDto.setEvaTechScoreList(this.getTechScoreHead(projectId));

        //查看风险数量
        ApiExtSouTechManageQueryRespDto riskDto = this.getExtractRisk(projectId, souType);
        respDto.setExtractRiskNum(riskDto.getRiskNum());

        //技术标管理查询后续处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechManagementQueryHandler.class).doHandlerAfterGetTechManagement(projectId, souType, respDto);
        return respDto;
    }

    @Override
    public List<ExtSouExpertRecordDto> getExpertRecord(Long projectId, String souType) {
        List<ExtSouExpertRecord> recordList = expertRecordService.lambdaQuery()
                .eq(ExtSouExpertRecord::getProjectId, projectId).orderByDesc(ExtSouExpertRecord::getExtractTime).list();
        List<ExtSouExpertRecordDto> recordDtoList = JSON.parseArray(JSON.toJSONString(recordList), ExtSouExpertRecordDto.class);
        //返回前端
        recordDtoList.stream().forEach(r -> r.setExtRemoveReason(r.getRemoveReason()));
        return recordDtoList;
    }

    @Override
    public ApiExtSouTechManageQueryRespDto getExtractRisk(Long projectId, String souType) {
        ApiExtSouTechManageQueryRespDto respDto = new ApiExtSouTechManageQueryRespDto();

        //查询供应商
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId)
                .orderByAsc(ExtSouVendor::getVendorName).list();

        //查询招标工作组
        List<ExtSouGroup> groupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectId)
                .eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue()).list();

        if(CollectionUtils.isEmpty(groupList)) {
            return new ApiExtSouTechManageQueryRespDto();
        }

        Map<String, Object> param = new HashMap<>(50);
        param.put("userIdList", groupList.stream().map(ExtSouGroup::getUserId).distinct().collect(Collectors.toList()));
        List<ExtSouExpertRiskDto> expertRisks = extSouNpmExpertMapper.queryExpertRisk(param);


        List<ExtSouExpertRiskDto> expertRiskDtos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(expertRisks)) {
            for(ExtSouExpertRiskDto extSouExpertRiskDto : expertRisks) {
                if(StringUtils.isNotBlank(extSouExpertRiskDto.getVendorName())) {
                    //添加工作履历
                    expertRiskDtos.add(extSouExpertRiskDto);
                }
                if(StringUtils.isNotBlank(extSouExpertRiskDto.getRelationVendorName())) {
                    //添加亲友履历
                    ExtSouExpertRiskDto riskDto = new ExtSouExpertRiskDto();
                    BeanCopyUtil.copyProperties(riskDto, extSouExpertRiskDto);
                    riskDto.setVendorName(extSouExpertRiskDto.getRelationVendorName());
                    expertRiskDtos.add(riskDto);
                }
            }
        }

        //去重
        expertRiskDtos = expertRiskDtos.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StringUtils.joinWith("_", o.getUserName(), o.getVendorName())))), ArrayList::new)
        );

        List<ExtSouVendorDto> vendorDtoList = JSON.parseArray(JSON.toJSONString(vendorList), ExtSouVendorDto.class);

        List<String> vendorNameList = expertRiskDtos.stream().map(ExtSouExpertRiskDto::getVendorName).distinct().collect(Collectors.toList());
        Set<String> riskVendorNameSet = new HashSet<>();
        vendorDtoList.stream().forEach(v -> {
            if (vendorNameList.contains(v.getVendorName())) {
                v.setRiskFlag(YesOrNo.YES.name());
                riskVendorNameSet.add(v.getVendorName());
            }
        });

        AtomicReference<Integer> count = new AtomicReference<>(0);
        expertRiskDtos.stream().forEach(v -> {
            v.setProjectId(projectId);
            v.setExpertRiskId(IdGenrator.generate());
            if(riskVendorNameSet.contains(v.getVendorName())) {
                v.setRiskFlag(YesOrNo.YES.getValue());
                count.getAndSet(count.get() + 1);
            }
        });

        //投标供应商
        respDto.setTenderVendorList(vendorDtoList);
        //专家及亲友履历
        respDto.setExpertRiskList(expertRiskDtos);

        //风险数量
        respDto.setRiskNum(count.get());

        return respDto;
    }

    @Override
    public ApiExtSouOrderBusManagementDto getBusinessManagementOrderInfo(Long projectId, String souType) {
        ApiExtSouOrderBusManagementDto managementDto = new ApiExtSouOrderBusManagementDto();
        //查询轮次表
        LambdaQueryWrapper<ExtSouRound> roundQuery = new LambdaQueryWrapper<>();
        roundQuery.eq(ExtSouRound::getProjectId, projectId);
        roundQuery.gt(ExtSouRound::getRound, 0);
        roundQuery.orderByAsc(ExtSouRound::getRound);
        List<ExtSouRound> roundList = souRoundService.list(roundQuery);

        managementDto.setRoundList(roundList.stream().map(ExtSouRound::getRound).distinct().collect(Collectors.toList()));

        //供应商
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        vendorQuery.orderByAsc(ExtSouVendor::getVendorId);
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);

        managementDto.setVendorList(vendorList);

        //报价模板
        ApiExtSouPriceTemplateDto priceTemplateDto = bidSouInitQueryWebService.listPriceTemplate(projectId);
        managementDto.setSelectedList(priceTemplateDto.getSelectedList());

        return managementDto;
    }

    @Override
    public List<String> getProjectPackName(Long projectId, String souType) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, projectId);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        queryWrapper.orderByAsc(ExtSouDemand::getSortIndex);
        List<ExtSouDemand> demandList = demandService.list(queryWrapper);

        return demandList.stream().map(ExtSouDemand::getPackageName).distinct().collect(Collectors.toList());
    }

    @Override
    public List<ExtNpmSouAjustTime> listAjustTime(Long projectId, String souType) {
        return iExtNpmSouAjustTimeService.lambdaQuery().eq(ExtNpmSouAjustTime::getProjectId, projectId).orderByDesc(ExtNpmSouAjustTime::getAjustTimeId).list();
    }

    @Override
    public RecommvendorRiskDto getRecommvendorRiskDto(Long projectId) {
        String baseMsg = "{0}供应商 投标联系人 与{1}供应商{2}重复，重复名称为：{3}";
        String extraMsg = ",(推荐单/项目)为：{0}";

        //查询此项目下已投标供应商
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.eq(SouOrder::getProjectId, projectId);
        queryOrderWrapper.eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());
        List<ExtSouOrder> orderList = orderService.list(queryOrderWrapper);
        if(CollectionUtils.isEmpty(orderList)){
            return null;
        }
        Set<Long> vendorList = orderList.stream().map(ExtSouOrder::getVendorId).collect(Collectors.toSet());
        //初始化返回结果
        RiskRequest request = new RiskRequest();
        request.setSunshineCreditFlag(false);
        request.setVendorIdList(Arrays.asList(vendorList.toArray()));
        RecommvendorRiskDto recommvendorRiskDto = (RecommvendorRiskDto) VendorRiskFactory.getInstance().riskDataGenerator().todo(request).getData();

        //查询供应商常用联系人
        List<ContactInfoDto> vendorContactList = RiskComponent.getInstance().getQlOpenClient()
                .query(ContextPath.SUP, QlOpenWrappers.query(MqlType.CONTACTINFO).in(ContactInfoDto::getCompanyId, Arrays.asList(vendorList.toArray())), ContactInfoDto.class);

        riskNameFill(baseMsg, extraMsg, orderList, vendorList, recommvendorRiskDto, vendorContactList);
        riskPhoneFill(baseMsg, extraMsg, orderList, vendorList, recommvendorRiskDto, vendorContactList);
        riskEmailFill(baseMsg, extraMsg, orderList, vendorList, recommvendorRiskDto, vendorContactList);
        return recommvendorRiskDto;
    }

    private void riskNameFill(String baseMsg, String extraMsg, List<ExtSouOrder> orderList, Set<Long> vendorList, RecommvendorRiskDto recommvendorRiskDto, List<ContactInfoDto> vendorContactList) {
        //查询供应商历史投标信息
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.in(SouOrder::getVendorId, vendorList);
        queryOrderWrapper.in(ExtSouOrder::getExtOrderType, Arrays.asList(ExtOrderTypeEnum.BUS.getCode(),ExtOrderTypeEnum.TECH.getCode()));
        queryOrderWrapper.in(ExtSouOrder::getExtTenderName, orderList.stream().map(ExtSouOrder::getExtTenderName).filter(StringUtil::notEmpty).collect(Collectors.toSet()));
        queryOrderWrapper.eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());

        List<ExtSouOrder> orderHisList = orderService.list(queryOrderWrapper);
        Map<Long, Set<String>> orderHisNameMap = orderHisList.stream()
                .collect(Collectors.groupingBy(ExtSouOrder::getVendorId, Collectors.mapping(ExtSouOrder::getExtTenderName, Collectors.toSet())));

        //查询供应商历史报名信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("v.vendor_id", vendorList);
        queryWrapper.in("v.linkman_name", orderList.stream().map(ExtSouOrder::getExtTenderName).filter(StringUtil::notEmpty).collect(Collectors.toSet()));
        List<ExtSouVendor> vendorHisList = extSouVendorMapper.listVendor(queryWrapper);
        Map<Long, Set<String>> vendorHisNameMap = vendorHisList.stream()
                .collect(Collectors.groupingBy(ExtSouVendor::getVendorId, Collectors.mapping(ExtSouVendor::getLinkmanName, Collectors.toSet())));

        //添加联系人 到set
        vendorContactList.forEach(e-> orderHisNameMap.computeIfAbsent(e.getCompanyId(), k -> new HashSet<>()).add(e.getContactName()));
        //添加历史报名人员到set
        vendorHisNameMap.forEach((key, value)-> orderHisNameMap.computeIfAbsent(key, k -> new HashSet<>()).addAll(value));
        //获取重复数据
        Set<String> nameDiffSet = getDataDiffSet(orderHisNameMap);

        //本次投标人员
        Set<String> orderNameSet = orderList.stream()
                .map(ExtSouOrder::getExtTenderName)
                .filter(StringUtil::notEmpty)
                .collect(Collectors.toSet());
        //取交集 本次投标人员 包含重复人员
        Set<String> nameRiskSet = Sets.intersection(orderNameSet, nameDiffSet);
        //如果不包含重复人员 跳过
        if(CollectionUtils.isEmpty(nameRiskSet)){
            return;
        }
        //获取人员对应公司
        Map<String, Set<Long>> orderHisCompanyMap = orderHisList.stream()
                .filter(e -> StringUtil.notEmpty(e.getExtTenderName()))
                .collect(Collectors.groupingBy(ExtSouOrder::getExtTenderName, Collectors.mapping(ExtSouOrder::getVendorId, Collectors.toSet())));
        Map<String, Set<Long>> orderCompanyMap = orderList.stream()
                .filter(e -> StringUtil.notEmpty(e.getExtTenderName()))
                .collect(Collectors.groupingBy(ExtSouOrder::getExtTenderName, Collectors.mapping(ExtSouOrder::getVendorId, Collectors.toSet())));

        //遍历当前风险人员
        for (String name : nameRiskSet) {
            List<List<Long>> riskList = getDataRiskList(orderHisCompanyMap, orderCompanyMap, name);
            riskList.forEach(e->{
                riskNameFillExtracted(baseMsg, extraMsg, recommvendorRiskDto, vendorContactList, orderHisList, vendorHisList, name, e);
            });
        }
    }



    private void riskNameFillExtracted(String baseMsg, String extraMsg, RecommvendorRiskDto recommvendorRiskDto, List<ContactInfoDto> vendorContactList, List<ExtSouOrder> orderHisList, List<ExtSouVendor> vendorHisList, String name, List<Long> e) {
        recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> e.contains(item))
                .forEach(item -> item.setContackRepeatFlag(YesOrNo.YES.getValue()));

        //供应商A 与 B 重复
        String temA = recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> Objects.equals(item.getVendorId(), e.get(0)))
                .map(RecommvendorDto::getVendorName)
                .findFirst()
                .orElse("");
        String temB = recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> Objects.equals(item.getVendorId(), e.get(1)))
                .map(RecommvendorDto::getVendorName)
                .findFirst()
                .orElse("");
        //判断name 在 B中 类型
        //常用联系人
        boolean contractFlag = vendorContactList.stream()
                .anyMatch(o -> Objects.equals(o.getCompanyId(), e.get(1)) && Objects.equals(o.getContactName(), name));
        //投标联系人
        ExtSouOrder extSouOrder = orderHisList.stream().filter(o -> Objects.equals(o.getVendorId(), e.get(1)) && Objects.equals(o.getExtTenderName(), name))
                .findFirst()
                .orElse(null);
        //报名联系人
        ExtSouVendor extSouVendor = vendorHisList.stream().filter(o -> Objects.equals(o.getVendorId(), e.get(1)) && Objects.equals(o.getLinkmanName(), name))
                .findFirst()
                .orElse(null);
        //判断重复选项
        if(contractFlag){
            //重复类型
            recommvendorRiskDto.addRiskItem(RiskItemType.NAME,MessageFormat.format(baseMsg,temA,temB,"常用联系人", name));
        }
        if(Objects.nonNull(extSouOrder)){
            ExtSouProject extSouProject = projectMapper.selectById(extSouOrder.getProjectId());
            recommvendorRiskDto.addRiskItem(RiskItemType.NAME, MessageFormat.format(baseMsg,temA,temB,"历史投标联系人", name)
                    .concat(MessageFormat.format(extraMsg,extSouProject.getSouNo())));
        }
        if(Objects.nonNull(extSouVendor)){
            ExtSouProject extSouProject = projectMapper.selectById(extSouVendor.getProjectId());
            recommvendorRiskDto.addRiskItem(RiskItemType.NAME, MessageFormat.format(baseMsg,temA,temB,"历史报名联系人", name)
                    .concat(MessageFormat.format(extraMsg,extSouProject.getSouNo())));
        }
    }

    private void riskPhoneFill(String baseMsg, String extraMsg, List<ExtSouOrder> orderList, Set<Long> vendorList, RecommvendorRiskDto recommvendorRiskDto, List<ContactInfoDto> vendorContactList) {
        //查询供应商历史投标信息
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.in(SouOrder::getVendorId, vendorList);
        queryOrderWrapper.in(ExtSouOrder::getExtOrderType, Arrays.asList(ExtOrderTypeEnum.BUS.getCode(),ExtOrderTypeEnum.TECH.getCode()));
        queryOrderWrapper.in(ExtSouOrder::getExtTenderPhone, orderList.stream().map(ExtSouOrder::getExtTenderPhone).filter(StringUtil::notEmpty).collect(Collectors.toSet()));
        queryOrderWrapper.eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());

        List<ExtSouOrder> orderHisList = orderService.list(queryOrderWrapper);
        Map<Long, Set<String>> orderHisNameMap = orderHisList.stream()
                .collect(Collectors.groupingBy(ExtSouOrder::getVendorId, Collectors.mapping(ExtSouOrder::getExtTenderPhone, Collectors.toSet())));

        //查询供应商历史报名信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("v.vendor_id", vendorList);
        queryWrapper.in("v.phone", orderList.stream().map(ExtSouOrder::getExtTenderPhone).filter(StringUtil::notEmpty).collect(Collectors.toSet()));
        List<ExtSouVendor> vendorHisList = extSouVendorMapper.listVendor(queryWrapper);
        Map<Long, Set<String>> vendorHisNameMap = vendorHisList.stream()
                .collect(Collectors.groupingBy(ExtSouVendor::getVendorId, Collectors.mapping(ExtSouVendor::getPhone, Collectors.toSet())));

        //添加联系人 到set
        vendorContactList.forEach(e-> orderHisNameMap.computeIfAbsent(e.getCompanyId(), k -> new HashSet<>()).add(e.getPhoneNumber()));
        //添加历史报名人员到set
        vendorHisNameMap.forEach((key, value)-> orderHisNameMap.computeIfAbsent(key, k -> new HashSet<>()).addAll(value));
        //获取重复数据
        Set<String> nameDiffSet = getDataDiffSet(orderHisNameMap);

        //本次投标人员
        Set<String> orderNameSet = orderList.stream()
                .map(ExtSouOrder::getExtTenderPhone)
                .filter(StringUtil::notEmpty)
                .collect(Collectors.toSet());
        //取交集 本次投标人员 包含重复人员
        Set<String> nameRiskSet = Sets.intersection(orderNameSet, nameDiffSet);
        //如果不包含重复人员 跳过
        if(CollectionUtils.isEmpty(nameRiskSet)){
            return;
        }
        //获取人员对应公司
        Map<String, Set<Long>> orderHisCompanyMap = orderHisList.stream()
                .filter(e -> StringUtil.notEmpty(e.getExtTenderPhone()))
                .collect(Collectors.groupingBy(ExtSouOrder::getExtTenderPhone, Collectors.mapping(ExtSouOrder::getVendorId, Collectors.toSet())));
        Map<String, Set<Long>> orderCompanyMap = orderList.stream()
                .filter(e -> StringUtil.notEmpty(e.getExtTenderPhone()))
                .collect(Collectors.groupingBy(ExtSouOrder::getExtTenderPhone, Collectors.mapping(ExtSouOrder::getVendorId, Collectors.toSet())));

        //遍历当前风险人员
        for (String name : nameRiskSet) {
            //两两组合
            List<List<Long>> riskList = getDataRiskList(orderHisCompanyMap, orderCompanyMap, name);

            riskList.forEach(e->{
                riskPhoneFillExtracted(baseMsg, extraMsg, recommvendorRiskDto, vendorContactList, orderHisList, vendorHisList, name, e);
            });
        }
    }

    private void riskPhoneFillExtracted(String baseMsg, String extraMsg, RecommvendorRiskDto recommvendorRiskDto, List<ContactInfoDto> vendorContactList, List<ExtSouOrder> orderHisList, List<ExtSouVendor> vendorHisList, String name, List<Long> e) {
        //供应商A 与 B 重复
        recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> e.contains(item))
                .forEach(item -> item.setTelRepeatFlag(YesOrNo.YES.getValue()));

        String temA = recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> Objects.equals(item.getVendorId(), e.get(0)))
                .map(RecommvendorDto::getVendorName)
                .findFirst()
                .orElse("");
        String temB = recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> Objects.equals(item.getVendorId(), e.get(1)))
                .map(RecommvendorDto::getVendorName)
                .findFirst()
                .orElse("");
        //判断name 在 B中 类型
        //常用联系人
        boolean contractFlag = vendorContactList.stream()
                .anyMatch(o -> Objects.equals(o.getCompanyId(), e.get(1)) && Objects.equals(o.getPhoneNumber(), name));
        //投标联系人
        ExtSouOrder extSouOrder = orderHisList.stream().filter(o -> Objects.equals(o.getVendorId(), e.get(1)) && Objects.equals(o.getExtTenderPhone(), name))
                .findFirst()
                .orElse(null);
        //报名联系人
        ExtSouVendor extSouVendor = vendorHisList.stream().filter(o -> Objects.equals(o.getVendorId(), e.get(1)) && Objects.equals(o.getPhone(), name))
                .findFirst()
                .orElse(null);
        //判断重复选项
        if(contractFlag){
            //重复类型
            recommvendorRiskDto.addRiskItem(RiskItemType.TEL,MessageFormat.format(baseMsg,temA,temB,"常用联系人电话", name));
        }
        if(Objects.nonNull(extSouOrder)){
            ExtSouProject extSouProject = projectMapper.selectById(extSouOrder.getProjectId());
            recommvendorRiskDto.addRiskItem(RiskItemType.TEL, MessageFormat.format(baseMsg,temA,temB,"历史投标联系人电话", name)
                    .concat(MessageFormat.format(extraMsg,extSouProject.getSouNo())));
        }
        if(Objects.nonNull(extSouVendor)){
            ExtSouProject extSouProject = projectMapper.selectById(extSouVendor.getProjectId());
            recommvendorRiskDto.addRiskItem(RiskItemType.TEL, MessageFormat.format(baseMsg,temA,temB,"历史报名联系人电话", name)
                    .concat(MessageFormat.format(extraMsg,extSouProject.getSouNo())));
        }
    }

    private void riskEmailFill(String baseMsg, String extraMsg, List<ExtSouOrder> orderList, Set<Long> vendorList, RecommvendorRiskDto recommvendorRiskDto, List<ContactInfoDto> vendorContactList) {
        //查询供应商历史投标信息
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.in(SouOrder::getVendorId, vendorList);
        queryOrderWrapper.in(ExtSouOrder::getExtOrderType, Arrays.asList(ExtOrderTypeEnum.BUS.getCode(),ExtOrderTypeEnum.TECH.getCode()));
        queryOrderWrapper.in(ExtSouOrder::getExtTenderEmail, orderList.stream().map(ExtSouOrder::getExtTenderEmail).filter(StringUtil::notEmpty).collect(Collectors.toSet()));
        queryOrderWrapper.eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());

        List<ExtSouOrder> orderHisList = orderService.list(queryOrderWrapper);
        Map<Long, Set<String>> orderHisNameMap = orderHisList.stream()
                .collect(Collectors.groupingBy(ExtSouOrder::getVendorId, Collectors.mapping(ExtSouOrder::getExtTenderEmail, Collectors.toSet())));

        //查询供应商历史报名信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("v.vendor_id", vendorList);
        queryWrapper.in("v.email", orderList.stream().map(ExtSouOrder::getExtTenderEmail).filter(StringUtil::notEmpty).collect(Collectors.toSet()));
        List<ExtSouVendor> vendorHisList = extSouVendorMapper.listVendor(queryWrapper);
        Map<Long, Set<String>> vendorHisNameMap = vendorHisList.stream()
                .collect(Collectors.groupingBy(ExtSouVendor::getVendorId, Collectors.mapping(ExtSouVendor::getEmail, Collectors.toSet())));

        //添加联系人 到set
        vendorContactList.forEach(e-> orderHisNameMap.computeIfAbsent(e.getCompanyId(), k -> new HashSet<>()).add(e.getEmail()));
        //添加历史报名人员到set
        vendorHisNameMap.forEach((key, value)-> orderHisNameMap.computeIfAbsent(key, k -> new HashSet<>()).addAll(value));
        //获取重复数据
        Set<String> nameDiffSet = getDataDiffSet(orderHisNameMap);

        //本次投标人员
        Set<String> orderNameSet = orderList.stream()
                .map(ExtSouOrder::getExtTenderEmail)
                .filter(StringUtil::notEmpty)
                .collect(Collectors.toSet());
        //取交集 本次投标人员 包含重复人员
        Set<String> nameRiskSet = Sets.intersection(orderNameSet, nameDiffSet);
        //如果不包含重复人员 跳过
        if(CollectionUtils.isEmpty(nameRiskSet)){
            return;
        }
        //获取人员对应公司
        Map<String, Set<Long>> orderHisCompanyMap = orderHisList.stream()
                .filter(e -> StringUtil.notEmpty(e.getExtTenderEmail()))
                .collect(Collectors.groupingBy(ExtSouOrder::getExtTenderEmail, Collectors.mapping(ExtSouOrder::getVendorId, Collectors.toSet())));
        Map<String, Set<Long>> orderCompanyMap = orderList.stream()
                .filter(e -> StringUtil.notEmpty(e.getExtTenderEmail()))
                .collect(Collectors.groupingBy(ExtSouOrder::getExtTenderEmail, Collectors.mapping(ExtSouOrder::getVendorId, Collectors.toSet())));

        //遍历当前风险人员
        for (String name : nameRiskSet) {
            //两两组合
            List<List<Long>> riskList = getDataRiskList(orderHisCompanyMap, orderCompanyMap, name);

            riskList.forEach(e->{
                riskEmailFillExtracted(baseMsg, extraMsg, recommvendorRiskDto, vendorContactList, orderHisList, vendorHisList, name, e);
            });
        }
    }

    private void riskEmailFillExtracted(String baseMsg, String extraMsg, RecommvendorRiskDto recommvendorRiskDto, List<ContactInfoDto> vendorContactList, List<ExtSouOrder> orderHisList, List<ExtSouVendor> vendorHisList, String name, List<Long> e) {
        //供应商A 与 B 重复
        recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> e.contains(item))
                .forEach(item -> item.setEmailRepeatFlag(YesOrNo.YES.getValue()));

        String temA = recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> Objects.equals(item.getVendorId(), e.get(0)))
                .map(RecommvendorDto::getVendorName)
                .findFirst()
                .orElse("");
        String temB = recommvendorRiskDto.getVendorRiskList().stream()
                .filter(item-> Objects.equals(item.getVendorId(), e.get(1)))
                .map(RecommvendorDto::getVendorName)
                .findFirst()
                .orElse("");
        //判断name 在 B中 类型
        //常用联系人
        boolean contractFlag = vendorContactList.stream()
                .anyMatch(o -> Objects.equals(o.getCompanyId(), e.get(1)) && Objects.equals(o.getEmail(), name));
        //投标联系人
        ExtSouOrder extSouOrder = orderHisList.stream().filter(o -> Objects.equals(o.getVendorId(), e.get(1)) && Objects.equals(o.getExtTenderEmail(), name))
                .findFirst()
                .orElse(null);
        //报名联系人
        ExtSouVendor extSouVendor = vendorHisList.stream().filter(o -> Objects.equals(o.getVendorId(), e.get(1)) && Objects.equals(o.getEmail(), name))
                .findFirst()
                .orElse(null);
        //判断重复选项
        if(contractFlag){
            //重复类型
            recommvendorRiskDto.addRiskItem(RiskItemType.EMAIL,MessageFormat.format(baseMsg,temA,temB,"常用联系人邮箱", name));
        }
        if(Objects.nonNull(extSouOrder)){
            ExtSouProject extSouProject = projectMapper.selectById(extSouOrder.getProjectId());
            recommvendorRiskDto.addRiskItem(RiskItemType.EMAIL, MessageFormat.format(baseMsg,temA,temB,"历史投标联系人邮箱", name)
                    .concat(MessageFormat.format(extraMsg,extSouProject.getSouNo())));
        }
        if(Objects.nonNull(extSouVendor)){
            ExtSouProject extSouProject = projectMapper.selectById(extSouVendor.getProjectId());
            recommvendorRiskDto.addRiskItem(RiskItemType.EMAIL, MessageFormat.format(baseMsg,temA,temB,"历史报名联系人邮箱", name)
                    .concat(MessageFormat.format(extraMsg,extSouProject.getSouNo())));
        }
    }

    private Set<String> getDataDiffSet(Map<Long, Set<String>> orderHisDataMap) {
        //获取重复数据
        return orderHisDataMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashMultiset::create))
                .entrySet()
                .stream()
                .filter(e-> e.getCount() > 1)
                .map(e -> e.getElement())
                .collect(Collectors.toSet());
    }

    private List<List<Long>> getDataRiskList(Map<String, Set<Long>> orderHisCompanyMap, Map<String, Set<Long>> orderCompanyMap, String name) {
        //获取风险公司
        List<Long> companyHisList = orderHisCompanyMap.get(name).stream().collect(Collectors.toList());
        //两两组合
        return companyHisList.stream()
                .flatMap(a -> IntStream.range(0, companyHisList.size())
                        .filter(i -> i < companyHisList.indexOf(a))
                        .mapToObj(b ->
                                orderCompanyMap.get(name).contains(a) ? Arrays.asList(a, companyHisList.get(b)) : Arrays.asList(companyHisList.get(b), a)
                        )
                )
                .collect(Collectors.toList());
    }



    @Override
    public String checkSouMarginRecord(List<Long> idList) {
        StringBuilder sb = new StringBuilder();
        if(idList.isEmpty()) {
            return sb.toString();
        }
        LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouMargin::getMarginId, idList);
        queryWrapper.eq(ExtSouMargin::getYearFlag, YesOrNo.NO.getValue());
        List<ExtSouMargin> marginList = souMarginService.list(queryWrapper);
        if(marginList.isEmpty()) {
            return sb.toString();
        }
        Long projectId = marginList.get(0).getProjectId();
        Map<Long, List<BidNoticeDetailDTO>> noticeMap = querySouMarginRecordNotice(projectId);
        ExtSouProject extSouProject = projectMapper.selectById(projectId);

        for(ExtSouMargin param : marginList) {
            //退款的限制条款：1.保证金的退款，满足以下任一条件，都可退款
            boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false;
            if(noticeMap.containsKey(param.getVendorId())) {
                List<BidNoticeDetailDTO> noticeList = noticeMap.get(param.getVendorId());
                String isWin = noticeList.get(0).getIsWin();
                if(YesOrNo.NO.getValue().equals(isWin)) {
                    //①落标单位，发布落标通知后方可退款  发送后可以退款
                    flag1 = checkSouMarginRecordByNotice(noticeList);
                }
                if(YesOrNo.YES.getValue().equals(isWin)) {
                    //②中标单位，签订合同后方可退款, 合同关联字段：SOURCE_NUMBER 招标单号
                    flag2 = checkSouMarginRecordByContract(param, extSouProject);
                }
            }

            //④取消项目，项目取消后即可退款
            flag3 = checkSouMarginRecordByCancle(extSouProject);
            //③废标单位，通知废标后方可退款
            //⑤交钱后未投标的
            flag4 = checkSouMarginRecordByOrderStatus(param);
            boolean result = flag1 || flag2 || flag3 || flag4;
            if(!result) {
                sb.append(param.getVendorName()+"供应商不服符合退款条件;");
            }
        }
        return sb.toString();
    }

    /**
     * ②中标单位，签订合同后方可退款, 合同关联字段：SOURCE_NUMBER 招标单号
     * @param param
     * @return
     */
    private boolean checkSouMarginRecordByContract(ExtSouMargin param,ExtSouProject extSouProject){
        List<RecordDTO> heads = qlOpenClient.query(ContextPath.CM, QlOpenWrappers.query(ContractHead.class)
                //供应商的组织品类与寻源公示的组织品类需要一样
                .eq(ContractHead::getSourceNumber, extSouProject.getExtProjectNo())
                .eq(ContractHead::getVendorId, param.getVendorId()));
        return !heads.isEmpty();
    }

    /**
     * ③废标单位，通知废标后方可退款
     * ⑤交钱后未投标的
     * @param param
     * @return
     */
    private boolean checkSouMarginRecordByOrderStatus(ExtSouMargin param){
        //查询投标头表
        LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
        queryOrderWrapper.eq(SouOrder::getProjectId, param.getProjectId());
        queryOrderWrapper.eq(SouOrder::getVendorId, param.getVendorId());
        queryOrderWrapper.orderByDesc(SouOrder::getRound);
        List<ExtSouOrder> orderList = orderService.list(queryOrderWrapper);
        if(orderList.isEmpty()) {
            return true;
        }
        ExtSouOrder souOrder = orderList.get(0);
        if(SouOrderStatusEnum.CANCEL.equals(souOrder.getOrderStatus())
                || SouOrderStatusEnum.WITHDRAW.equals(souOrder.getOrderStatus())){
            return true;
        }
        return false;
    }
    /**
     * ④取消项目，项目取消后即可退款
     * @param extSouProject
     * @return
     */
    private boolean checkSouMarginRecordByCancle(ExtSouProject extSouProject){
        if(SouBiddingProStatusEnum.ABANDON.getCode().equals(extSouProject.getProjectStatus())) {
            return true;
        }
        return false;

    }

    /**
     * 落标单位，发布落标通知后方可退款  发送后可以退款
     * @param noticeList
     * @return
     */
    private boolean checkSouMarginRecordByNotice(List<BidNoticeDetailDTO> noticeList){
        String isSend = noticeList.get(0).getIsSend();
        //为空则不能退款，有数据则可以退
        return YesOrNo.YES.getValue().equals(isSend);
    }
    /**
     * 查询中落标信息
     * @param projectId
     * @return
     */
    private  Map<Long, List<BidNoticeDetailDTO>>  querySouMarginRecordNotice(Long projectId){
        //查询中落表通知头表
        List<BidNoticeDTO> noticeDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode())
                        .eq(BidNoticeDTO::getProjectId, projectId)
                        .eq(BidNoticeDTO::getStatus, BidNoticeStatusEnum.APPROVED.getCode())
                        .eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode())
                        .orderByDesc(BidNoticeDTO::getCreationDate)
                , BidNoticeDTO.class);

        if (CollectionUtils.isEmpty(noticeDTOList)) {
            return new HashMap<>();
        }

        BidNoticeDTO bidNoticeDTO = noticeDTOList.get(0);
        //查看中/落标通知
        List<BidNoticeDetailDTO> noticeDetailDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
                        .eq(BidNoticeDetailDTO::getBidNoticeId, bidNoticeDTO.getBidNoticeId())
                , BidNoticeDetailDTO.class);
        Map<Long, List<BidNoticeDetailDTO>> map = noticeDetailDTOList.stream().collect(Collectors.groupingBy(BidNoticeDetailDTO::getVendorId));
        return map;
    }
}
