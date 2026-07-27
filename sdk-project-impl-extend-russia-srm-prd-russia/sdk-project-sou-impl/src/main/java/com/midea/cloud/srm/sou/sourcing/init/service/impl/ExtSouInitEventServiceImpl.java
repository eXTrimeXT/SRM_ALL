package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreCreateDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreGroupTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemResultEditParam;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.timertasks.entity.TimerTaskEntity;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.bid.event.ExtBidSouEvent;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitEventWebService;
import com.midea.cloud.srm.sou.bid.init.service.impl.ExtAnalysisEventListenerImpl;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertEventService;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import com.midea.cloud.srm.sou.meiql.ca.service.CaService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouDemandMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouGroupMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouTechScoreFileMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouConfirmBidJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouInitJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouInitProjectStatusJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouWinLossNoticeBidJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.delprojects.ApiExtSouInitProjectDelHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editendtimes.ApiExtSouInitEndTimeJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editendtimes.ExtSouEndTimePo;
import com.midea.cloud.srm.sou.sourcing.spi.init.editexperts.ApiExtSouInitRandomExtractExpertHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editexperts.ExtSouRandomExtractExpertPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editgroups.ApiExtSouGroupEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editgroups.ApiExtSouInitGroupHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editinvitesuppliers.ApiExtSouVendorEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editinvitesuppliers.ExtSouVendorEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editmargins.ApiExtSouMarginJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editmargins.ApiExtSouMarginRecordEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editorderitemresults.ApiExtSouOrderItemResultEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editorderitemresults.ApiExtSouOrderItemResultPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editpricetemplates.ApiExtSouPriceTemplateEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editpricetemplates.ExtSouPriceTemplatePo;
import com.midea.cloud.srm.sou.sourcing.spi.init.editscorerules.ApiExtSouScoreRuleEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editscorerules.ExtSouScoreRuleEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ApiExtSouItemEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ExtSouItemEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores.ApiExtSouTechScoreEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores.ApiExtSouTechScoreHeadJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores.ExtSouTechScorePO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ApiExtSouProjectEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editmargins.ExtSouMarginRecordPo;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ExtSouProjectEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.doneproject.ApiExtSouInitProjectDoneHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.open.*;
import com.midea.cloud.srm.sou.sourcing.spi.init.startprices.ApiExtStartPriceEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.startprices.ExtStartPriceEditPO;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import com.midea.cloud.srm.sou.souseq.service.IExtSouSeqService;
import com.midea.cloud.srm.sou.timertasks.enums.TimerTaskTypeEnum;
import feign.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ExtSouInitEventServiceImpl implements ExtSouInitEventService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouFileService fileService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouMarginRecordService recordService;

    @Autowired
    private IExtSouPriceTemplateService priceTemplateService;

    @Autowired
    private SouRoundDAO souRoundDAO;

    @Autowired
    private ExtSouGroupMapper groupMapper;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouScoreRuleService scoreRuleService;

    @Autowired
    private ExtSouInitQueryService souInitQueryService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private IExtSouTechScoreLineService techScoreLineService;

    @Autowired
    private IExtSccSouTechScoreHistoryService techScoreHistoryService;

    @Autowired
    private ExtBidSouInitEventWebService bidSouInitEventWebService;

    @Autowired
    private IExtSouProcessConfigService souProcessConfigService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private IExtSouRoundService souRoundService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private CaService caService;

    @Autowired
    private BidNoticeService bidNoticeService;
    @Autowired
    private ExtSouDemandMapper demandMapper;

    @Autowired
    private IExtSouSeqService souSeqService;

    @Autowired
    private IExtSouExpertRecordService expertRecordService;

    @Autowired
    private IExtSouExpertRiskService expertRiskService;

    @Autowired
    private IExtSouOrderService souOrderService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private ExtSouInviteService extSouInviteService;
    @Autowired
    private IExtSouGroupService extSouGroupService;
    @Autowired
    private QlService qlService;
    @Autowired
    private ExtSouExpertEventService extSouExpertEventService;
    @Autowired
    private IExtSouDemandService extSouDemandService;

    @Autowired
    private IExtNpmSouAjustTimeService iExtNpmSouAjustTimeService;

    @Autowired
    private ExtNpmSouOpenTodoService extNpmSouOpenTodoService;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private ExtSouTechScoreFileMapper extSouTechScoreFileMapper;

    @Autowired
    private ExtBidSouEvent extBidSouEvent;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long editProject(ApiExtSouProjectInfoDTO param, boolean isCopy, String souType) {
        // 校验操作条件/权限
        if (param.getProject().getProjectId() != null) {
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitJudgeHandler.class).judgeEditProjectAuth(param.getProject().getProjectId(), souType);
        }

        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitJudgeHandler.class).doHandlerBeforeEditProject(param, isCopy, souType);

        // 入参校验+转换处理
        ExtSouProjectEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouProjectEditHandler.class).formatValidateAndConvert(param, isCopy, souType);

        //保存项目信息
        projectService.saveOrUpdate(po.getProject());
        //保存招标小组 - 先删后存
        LambdaQueryWrapper<ExtSouGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouGroup::getProjectId, po.getProject().getProjectId());
        queryWrapper.notIn(CollectionUtils.isNotEmpty(po.getGroupList()), ExtSouGroup::getGroupId, po.getGroupList().stream().map(ExtSouGroup::getGroupId).collect(Collectors.toList()));
        groupService.remove(queryWrapper);
        if (CollectionUtils.isNotEmpty(po.getGroupList())) {
            groupService.saveOrUpdateBatch(po.getGroupList());
        }

        //保存招标附件 - 先删后存
        LambdaQueryWrapper<ExtSouFile> queryFileWrapper = new LambdaQueryWrapper<>();
        queryFileWrapper.eq(ExtSouFile::getProjectId, po.getProject().getProjectId());
        queryFileWrapper.notIn(CollectionUtils.isNotEmpty(po.getSouFileList()), ExtSouFile::getSouFileId, po.getSouFileList().stream().map(ExtSouFile::getSouFileId).collect(Collectors.toList()));
        queryFileWrapper.in(ExtSouFile::getFileType, Arrays.asList(SouBidAttachmentTypeEnum.APPLY.getCode(), SouBidAttachmentTypeEnum.BID.getCode()));
        fileService.remove(queryFileWrapper);
        if (CollectionUtils.isNotEmpty(po.getSouFileList())) {
            fileService.saveOrUpdateBatch(po.getSouFileList());
        }


        //保存招标计划 - 先删后存
        LambdaQueryWrapper<ExtSouPlan> queryPlanWrapper = new LambdaQueryWrapper<>();
        queryPlanWrapper.eq(ExtSouPlan::getProjectId, po.getProject().getProjectId());
        queryPlanWrapper.notIn(CollectionUtils.isNotEmpty(po.getPlanList()), ExtSouPlan::getPlanId, po.getPlanList().stream().map(ExtSouPlan::getPlanId).collect(Collectors.toList()));
        planService.remove(queryPlanWrapper);
        if (CollectionUtils.isNotEmpty(po.getPlanList())) {
            planService.saveOrUpdateBatch(po.getPlanList());
            //更新实际创建时间
            po.getPlanList().stream().filter(plan -> SouBidPlanTypeEnum.ACTUAL.getCode().equals(plan.getPlanType()) && Objects.isNull(plan.getBidCreationDate())).forEach(plan -> {
                planService.applyAtualPoint(po.getProject().getProjectId(), po.getProject().getCreationDate(), ExtSouPlan::getBidCreationDate);
            });
        }


        //保存合并申请单号
        if (CollectionUtils.isNotEmpty(po.getSouDemands())) {
            demandService.saveOrUpdateBatch(po.getSouDemands());
        }

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouProjectEditHandler.class).doHandlerAfterEditProject(param, isCopy, souType, po);
        return po.getProject().getProjectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExtSouMarginRecordDto> editMarginRecord(ApiExtSouMarginRecordDto param, String souType) {
        if(param.getProjectId() == null){
            throw new BaseException("projectId参数不能为空！");
        }

        String redisKey = "editMarginRecord_"+param.getProjectId();
        log.info("editMarginRecord redisKey="+redisKey);
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 300, TimeUnit.SECONDS);
        if(!lock){
            throw new BaseException(String.format("该单据正在提交中， 请稍等！"));
        }
        log.info(redisKey+"开始提交保证金扣款、退款...");

        try {
            // 行业包额外处理(前置)
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouMarginRecordEditHandler.class).doHandlerBeforeEditProject(param, souType);

            // 入参校验+转换处理
            ExtSouMarginRecordPo po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouMarginRecordEditHandler.class).formatValidateAndConvert(param, souType);

            //保存
            recordService.saveOrUpdateBatch(po.getMarginRecordList());

            // 行业包额外处理(后置)
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouMarginRecordEditHandler.class).doHandlerAfterEditProject(param, souType, po);
            log.info(redisKey+"提交保证金扣款、退款结束...");
            return po.getMarginRecordDtoList();
        }catch (Exception e){
            log.error("提交保证金扣款、退款失败：", e);
            throw new BaseException(e.getMessage());
        }finally {
            try {
                this.redisTemplate.delete(redisKey);
            } catch (Exception e) {
                log.info("删除redis锁失败 editMarginRecord redisKey="+redisKey);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long editPriceTemplate(ApiExtSouPriceTemplateDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouPriceTemplateEditHandler.class).doHandlerBeforeEditProject(param, souType);

        // 入参校验+转换处理
        ExtSouPriceTemplatePo po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouPriceTemplateEditHandler.class).formatValidateAndConvert(param, souType);

        //删除数据
        LambdaQueryWrapper<ExtSouPriceTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouPriceTemplate::getProjectId, param.getProjectId());
        queryWrapper.notIn(CollectionUtils.isNotEmpty(po.getPriceTemplateList()), ExtSouPriceTemplate::getTemplateId, po.getPriceTemplateList().stream().map(ExtSouPriceTemplate::getTemplateId).collect(Collectors.toList()));
        priceTemplateService.remove(queryWrapper);
        //保存
        priceTemplateService.saveOrUpdateBatch(po.getPriceTemplateList());

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouPriceTemplateEditHandler.class).doHandlerAfterEditProject(param, souType, po);

        return param.getProjectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long modifyProjectStatus(ApiExtSouProjectModifyDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectStatusJudgeHandler.class).doHandlerBeforeEditProject(param, true, souType);

        // 入参校验+转换处理
        ExtSouProjectEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectStatusJudgeHandler.class).doHandlerValidAndConvertEditProject(param, true, souType);

        projectService.updateById(po.getProject());

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectStatusJudgeHandler.class).doHandlerAfterEditProject(param, true, souType, po);

        return param.getProjectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long editProjectEndTime(ApiExtSouEndTimeDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitEndTimeJudgeHandler.class).doHandlerBeforeEditProject(param, true, souType);

        // 入参校验+转换处理
        ExtSouEndTimePo po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitEndTimeJudgeHandler.class).doHandlerValidAndConvertEditProject(param, true, souType);

        if(!Objects.isNull(po.getSouRound())) {
            souRoundDAO.updateById(po.getSouRound());
        }

        if(!Objects.isNull(po.getSouPlan())) {
            planService.updateById(po.getSouPlan());
        }

        //记录调整时间
        iExtNpmSouAjustTimeService.recordAjustEndTime(param);

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitEndTimeJudgeHandler.class).doHandlerAfterEditProject(param, true, souType, po);

        return param.getProjectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delProject(Long projectId, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectDelHandler.class).doHandlerBeforeDelProject(projectId, souType);

        ExtSouProject souProject = projectService.getById(projectId);

        ExtSouProjectDto projectDto = new ExtSouProjectDto();
        BeanCopyUtil.copyProperties(souProject, projectDto);
        projectDto.setDemandList(demandService.lambdaQuery().eq(ExtSouDemand::getProjectId, projectId).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO).list());

        projectService.removeById(projectId);
        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectDelHandler.class).doHandlerAfterDelProject(projectDto, souType);
    }

    @Override
    public void removeEvaGroup(ExtSouGroup param) {
        ExtSouGroup group = groupMapper.selectById(param.getGroupId());
        AssertUtils.notNull(group, "评标小组信息不存在！");

        if (StringUtils.isBlank(param.getExtRemoveReason())) {
            throw new BaseException("移除招标工作组原因不能为空！");
        }

        group.setExtRemoveReason(param.getExtRemoveReason());
        group.setExtEvaFlag(YesOrNo.NO.getValue());
        groupMapper.updateById(group);

        ExtSouProject project = projectService.getById(group.getProjectId());

        //生成记录
        expertRecordService.addRecord(Collections.singletonList(group), project.getExtExpertRange());
    }

    @Override
    public Long editRequires(ApiExtSouItemDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouItemEditHandler.class).doHandlerBeforeEditProject(param, souType);

        //行业包转换处理
        ExtSouItemEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouItemEditHandler.class).formatValidateAndConvert(param, souType);

        //保存
        LambdaQueryWrapper<ExtSouItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouItem::getProjectId, param.getProjectId());
        queryWrapper.notIn(CollectionUtils.isNotEmpty(po.getItemList()), ExtSouItem::getSouItemId, po.getItemList().stream().map(ExtSouItem::getSouItemId).collect(Collectors.toList()));
        itemService.remove(queryWrapper);

        itemService.saveOrUpdateBatch(po.getItemList());

        //更新节点状态
        souProcessConfigService.updateNodeStatus(param.getProjectId(), SouProcessNodeEnum.requireInfo, Enable.Y, param.isTempSave());


        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouItemEditHandler.class).doHandlerAfterEditProject(param, souType, po);

        return param.getProjectId();
    }

    @Override
    public Long editInviteSupplier(ApiExtSouVendorDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouVendorEditHandler.class).doHandlerBeforeEditProject(param, souType);

        //行业包转换处理
        ExtSouVendorEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouVendorEditHandler.class).formatValidateAndConvert(param, souType);

        //保存
        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, param.getProjectId());
        queryWrapper.notIn(ExtSouVendor::getSouVendorId, po.getVendorList().stream().map(ExtSouVendor::getSouVendorId).collect(Collectors.toList()));
        vendorService.remove(queryWrapper);

        vendorService.saveOrUpdateBatch(po.getVendorList());

        //更新节点状态
        souProcessConfigService.updateNodeStatus(param.getProjectId(), SouProcessNodeEnum.inviteVendor, Enable.Y, param.isTempSave());


        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouVendorEditHandler.class).doHandlerAfterEditProject(param, souType, po);

        return param.getProjectId();
    }

    @Override
    public Long editScoreRule(ApiExtSouScoreRuleDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouScoreRuleEditHandler.class).doHandlerBeforeEditProject(param, souType);

        //行业包转换处理
        ExtSouScoreRuleEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouScoreRuleEditHandler.class).formatValidateAndConvert(param, souType);

        //保存
        LambdaQueryWrapper<ExtScoreRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtScoreRule::getProjectId, param.getProjectId());
        queryWrapper.notIn(ExtScoreRule::getScoreRuleId, po.getScoreRuleList().stream().map(ExtScoreRule::getScoreRuleId).collect(Collectors.toList()));
        scoreRuleService.remove(queryWrapper);

        scoreRuleService.saveOrUpdateBatch(po.getScoreRuleList());

        //更新节点状态
        souProcessConfigService.updateNodeStatus(param.getProjectId(), SouProcessNodeEnum.scoreRule, Enable.Y, param.isTempSave());

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouScoreRuleEditHandler.class).doHandlerAfterEditProject(param, souType, po);

        return param.getProjectId();
    }

    @Override
    public Map<String, Object> importScoreExcel(ApiExtSouTechScoreLineQueryDTO query, MultipartFile file, Fileupload fileupload, String souType) throws Exception {
        // 检查参数
        EasyExcelUtil.checkParam(file, fileupload);

        List<ApiExtScoreRuleDto> scoreRuleDtoList = souInitQueryService.getExtScoreRule(query);

        List<List<String>> headList = ExtSouTechScorePO.getHeadList(scoreRuleDtoList);

        //查询字典
        List<DictItem> dictItems = baseClient.listDictItemByDictCode(DictCodeEnum.SOU_SCORE_CONFIG_ITEM.getCode());
        Map<String, String> scoreItemMap = dictItems.stream().collect(Collectors.toMap(DictItem::getDictItemCode, DictItem::getDictItemName, (k1, k2) -> k2));

        //打分项
        Map<String, List<ApiExtScoreRuleDto>> ruleDtoMap = scoreRuleDtoList.stream().collect(Collectors.groupingBy(ruleDto -> ApiExtSouTechScoreEditHandler.formatInputValue(StringUtils.joinWith("_", scoreItemMap.getOrDefault(ruleDto.getScoreItem(), ruleDto.getScoreItem()), ruleDto.getReviewItem()))));
        // 获取输入流
        InputStream inputStream = file.getInputStream();
        // 数据收集器
        ExtAnalysisEventListenerImpl<Map<Integer, Object>> listener = new ExtAnalysisEventListenerImpl<>();
        ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();

        // 第一个sheet读取类型
        ReadSheet readSheet = EasyExcel.readSheet(0).head(headList).build();
        // 开始读取第一个sheet
        excelReader.read(readSheet);
        List<Map<Integer, Object>> list = new ArrayList<>();
        list = listener.getDatas();

        Map<Integer, String> headMap = listener.getHeadMap();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Integer key : headMap.keySet()) {
                String value = headMap.get(key);
                String subValue = (String) list.get(0).get(key);
                if (!value.equals(subValue)) {
                    headMap.put(key, StringUtils.joinWith("_", value, subValue));
                }
            }
        }

        List<Map<String, Object>> importDataList = new ArrayList();
        if (CollectionUtils.isNotEmpty(list) && list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                Map<Integer, Object> data = list.get(i);
                Map<String, Object> converData = new HashMap<>(50);
                for (int dataKey : data.keySet()) {
                    converData.put(headMap.get(dataKey), data.get(dataKey));
                }
                importDataList.add(converData);
            }
        }
        ApiExtSouTechScoreDto param = new ApiExtSouTechScoreDto();
        param.setImportDataList(importDataList);
        param.setRuleDtoMap(ruleDtoMap);
        param.setScoreRuleDtoList(scoreRuleDtoList);
        param.setTempSave(true);
        //校验数据
        ExtSouTechScorePO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreEditHandler.class).formatValidateAndConvertFroImport(param, souType);

        if (param.getImportCheck().get()) {
            //保存评分
            List<ExtSouTechScoreLine> scoreLineList = po.getScoreLineList();
            scoreLineList.stream().filter(s -> ObjectUtils.anyNull(s.getExtDescription())).forEach(s -> s.setExtDescription(""));
            techScoreLineService.saveOrUpdateBatch(scoreLineList);

            //计算总得分
            caculateTotalScore(po.getScoreHead());
            techScoreHeadService.updateById(po.getScoreHead());
        } else {
            List<List<Object>> errorDataList = new ArrayList<>();
            //添加错误列
            headList.add(Arrays.asList(ApiExtSouItemDto.ERROR_MSG, ApiExtSouItemDto.ERROR_MSG));

            List<Integer> keyList = headMap.keySet().stream().sorted(Comparator.comparingInt(s -> s)).collect(Collectors.toList());

            param.getImportDataList().stream().forEach(data -> {
                List<Object> errorData = new ArrayList<>();
                for (int key : keyList) {
                    errorData.add(data.get(headMap.get(key)));
                }
                errorData.add(data.get(ApiExtSouItemDto.ERROR_MSG));
                errorDataList.add(errorData);
            });

            Fileupload errorFileupload = bidSouInitEventWebService.uploadFile(fileupload, file, headList, errorDataList);
            return ImportStatus.importError(errorFileupload.getFileuploadId(), errorFileupload.getFileSourceName());
        }

        //后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreEditHandler.class).doHandlerAfterEditProject(param, souType, po);
        return ImportStatus.importSuccess();
    }

    @Override
    public Long editScore(ApiExtSouTechScoreDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreEditHandler.class).doHandlerBeforeEditProject(param, souType);

        //行业包转换处理
        ExtSouTechScorePO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreEditHandler.class).formatValidateAndConvert(param, souType);

        //保存评分
        List<ExtSouTechScoreLine> scoreLineList = po.getScoreLineList();
        techScoreLineService.saveOrUpdateBatch(scoreLineList);

        //保存附件
        List<ExtSouTechScoreFile> fileList = param.getFileList();
        extSouTechScoreFileMapper.delete(new LambdaQueryWrapper<ExtSouTechScoreFile>().eq(ExtSouTechScoreFile::getTechScoreHeadId, param.getTechScoreHeadId()));
        if (CollectionUtils.isNotEmpty(fileList)) {
            fileList.forEach(e -> e.setTechScoreHeadId(param.getTechScoreHeadId()));
            extSouTechScoreFileMapper.insertBatch(fileList);
        }
        ExtSouTechScoreHead scoreHead = po.getScoreHead();

        //计算得分--一个评分头对应多个供应商，计算得分无意义
//        caculateTotalScore(scoreHead);

        if (!param.isTempSave()) {
            //提交
            scoreHead.setScoreStatus(TechScoreStatusEnum.FINISHED.getCode());

            //记录历史
            techScoreHistoryService.saveTechScoreHist(scoreLineList);
        }

        techScoreHeadService.updateById(scoreHead);

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreEditHandler.class).doHandlerAfterEditProject(param, souType, po);

        return param.getProjectId();
    }

    private void caculateTotalScore(ExtSouTechScoreHead scoreHead) {
        //查询技术标
        LambdaQueryWrapper<ExtScoreRule> queryRuleWrapper = new LambdaQueryWrapper<>();
        queryRuleWrapper.eq(ExtScoreRule::getProjectId, scoreHead.getProjectId());
        queryRuleWrapper.eq(ExtScoreRule::getScoreItem, ScoreConfigItemEnum.TEH_REVIEW.getCode());
        List<ExtScoreRule> extScoreRuleList = scoreRuleService.list(queryRuleWrapper);
        if (CollectionUtils.isEmpty(extScoreRuleList)) {
            return;
        }

        LambdaQueryWrapper<ExtSouTechScoreLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouTechScoreLine::getTechScoreHeadId, scoreHead.getTechScoreHeadId());
        queryWrapper.in(ExtSouTechScoreLine::getScoreRuleLineId, extScoreRuleList.stream().map(ExtScoreRule::getScoreRuleId).collect(Collectors.toList()));

        List<ExtSouTechScoreLine> lineList = techScoreLineService.list(queryWrapper);
        BigDecimal totalScore = BigDecimal.ZERO;

        for (ExtSouTechScoreLine line : lineList) {
            if (!Objects.isNull(line.getScore())) {
                totalScore = totalScore.add(line.getScore());
            }
        }

        scoreHead.setTotalScore(totalScore);

    }

    /**
     * 更新招标状态
     *
     * @param projectId
     * @param souBiddingProStatusEnum
     */
    @Override
    public void updateSouBidStatus(Long projectId, SouBiddingProStatusEnum souBiddingProStatusEnum) {
        ApiExtSouProjectModifyDto dto = new ApiExtSouProjectModifyDto();
        dto.setProjectId(projectId);
        dto.setProjectStatus(souBiddingProStatusEnum.getCode());
        modifyProjectStatus(dto, SouTypeEnum.bid.name());
    }

    @Override
    public Long rejectScoreHead(ExtSouTechScoreHeadDto param, String souType) {

        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreHeadJudgeHandler.class).doHandlerBeforeReject(param, souType);

        ExtSouTechScorePO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreHeadJudgeHandler.class).formatValidateAndConvert(param, souType);
        techScoreHeadService.updateById(po.getScoreHead());

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouTechScoreHeadJudgeHandler.class).doHandlerAfterEditReject(param, souType, po);

        return param.getProjectId();
    }

    @Override
    public Long openTech(Long projectId, String souType) {
        //开标前校验脱敏文件
        checkBeforeEvaTech(projectId);
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtThechOpenHandler.class).judgeOpenTechAuth(projectId, souType);

        // 行业包数据转换处理
        ExtTechOpenEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtThechOpenHandler.class).doHandlerConvertAndFormateTechOpen(projectId, souType);
        if(!Objects.isNull(po.getProject())) {
            projectService.updateById(po.getProject());
        }

        //更新节点状态
        if(SouBiddingProStatusEnum.TECH_BID_OPEN.getCode().equals(po.getProject().getProjectStatus())) {
            souProcessConfigService.updateNodeStatus(projectId, SouProcessNodeEnum.techManagement, Enable.Y, false);

            //更新实际技术开标时间
            planService.applyAtualPoint(projectId, new Date(), ExtSouPlan::getTechOpenTime);

            //自动开始评标
            if(po.isAutoEvaTech()) {
                this.evaTech(projectId, souType);
                //推送ai
                extBidSouEvent.pushTechEvent(projectId);
            }
        }

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtThechOpenHandler.class).doHandlerAfterOpenTech(projectId, souType, po);
        return projectId;
    }

    @Override
    public void checkBeforeEvaTech(Long projectId) {
        //查询供应商
        LambdaQueryWrapper<ExtSouOrder> querySouVendorWrapper = new LambdaQueryWrapper<>();
        querySouVendorWrapper.eq(ExtSouOrder::getProjectId, projectId);
        querySouVendorWrapper.eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());
        querySouVendorWrapper.eq(ExtSouOrder::getRound, 1);
        List<ExtSouOrder> extSouOrders = souOrderService.list(querySouVendorWrapper);

        Assert.isTrue(extSouOrders!=null&&extSouOrders.size()>0,"找不到对应的投标供应商");

        Set<Long> orderIdSet = extSouOrders.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toSet());

        List<ExtSouOrderFile> souOrderFiles = orderFileService.lambdaQuery().in(ExtSouOrderFile::getOrderId,orderIdSet).isNotNull(ExtSouOrderFile::getOrderDocId).list();

        Set<Long> fileOrderIdSet = souOrderFiles.stream().map(ExtSouOrderFile::getOrderId).collect(Collectors.toSet());

        Assert.isTrue(orderIdSet.size()==fileOrderIdSet.size(),"供应商脱敏文件未全上传");
    }

    @Override
    public Long evaTech(Long projectId, String souType) {


        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtThechEvaHandler.class).judgeEvaTechAuth(projectId, souType);

        updateSouBidStatus(projectId, SouBiddingProStatusEnum.TECH_BID_EVA);

        ExtTechEvaEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtThechEvaHandler.class).formatvalidAndConvertPo(projectId, souType);

        //生成评分项
        if(CollectionUtils.isNotEmpty(po.getTechScoreHeadList())) {
            techScoreHeadService.saveOrUpdateBatch(po.getTechScoreHeadList());
        }

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtThechEvaHandler.class).doHandlerAfterEvaTech(projectId, souType, po);
        return projectId;
    }

    @Override
    public Long openBus(Long projectId, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtBusOpenHandler.class).judgeOpenBusAuth(projectId, souType);

        ExtBusOpenEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtBusOpenHandler.class).doFormatevalidAndConvert(projectId, souType);

        if(!Objects.isNull(po.getSouProject())) {
            projectService.updateById(po.getSouProject());
            //更新节点状态
            souProcessConfigService.updateNodeStatus(projectId, SouProcessNodeEnum.businessManagement, Enable.Y, false);
        }

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtBusOpenHandler.class).doHandlerAfterOpenBusTech(projectId, souType, po);

        return projectId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startPrice(ApiExtRoundDto param, String souType) {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtStartPriceEditHandler.class).doHandlerBeforeEditStartPrice(param, souType);

        ExtStartPriceEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtStartPriceEditHandler.class).formatValidateAndConvert(param, souType);

        //生成轮次表
        ExtSouRound souRound = po.getRound();
        souRoundService.saveOrUpdate(souRound);
        //更新基本信息表轮次信息
        projectService.updateById(po.getProject());
        //更新报价数量
        if (CollectionUtils.isNotEmpty(po.getItemList())) {
            itemService.updateBatchById(po.getItemList());
        }
        //保存报价主表信息
        if (CollectionUtils.isNotEmpty(po.getOrderList())) {
            orderService.saveOrUpdateBatch(po.getOrderList());
            //清空投标时间
            orderService.lambdaUpdate().set(ExtSouOrder::getSubmitTime, null)
                    .set(ExtSouOrder::getSubmitBy, null).set(ExtSouOrder::getSubmitById, null).set(ExtSouOrder::getSubmitFullName, null)
                    .in(ExtSouOrder::getOrderId, po.getOrderList().stream().map(o -> o.getOrderId()).collect(Collectors.toList())).update();
        }

        //保存报价明细表
        if (CollectionUtils.isNotEmpty(po.getOrderItemList())) {
            orderItemService.saveOrUpdateBatch(po.getOrderItemList());
        }

        //更新节点状态
        souProcessConfigService.updateNodeStatus(po.getProject().getProjectId(), SouProcessNodeEnum.businessManagement, Enable.N, false);

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtStartPriceEditHandler.class).doHandlerAfterEditStartPrice(param, souType, po);

        return null;
    }

    @Override
    public Long canNotNeedPayMargin(ExtSouMargin param, String souType) {
        ExtSouMargin souMargin = marginService.getById(param.getMarginId());
        AssertUtils.notNull(souMargin, "保证金不存在");

        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouMarginJudgeHandler.class).judgeMarginCanNotNeesPayBeforeHandler(param, souMargin, souType);

        marginService.updateById(souMargin);
        return souMargin.getProjectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long rejectOrder(ApiExtSouOrderDto param, String souType) {

        ExtNpmSouOrder extNpmSouOrder = extNpmSouOrderService.getById(param.getOrderId());
        AssertUtils.notNull(extNpmSouOrder, "投标单据不存在！");

        ExtSouOrder souOrder = orderService.getById(extNpmSouOrder.getOrderId());
        AssertUtils.notNull(souOrder, "投标单据不存在！");

        ExtSouProject project = projectService.getById(souOrder.getProjectId());
        if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode(), SouBiddingProStatusEnum.BUS_BID.getCode()
                , SouBiddingProStatusEnum.BUS_BID_END.getCode(), SouBiddingProStatusEnum.BUS_BID_OPEN.getCode()).contains(project.getProjectStatus())) {

            throw new BaseException("处在定标发起后续环节，不允许废标！");
        }

        //校验最新轮次
        if(Integer.compare(souOrder.getRound(), extNpmSouOrder.getRound()) != 0) {
            throw new BaseException("非当前轮次，不可废标");
        }
        if(!ObjectUtils.defaultIfNull(souOrder.getExtOrderType(), "").equals(extNpmSouOrder.getExtOrderType())) {
            throw new BaseException("已进入商务标环节，技术标不可废标");
        }

        AssertUtils.isTrue(StringUtils.isNotBlank(param.getRejectReason()), "作废原因不能为空！");

        souOrder.setRejectReason(param.getRejectReason());
        souOrder.setOrderStatus(SouOrderStatusEnum.CANCEL);
        souOrder.setRejectTime(new Date());

        orderService.updateById(souOrder);

        LambdaQueryWrapper<ExtSouOrderItem> orderItemQuery = new LambdaQueryWrapper<>();
        orderItemQuery.eq(ExtSouOrderItem::getOrderId, souOrder.getOrderId());
        //行表状态同步
        List<ExtSouOrderItem> orderItemList = orderItemService.list(orderItemQuery);
        if (CollectionUtils.isNotEmpty(orderItemList)) {
            orderItemList.stream().forEach(item -> item.setOrderStatus(souOrder.getOrderStatus()));
            orderItemService.updateBatchById(orderItemList);
        }
        //同步更新投标扩展表信息
        extNpmSouOrderService.extendSouOrder(Collections.singletonList(souOrder));

        //是否废标 --- 是
        extSouInviteService.updateIsInvalidBid(project, souOrder.getVendorId(), YesOrNo.YES.getValue());
        return souOrder.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long withdrawOrderBatch(List<ApiExtSouOrderDto> param, String souType) {
        List<ExtNpmSouOrder> extNpmSouOrders = extNpmSouOrderService.listByIds(param.stream().map(o -> o.getOrderId()).collect(Collectors.toList()));
        Map<Long, ApiExtSouOrderDto> paramMap = param.stream().collect(Collectors.toMap(k -> k.getOrderId(), Function.identity(), (k1, k2)->k2));

        AssertUtils.isTrue(CollectionUtils.isNotEmpty(extNpmSouOrders), "投标明细数据不存在！");

        List<SouInviteItem> souInviteItems = new ArrayList<>();

        LambdaQueryWrapper<ExtSouOrder> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.in(ExtSouOrder::getOrderId, extNpmSouOrders.stream().map(o -> o.getOrderId()).collect(Collectors.toList()));
        List<ExtSouOrder> souOrderList = orderService.list(orderQuery);

        AssertUtils.isTrue(CollectionUtils.isNotEmpty(souOrderList), "投标信息不存在！");

        Map<Long, ExtSouOrder>  souOrderMap = souOrderList.stream().collect(Collectors.toMap(o -> o.getOrderId(), Function.identity(), (k1, k2)-> k2));

        Set<Long> vendorIdSet = new HashSet<>();
        extNpmSouOrders.stream().forEach(extNpmSouOrder -> {
            extNpmSouOrder.setExtNotjoinReason(paramMap.getOrDefault(extNpmSouOrder.getExtOrderId(), new ApiExtSouOrderDto()).getExtNotjoinReason());

            if(StringUtils.isNotBlank(extNpmSouOrder.getExtNotjoinReason())) {
                SouInviteItem souInviteItem = new SouInviteItem();
                souInviteItem.setVendorId(souOrderMap.getOrDefault(extNpmSouOrder.getOrderId(), new ExtSouOrder()).getVendorId());
                souInviteItem.setNotParticipatingReason(extNpmSouOrder.getExtNotjoinReason());
                if(!vendorIdSet.contains(souInviteItem.getVendorId())) {
                   souInviteItems.add(souInviteItem);
                }
                vendorIdSet.add(souInviteItem.getVendorId());
            }
        });

        extNpmSouOrderService.updateBatchById(extNpmSouOrders);

        //保存不参与原因
        extSouInviteService.updateNotParticipatingReasonBatch(projectService.getById(souOrderList.get(0).getProjectId()), souInviteItems);

        return souOrderList.get(0).getOrderId();
    }

    @Override
    public ExtSouProjectDto confirmBid(Long projectId, String souType) throws Exception {
        // 行业包额外处理(校验)
        ExtSouProject souProject = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouConfirmBidJudgeHandler.class).judgeConfirmBidAuth(projectId, souType);
        //调用定标申请接口
        CaDTO caDTO = caService.add(projectId);
        //更新状态
        updateSouBidStatus(projectId, SouBiddingProStatusEnum.CONFIRM_BID);
        //更新节点
        souProcessConfigService.updateNodeStatus(projectId, SouProcessNodeEnum.businessManagement, Enable.Y, false);

        ExtSouProject projectDto = projectService.getById(projectId);
        ExtSouProjectDto extSouProjectDto = new ExtSouProjectDto();
        BeanCopyUtil.copyProperties(extSouProjectDto, projectDto);
        extSouProjectDto.setCaId(caDTO.getCaId());
        extSouProjectDto.setCaNo(caDTO.getCaNo());

        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouConfirmBidJudgeHandler.class).doHandlerAfterConfirmBid(projectId, souType, souProject, extSouProjectDto);

        return extSouProjectDto;
    }

    @Override
    public Long editOrderItemResult(ApiExtSouOrderItemResultEditParam param, String souType) {
        // 行业包处理
        ApiExtSouOrderItemResultPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemResultEditHandler.class).formatValidateAndConvert(param, souType);

        orderItemService.updateBatchById(po.getOrderItemList());

        // 行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemResultEditHandler.class).doHandlerAfterEditeOrderItemResult(param, souType, po);

        return param.getProjectId();
    }

    @Override
    public ExtSouProjectDto generateWinLossBidNotice(Long projectId, String souType) throws Exception {
        // 行业包额外处理(校验)
        ExtSouProject souProject = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouWinLossNoticeBidJudgeHandler.class).judgeWinLossNoticeBidAuth(projectId, souType);
        //调用定标申请接口
        BidNoticeDTO noticeDTO = bidNoticeService.add(projectId);
        //更新状态
        updateSouBidStatus(projectId, SouBiddingProStatusEnum.NOTICE_ING);
        //更新节点
        souProcessConfigService.updateNodeStatus(projectId, SouProcessNodeEnum.bidReuslt, Enable.Y, false);
        ExtSouProject projectDto = projectService.getById(projectId);
        ExtSouProjectDto extSouProjectDto = new ExtSouProjectDto();
        BeanCopyUtil.copyProperties(extSouProjectDto, projectDto);
        extSouProjectDto.setBidNoticeId(noticeDTO.getBidNoticeId());
        extSouProjectDto.setBidNoticeNo(noticeDTO.getBidNoticeNo());
        // 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouWinLossNoticeBidJudgeHandler.class).doHandlerAfterWinLossNoticeBidBid(projectId, souType, souProject);

        return extSouProjectDto;
    }

    @Override
    public Long editBusTalkFile(ApiExtSouTalkFileEditDto param, String souType) {
        ExtSouProject souProject = projectService.getById(param.getProjectId());
        AssertUtils.notNull(souProject, "项目信息不存在");

        List<ExtSouFile> souFileList = param.getTalkFileList();
        AtomicInteger index = new AtomicInteger(1);
        souFileList.stream().forEach(f -> {
            if (Objects.isNull(f.getSouFileId())) {
                f.setSouFileId(IdGenrator.generate());
                f.setSortIndex(index.getAndAdd(1));
                f.setFileType(SouBidAttachmentTypeEnum.TALK.getCode());
                f.setProjectId(param.getProjectId());
            }
        });

        LambdaQueryWrapper<ExtSouFile> fileQuery = new LambdaQueryWrapper<>();
        fileQuery.eq(ExtSouFile::getProjectId, param.getProjectId());
        fileQuery.eq(ExtSouFile::getFileType, SouBidAttachmentTypeEnum.TALK.getCode());
        fileQuery.notIn(CollectionUtils.isNotEmpty(souFileList), ExtSouFile::getSouFileId, souFileList.stream().map(ExtSouFile::getSouFileId).collect(Collectors.toList()));

        fileService.remove(fileQuery);

        if (CollectionUtils.isNotEmpty(souFileList)) {
            fileService.saveOrUpdateBatch(souFileList);
        }


        return param.getProjectId();
    }

    @Override
    public Long editWinOrLossNotice(Long projectId, String souType) {

        //更新状态
        updateSouBidStatus(projectId, SouBiddingProStatusEnum.ARCHIVE_TODO);

        //更新节点
        souProcessConfigService.updateNodeStatus(projectId, SouProcessNodeEnum.bidWinOrLoss, Enable.Y, false);
        return projectId;
    }

    @Override
    public Long editArchiveFile(ApiExtSouArchiveFileEditDto param, String souType) {
        ExtSouProject souProject = projectService.getById(param.getProjectId());
        AssertUtils.notNull(souProject, "项目信息不存在");

        List<ExtSouFile> souFileList = param.getArchiveFileList();
        AtomicInteger index = new AtomicInteger(1);
        souFileList.stream().forEach(f -> {
            if (Objects.isNull(f.getSouFileId())) {
                f.setSouFileId(IdGenrator.generate());
                f.setSortIndex(index.getAndAdd(1));
                f.setFileType(SouBidAttachmentTypeEnum.ARCHIVE.getCode());
                f.setProjectId(param.getProjectId());
            }
        });

        LambdaQueryWrapper<ExtSouFile> fileQuery = new LambdaQueryWrapper<>();
        fileQuery.eq(ExtSouFile::getProjectId, param.getProjectId());
        fileQuery.eq(ExtSouFile::getFileType, SouBidAttachmentTypeEnum.ARCHIVE.getCode());
        fileQuery.notIn(ExtSouFile::getSouFileId, souFileList.stream().map(ExtSouFile::getSouFileId).collect(Collectors.toList()));

        fileService.remove(fileQuery);

        fileService.saveOrUpdateBatch(souFileList);

        //项目归档
        if (!param.isTempSave()) {
            updateSouBidStatus(param.getProjectId(), SouBiddingProStatusEnum.ARCHIVE_DONE);
            //更新节点
            souProcessConfigService.updateNodeStatus(param.getProjectId(), SouProcessNodeEnum.bidArchive, Enable.Y, param.isTempSave());

            //更新完成时间
            planService.applyAtualPoint(param.getProjectId(), new Date(), ExtSouPlan::getCompleteTime);

            ExtSouProjectDto projectDto = new ExtSouProjectDto();
            BeanCopyUtil.copyProperties(souProject, projectDto);
            projectDto.setDemandList(demandService.lambdaQuery().eq(ExtSouDemand::getProjectId, param.getProjectId()).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO).list());
            // 行业包额外处理(后置)
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitProjectDoneHandler.class).doHandlerAfterEndProject(projectDto, souType);

        }

        return param.getProjectId();

    }

    @Override
    public String generateProjectNum(String prefix, String code, String dateStr) {

        return souSeqService.getSerial(prefix, code, dateStr, 3L);
    }

    @Override
    public Long randomExtractExpert(ApiExtSouExpertRandomExtractDto param, String souType) {

        //行业包前置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitRandomExtractExpertHandler.class).doHandlerBeforeRandomExtractExpert(param, souType);

        //行业包转换处理
        ExtSouRandomExtractExpertPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitRandomExtractExpertHandler.class).doHandlerValidAndConvertRandomExtractExpert(param, souType);

        //保存数据
        if(!Objects.isNull(po.getSouProject())) {
            projectService.updateById(po.getSouProject());
        }
        if (CollectionUtils.isNotEmpty(po.getExpertRecordList())) {
            expertRecordService.saveOrUpdateBatch(po.getExpertRecordList());
        }
        if (CollectionUtils.isNotEmpty(po.getExpertRiskList())) {
            expertRiskService.saveOrUpdateBatch(po.getExpertRiskList());
        }
        if (CollectionUtils.isNotEmpty(po.getGroupList())) {
            groupService.saveOrUpdateBatch(po.getGroupList());
        }

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitRandomExtractExpertHandler.class).doHandlerAfterRandomExtractExpert(param, souType, po);

        return param.getProjectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long confirmTechEva(Long projectId, String souType) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "信息信息不存在");

        if(!SouBiddingProStatusEnum.TECH_BID_EVA.getCode().equals(souProject.getProjectStatus())) {
            throw new BaseException(MessageFormat.format("当前状态非{0}，不允许确认评标！", SouBiddingProStatusEnum.TECH_BID_EVA.getName()));
        }

        LambdaQueryWrapper<ExtSouTechScoreHead> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouTechScoreHead::getProjectId, projectId);

        List<ExtSouTechScoreHead> headList = techScoreHeadService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(headList)) {
            //所有已评标
            headList.stream().forEach(h -> {
                if (StringUtils.isBlank(h.getScoreStatus()) || TechScoreStatusEnum.UNFINISHED.getCode().equals(h.getScoreStatus())) {
                    throw new BaseException("存在未评标的数据，不允许确认评标");
                }
                h.setExtConfirmFlag(YesOrNo.YES.getValue());
            });
        }

        //更新状态
        souProject.setProjectStatus(SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode());
        projectService.updateById(souProject);
        //更新投标头表
        techScoreHeadService.updateBatchById(headList);
        //确认评标
        planService.applyAtualPoint(projectId, new Date(), ExtSouPlan::getTechEvaluationTime);
        // 额外: 生成专家评分信息到专家库模块中
        if (!headList.isEmpty()) {
            // 1: 查询招标成员信息
            List<ExtSouGroup> groupList = extSouGroupService.lambdaQuery()
                    .eq(ExtSouGroup::getProjectId, projectId)
                    .eq(ExtSouGroup::getExtEvaFlag, Enable.Y)
                    .list();
            // 招标负责人
            ExtSouGroup managerGroup = groupList.stream().filter(e -> "PRINCIPAL".equals(e.getGroupRole())).findAny().orElse(null);
            // 招标组长
            ExtSouGroup leaderGroup = groupList.stream().filter(e -> "LEADER".equals(e.getGroupRole())).findAny().orElse(null);
            // 招标评分成员
            List<ExtSouGroup> scoreGroupList = groupList.stream().filter(e -> "SOU_TECH".equals(e.getScoreAuth())).collect(Collectors.toList());
            // 2: 构造数据并保存
            extracted(souProject, managerGroup, leaderGroup, scoreGroupList);
        }
        /** 发送商务标待办 */
        sendBusTodo(souProject);
        return projectId;
    }

    /**
     * 发送商务标待办
     * @param souProject
     */
    private void sendBusTodo(ExtSouProject souProject) {
        //判断是否存在商务标数据
        Long count = orderService.lambdaQuery()
                .eq(ExtSouOrder::getProjectId, souProject.getProjectId())
                .eq(ExtSouOrder::getExtOrderType, ExtOrderTypeEnum.BUS.getCode())
                .eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name()).count();
        if(Long.compare(count, SrmConstant.LONG_ZERO) == 1) {
            extNpmSouOpenTodoService.sendTodo(Collections.singletonList(souProject));
        }
    }

    /**
     * 2: 构造数据并保存
     * @param souProject 参数
     * @param managerGroup 参数
     * @param leaderGroup 参数
     * @param scoreGroupList 参数
     */
    private void extracted(ExtSouProject souProject, ExtSouGroup managerGroup, ExtSouGroup leaderGroup, List<ExtSouGroup> scoreGroupList) {
        if (!scoreGroupList.isEmpty()) {
            // 只拿
            Set<String> usernames = scoreGroupList.stream().map(ExtSouGroup::getUserName).collect(Collectors.toSet());
            Map<String/* username */, ExtSouExpert> expertMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpert.class)
                    .in(ExtSouExpert::getExpertUsername, usernames), ExtSouExpert.class)
                    .stream().collect(Collectors.toMap(ExtSouExpert::getExpertUsername, Function.identity()));
            scoreGroupList = scoreGroupList.stream().filter(e -> expertMap.containsKey(e.getUserName())).collect(Collectors.toList());
            boolean canCreateScoreInfo = (managerGroup != null || leaderGroup != null) && !scoreGroupList.isEmpty();
            if (canCreateScoreInfo) {
                List<ExtSouExpertScoreCreateDTO> scoreDTOList = new ArrayList<>(scoreGroupList.size());
                Set<String> groupUsernames = new HashSet<>(scoreGroupList.size());
                for (ExtSouGroup scoreGroup : scoreGroupList) {
                    if (!groupUsernames.add(scoreGroup.getUserName())) { continue; }
                    ExtSouExpert expert = expertMap.get(scoreGroup.getUserName());

                    ExtSouExpertScoreCreateDTO expertScoreDTO = new ExtSouExpertScoreCreateDTO();
                    scoreDTOList.add(expertScoreDTO);

                    expertScoreDTO.setSouProjectId(souProject.getProjectId());
                    expertScoreDTO.setSouNo(souProject.getSouNo());
                    expertScoreDTO.setSouName(souProject.getSouName());
                    expertScoreDTO.setProjectAddress(souProject.getOrderSite());
                    expertScoreDTO.setTotalAmountByTenKilo(souProject.getExtBudget());
                    expertScoreDTO.setExpertUserId(expert.getExpertUserId());
                    expertScoreDTO.setExpertUsername(expert.getExpertUsername());
                    expertScoreDTO.setExpertFullName(expert.getExpertFullName());

                    // 如果当前评分对象是组长，则明细数据为负责人+其他人；否则为组长+负责人
                    boolean isForLeader = leaderGroup != null && scoreGroup.getUserId().equals(leaderGroup.getUserId());
                    expertScoreDTO.setScoreForLeader(isForLeader ? Enable.Y : Enable.N);

                    expertScoreDTO.setScoreLineList(new ArrayList<>());
                    if (managerGroup != null) {
                        ExtSouExpertScoreLine scoreLine = new ExtSouExpertScoreLine();
                        expertScoreDTO.getScoreLineList().add(scoreLine);
                        scoreLine.setUserId(managerGroup.getUserId());
                        scoreLine.setUsername(managerGroup.getUserName());
                        scoreLine.setNickname(managerGroup.getFullName());
                        scoreLine.setGroupType(ExtSouExpertScoreGroupTypeEnum.SOU_MANAGER.name());
                    }
                    if (!isForLeader && leaderGroup != null) {
                        ExtSouExpertScoreLine scoreLine = new ExtSouExpertScoreLine();
                        expertScoreDTO.getScoreLineList().add(scoreLine);
                        scoreLine.setUserId(leaderGroup.getUserId());
                        scoreLine.setUsername(leaderGroup.getUserName());
                        scoreLine.setNickname(leaderGroup.getFullName());
                        scoreLine.setGroupType(ExtSouExpertScoreGroupTypeEnum.SOU_LEADER.name());
                    }
                    if (isForLeader) {
                        for (ExtSouGroup group : scoreGroupList) {
                            if (managerGroup != null && managerGroup.getUserId().equals(group.getUserId())) { continue; }
                            if (leaderGroup.getUserId().equals(group.getUserId())) { continue; }
                            // 其他普通的评分成员，由组长代理评分
                            ExtSouExpertScoreLine scoreLine = new ExtSouExpertScoreLine();
                            expertScoreDTO.getScoreLineList().add(scoreLine);
                            scoreLine.setUserId(group.getUserId());
                            scoreLine.setUsername(group.getUserName());
                            scoreLine.setNickname(group.getFullName());
                            scoreLine.setGroupType(ExtSouExpertScoreGroupTypeEnum.SOU_LEADER.name());
                            scoreLine.setProxyUserId(leaderGroup.getUserId());
                        }
                    }
                }

                // 保存数据
                extSouExpertEventService.createExpertScores(scoreDTOList);
            }
        }
    }

    @Override
    public Long addTechManagementGroup(ApiExtSouGroupEditDto param, String souType) {
        //行业包前置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitGroupHandler.class).doHandlerBeforeAddGroup(param, souType);

        //行业包转换处理
        ApiExtSouGroupEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitGroupHandler.class).doHandlerValidAndConvertAddGroup(param, souType);

        //保存数据
        if (CollectionUtils.isNotEmpty(po.getGroupList())) {
            groupService.saveOrUpdateBatch(po.getGroupList());
        }

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitGroupHandler.class).doHandlerAfterAddGroup(param, souType, po);

        return param.getProjectId();
    }

    @Override
    public Long saveCaSumbimteReport(Long caId, String souType) {
        /** 定标申请到4.1节点时请求更新汇总上报时间 */
        if(ObjectUtils.allNotNull(caId)) {
            List<CaDTO> caList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.Ca.getCode()).eq(CaDTO::getCaId, caId), CaDTO.class);
            if(CollectionUtils.isNotEmpty(caList)) {
                caList.stream().filter(ca -> ObjectUtils.allNotNull(ca.getProjectId())).forEach(ca -> {
                    planService.applyAtualPoint(ca.getProjectId(), new Date(), ExtSouPlan::getSumReportTime, false);
                });
            }
        }
        return caId;
    }

    @Override
    public void cancleBid(List<Long> projectIdList) {
        if(CollectionUtils.isEmpty(projectIdList)) {
            return ;
        }

        //取消任务
        qlService.deleteByWrapper(QlWrappers.update(MqlType.TIMER_TASK).in(TimerTaskEntity::getBusinessId, projectIdList).in(TimerTaskEntity::getBusinessType, Arrays.asList(TimerTaskTypeEnum.OPEN_BUSINESS_BID.name(), TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name())));

        //取消待办
        List<ExtNpmSouOpenBidRecord> recordList = openBidRecordService.lambdaQuery().in(ExtNpmSouOpenBidRecord::getProjectId, projectIdList).eq(ExtNpmSouOpenBidRecord::getOpenStatus, ProcessStatusEnum.PENDING.getCode()).list();
        if(CollectionUtils.isNotEmpty(recordList)) {
            recordList.stream().forEach(record -> {
                extNpmSouOpenTodoService.havedone(record.getProjectId(), record);
            });
        }
    }
}
