package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.createsou;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.ExtRbacClient;
import com.midea.cloud.srm.feign.InviteTendersExtClient;
import com.midea.cloud.srm.feign.PjProjectBidExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.BidDataSubmitDetails;
import com.midea.cloud.srm.model.sou.req.BidDataSubmitEvaluator;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCreateSouVO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.SouProjectDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.IPrSouRequirementCreateSouPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.PrSouRequirementCreateSouContext;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitQueryService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 创建寻源招标单插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class BidPrSouRequirementCreateSouPlugin implements IPrSouRequirementCreateSouPlugin {

    @Autowired
    private MqlPrRequirementInitQueryService mqlPrRequirementInitQueryService;
    @Autowired
    private QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private InviteTendersExtClient inviteTendersExtClient;

    @Autowired
    private PjProjectBidExtClient pjProjectBidExtClient;

    @Autowired
    private ExtRbacClient extRbacClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;


    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCreateSouContext judgeCreateSouAuth(PrSouRequirementCreateSouContext context) {
        context = SdkPluginProxy.callSuper(IPrSouRequirementCreateSouPlugin.class, context, this).judgeCreateSouAuth(context);
        Map<Long/* requirementHeadId */, PrRequirementHead> prHeadMap = context.getPrHeadList().stream().collect(Collectors.toMap(PrRequirementHead::getRequirementHeadId, Function.identity()));
        for (ExtPrSouRequirementHead prSouHead : context.getSouPrHeadList()) {
            PrRequirementHead prHead = prHeadMap.get(prSouHead.getRequirementHeadId());
            if (!Enable.Y.equals(prSouHead.getHasSendSouProfile())) {
                throw new IllegalArgumentException("招标计划[" + prHead.getRequirementHeadNum() + "]尚未提交招标资料");
            }
            AssertUtils.isTrue(Enable.Y.equals(prSouHead.getHasAssigned()), "招标计划[{0}]未分配完成，不能创建标书", prHead.getRequirementHeadNum());
        }

        return context;
    }

    /**
     * 前置处理(构造数据)
     * @param context 参数
     * @return
     */
    @Override
    @ApiOperation("前置处理(构造数据)")
    public PrSouRequirementCreateSouContext beforeCreateSou(PrSouRequirementCreateSouContext context) {
        Set<Long> requirementHeadIds = context.getSouPrHeadList().stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toSet());
        // 1: 查询招标计划信息集合
        List<ExtPrSouRequirementHeadVO> souPrHeadList = new ArrayList<>(context.getPrHeadList().size());
        {
            souPrHeadList = SouObjectXUtil.convertList(context.getPrHeadList(), ExtPrSouRequirementHeadVO.class);

            Map<Long/* requirementHeadId */, ExtPrSouRequirementHead> souPrHeadMap = context.getSouPrHeadList().stream()
                    .collect(Collectors.toMap(ExtPrSouRequirementHead::getRequirementHeadId, Function.identity()));
            souPrHeadList.forEach(pr -> pr.setSouReqHead(souPrHeadMap.get(pr.getRequirementHeadId())));

            // 1.1: 查询招标计划工作小组
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementGroup>> groupMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                            .in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementGroup::getSortIndex), ExtPrSouRequirementGroup.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementGroup::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouGroupList(groupMap.get(pr.getRequirementHeadId())));
            // 1.2: 查询招标计划推荐供应商
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementVendor>> vendorMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                            .in(ExtPrSouRequirementVendor::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementVendor::getSortIndex), ExtPrSouRequirementVendor.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementVendor::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouVendorList(vendorMap.get(pr.getRequirementHeadId())));
            // 1.3: 查询招标计划附件
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementAttach>> attachMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                            .in(ExtPrSouRequirementAttach::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementAttach::getSortIndex), ExtPrSouRequirementAttach.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementAttach::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouAttachList(attachMap.get(pr.getRequirementHeadId())));
        }
        // 加个defaultIfNull判断
        souPrHeadList = souPrHeadList.stream().sorted((o1, o2)
                        -> org.apache.commons.lang3.ObjectUtils.defaultIfNull(o1.getSouReqHead().getTotalAmountByTenKilo(), BigDecimal.ZERO)
                        .compareTo(org.apache.commons.lang3.ObjectUtils.defaultIfNull(o2.getSouReqHead().getTotalAmountByTenKilo(), BigDecimal.ZERO)))
                .collect(Collectors.toList());
        // 2: 根据详情信息，构造一个推荐供应商单dto
        ApiExtSouProjectInfoDTO apiExtSouProjectInfoDTO = new ApiExtSouProjectInfoDTO();
        //推荐供应商基本信息
        ExtSouProjectDto project = new ExtSouProjectDto();
        // 招标工作小组
        List<ExtSouGroup> extSouGroups = new ArrayList<>();
        //默认值
        extSouGroups.addAll(defaultGroup());
        // 招标资料
        List<ExtSouFile> extSouFileList = new ArrayList<>();

        List<String> requirementHeadNumList = souPrHeadList.stream().map(ExtPrSouRequirementHeadVO::getRequirementHeadNum).distinct().collect(Collectors.toList());
        //查询招标资料递交主表,取已审批通过的
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("SubmitBuyer")
                .in(BidDataSubmit::getRequirementHeadNum, requirementHeadNumList)
                .eq(BidDataSubmit::getStatus, "APPROVED");
        List<BidDataSubmit> records = qlOpenClient.query(ContextPath.SOU, wrapper, BidDataSubmit.class);
        if (CollectionUtils.isEmpty(records)) {
            throw new IllegalArgumentException("所勾选的行存在未提交招标资料，不允许创建标书");
        }
        List<String> dataSubmiteRequirementNumList = records.stream().map(r -> r.getRequirementHeadNum()).distinct().collect(Collectors.toList());
        if(Integer.compare(dataSubmiteRequirementNumList.size(), requirementHeadNumList.size()) != 0) {
            throw new BaseException(MessageFormat.format("存在以下申请单未完成招标资料提交：{0}", requirementHeadNumList.stream().filter(a -> !dataSubmiteRequirementNumList.contains(a)).collect(Collectors.joining(";"))));
        }
        Map<String, Integer> requirementNumIndex = new HashMap<>(50);
        AtomicInteger index = new AtomicInteger(0);
        requirementHeadNumList.stream().forEach(r -> requirementNumIndex.put(r, index.getAndAdd(1)));
        //按采购申请单排序
        records = records.stream().sorted(Comparator.comparingInt(r -> requirementNumIndex.getOrDefault(r.getRequirementHeadNum(), 0))).collect(Collectors.toList());
        BidDataSubmit dataSubmit = records.get(records.size() -1);
        for (ExtPrSouRequirementHeadVO souPrHeadVO : souPrHeadList) {
            //寻源单名称,申请单号带出,可修改
            project.setSouName(souPrHeadVO.getSouReqHead().getProjectName());
            //板块，取申请单
            project.setExtOrgBuName(souPrHeadVO.getSouReqHead().getOrgBuName());
            project.setExtOrgBuCode(souPrHeadVO.getSouReqHead().getOrgBuCode());
            project.setExtOrgBuId(souPrHeadVO.getSouReqHead().getOrgBuId());
            //公司名称,取申请单
            project.setExtOrgOuName(souPrHeadVO.getOrgName());
            project.setExtOrgOuCode(souPrHeadVO.getOrgCode());
            project.setExtOrgOuId(souPrHeadVO.getOrgId());
            if (CollectionUtils.isNotEmpty(records)) {
                //招标流程,取招标资料递交
                //SIMPLE，JINGJIA，STANDARD，COMPETE竞争性谈判，INQUIRY询比价招标
                project.setExtSouProcess(records.get(0).getBidFlow());
            } else {
                project.setExtSouProcess(null);
            }
            //收标方式,标准招标：先收技术后收商务;简易：同时收标;询比价招标：同时收标;竞争性谈判：同时收标
            if (!ObjectUtils.isEmpty(project.getExtSouProcess())) {
                if ("STANDARD".equals(project.getExtSouProcess())) {
                    project.setExtSouMode("TECH_THEN_BUS");
                } else {
                    project.setExtSouMode("SAME_TIME");
                }
            } else {
                project.setExtSouMode("");
            }

            //招标方式
            project.setPublishScope(StringUtils.equals(souPrHeadVO.getSouReqHead().getNeedPublic().name(), Enable.Y.name()) ? "OPEN_TENDER" : "INVITE_TENDER");
            if (project.getExtSouProcess() != null && "INQUIRY".equals(project.getExtSouProcess())) {
                //评选方式(低价/高价/综合)【评分规则】
                project.setScoreRuleType("LOW_PRICE");
            } else {
                project.setScoreRuleType("COMPOSITE_SCORE");
            }
            //投资编号,取招标资料递交
            extracted(project, records, dataSubmit, souPrHeadVO);
            apiExtSouProjectInfoDTO.setProject(project);
            // 招标工作小组
            extracted(extSouGroups, records, souPrHeadVO);
            apiExtSouProjectInfoDTO.setGroupList(extSouGroups);
            // 招标资料
            if (CollectionUtils.isNotEmpty(souPrHeadVO.getSouAttachList())) {
                for (ExtPrSouRequirementAttach extPrSouRequirementAttach : souPrHeadVO.getSouAttachList()) {
                    ExtSouFile extSouFile = new ExtSouFile();
                    extSouFile.setFileType(extPrSouRequirementAttach.getFileType());
                    extSouFile.setSouFileId(extPrSouRequirementAttach.getFileId());
                    extSouFile.setSouFileName(extPrSouRequirementAttach.getFileName());
                    extSouFileList.add(extSouFile);
                }
            }
            apiExtSouProjectInfoDTO.setBidFileList(extSouFileList);
        }
        ApiExtSouProjectInfoDTO o = apiExtSouProjectInfoDTO;
        // 将构造好的数据放到上下文，方便后续 executeCreateSou 方法中使用
        context.putX("datax", o);
        return context;
    }

    /**
     * 拆分
     * @param project 参数
     * @param records 参数
     * @param dataSubmit 参数
     * @param souPrHeadVO 参数
     */
    private void extracted(ExtSouProjectDto project, List<BidDataSubmit> records, BidDataSubmit dataSubmit, ExtPrSouRequirementHeadVO souPrHeadVO) {
        project.setExtInvestNo(records.stream().map(r -> r.getInvestNo()).distinct().collect(Collectors.joining(";")));
        //品类
        project.setExtCategoryName(souPrHeadVO.getCategoryName());
        project.setExtCategoryCode(souPrHeadVO.getCategoryCode());
        //编码
        project.setExtCategoryId(souPrHeadVO.getCategoryId());

        if(!Objects.isNull(dataSubmit)) {
            //技术负责人
            project.setExtTechPrincipal(dataSubmit.getTechPrincipal());
            project.setExtTechPrincipalTel(dataSubmit.getPhone());
        }

        //预算（万元）,累加
        if (project.getExtBudget() == null) {
            project.setExtBudget(BigDecimal.ZERO);
        }
        //预算改成取资料递交的预算
        project.setExtBudget(records.stream().map(BidDataSubmit::getTotalBudget).reduce(BigDecimal.ZERO, (a, b) -> BigDecimalUtil.add(a, b)));
        //招标类型,询比价招标：商务,其他：技术加商务
        String inquiry = "INQUIRY";
        if (project.getExtSouProcess() != null &&inquiry.equals(project.getExtSouProcess())) {
            project.setOrderType("BUSINESS");
        } else {
            project.setOrderType("TECHNOLOGY_BUSINESS");
        }
        //规模数量
        project.setExtScaleQuantity(souPrHeadVO.getSouReqHead().getRequireQuantity());
        //申请人
        project.setExtApplicant(souPrHeadVO.getApplyByNickname());
        //申请部门
        project.setExtApplicantDepart(souPrHeadVO.getCeeaDepartmentName());
        //是否指定评标人
        project.setExtAssignEvaluator(records.get(0).getIsAppointEvaluator());
        //评标总人数
        if (CollectionUtils.isNotEmpty(records)) {
            project.setExtBidEvaluatorNum(records.get(0).getBidEvaluatorNum());
        }
        //要求高级专家人数
        if (CollectionUtils.isNotEmpty(records)) {
            project.setExtAskSeniorExpertNum(records.get(0).getAskSeniorExpertNum());
        }
        //申请单号,来源单据号
        project.setSourceFromNo(souPrHeadVO.getRequirementHeadNum());
        //来源单据ID
        project.setSourceFromId(souPrHeadVO.getRequirementHeadId());
        //需求来源,来源类型
        project.setSourceFromType(souPrHeadVO.getSourceFromType());
        //合并申请单号
        if (ObjectUtils.isEmpty(project.getApplicantNo())) {
            project.setApplicantNo(souPrHeadVO.getRequirementHeadNum());
        } else {
            project.setApplicantNo(project.getApplicantNo() + ";" + souPrHeadVO.getRequirementHeadNum());
        }
        //展示联系人、办公电话、邮箱
        ExtUser extUser = extRbacClient.getByUserId(AppUserUtil.getLoginAppUser().getUserId());
        if(!Objects.isNull(extUser)) {
            project.setLinkman(extUser.getNickname());
            project.setTel(extUser.getExtOfficePhone());
            project.setEmail(extUser.getEmail());
        }
        //默认开启保证金
        project.setExtEarnestFlag(YesOrNo.YES.getValue());
    }

    /**
     * 招标工作小组
     * @param extSouGroups 参数
     * @param records 参数
     * @param souPrHeadVO 参数
     */
    private void extracted(List<ExtSouGroup> extSouGroups, List<BidDataSubmit> records, ExtPrSouRequirementHeadVO souPrHeadVO) {
        if (CollectionUtils.isNotEmpty(records)) {
            //查询招标资料递交明细
            QlOpenQueryWrapper wrapperList = QlOpenWrappers.query("SubmitEvaluatorBuyer")
                    .eq(BidDataSubmitEvaluator::getDataSubmitId, records.get(0).getDataSubmitId());
            List<BidDataSubmitEvaluator> recordsList = qlOpenClient.query(ContextPath.SOU, wrapperList, BidDataSubmitEvaluator.class);

            for (int i = 0; i < recordsList.size(); i++) {
                ExtSouGroup extSouGroup = new ExtSouGroup();

                //包名--保存时需要转换成包1、包2...
                extSouGroup.setExtPackageName(souPrHeadVO.getRequirementHeadNum());
                //工号
                extSouGroup.setUserName(recordsList.get(i).getCeeaEmpNo());
                extSouGroup.setUserId(recordsList.get(i).getUserId());
                //成员姓名
                extSouGroup.setFullName(recordsList.get(i).getEvaluatorName());
                //电话
                extSouGroup.setPhone(recordsList.get(i).getPhone());
                //电子邮箱
                extSouGroup.setEmail(recordsList.get(i).getEmail());
                //岗位,取人员表
                extSouGroup.setPosition(null);
                //角色
                extSouGroup.setGroupRole(recordsList.get(i).getEvaluatorRole());
                extSouGroup.setOperateAuth(null);
                //专家等级
                extSouGroup.setExtExpertLevel(recordsList.get(i).getExpertLevel());
                //评分权限
                extSouGroup.setScoreAuth("SOU_TECH");

                //评标组长 的 操作权限是技术开标
                if ("LEADER".equals(recordsList.get(i).getEvaluatorRole())) {
                    extSouGroup.setOperateAuth("SOU_TECH_OPEN");
                }

                extSouGroups.add(extSouGroup);
            }
        }
    }

    private List<ExtSouGroup> defaultGroup() {
        List<ExtSouGroup> defaultGroupList = new ArrayList<>();
        ExtSouGroup extSouGroup1 = new ExtSouGroup();
        ExtSouGroup extSouGroup2 = new ExtSouGroup();
        defaultGroupList.add(extSouGroup1);
        defaultGroupList.add(extSouGroup2);

        //成员账号
        extSouGroup1.setUserName(AppUserUtil.getLoginAppUser().getUsername());
        extSouGroup1.setUserId(AppUserUtil.getLoginAppUser().getUserId());
        extSouGroup1.setFullName(AppUserUtil.getLoginAppUser().getNickname());
        //电话
        extSouGroup1.setPhone("");
        //电子邮箱
        extSouGroup1.setEmail("");
        //角色 为招标负责人
        extSouGroup1.setGroupRole(ExtSouGroupRoleEnum.PRINCIPAL.getCode());
        //操作权限为 技术开标&商务开标
        extSouGroup1.setOperateAuth("SOU_TECH_BUSINESS");
        //岗位,取人员表
        extSouGroup1.setPosition(null);


        // 角色为 招标组织负责人 就是招标部长 人员没有
        extSouGroup2.setGroupRole(ExtSouGroupRoleEnum.MINISTER.getCode());
        // 操作权限为商务开标
        extSouGroup2.setOperateAuth("SOU_BUSINESS_OPEN");
        // 岗位为招标部长
        extSouGroup2.setPosition(null);

        return defaultGroupList;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCreateSouContext executeCreateSou(PrSouRequirementCreateSouContext context) {
        // 从上下文中拿出构造好的数据
        ApiExtSouProjectInfoDTO o = context.getX("datax");
        // TODO: 调用寻源接口创建招标单
        Long projectId = inviteTendersExtClient.editProjectInfo(o);
        if (!ObjectUtils.isEmpty(projectId)) {
            ApiExtSouProjectInfoDTO bidInfo = inviteTendersExtClient.getProjectInfo(projectId);
            // 将创建的招标单信息放到上下文
            context.putX("bidInfo", bidInfo);
        } else {
            // 将创建的招标单信息放到上下文
            context.putX("bidInfo", null);
        }

        return context;
    }

    @Override
    @ApiOperation("前置处理(构造数据)")
    public PrSouRequirementCreateSouContext beforeCreateBidSou(PrSouRequirementCreateSouContext context) {
        Set<Long> requirementHeadIds = context.getSouPrHeadList().stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toSet());
        // 1: 查询招标计划信息集合
        List<ExtPrSouRequirementHeadVO> souPrHeadList = new ArrayList<>(context.getPrHeadList().size());
        {
            souPrHeadList = SouObjectXUtil.convertList(context.getPrHeadList(), ExtPrSouRequirementHeadVO.class);

            Map<Long/* requirementHeadId */, ExtPrSouRequirementHead> souPrHeadMap = context.getSouPrHeadList().stream()
                    .collect(Collectors.toMap(ExtPrSouRequirementHead::getRequirementHeadId, Function.identity()));
            souPrHeadList.forEach(pr -> pr.setSouReqHead(souPrHeadMap.get(pr.getRequirementHeadId())));

            // 1.1: 查询招标计划工作小组
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementGroup>> groupMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                            .in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementGroup::getSortIndex), ExtPrSouRequirementGroup.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementGroup::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouGroupList(groupMap.get(pr.getRequirementHeadId())));
            // 1.2: 查询招标计划推荐供应商
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementVendor>> vendorMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                            .in(ExtPrSouRequirementVendor::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementVendor::getSortIndex), ExtPrSouRequirementVendor.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementVendor::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouVendorList(vendorMap.get(pr.getRequirementHeadId())));
            // 1.3: 查询招标计划附件
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementAttach>> attachMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                            .in(ExtPrSouRequirementAttach::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementAttach::getSortIndex), ExtPrSouRequirementAttach.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementAttach::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouAttachList(attachMap.get(pr.getRequirementHeadId())));
        }

        //根据概算金额倒序，循环追加保存，最后一个就是最大金额，取最大金额招标计划信息，将多个需求单号的预算相加
       /* souPrHeadList = souPrHeadList.stream().sorted((o1, o2)
                        -> o1.getSouReqHead().getTotalAmountByTenKilo()
                        .compareTo(o2.getSouReqHead().getAfterTotalAmountByTenKilo()))
                .collect(Collectors.toList());*/

        // 加个defaultIfNull判断
        souPrHeadList = souPrHeadList.stream().sorted((o1, o2)
                        -> org.apache.commons.lang3.ObjectUtils.defaultIfNull(o1.getSouReqHead().getTotalAmountByTenKilo(), BigDecimal.ZERO)
                        .compareTo(org.apache.commons.lang3.ObjectUtils.defaultIfNull(o2.getSouReqHead().getAfterTotalAmountByTenKilo(), BigDecimal.ZERO)))
                .collect(Collectors.toList());

        // 2: 根据详情信息，构造一个竞价单dto
        SouProject projectInfo = new SouProject();
        // 2.1: 物料需求DTO
        List<SouItem> listSouItemInfo = new ArrayList<>();
        // 2.1: 供应商DTO
        List<SouVendor> listSouVendorInfo = new ArrayList<>();

        Map<String, Object> bidInfoMap = new HashMap<>(50);
        //查询招标资料递交主表,取已审批通过的
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("SubmitBuyer")
                .eq(BidDataSubmit::getRequirementHeadNum, souPrHeadList.get(souPrHeadList.size() - 1).getRequirementHeadNum())
                .eq(BidDataSubmit::getStatus, "APPROVED");
        List<BidDataSubmit> records = qlOpenClient.query(ContextPath.SOU, wrapper, BidDataSubmit.class);

        projectInfo.setSouNo(souPrHeadList.get(0).getSouReqHead().getSouNo());
        {
            // 寻源单名称
            String souName = "需求池转竞价_" + System.currentTimeMillis();
            projectInfo.setProjectId(IdGenrator.generate());
           // projectInfo.setSouName(souName);
            projectInfo.setSouName(souPrHeadList.get(0).getSouReqHead().getSouName());
            // 来源类型
            projectInfo.setSourceFromType(SouSourceFromTypeEnum.SOU_REQ.name());
            // 来源单据ID
            projectInfo.setSourceFromId(souPrHeadList.get(0).getRequirementHeadId());
            // 来源单据号
            projectInfo.setSourceFromNo(souPrHeadList.get(0).getRequirementHeadNum());
        }
        bidInfoMap.put("projectInfo", projectInfo);
        //推荐供应商ID
        Long recommVendorId = inviteTendersExtClient.getDemandByAppNo(context.getPrHeadList().get(0).getRequirementHeadNum());
        //推荐供应商
        List<ExtSouVendor> souVendorList = inviteTendersExtClient.getRecommVendorInfoByProjectId(recommVendorId);

        for (ExtSouVendor extPrSouRequirementVendor : souVendorList) {
            SouVendor vendorInfo = new SouVendor();
            vendorInfo.setEmail(extPrSouRequirementVendor.getEmail());
            vendorInfo.setPhone(extPrSouRequirementVendor.getPhone());
            vendorInfo.setLinkmanName(extPrSouRequirementVendor.getLinkmanName());
            vendorInfo.setVendorId(extPrSouRequirementVendor.getVendorId());
            vendorInfo.setVendorCode(extPrSouRequirementVendor.getVendorCode());
            vendorInfo.setVendorName(extPrSouRequirementVendor.getVendorName());
            vendorInfo.setProjectId(projectInfo.getProjectId());
            vendorInfo.setSouVendorId(IdGenrator.generate());
            listSouVendorInfo.add(vendorInfo);
        }
        bidInfoMap.put("vendorInfo", listSouVendorInfo);
        //物料
        if (CollectionUtils.isNotEmpty(records)) {
            if (!SouConstant.JI_NG_JIA.equals(records.get(0).getBidFlow())) {
                throw new IllegalArgumentException("请选择竞价单据");
            }
            //查询招标资料递交明细
            QlOpenQueryWrapper wrapperList = QlOpenWrappers.query("SubmitDetailsBuyer")
                    .eq(BidDataSubmitDetails::getDataSubmitId, records.get(0).getDataSubmitId());
            List<BidDataSubmitDetails> recordsList = qlOpenClient.query(ContextPath.SOU, wrapperList, BidDataSubmitDetails.class);

            for (BidDataSubmitDetails bidDataSubmitDetails : recordsList) {
                SouItem souItem = new SouItem();
                souItem.setStartPrice(bidDataSubmitDetails.getStartBidPrice());
                souItem.setEchelonPrice(bidDataSubmitDetails.getEchelonBidPrice());
                souItem.setItemDesc(bidDataSubmitDetails.getMaterialName());
                souItem.setItemGroup(bidDataSubmitDetails.getCombination());
                souItem.setMeteringUnit(bidDataSubmitDetails.getAffiliatedUnit());
                souItem.setMonthlyProduction(bidDataSubmitDetails.getMonthProduction());
                souItem.setPerformanceBond(bidDataSubmitDetails.getPerformDeposit());
                souItem.setMeteringUnit(bidDataSubmitDetails.getMeteringUnit());
                souItem.setProjectId(projectInfo.getProjectId());
                souItem.setSouItemId(IdGenrator.generate());
                souItem.setAffiliatedUnit(bidDataSubmitDetails.getAffiliatedUnit());
                souItem.setAdvanceCharge(bidDataSubmitDetails.getAdvanceAmount());
                listSouItemInfo.add(souItem);
            }
            bidInfoMap.put("souItemInfo", listSouItemInfo);
        }
        // 将构造好的数据放到上下文，方便后续 executeCreateSou 方法中使用
        context.putX("datax", bidInfoMap);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCreateSouContext executeCreateBidSou(PrSouRequirementCreateSouContext context) {
        // 从上下文中拿出构造好的数据
        Map<String, Object> o = context.getX("datax");
        SouProjectDTO projectInfo = SouObjectXUtil.convertTargetObj(o.get("projectInfo"),SouProjectDTO.class);
        String userName;
        if(CollectionUtils.isNotEmpty(context.getPrHeadList())){
            userName = context.getPrHeadList().get(0).getCreatedBy();
            //TODO: 调用Hr接口获取板块公司部门
            HrUserOrgnizationDto hruserOrgDto = pjProjectExtClient.getHrUserOrgnizationByUsername(userName);
            projectInfo.setOrgBuId(hruserOrgDto.getBuOrganization().getOrganizationId());
            projectInfo.setOrgBuCode(hruserOrgDto.getBuOrganization().getOrganizationCode());
            projectInfo.setOrgBuNAME(hruserOrgDto.getBuOrganization().getOrganizationName());
            projectInfo.setCompanyId(hruserOrgDto.getOuOrganization().getOrganizationId());
            projectInfo.setCompanyCode(hruserOrgDto.getOuOrganization().getOrganizationCode());
            projectInfo.setCompanyName(hruserOrgDto.getOuOrganization().getOrganizationName());
            projectInfo.setDepId(hruserOrgDto.getDepartmentOrganization().getOrganizationId());
            projectInfo.setDepCode(hruserOrgDto.getDepartmentOrganization().getOrganizationCode());
            projectInfo.setDepName(hruserOrgDto.getDepartmentOrganization().getOrganizationName());
            o.put("projectInfo",projectInfo);
        }
        // TODO: 调用寻源接口创建招标单
        projectInfo = SouObjectXUtil.convertTargetObj(pjProjectBidExtClient.editSouBidInfo(o),SouProjectDTO.class);
        // 将创建的招标单信息放到上下文
        context.putX("bidInfo", projectInfo);
        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public PrSouRequirementCreateSouContext afterCreateSou(PrSouRequirementCreateSouContext context) {
        // 1: 回写招标计划信息
        ApiExtSouProjectInfoDTO bidInfo = (ApiExtSouProjectInfoDTO) context.getX("bidInfo");
        if (bidInfo != null) {
            qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                    .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.PROJECT)
                    .set(ExtPrSouRequirementHead::getHasCreateSou, Enable.Y)
                    .set(ExtPrSouRequirementHead::getSouType, context.getParam().getSouType())
                    .set(ExtPrSouRequirementHead::getSouProjectId, bidInfo.getProject().getProjectId())
                    .set(ExtPrSouRequirementHead::getSouNo, bidInfo.getProject().getSouNo())
                    .set(ExtPrSouRequirementHead::getSouName, bidInfo.getProject().getSouName())
                    .in(ExtPrSouRequirementHead::getRequirementHeadId, context.getParam().getReqHeadList().stream().map(ExtPrSouRequirementHeadDTO::getRequirementHeadId).collect(Collectors.toList())));
        }
        ExtPrSouRequirementCreateSouVO resultVO = new ExtPrSouRequirementCreateSouVO();
        {
            resultVO.setSouType(SouTypeEnum.bid.name());
            resultVO.setSouVO(bidInfo);
        }
        context.setResult(resultVO);

        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public PrSouRequirementCreateSouContext afterCreateBidSou(PrSouRequirementCreateSouContext context) {
        // 1: 回写招标计划信息
        SouProject bidInfo = null;
        Object bidObject = context.getX("bidInfo");

        if (bidObject != null) {

            bidInfo = JSON.parseObject(JSON.toJSONString(bidObject), SouProject.class);

            qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                    .set(ExtPrSouRequirementHead::getHasCreateSou, Enable.Y)
                    .set(ExtPrSouRequirementHead::getSouType, context.getParam().getSouType())
                    .set(ExtPrSouRequirementHead::getSouProjectId, bidInfo.getProjectId())
                    .set(ExtPrSouRequirementHead::getSouNo, bidInfo.getSouNo())
                    .set(ExtPrSouRequirementHead::getSouName, bidInfo.getSouName())
                    .eq(ExtPrSouRequirementHead::getRequirementHeadId, context.getParam().getReqHeadList().get(0).getRequirementHeadId()));
        }
        ExtPrSouRequirementCreateSouVO resultVO = new ExtPrSouRequirementCreateSouVO();
        {
            resultVO.setSouType(SouTypeEnum.bid.name());
            resultVO.setSouVO(bidInfo);
        }
        context.setResult(resultVO);

        return context;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public String matchScene() {
        return SouTypeEnum.bid.name();
    }

}
