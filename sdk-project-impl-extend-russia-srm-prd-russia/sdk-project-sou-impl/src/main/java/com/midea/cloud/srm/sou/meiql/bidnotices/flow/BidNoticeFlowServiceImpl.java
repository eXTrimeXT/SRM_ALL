package com.midea.cloud.srm.sou.meiql.bidnotices.flow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.constant.DingTalkConstant;
import com.midea.cloud.common.dingtalks.DingTalkClient;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.SignTypeEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementSpecialSouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementBidResult;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitEventService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class BidNoticeFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private QlService qlService;

    @Autowired
    private BidNoticeService bidNoticeService;

    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Value("${bpm.ZLBTZ.processGroupId}")
    private String processGroupId;
    @Value("${bpm.ZLBTZ.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    public BaseClient baseClient;

    @Autowired
    public BaseExtClient baseExtClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IExtSouDemandService extSouDemandService;

    @Autowired
    private SupplierClient supplierClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Resource
    private ExtSouProjectMapper projectMapper;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouInviteService extSouInviteService;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    private static final String BUSINESS_TYPE = "SOU_TN";
    @Autowired
    private RedisUtil redisUtil;

    @Value("${dingtalk.specSou.receiver:GW00077028}")
    private String specSouChargeUser;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(BidNoticeDTO::getBidNoticeId,businessId);
                r.put(BidNoticeDTO::getStatus, CaStatusEnum.APPROVING.getCode());
                r.put(BidNoticeDTO::getStartBpmUsername, loginAppUser.getUsername());
                r.put(BidNoticeDTO::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            Record r = new Record();
            r.put(BidNoticeDTO::getBidNoticeId,businessId);
            r.put(BidNoticeDTO::getStatus, CaStatusEnum.APPROVING.getCode());
            qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNotice.getCode(),businessId, BidNoticeDTO.class);
        //1.修改单据状态为已审批
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.APPROVED.getCode());
        r.put(BidNoticeDTO::getPassTime, new Date());
        qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));
        //2.修改招标单据状态
        if (null != bidNoticeDTO.getProjectId() && SouTypeEnum.bid.name().equals(bidNoticeDTO.getSouType())) {
            extSouInitEventService.updateSouBidStatus(bidNoticeDTO.getProjectId(),SouBiddingProStatusEnum.ARCHIVE_TODO);
        }

        ExtSouProject extSouProject = projectMapper.selectById(bidNoticeDTO.getProjectId());

        String now = new SimpleDateFormat("yyyy 年 M 月 d 日").format(new Date());

        //判断 如果是集团招标负责人角色，则盖章，否则不盖章
        Boolean upperHundredThousandAsBid = ExtSouBidComponent.getInstance().checkIsGroupBidding(bidNoticeDTO.getCreatedId());

        //3.中落标PDF盖章
        List<Record> details = qlService.queryByWrapper(QlWrappers
                .query(TypeEnum.BidNoticeDetail.getCode())
                .select(BidNoticeDetailDTO::getBidNoticeDetailId,
                        BidNoticeDetailDTO::getNoticeAttachmentId,
                        BidNoticeDetailDTO::getNoticeAttachmentName,
                        BidNoticeDetailDTO::getVendorId,
                        BidNoticeDetailDTO::getIsWin)
                .eq(BidNoticeDetailDTO::getBidNoticeId,businessId),Record.class);
        this.signVendor(details, bidNoticeDTO.getExtOrgBuCode(),now, upperHundredThousandAsBid);

        //4.内部通知文件签字, 满足10万以上且在招标范围内，走自动盖章逻辑
        List<Record> internalDetails = qlService.queryByWrapper(QlWrappers
                .query(TypeEnum.BidNoticeInternal.getCode())
                .select(BidNoticeInternalDTO::getInternalId,
                        BidNoticeInternalDTO::getAttachmentId,
                        BidNoticeInternalDTO::getAttachmentName)
                .eq(BidNoticeInternalDTO::getBidNoticeId,businessId),Record.class);
        if(upperHundredThousandAsBid) {
            this.signInner(internalDetails,bidNoticeDTO.getExtOrgBuCode(),now);
        }


        // 5: 将中标供应商信息回写招标计划用于展示
        this.callBackBidPlan(bidNoticeDTO);

        //提取中标供应商ID
        List<Long> ids = details.stream().filter(record -> YesOrNo.YES.getValue().equals(record.get(BidNoticeDetailDTO::getIsWin)))
                .map(record -> record.get(BidNoticeDetailDTO::getVendorId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            //6.修改对应的中标供应商为正式供应商 CompanyInfo.PJ_COMPANY_STATUS=OFFICIAL_SUPPLIER
            this.updateCompanyInfo(ids);
            //7.修改对应的供应商品类库(供应商+单位+品类)为合格 OrgCategory.SERVICE_STATUS=QUALIFIED
            this.updateCategory(ids,extSouProject.getExtCategoryId(),bidNoticeDTO.getExtOrgOuId());
        }

        if(!Objects.isNull(bidNoticeDTO.getProjectId())) {
            planService.applyAtualPoint(bidNoticeDTO.getProjectId(), new Date(), ExtSouPlan::getPublishWinLossTime);
        }
        //创建合同
        this.createContract(bidNoticeDTO, internalDetails);

        //钉钉消息通知
        this.dingTalkAsWin(extSouProject, ids);

        /** 更新邀请供应商中标情况 */
        updateInviteVendorSccussBid(extSouProject, details);

    }

    private void updateInviteVendorSccussBid(ExtSouProject extSouProject, List<Record> details) {
        if(CollectionUtils.isEmpty(details)) {
            return;
        }
        List<SouInviteItem> souInviteItems = new ArrayList<>(details.size());
        details.stream().forEach(detail -> {
            SouInviteItem souInviteItem = new SouInviteItem();
            souInviteItem.setVendorId(detail.get(BidNoticeDetailDTO::getVendorId));
            souInviteItem.setIsSuccBid(YesOrNo.YES.getValue().equals(detail.get(BidNoticeDetailDTO::getIsWin)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            souInviteItems.add(souInviteItem);
        });
        extSouInviteService.updateIsSuccBidBatch(extSouProject, souInviteItems);
    }

    /**
     * 时间紧急特殊招标， 后续实际执行时间（发标到中标结果）超出申请时间，预警给特殊招标负责人
     * BUG2024010400051
     * @param extSouProject
     */
    private void specBidLeaveTimeRemindWithDingTalk(ExtSouProject extSouProject){
        if(Objects.isNull(extSouProject)){
            log.info("项目不能为空");
            return;
        }
        List<ExtSouPlan> plans  = planService.lambdaQuery()
                .eq(ExtSouPlan::getProjectId,extSouProject.getProjectId())
                .eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.ACTUAL.getCode()).list();
        if(CollUtil.isEmpty(plans)){
            log.info(MessageFormat.format("找不到相应的招标计划 项目:{0}",extSouProject));
            return;
        }
        //发标时间
        Date publishTime = plans.get(0).getPublishTime();
        //审批通过时间
        Date now = new Date();

        Set<String> requirementHeadNums = extSouDemandService.lambdaQuery().eq(ExtSouDemand::getProjectId, extSouProject.getProjectId()).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO)
                .list().stream().map(ExtSouDemand::getApplicantNo).filter(Objects::nonNull).collect(Collectors.toSet());

        List<RecordDTO> dtoList = qlOpenClient.query(ContextPath.SUP_CE,
                QlOpenWrappers.query(PrRequirementHead.class)
                .in(PrRequirementHead::getRequirementHeadNum, new ArrayList<>(requirementHeadNums)));

        List<Long> headIds = dtoList.stream().map(e->e.get(PrRequirementHead::getRequirementHeadId)).collect(Collectors.toList());
        //时间紧急的寻源需求
        List<RecordDTO> souRequireData = qlOpenClient.query(ContextPath.SUP_CE,
                QlOpenWrappers.query(ExtPrSouRequirementHead.class)
                        .in(ExtPrSouRequirementHead::getRequirementHeadId,headIds)
                        .eq(ExtPrSouRequirementHead::getSpecialSouType, PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.toString())
        );
        if(CollUtil.isEmpty(souRequireData)){
            log.info(MessageFormat.format("时间紧急的申请单不存 项目信息-{0}",extSouProject.getSouNo()));
            return;
        }
        //获取最少剩余日期
        Optional<BigDecimal> optionalRemainingDay = souRequireData.stream().filter(e-> ObjectUtil.isNotEmpty(e.get(ExtPrSouRequirementHead::getRemainingDay))).map(e->e.get(ExtPrSouRequirementHead::getRemainingDay))
                    .min((a,b)->a.subtract(b).intValue());


        Optional<RecordDTO> passTimeOp = souRequireData.stream().filter(e -> ObjectUtils.allNotNull(e.get(ExtPrSouRequirementHead::getApprovalPassTime)))
                .min(new Comparator<RecordDTO>() {
                    @Override
                    public int compare(RecordDTO o1, RecordDTO o2) {
                        Long passTime1 = o1.get(ExtPrSouRequirementHead::getApprovalPassTime).getTime();
                        Long passTime2 = o2.get(ExtPrSouRequirementHead::getApprovalPassTime).getTime();
                        return passTime1.compareTo(passTime2);
                    }
                });

        if(optionalRemainingDay.isPresent() && passTimeOp.isPresent()) {
            Date passTime = passTimeOp.get().get(ExtPrSouRequirementHead::getApprovalPassTime);

            //求相差天数
            long diff1= (now.getTime() - passTime.getTime())/(24*60*60*1000);
            BigDecimal remainDay = optionalRemainingDay.get();
            BigDecimal actualBetweenDay = new BigDecimal(diff1);
            if(actualBetweenDay.compareTo(remainDay)>0){
                Map<String,String> params = makeSpecSouEmergency(extSouProject,remainDay.setScale(0));
                DingTalkClient.newInstance(baseClient,pjProjectExtClient).sendDingTalk(Collections.singletonList(specSouChargeUser),DingTalkConstant.SPECIAL_SOU_EMERGENCY,params);
            }
        } else {
            log.info(MessageFormat.format("时间紧急的申请单没有剩余日期或需求提报没有审批通过时间 项目{0}",extSouProject));
        }

    }

    private Map<String, String> makeSpecSouEmergency(ExtSouProject extSouProject, BigDecimal actualBetweenDay) {
        Map<String,String> params = new HashMap<>(16);
        params.put("projectNo",extSouProject.getExtProjectNo());
        params.put("souName",extSouProject.getSouName());
        params.put("overDay",actualBetweenDay.toString());
        return params;
    }

    /**
     * 创建合同
     * @param extSouProject
     * @param vendorIdList
     */
    private void dingTalkAsWin(ExtSouProject extSouProject, List<Long> vendorIdList) {
        if(ObjectUtils.anyNull(extSouProject, vendorIdList) || CollectionUtils.isEmpty(vendorIdList)) {
            log.info(MessageFormat.format("dingTalkAsWin data is null: 项目信息-{0}", Objects.isNull(extSouProject)?"空":extSouProject.getSouNo()));
            return;
        }
        List<Map<String, Object>> statisticalBidTimesMapList = projectService.statisticalBidTimes(vendorIdList, extSouProject.getExtCategoryId());
        log.info(MessageFormat.format("dingTalkAsWin statisticalBidTimesMap: 项目信息-{0}, 统计信息-{1}", extSouProject.getSouNo(), JSON.toJSONString(statisticalBidTimesMapList)));

        /** 限定投标次数 */
        Integer limitTimes = 5;
        /** 限定中标率 */
        BigDecimal limitWinRate = new BigDecimal(60);

        /** 提醒的供应商名单 */
        List<String> vendorNameList = new ArrayList<>();

        for(Map<String, Object> bidTimesMap : statisticalBidTimesMapList) {
            /** 投标次数 */
            Integer bidTimes = MapUtils.getInteger(bidTimesMap, "bidTimes", 0);
            /** 中标次数 */
            Integer winTimes = MapUtils.getInteger(bidTimesMap, "winTimes", 0);
            /** 供应商名字 */
            String vendorName = MapUtils.getString(bidTimesMap, "vendorName");

            /** 中落标审批通过后，查询中标供应商在招标基础数据中该品类的投标次数，若大于等于5或者中标率≥60%（中标次数/已投标次数）*100% */
            BigDecimal winRate = new BigDecimal(winTimes).divide(new BigDecimal(bidTimes), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            if(Integer.compare(bidTimes, limitTimes) >= 0 || winRate.compareTo(limitWinRate) >= 0) {
                log.info(MessageFormat.format("dingTalkAsWin statisticalBidTimesMap: 项目信息-{0}, 供应商满足投标次数大于等于5或者中标率≥60%-{1}", extSouProject.getSouNo(), vendorName));
                vendorNameList.add(vendorName);
            }
        }

        if(CollectionUtils.isEmpty(vendorNameList)) {
            return;
        }

        /** 品类[${extCategoryName}]以下供应商投标次数大于等于5次或者中标率≥60%: {vendorNameList}*/
        Map<String, String> var = new HashMap<>(16);
        var.put("${extCategoryName}", extSouProject.getExtCategoryName());
        var.put("${vendorNameList}", vendorNameList.stream().collect(Collectors.joining("、")));

        /** 通知人：品类供应商负责人 ceea_pr_division_category */
        List<RecordDTO> divisionCategory = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.DIVISION_CATEGORY)
                .eq(DivisionCategory::getCategoryId, extSouProject.getExtCategoryId())
                .eq(DivisionCategory::getIfMainPerson, YesOrNo.YES.getValue())
                .eq(DivisionCategory::getDuty, SrmConstant.DIVISION_CATEGORY_DUTY_SUPPLIER_LEADER));

        if(CollectionUtils.isEmpty(divisionCategory)) {
            log.info(MessageFormat.format("dingTalkAsWin statisticalBidTimesMap: 项目信息-{0}, 供应商满足投标次数大于等于5或者中标率≥60%, 找不到品类的供应商负责人信息", extSouProject.getSouNo()));
            return;
        }

        LocalDate currentDate = LocalDate.now();
        List<String> personInChargeUsernameList = divisionCategory.stream().filter(r -> Objects.isNull(formateLocalDate(r, ExtSouBidComponent.fieldName(DivisionCategory::getStartDate))) || formateLocalDate(r, ExtSouBidComponent.fieldName(DivisionCategory::getStartDate)).isBefore(currentDate))
                .filter(r -> Objects.isNull(formateLocalDate(r, ExtSouBidComponent.fieldName(DivisionCategory::getEndDate))) || formateLocalDate(r, ExtSouBidComponent.fieldName(DivisionCategory::getEndDate)).isAfter(currentDate)).map(r -> r.get(DivisionCategory::getPersonInChargeUsername)).distinct().collect(Collectors.toList());

        if(CollectionUtils.isEmpty(personInChargeUsernameList)) {
            log.info(MessageFormat.format("dingTalkAsWin statisticalBidTimesMap: 项目信息-{0}, 供应商满足投标次数大于等于5或者中标率≥60%, 找不到品类有效的供应商负责人信息", extSouProject.getSouNo()));
            return;
        }

        DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(personInChargeUsernameList, DingTalkConstant.HIGHT_LEVEL_BID_SAME_TIMES, var);
        log.info(MessageFormat.format("dingTalkAsWin statisticalBidTimesMap: 项目信息-{0}, 供应商满足投标次数大于等于5或者中标率≥60%, 已发送钉钉消息通知品类[{1}]对应的供应商负责人{2}, 供应商信息{3}", extSouProject.getSouNo(), extSouProject.getExtCategoryName(), JSON.toJSONString(personInChargeUsernameList), JSON.toJSONString(vendorNameList)));
    }

    @SneakyThrows(value = {Exception.class})
    private LocalDate formateLocalDate(RecordDTO record, String fieldName) {
        Object value = record.get(fieldName);
        if(Objects.isNull(value)) {
            return null;
        }
        log.info("formateLocalDate {0}, {1}", value, value.getClass().getSimpleName());
        if(value instanceof String) {
            return DateUtil.dateToLocalDate(DateUtil.parseDate((String) value));
        }
        if(value instanceof Date) {
            return DateUtil.dateToLocalDate((Date) value);
        }
        if(value instanceof LocalDate) {
            return (LocalDate) value;
        }
        return DateUtil.dateToLocalDate(DateUtil.parseDate((String) value));
    }

    /**
     * 创建合同
     */
    private void createContract(BidNoticeDTO bidNoticeDTO, List<Record> bidNoticeInternalRecords) {

        log.info("中落标通知审批通过自动创建合同开始" + bidNoticeDTO.getBidNoticeNo());
        if(CollectionUtils.isEmpty(bidNoticeInternalRecords)) {
            return;
        }

        ExtSouProject project = projectService.getById(bidNoticeDTO.getProjectId());

        //经办人ID
        List<String> extContractHandlerUsers = new ArrayList<>();

        List<Record> contractHeads = new ArrayList<>();

        for (Record record : bidNoticeInternalRecords) {
            record.put(BidNoticeInternalDTO::getIsSend, YesOrNo.YES.getValue());
            record.put(BidNoticeInternalDTO::getSendTime,new Date());
            //生成合同
            List<Record> subContractHeads = bidNoticeService.createContract(record.get(BidNoticeInternalDTO::getInternalId));
            if(CollectionUtils.isNotEmpty(subContractHeads)) {
                contractHeads.addAll(subContractHeads);
            }
        }

        if(ObjectUtils.allNotNull(project) && CollectionUtils.isNotEmpty(contractHeads)) {
            extContractHandlerUsers = contractHeads.stream().filter(contract -> org.apache.commons.lang3.StringUtils.isNoneBlank(contract.getString("extContractHandlerAccount"))).map(contract -> contract.getString("extContractHandlerAccount")).distinct().collect(Collectors.toList());
            DingTalkClient dingTalkClient = DingTalkClient.newInstance(baseClient, pjProjectExtClient);
            //${project.souName}（${project.souNo}）已出具中标通知，请尽快登录中标平台完成合同签订，如有疑问请联系招标负责人：${souProject.linkMan} ${souProject.tel}。
            Map<String, String> var = new HashMap<>(16);
            var.put("${project.souName}", project.getSouName());
            var.put("${project.souNo}", project.getExtProjectNo());
            var.put("${souProject.linkMan}", project.getLinkman());
            var.put("${souProject.tel}", project.getTel());
            dingTalkClient.sendDingTalk(extContractHandlerUsers, DingTalkConstant.BID_NOTICE_CONTRACT_CREATE, var);
        }

        log.info("中落标通知审批通过自动创建合同结束" + bidNoticeDTO.getBidNoticeNo());
    }

    /**
     * 将中标供应商信息回写招标计划用于展示
     * @param bidNoticeDTO
     */
    private void callBackBidPlan(BidNoticeDTO bidNoticeDTO) {
        if (CollectionUtils.isNotEmpty(bidNoticeDTO.getBidNoticeDetails())) {
            Set<String> requirementHeadNums = extSouDemandService.lambdaQuery().eq(ExtSouDemand::getProjectId, bidNoticeDTO.getProjectId()).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO)
                    .list().stream().map(ExtSouDemand::getApplicantNo).filter(Objects::nonNull).collect(Collectors.toSet());
            List<BidNoticeDetailDTO> bidNoticeDetailList = bidNoticeDTO.getBidNoticeDetails().stream()
                    .filter(e -> "是".equals(e.getIsWin()) || Enable.Y.name().equals(e.getIsWin())).collect(Collectors.toList());
            if (!requirementHeadNums.isEmpty() && !bidNoticeDetailList.isEmpty()) {
                List<RecordDTO> dtoList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(PrRequirementHead.class)
                        .in(PrRequirementHead::getRequirementHeadNum, new ArrayList<>(requirementHeadNums)));
                Set<Long> requirementHeadIds = new HashSet<>(requirementHeadNums.size()); {
                    dtoList.forEach(dto -> {
                        Long id = dto.getLong("requirementHeadId");
                        if (id != null) {
                            requirementHeadIds.add(id);
                        }
                    });
                }
                if (!requirementHeadIds.isEmpty()) {
                    List<ExtPrSouRequirementBidResult> resultList = new ArrayList<>(bidNoticeDetailList.size());

                    Map<Long/* vendorId */, List<ContactInfo>> contactInfoMap = Collections.emptyMap(); {
                        Set<Long> vendorIds = bidNoticeDetailList.stream().map(BidNoticeDetailDTO::getVendorId).filter(Objects::nonNull).collect(Collectors.toSet());
                        if (!vendorIds.isEmpty()) {
                            contactInfoMap = supplierClient.listContactInfoByParam(new ArrayList<>(vendorIds))
                                    .stream().collect(Collectors.groupingBy(ContactInfo::getCompanyId));
                        }
                    }

                    for (Long requirementHeadId : requirementHeadIds) {
                        for (BidNoticeDetailDTO dto : bidNoticeDetailList) {
                            ExtPrSouRequirementBidResult result = new ExtPrSouRequirementBidResult();
                            resultList.add(result);

                            result.setBidResultId(IdGenrator.generate());
                            result.setRequirementHeadId(requirementHeadId);
                            result.setVendorId(dto.getVendorId());
                            result.setVendorCode(dto.getVendorCode());
                            result.setVendorName(dto.getVendorName());
                            ContactInfo contactInfo = null; {
                                List<ContactInfo> contactInfoList = contactInfoMap.get(dto.getVendorId());
                                if (!contactInfoList.isEmpty()) {
                                    contactInfo = contactInfoList.stream().filter(e -> Enable.Y.name().equals(e.getCeeaDefaultContact())).findFirst().orElse(null);
                                    if (contactInfo == null) {
                                        contactInfo = contactInfoList.get(0);
                                    }
                                }
                            }
                            result.setLinkman(contactInfo != null ? contactInfo.getContactName() : null);
                            result.setPhone(contactInfo != null ? contactInfo.getCeeaContactMethod() : null);
                            result.setBidAmountByTenKilo(dto.getWinAmount());
                        }
                    }

                    qlOpenClient.create(ContextPath.SUP_CE, ExtPrSouRequirementHead.class.getSimpleName(), resultList);
                }
            }
        }
    }

    /**
     * 供应商盖章
     * @param details
     * @param extOrgBuCode
     * @param now
     * @param upperHundredThousandAsBid
     */
    private void signVendor(List<Record> details,String extOrgBuCode,String now, Boolean upperHundredThousandAsBid) {
        if (CollectionUtils.isNotEmpty(details)){
            List<Record> updateList = new ArrayList<>();
            for (Record record : details) {
                Long noticeAttachmentId = record.get(BidNoticeDetailDTO::getNoticeAttachmentId);
                String noticeAttachmentName = record.get(BidNoticeDetailDTO::getNoticeAttachmentName);
                String isWin = record.get(BidNoticeDetailDTO::getIsWin);
                Long fileId = null;
                if(YesOrNo.YES.getValue().equals(isWin)) {
                    if(upperHundredThousandAsBid) {
                        //中标盖章
                        fileId = bidNoticeService.signByType(SignTypeEnum.WIN.getCode(), extOrgBuCode
                                ,noticeAttachmentId,noticeAttachmentName,now);
                        updateList.add(record);
                    }
                } else {
                    //落标盖章
                    fileId = bidNoticeService.signByType(SignTypeEnum.LOST.getCode(), extOrgBuCode
                            ,noticeAttachmentId,noticeAttachmentName,now);
                    updateList.add(record);
                }
                record.put(BidNoticeDetailDTO::getNoticeAttachmentId,fileId);
                record.put(BidNoticeDetailDTO::getIsSign,YesOrNo.YES.getValue());
            }
            if(CollectionUtils.isNotEmpty(updateList)) {
                qlService.update(TypeEnum.BidNoticeDetail.getCode(),updateList);
            }

        }
    }

    /**
     * 内部签字
     * @param internalDetails
     * @param extOrgBuCode
     * @param now
     */
    private void signInner(List<Record> internalDetails,String extOrgBuCode,String now) {
        if (CollectionUtils.isNotEmpty(internalDetails)){
            for (Record record : internalDetails) {
                Long attachmentId = record.get(BidNoticeInternalDTO::getAttachmentId);
                String attachmentName = record.get(BidNoticeInternalDTO::getAttachmentName);
                Long fileId = bidNoticeService.signByType(SignTypeEnum.INNER.getCode(), extOrgBuCode,attachmentId,attachmentName,now);
                record.put(BidNoticeInternalDTO::getAttachmentId,fileId);
                record.put(BidNoticeInternalDTO::getIsSign,YesOrNo.YES.getValue());
            }
            qlService.update(TypeEnum.BidNoticeInternal.getCode(),internalDetails);
        }
    }

    /**
     * 修改对应的中标供应商为正式供应商
     * @param ids
     */
    private void updateCompanyInfo(List<Long> ids) {
        List<Record> list = qlOpenClient.query(ContextPath.SUP,QlOpenWrappers.query("CompanyInfo")
                .in("companyId",ids)
                .ne("pjCompanyStatus","OFFICIAL_SUPPLIER")
                .select("companyId","pjCompanyStatus")
                ,Record.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Record record : list) {
                record.put("pjCompanyStatus","OFFICIAL_SUPPLIER");
            }
            qlOpenClient.update(ContextPath.SUP,"CompanyInfo",list);
        }
    }

    /**
     * 修改对应的供应商品类库(供应商+单位+品类)为合格
     */
    private void updateCategory(List<Long> ids,Long categoryId,Long extOrgOuId) {
        List<Record> list = qlOpenClient.query(ContextPath.SUP,QlOpenWrappers.query("OrgCategory")
                        .in(OrgCategory::getCompanyId,ids)
                        .eq(OrgCategory::getOrgId,extOrgOuId)
                        .eq(OrgCategory::getCategoryId,categoryId)
                        .ne(OrgCategory::getServiceStatus,"QUALIFIED")
                        .select(OrgCategory::getOrgCategoryId)
                ,Record.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Record record : list) {
                record.put(OrgCategory::getServiceStatus,"QUALIFIED");
            }
            qlOpenClient.update(ContextPath.SUP,"OrgCategory",list);
        }
    }


    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.REJECTED.getCode());
        qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.WITHDRAW.getCode());
        qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNotice.getCode(),businessId, BidNoticeDTO.class);
        //2.修改招标单据状态
        if (null != bidNoticeDTO.getProjectId() && SouTypeEnum.bid.name().equals(bidNoticeDTO.getSouType())) {
            extSouInitEventService.updateSouBidStatus(bidNoticeDTO.getProjectId(),SouBiddingProStatusEnum.WIN_LOSS_NOTICE);
        }
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNotice.getCode(),businessId, BidNoticeDTO.class);

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+bidNoticeDTO.getSouName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {

        /**
         * 查询 中落标通知 -   businessid  就是 bid_notice_id
         *中/落标通知头表DTO       BidNoticeDTO   scc_npm_sou_bid_notice
         * 中/落标通知明细表DTO对象  BidNoticeDetailDTO  scc_npm_sou_bid_notice_detail
         * 内部通知书表          BidNoticeInternalDTO      scc_npm_sou_bid_notice_internal
         */
        log.info("===================进入中落标申请装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        // 根据 businessid 查询中/落标通知
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNotice.getCode(),businessId, BidNoticeDTO.class);

        // 中/落标通知明细表
        List<BidNoticeDetailDTO> bidNoticeDetailDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
                .eq(BidNoticeDetailDTO::getBidNoticeId, businessId), BidNoticeDetailDTO.class);

        // 内部通知书表
        List<BidNoticeInternalDTO> bidnoticeinternaldtolist = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeInternal.getCode())
                .eq(BidNoticeInternalDTO::getBidNoticeId, businessId), BidNoticeInternalDTO.class);

        // 附件
        SceneFile sceneFileParam = (new SceneFile()).setBusinessId(businessId).setSceneModuleCode("SCENE_SOU_TN_ATTACHMENT");
        List<SceneFile> scenefilelist = baseClient.listSceneFile(sceneFileParam);

        // 主表数据
        Map<String, Object> mainTableData = new HashMap<>(16);
        // 中/落标通知单号
        mainTableData.put("ZLBTZDH",bidNoticeDTO.getBidNoticeNo());
        // 板块
        mainTableData.put("BK",bidNoticeDTO.getExtOrgBuName() );
        // 公司
        mainTableData.put("GS",bidNoticeDTO.getExtOrgOuName() );
        // 需求部门
        mainTableData.put("XQBM",bidNoticeDTO.getDemandDepartmentName());
        // 需求人
        mainTableData.put("XQR",bidNoticeDTO.getDemandUserNickname() );
        //招标技术负责人
        mainTableData.put("ZBJSFZR",bidNoticeDTO.getExtTechPrincipal());
        //联系电话
        mainTableData.put("LXDH",bidNoticeDTO.getExtTechPhone());
        //单据状态
        mainTableData.put("DJZT",getStatus(bidNoticeDTO.getStatus()));
        //  招标单号
        mainTableData.put("ZBDH",bidNoticeDTO.getSouNo());
        // 项目名称
        mainTableData.put("XMMC",bidNoticeDTO.getSouName());
        //招标项目编号
        mainTableData.put("ZBXMBH",bidNoticeDTO.getExtProjectNo());
        // 创建人
        mainTableData.put("CJR",bidNoticeDTO.getCreatedFullName() );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(bidNoticeDTO.getCreationDate() != null){
            // 创建日期
            mainTableData.put("CJRQ",simpleDateFormat.format(bidNoticeDTO.getCreationDate()));
        }
        if(bidNoticeDTO.getLastUpdateDate() != null){
            // 最后更新时间
            mainTableData.put("ZHGXSJ",simpleDateFormat.format(bidNoticeDTO.getLastUpdateDate()));
        }

        // 中/落标通知明细表
        List<Object> itemdata = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bidNoticeDetailDtoList)) {
            bidNoticeDetailDtoList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(16);
                map.put("GYSBM",e.getVendorCode());  // 供应商编码
                map.put("GYSMC",e.getVendorName()); // 供应商名称
                map.put("SFZB","Y".equals(e.getIsWin()) ? "是" : "否"); // 是否中标
                map.put("ZBJE",e.getWinAmount()); // 中标金额（元）
                // 合同签署单位
                map.put("HTQSDW", e.getContractSignUnit());
                map.put("HTZQ",e.getContractPeriod()); // 合同周期
                map.put("SFLYPJ","Y".equals(e.getIsPerformanceEvaluated()) ? "是" : "否");//是否履约评价
                map.put("BLYPJDYY",e.getNonPerformanceReason()); //不履约评价的原因
                map.put("SFXCKC", "Y".equals(e.getIsOnSiteInspected()) ? "是" : "否"); //是否现场考察
                map.put("KCXQ",e.getInspectId()); //考察详情   取考察详情id
                map.put("TZSFJ",dealFileList(e.getNoticeAttachmentId())); //通知书附件
                map.put("__TABLE", "BO_EU_ZLBTZZB1");
                itemdata.add(map);
            });
        }


        // 内部通知书表
        if (CollectionUtils.isNotEmpty(bidnoticeinternaldtolist)) {
            bidnoticeinternaldtolist.forEach(e -> {
                Map<String, Object> map = new HashMap<>(16);
                map.put("BK",e.getExtOrgBuName());  // 板块
                map.put("GS",e.getExtOrgOuName()); // 公司
                map.put("XQBM",e.getDemandDepartmentName()); // 需求部门
                map.put("TZSFJ",dealFileList(e.getAttachmentId())); // 通知书附件
                map.put("TZSMXFJ",null); // 通知书明细附件,没这个字段
                map.put("__TABLE", "BO_EU_ZLBTZZB2");
                itemdata.add(map);
            });
        }


        JSONObject dataPushFlowJsn = getJsonObject(bidNoticeDTO, scenefilelist, mainTableData, itemdata);
        log.info("===================进入中落标组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========中落标通知JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }

    /**
     * 组装
     * @param bidNoticeDTO 参数
     * @param scenefilelist 参数
     * @param mainTableData 参数
     * @param itemdata 参数
     * @return 返回
     */
    @NotNull
    private JSONObject getJsonObject(BidNoticeDTO bidNoticeDTO, List<SceneFile> scenefilelist, Map<String, Object> mainTableData, List<Object> itemdata) {
        // 附件
        if (CollectionUtils.isNotEmpty(scenefilelist)) {
            scenefilelist.forEach(e -> {
                if(e.getFileuploadId() != null){
                    Map<String, Object> map = new HashMap<>(16);
                    List<Map<String, Object>> file = new ArrayList<>();
                    Fileupload fileupload = new Fileupload();
                    fileupload.setFileuploadId(e.getFileuploadId());
                    fileupload.setPageNum(1);
                    fileupload.setPageSize(1);
                    PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
                    List<Fileupload> fileList = fileuploads.getList();
                    for (Fileupload fileUpload : fileList) {
                        Map<String, Object> fileMap = new HashMap<>(16);
                        fileMap.put("FILE_PATH_BYMOBILE", "");
                        fileMap.put("FILE_NAME", fileUpload.getFileSourceName());
                        String mes = "fileSourceName="+fileUpload.getFileSourceName()+"&fileuploadId="+fileUpload.getFileuploadId();
                        fileMap.put("FILE_PATH", fileDownloadPath+mes);
                        file.add(fileMap);
                    }
                    map.put("FJMC",file);
                    map.put("BZ",e.getFileName());
                    map.put("__TABLE", "BO_EU_ZLBTZZB3");
                    itemdata.add(map);
                }
            });
        }


        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_ZLBTZZB1", BpmResult.getFileField("TZSFJ"));
        itemFile.put("BO_EU_ZLBTZZB2", BpmResult.getFileField("TZSFJ,TZSMXFJ"));
        itemFile.put("BO_EU_ZLBTZZB3", BpmResult.getFileField("FJMC"));


        String processtitle = "中落标通知-"+ bidNoticeDTO.getSouName();
        String maintable = "BO_EU_ZLBTZ";

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();


        String createOrgId = null;
        SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new RuntimeException("查询不到hr组织id");
        }

        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_ZLBTZZB1");
        itemtable.add("BO_EU_ZLBTZZB2");
        itemtable.add("BO_EU_ZLBTZZB3");

        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processGroupId, appId,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        return dataPushFlowJsn;
    }


    public List<Map<String, Object>> dealFileList(Long fileId)  {

        com.midea.cloud.srm.model.file.upload.entity.Fileupload fileupload = new com.midea.cloud.srm.model.file.upload.entity.Fileupload();
        fileupload.setFileuploadId(fileId);
        fileupload.setPageNum(1);
        fileupload.setPageSize(1);
        PageInfo<com.midea.cloud.srm.model.file.upload.entity.Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
        List<com.midea.cloud.srm.model.file.upload.entity.Fileupload> list = fileuploads.getList();

        List<Map<String, Object>> fileList = new ArrayList<>();
        list.forEach(e -> {
            Map<String, Object> map = new HashMap<>(50);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileSourceName());
            String mes = "fileSourceName="+e.getFileSourceName()+"&fileuploadId="+e.getFileuploadId();
            map.put("FILE_PATH", fileDownloadPath+mes);
            fileList.add(map);
        });
        return fileList;
    }


    public String getStatus(String type){
        String draft = "DRAFT";
        String approving = "APPROVING";
        String approved = "APPROVED";
        String rejected = "REJECTED";
        String abandon = "ABANDON";
        String withdraw = "WITHDRAW";
        if(draft.equals(type)){
            return "拟定" ;
        }else if(approving.equals(type)){
            return "审批中" ;
        }else if(approved.equals(type)){
            return "已审批" ;
        }else if(rejected.equals(type)){
            return "已驳回" ;
        }else if(abandon.equals(type)){
            return "已废弃" ;
        }else if(withdraw.equals(type)){
            return "已撤回" ;
        }else {
            return type ;
        }
    }

}
