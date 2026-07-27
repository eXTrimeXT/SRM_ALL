package com.midea.cloud.srm.sou.sourcing.spi.init.editproject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.enums.SouBidAttachmentTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.souseq.service.IExtSouSeqService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouProjectEditHandler implements ISouSpiBean {

    @Autowired
    private ExtSouProjectMapper projectMapper;
    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjSouClient pjSouClient;

    @Autowired
    private IExtSouSeqService iExtSouSeqService;

    @Autowired
    private IExtSouProcessConfigService processConfigService;

    @Autowired
    private ISouApproveUserService approveUserService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private QlService qlService;

    private static final String SEQ_SOU_BID_NO = "SEQ_SOU_BID_NO";

    private static final String SEQ_PREFIX = "GW";

    private static final String SEQ_DATE_FORMATE = "yyyyMM";

    private static final String SEQ_DATE_FORMATE_YEAR = "yyyy";

    private static final Long SEQ_DIGIT = 3L;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouProjectEditPO formatValidateAndConvert(ApiExtSouProjectInfoDTO param, boolean isCopy, String souType) {
        if (isCopy) { param.setTempSave(true); }

        // 1: 数据格式化及校验
        this.formatAndValidate(param, param.getTempSave(), isCopy, souType);
        // 2: 数据转换
        return this.convert(param, param.getTempSave(), souType);
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param isTempSave 参数
     * @param isCopy 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouProjectInfoDTO param, boolean isTempSave, boolean isCopy, String souType) {

    }

    /**
     * 数据转换
     * @param param 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouProjectEditPO convert(ApiExtSouProjectInfoDTO param, boolean isTempSave, String souType) {
        ExtSouProjectEditPO projectEditPo = new ExtSouProjectEditPO();
        projectEditPo.setProject(this.doConvertProject(param, isTempSave, souType));
        //申请单号
        projectEditPo.setSouDemands(this.doConvertDemands(param, isTempSave, souType));
        projectEditPo.setGroupList(this.doConvertSouGroup(param, projectEditPo.getSouDemands(), isTempSave, souType));
        projectEditPo.setSouFileList(this.doConvertSouFile(param, isTempSave, souType));
        projectEditPo.setPlanList(this.doConvertSouPlan(param, isTempSave, souType));

        //默认带出公示模板上的开户信息
        this.doConvertDefaultBankInfoOfPublic(projectEditPo);

        return projectEditPo;
    }

    protected void doConvertDefaultBankInfoOfPublic(ExtSouProjectEditPO projectEditPo) {
        //不用查询公示模板情况：不需要缴纳保证金 或者 已经维护银行账号
        if(YesOrNo.NO.getValue().equals(projectEditPo.getProject().getExtEarnestFlag()) || StringUtils.isNotBlank(projectEditPo.getProject().getExtBankNumber())) {
            return;
        }

        List<SouReqHead> souReqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).in(SouReqHead::getRequirementHeadNo, projectEditPo.getSouDemands().stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList())), SouReqHead.class);

        //不用查询公示模板情况：寻源需求为空 或 寻源需求不是公示情况  或公示模板ID为空
        if(CollectionUtils.isEmpty(souReqHeadList) || !YesOrNo.YES.getValue().equals(souReqHeadList.get(0).getIsPublic()) || Objects.isNull(souReqHeadList.get(0).getPubconfigId())) {
            return;
        }

        //查询公示模板
        SccPjSourcePubconfig pubconfig = pjSouClient.queryPubconfig(souReqHeadList.get(0).getPubconfigId());
        //不用查询公示模板情况：公示模板不存在
        if(Objects.isNull(pubconfig)) {
            return;
        }

        projectEditPo.getProject().setExtBankNumber(pubconfig.getBankNumber());
        projectEditPo.getProject().setExtBankName(pubconfig.getBankName());
        projectEditPo.getProject().setExtBankAccount(pubconfig.getBankAccount());
        projectEditPo.getProject().setExtBankAccountName(pubconfig.getBankAccountName());
    }

    protected List<ExtSouDemand> doConvertDemands(ApiExtSouProjectInfoDTO param, boolean isTemSave, String souType) {
        if(StringUtils.isBlank(param.getProject().getApplicantNo())) {
            return new ArrayList<>();
        }

        //查询系统申请单号
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, param.getProject().getProjectId());
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        List<ExtSouDemand> demandList = demandService.list(queryWrapper);
        Map<String, ExtSouDemand> demandMap = demandList.stream().collect(Collectors.toMap(d -> d.getApplicantNo(), Function.identity(), (k1, k2)->k2));

        //合并申请单号
        String[] split = param.getProject()
                .getApplicantNo().split(";");
        List<ExtSouDemand> souDemands = new ArrayList<>();
        for (String appNo : split) {
            ExtSouDemand extSouDemand = new ExtSouDemand();
            extSouDemand.setStatus(SrmConstant.NUM_ZERO);

            if(demandMap.containsKey(appNo)) {
                extSouDemand = demandMap.get(appNo);
            } else {
                extSouDemand.setProjectId(param.getProject().getProjectId());
                extSouDemand.setApplicantNo(appNo);
                extSouDemand.setDemandId(IdGenrator.generate());
            }

            souDemands.add(extSouDemand);
        }
        //生成包名
        return demandService.generatePackName(souDemands);
    }

    /**
     * 转换得到寻源信息
     * @param projectInfo 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouProject doConvertProject(ApiExtSouProjectInfoDTO projectInfo, boolean isTempSave, String souType) {
        ExtSouProject project = projectInfo.getProject();
        project.setSouType(souType);
        if(Objects.isNull(project.getProjectId())) {
            project.setProjectId(IdGenrator.generate());
            project.setProjectStatus(SouBiddingProStatusEnum.DRAW_UP.getCode());
            project.setCreateApprovalStatus(SouApprovalStatusEnum.DRAFT.name());
            //SEQ_SOU_BID_NO
            project.setSouNo(baseClient.seqGen(SEQ_SOU_BID_NO));
            String dateStr = DateUtil.format(new Date(), SEQ_DATE_FORMATE);
            String yearStr = DateUtil.format(new Date(), SEQ_DATE_FORMATE_YEAR);
            project.setExtProjectNo(StringUtils.join(SEQ_PREFIX, dateStr, "-", project.getExtOrgBuCode(), iExtSouSeqService.getSerial(SEQ_PREFIX, project.getExtOrgBuCode(), yearStr, SEQ_DIGIT)));
        }
        return project;
    }

    /**
     * 转换得到工作小组
     * @param projectInfo 参数
     * @param souDemands 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouGroup> doConvertSouGroup(ApiExtSouProjectInfoDTO projectInfo, List<ExtSouDemand> souDemands, boolean isTempSave, String souType) {
        List<ExtSouGroup> groupList = projectInfo.getGroupList();
        AtomicInteger index = new AtomicInteger(1);
        //替换包名
        Map<String, ExtSouDemand> souDemandMap = new HashMap<>(50);
        if(CollectionUtils.isNotEmpty(souDemands)) {
            souDemandMap = souDemands.stream().collect(Collectors.toMap(s->s.getApplicantNo(), Function.identity(), (k1, k2)->k2));
        }
        if(CollectionUtils.isNotEmpty(groupList)) {
            Map<String, ExtSouDemand> finalSouDemandMap = souDemandMap;
            groupList.stream().forEach(group -> {
                if(Objects.isNull(group.getGroupId())) {
                    group.setGroupId(IdGenrator.generate());
                }
                group.setProjectId(projectInfo.getProject().getProjectId());
                group.setExtGroupFlag(YesOrNo.YES.getValue());
                group.setExtEvaFlag(YesOrNo.YES.getValue());
                group.setSortIndex(index.getAndAdd(1));
                if(finalSouDemandMap.containsKey(group.getExtPackageName())) {
                    group.setExtPackageName(finalSouDemandMap.get(group.getExtPackageName()).getPackageName());
                }

                //招标负责人默认为创建人
                if(ExtSouGroupRoleEnum.PRINCIPAL.getCode().equals(group.getGroupRole())) {
                    group.setUserName(AppUserUtil.getLoginAppUser().getUsername());
                    group.setFullName(AppUserUtil.getLoginAppUser().getNickname());
                    group.setUserId(AppUserUtil.getLoginAppUser().getUserId());
                    group.setPosition(AppUserUtil.getLoginAppUser().getCeeaCompanyDescr());
                    group.setPhone(AppUserUtil.getLoginAppUser().getPhone());
                    group.setEmail(AppUserUtil.getLoginAppUser().getEmail());
                }
            });
        }
        return groupList;
    }

    /**
     * 转换得到招标附件
     * @param projectInfo 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouFile> doConvertSouFile(ApiExtSouProjectInfoDTO projectInfo, boolean isTempSave, String souType) {
        List<ExtSouFile> souFileList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);
        if(CollectionUtils.isNotEmpty(projectInfo.getApplyFileList())) {
            projectInfo.getApplyFileList().stream().forEach(file -> {
                file.setFileType(SouBidAttachmentTypeEnum.APPLY.getCode());
                file.setProjectId(projectInfo.getProject().getProjectId());
                if(Objects.isNull(file.getSouFileId())) {
                    file.setSouFileId(IdGenrator.generate());
                }
                file.setSortIndex(index.getAndAdd(1));
                souFileList.add(file);
            });
        }

        if(CollectionUtils.isNotEmpty(projectInfo.getBidFileList())) {
            projectInfo.getBidFileList().stream().forEach(file -> {
                file.setFileType(SouBidAttachmentTypeEnum.BID.getCode());
                file.setProjectId(projectInfo.getProject().getProjectId());
                if(Objects.isNull(file.getSouFileId())) {
                    file.setSouFileId(IdGenrator.generate());
                }
                file.setSortIndex(index.getAndAdd(1));
                souFileList.add(file);
            });
        }
        return souFileList;
    }

    /**
     * 转换得到招标计划
     * @param projectInfo 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouPlan> doConvertSouPlan(ApiExtSouProjectInfoDTO projectInfo, boolean isTempSave, String souType) {
        List<ExtSouPlan> planList = projectInfo.getPlanList();
        if(CollectionUtils.isNotEmpty(planList)) {
            planList.stream().forEach(plan -> {
                if(Objects.isNull(plan.getPlanId())) {
                    plan.setPlanId(IdGenrator.generate());
                }
                //默认技术标截止时间放到调整时间，后续用调整截止时间判断是否截止
                plan.setTechEndFixTime(plan.getTechEndTime());
                plan.setProjectId(projectInfo.getProject().getProjectId());
            });
        }
        return planList;
    }

    @ApiOperation("行业包后置处理")
    public void doHandlerAfterEditProject(ApiExtSouProjectInfoDTO param, boolean isTempSave, String souType, ExtSouProjectEditPO po) {

        //保存审批人
        if(!Objects.isNull(param.getProject().getApproveUserId())) {
            SouApproveUser approveUser = approveUserService.getNewestApproveUser(po.getProject().getProjectId());
            if(Objects.isNull(approveUser)) {
                approveUserService.addApproveUser(po.getProject().getProjectId(), param.getProject().getApproveUserId(), param.getProject().getApproveUserName(), param.getProject().getApproveFullName());
            } else {
                if(Long.compare(approveUser.getUserId(), param.getProject().getApproveUserId()) != 0) {
                    approveUser.setUserId(param.getProject().getApproveUserId());
                    approveUser.setUserName(param.getProject().getApproveUserName());
                    approveUser.setFullName(param.getProject().getApproveFullName());
                    approveUserService.modifyApproveUser(approveUser);
                }
            }
        }

        //给定默认配置
        if(Objects.isNull(po.getProject().getProcessConfigId())) {
            processConfigService.generateDefaultProcessConfig(po.getProject().getProjectId());
        }

        //更新节点状态
        processConfigService.updateNodeStatus(po.getProject().getProjectId(), SouProcessNodeEnum.projectInfo, Enable.Y, param.getTempSave());
    }
}
