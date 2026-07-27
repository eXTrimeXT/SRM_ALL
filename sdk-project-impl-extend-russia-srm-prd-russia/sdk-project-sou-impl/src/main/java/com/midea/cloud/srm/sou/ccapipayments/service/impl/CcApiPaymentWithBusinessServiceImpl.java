package com.midea.cloud.srm.sou.ccapipayments.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.alibaba.fastjson.JSONObject;
import com.meicloud.paas.audit.util.UUIDUtils;
import com.midea.cloud.common.constant.DingTalkConstant;
import com.midea.cloud.common.dingtalks.DingTalkClient;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestHeadDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestItemDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentResponseDto;
import com.midea.cloud.srm.model.pj.ccapipayments.enums.ApiCustomerSupplyEnum;
import com.midea.cloud.srm.model.pj.ccapipayments.enums.ApiPaymentMethodEnum;
import com.midea.cloud.srm.model.pj.ccapipayments.enums.ApiPaymentTypeEnum;
import com.midea.cloud.srm.model.pj.ccapipayments.enums.ApiReceiveTypeStatusEnum;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.*;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.enums.ApiSettleAcountingPartnerTypeEnum;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.enums.ApiSettleAcountingSaleTypeEnum;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceCompany;
import com.midea.cloud.srm.model.sou.enums.CaApiAcountServiceEnum;
import com.midea.cloud.srm.model.sou.enums.SouMarginRecordTypeEnum;
import com.midea.cloud.srm.model.sou.paymentapis.dto.SccSouPaymentApiRelDto;
import com.midea.cloud.srm.model.sou.req.SouIntDepositRefund;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositRefundStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.ccapipayments.service.CcApiPaymentWithBusinessService;
import com.midea.cloud.srm.sou.deposit.service.FinanceService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginRecordService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description: 财务共享-批量付款及自动提交审批-业务触发-接口实现类
 */
@Slf4j
@Service
public class CcApiPaymentWithBusinessServiceImpl implements CcApiPaymentWithBusinessService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IExtSouMarginRecordService marginRecordService;

    @Value("${bpm.fbsp.addressPath}")
    private String addressPath;

    @Override
    public void callApiPaymentWithMargin(String type, List<ExtSouMarginRecord> marginRecordList) {
        if(CollectionUtils.isEmpty(marginRecordList)) {
            return;
        }
        if(SouMarginRecordTypeEnum.REFUND.getCode().equals(type)) {
            //保证金退款
           callApiPaymentWithMarginRefund(marginRecordList);
        } else {
            //保证金扣款
            callApiPaymentWithMargiCharge(marginRecordList);
        }

    }

    private void callApiPaymentWithMargiCharge(List<ExtSouMarginRecord> marginRecordList) {
        Map<Long, List<ExtSouMarginRecord>> marginGroup = marginRecordList.stream().collect(Collectors.groupingBy(ExtSouMarginRecord::getProjectId));
        for(Long projectId: marginGroup.keySet()) {
            paymentWithMarginCharge(projectId, marginGroup.get(projectId));
        }
    }

    private void paymentWithMarginCharge(Long projectId, List<ExtSouMarginRecord> marginRecordList) {
        ExtSouProject souProject = projectService.getById(projectId);
        ApiSettleAcountingRequestDto requestDto = new ApiSettleAcountingRequestDto();

        requestDto.setHeader(new ApiSettleAcountingRequestHead());
        requestDto.setItems(new ArrayList<>());

        FinanceCompany financeCompany = financeService.queryFinanceCompany(souProject.getExtBankAccount());

        /** 组装请求头*/
        paymentWithMarginChargeHead(requestDto.getHeader(), souProject, marginRecordList.get(0).getMarginId(), financeCompany);
        requestDto.getHeader().setOrgCode(financeCompany.getCompanyCode());

        /** 组装请求明细*/
        paymentWithMarginChargeItem(requestDto.getItems(), souProject, marginRecordList, financeCompany);

        ApiSettleAcountingResponseDto responseDto = pjProjectExtClient.accounting(requestDto);

        if(!SrmConstant.SUCCESS_CODE.equals(responseDto.getCode())) {
            throw new BaseException(responseDto.getMsg());
        }
        /** 是否拉黑为是，提交后，给履约管理员 发送一个钉钉通知
         通知模板：您有新的工作通知：“项目编号#项目名称#供应商名称#扣款说明”该供应商需做拉黑处理，请及时处理； */
        sendDingtalkToPerfRemindList(souProject, marginRecordList.stream().filter(r -> YesOrNo.YES.getValue().equals(r.getBlackFlag())).collect(Collectors.toList()));

    }

    private void sendDingtalkToPerfRemindList(ExtSouProject souProject, List<ExtSouMarginRecord> marginRecordList) {
        if(CollectionUtils.isEmpty(marginRecordList)) {
            return;
        }

        List<RecordDTO> configList = qlOpenClient.query(ContextPath.BASE,
                QlOpenWrappers.query(MqlType.SCC_BASE_SYSTEM_CONFIGURE).eq(SystemConfigureDTO::getParamKey, SrmConstant.SYSTEM_CONFIG_PERF_REMIND_LIST)
                        .eq(SystemConfigureDTO::getParamStatus, YesOrNo.YES.getValue()));
        if(CollectionUtils.isEmpty(configList)) {
            return;
        }
        List<String> userNameList = new ArrayList<>(16);
        configList.stream().filter(c -> StringUtils.isNotBlank(c.get(SystemConfigureDTO::getParamValue))).forEach(c -> {
            userNameList.addAll(Arrays.asList(c.get(SystemConfigureDTO::getParamValue).split(SrmConstant.SIG_3)));
        });

        marginRecordList.stream().forEach(r -> {
            Map<String, String> vars = new HashMap<>(16);
            vars.put("${souNo}", souProject.getExtProjectNo());
            vars.put("${souName}", souProject.getSouName());
            vars.put("${vendorName}", r.getVendorName());
            vars.put("${description}", Objects.toString(r.getDescription(), ""));

            DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(userNameList.stream().distinct().collect(Collectors.toList()), DingTalkConstant.PAY_BLACK_AS_Y, vars);
        });


    }

    private void paymentWithMarginChargeItem(List<ApiSettleAcountingRequestItems> items, ExtSouProject souProject, List<ExtSouMarginRecord> marginRecordList, FinanceCompany financeCompany) {
        AtomicReference<Integer> index = new AtomicReference<>(0);
        marginRecordList.stream().forEach(margin -> {
            index.getAndSet(index.get() + 1);
            ApiSettleAcountingRequestItems item = new ApiSettleAcountingRequestItems();
            items.add(item);

            item.setBaseInfo(new ApiSettleAcountingRequestItemsBase());
            paymentWithMarginChargeItemBase(item.getBaseInfo(), souProject, margin, index.get(), financeCompany);

            item.setSettleDetailList(new ArrayList<>());
            paymentWithMarginChargeItemSettle(item.getSettleDetailList(), souProject, margin, index.get());

            item.setAttachList(new ArrayList<>());
            paymentWithMarginChargeAttach(item.getAttachList(), margin);

        });
    }

    private void paymentWithMarginChargeAttach(List<ApiSettleAcountingRequestItemsAttach> attachList, ExtSouMarginRecord margin) {
        if(Objects.isNull(margin.getFileId())) {
         return;
        }
        ApiSettleAcountingRequestItemsAttach attach = new ApiSettleAcountingRequestItemsAttach();
        attachList.add(attach);
        attach.setAttachName(margin.getFileName());
        attach.setAttachUrl(String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", margin.getFileName(), margin.getFileId()));

    }

    private void paymentWithMarginChargeItemSettle(List<ApiSettleAcountingRequestItemsSettle> settleDetailList, ExtSouProject souProject, ExtSouMarginRecord margin, Integer index) {
        ApiSettleAcountingRequestItemsSettle settle = new ApiSettleAcountingRequestItemsSettle();
        settleDetailList.add(settle);

        //默认：FW0907/意向金冲销FW9901/保证金扣款FW9902
        settle.setServiceCode(CaApiAcountServiceEnum.MARGIN_DEDUCTION.getServiceCode());
        //默认：3049900000000000000/意向金冲销、保证金扣款不传
        settle.setTaxRate(SrmConstant.NUM_ZERO.toString());
        settle.setTaxClassifyCode(SrmConstant.API_SETTLE_TAX_CLASSIFY_CODE);
        settle.setAccSubjectCode(SrmConstant.ACC_SUBJECT_CODE_MARGIN);
        settle.setAccSubjectName(SrmConstant.ACC_SUBJECT_NAME_MARGIN);

        settle.setSettleInfoList(new ArrayList<>());

        ApiSettleAcountingRequestItemsSettleInfo info = new ApiSettleAcountingRequestItemsSettleInfo();
        settle.getSettleInfoList().add(info);

        //默认：FW0907/意向金冲销FW9901/保证金扣款FW9902
        info.setBusinessCode(CaApiAcountServiceEnum.MARGIN_DEDUCTION.getBusinessCode());
        info.setSettleNumber(BigDecimal.ONE);
        BigDecimal amount = ObjectUtils.defaultIfNull(margin.getAmount(), BigDecimal.ZERO).multiply(new BigDecimal(SrmConstant.TEN_THOUSAND_STR));
        info.setPrice(amount);
        info.setUnit("项");
        info.setDiscountFlag(false);
    }

    /**
     * 明细行基本信息
     * @param base
     * @param souProject
     * @param margin
     * @param index 
     */
    private void paymentWithMarginChargeItemBase(ApiSettleAcountingRequestItemsBase base, ExtSouProject souProject, ExtSouMarginRecord margin, Integer index, FinanceCompany financeCompany) {
        base.setItemNo(index);
        base.setPartnerType(ApiSettleAcountingPartnerTypeEnum.VENDOR.getCode());
        base.setPartnerCode(margin.getVendorCode());
        base.setPartnerName(margin.getVendorName());
        base.setProfitCenterCode(financeCompany.getProfitCenterCode());
        base.setProfitCenterName(financeCompany.getProfitCenterName());
        base.setDocumentExplain(StringUtils.join(margin.getVendorName(), "保证金扣款"));
        base.setContainTax(false);
        base.setCostCenterCode(financeCompany.getCenterCode());
        base.setCostCenterName(financeCompany.getCenterName());
    }

    private void paymentWithMarginChargeHead(ApiSettleAcountingRequestHead header, ExtSouProject souProject, Long marginId, FinanceCompany financeCompany) {
        header.setSystemCode(SrmConstant.SYS_SRM);
        header.setBusinessNo(StringUtils.joinWith(SrmConstant.UNDER_LINE, souProject.getExtProjectNo(), marginId));
        header.setReqSn(UUIDUtils.getUUID());
        header.setSaleType(ApiSettleAcountingSaleTypeEnum.SERVICE.getCode());
        header.setDeptName(SrmConstant.API_SETTLE_DEAULT_DEPART_NAME);
        header.setCreateUserNo(AppUserUtil.getUserName());
        header.setCreateUserName(AppUserUtil.getLoginAppUser().getNickname());
        header.setCreateTime(toDateStr(new Date()));
        header.setOrgCode(financeCompany.getCompanyCode());
        header.setNeedApprove(true);
        header.setAccountUserNo(AppUserUtil.getUserName());
        header.setAccountUserName(AppUserUtil.getLoginAppUser().getNickname());
        header.setSettleExplain("保证金扣款记账");
    }



    private void callApiPaymentWithMarginRefund(List<ExtSouMarginRecord> marginRecordList) {
        Map<Long, List<ExtSouMarginRecord>> marginGroup = marginRecordList.stream().collect(Collectors.groupingBy(ExtSouMarginRecord::getProjectId));
        for(Long projectId: marginGroup.keySet()) {
            paymentWithMargin(projectId, marginGroup.get(projectId));
        }
    }

    private void paymentWithMargin(Long projectId, List<ExtSouMarginRecord> marginRecordList) {
        ExtSouProject souProject = projectService.getById(projectId);

        ApiPaymentRequestDto paymentRequestDto = new ApiPaymentRequestDto();
        paymentRequestDto.setPaymentRequestHead(new ApiPaymentRequestHeadDto());
        paymentRequestDto.setBatchImportPaymentRequestItems(new ArrayList<>(16));

        head(paymentRequestDto, souProject);

        item(paymentRequestDto, marginRecordList, souProject);

        ApiPaymentResponseDto responseDto = pjProjectExtClient.saveOutSourceOneVo(paymentRequestDto);

        if(SrmConstant.SUCCESS.equals(responseDto.getStatus())) {
//            saveOutSourceOneVoAsSuccess(projectId, marginRecordList.stream().map(m -> Objects.toString(m.getRecordId(), "")).collect(Collectors.joining(SrmConstant.SIG_3)), SrmConstant.CC_API_PAYMENT_BID, responseDto.getData().stream().map(d -> d.getRequestItemId()).collect(Collectors.joining(SrmConstant.SIG_3)));
            paymentWithMarginAsSuccess(projectId, marginRecordList, responseDto);
            updateMarginInfo(marginRecordList.stream().map(m -> m.getMarginId()).distinct().collect(Collectors.toList()));
        } else {
            throw new BaseException(responseDto.getMessage());
        }

    }

    private void sendDingTalkBid(ExtSouProject souProject, List<ExtSouMargin> marginRecordList, String payResult) {
        Map<String, String> var = new HashMap<>(16);
        var.put("${souNo}", souProject.getExtProjectNo());
        var.put("${souName}", souProject.getSouName());
        var.put("${vendorName}", marginRecordList.stream().map(m -> m.getVendorName()).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
        var.put("${payResult}", payResult);
        DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(Arrays.asList(souProject.getCreatedBy()), DingTalkConstant.PAY_SUCESS_BID, var);
    }

    List<SccSouPaymentApiRelDto> paymentWithMarginAsSuccess(Long projectId, List<ExtSouMarginRecord> marginRecordList, ApiPaymentResponseDto responseDto) {
        List<SccSouPaymentApiRelDto> apiRelDtoList = new ArrayList<>(16);
        Map<Integer, ExtSouMarginRecord> extSouMarginRecordMap = marginRecordList.stream().collect(Collectors.toMap(k -> {
            ApiPaymentRequestItemDto itemDto = (ApiPaymentRequestItemDto)k.getExtensions().getOrDefault(ApiPaymentRequestItemDto.class.getSimpleName(), new ApiPaymentRequestItemDto());
            return ObjectUtils.defaultIfNull(itemDto.getXh(), SrmConstant.NUM_MINUS_ONE);
        }, Function.identity(), (k1, k2) -> k2));
        if(CollectionUtils.isNotEmpty(responseDto.getData())) {
            responseDto.getData().stream().forEach(data -> {
                SccSouPaymentApiRelDto sccSouPaymentApiRelDto = new SccSouPaymentApiRelDto();
                sccSouPaymentApiRelDto.setPaymentApiId(IdGenrator.generate());
                sccSouPaymentApiRelDto.setBusinessId(projectId);
                sccSouPaymentApiRelDto.setBusinessCode(Objects.toString(extSouMarginRecordMap.getOrDefault(data.getItemNum(), new ExtSouMarginRecord()).getRecordId()));
                sccSouPaymentApiRelDto.setBusinessType(SrmConstant.CC_API_PAYMENT_BID);
                sccSouPaymentApiRelDto.setRequestItemId(data.getRequestItemId());
                apiRelDtoList.add(sccSouPaymentApiRelDto);
            });
        }
        if(CollectionUtils.isNotEmpty(apiRelDtoList)) {
            qlService.create(MqlType.SCC_SOU_PAYMENT_API_REL, apiRelDtoList);
        }
        return apiRelDtoList;
    }

    private void updateMarginInfo(List<Long> marginIdList) {
        marginService.update(new LambdaUpdateWrapper<ExtSouMargin>().set(ExtSouMargin::getRefundStatus, IntDepositRefundStatusEnum.REFUNDING.getCode())
                .in(ExtSouMargin::getMarginId, marginIdList));
    }

    private void saveOutSourceOneVoAsSuccess(Long businessId, String businessCode, String businessType, String requestItemId) {
        SccSouPaymentApiRelDto sccSouPaymentApiRelDto = new SccSouPaymentApiRelDto();
        sccSouPaymentApiRelDto.setPaymentApiId(IdGenrator.generate());
        sccSouPaymentApiRelDto.setBusinessId(businessId);
        sccSouPaymentApiRelDto.setBusinessCode(businessCode);
        sccSouPaymentApiRelDto.setBusinessType(businessType);
        sccSouPaymentApiRelDto.setRequestItemId(requestItemId);

        qlService.create(MqlType.SCC_SOU_PAYMENT_API_REL, Collections.singletonList(sccSouPaymentApiRelDto));
    }

    /**
     * 组装明细
     * @param paymentRequestDto
     * @param marginRecordList
     * @param souProject
     */
    private void item(ApiPaymentRequestDto paymentRequestDto, List<ExtSouMarginRecord> marginRecordList, ExtSouProject souProject) {
        AtomicReference<Integer> index = new AtomicReference<>(0);
        marginRecordList.stream().forEach(marginRecord -> {
            index.getAndSet(index.get() + 1);
            ApiPaymentRequestItemDto itemDto = toItem(marginRecord, souProject);
            itemDto.setXh(index.get());
            paymentRequestDto.getBatchImportPaymentRequestItems().add(itemDto);

            marginRecord.getExtensions().put(ApiPaymentRequestItemDto.class.getSimpleName(), itemDto);
        });
    }

    private ApiPaymentRequestItemDto toItem(ExtSouMarginRecord marginRecord, ExtSouProject souProject) {
        ApiPaymentRequestItemDto itemDto = new ApiPaymentRequestItemDto();

        FinanceCompany financeCompany = financeService.queryFinanceCompany(souProject.getExtBankAccount());

        //利润中心代码	profitCenterCode scc_sou_finance_company acount 开户账号  去掉空格
        itemDto.setProfitCenterCode(financeCompany.getProfitCenterCode());

        //利润中心名称	profitCenterName
        itemDto.setProfitCenterName(financeCompany.getProfitCenterName());

        //成本中心代码	costCenterCode
        itemDto.setCostCenterCode(financeCompany.getCenterCode());

        //成本中心名称	costCenterName
        itemDto.setCostCenterName(financeCompany.getCenterName());

        itemDto.setSupplyCode(marginRecord.getVendorCode());
        itemDto.setSupplyName(marginRecord.getVendorName());

        itemDto.setPaymentNatureName(SrmConstant.API_PAYMENT_NATURE_NAME_MARGIN);

        BigDecimal amount = ObjectUtils.defaultIfNull(marginRecord.getAmount(), BigDecimal.ZERO).multiply(new BigDecimal(SrmConstant.TEN_THOUSAND_STR));

        itemDto.setAccountsPayableAmount(amount);

        itemDto.setCustomerSupplyEnum(ApiCustomerSupplyEnum.SUPPLY.getCode());
        itemDto.setReceiveTypeStatus(ApiReceiveTypeStatusEnum.SUPPLY_UNIT.getCode());

        itemDto.setPaymentDate(toDateStr(marginRecord.getExpectRefundTime()));

        itemDto.setPaymentMethod(ApiPaymentMethodEnum.TELEGRAPHIC.getCode());

        itemDto.setTotalAmount(amount);
        itemDto.setPaymentMoney(amount);

        itemDto.setReceivingBankName(marginRecord.getRefundBank());
        itemDto.setBankLinkNumber(marginRecord.getRefundBankNum());
        itemDto.setReceivingBankAccount(marginRecord.getRefundAccount());

        itemDto.setArrivedTicketAmount(BigDecimal.ZERO);

        itemDto.setReceiverCode(marginRecord.getVendorCode());
        itemDto.setReceiverName(marginRecord.getRefundAccountName());

        itemDto.setPaymentNote(souProject.getExtProjectNo());

        itemDto.setOnlyKey(UUIDUtils.getUUID());

        return itemDto;
    }

    private void head(ApiPaymentRequestDto paymentRequestDto, ExtSouProject souProject) {
        /** 组装头*/
        //保证金：招标负责人 GW
        paymentRequestDto.getPaymentRequestHead().setCreator(souProject.getCreatedBy());
        paymentRequestDto.getPaymentRequestHead().setSysFlag(SrmConstant.SYS_SRM);

        //HR的id
        SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(souProject.getCreatedBy());
        if(!Objects.isNull(sccPjUser)) {
            paymentRequestDto.getPaymentRequestHead().setOrganizationCode(Objects.toString(sccPjUser.getGroupId(), ""));
        }
        FinanceCompany financeCompany = financeService.queryFinanceCompany(souProject.getExtBankAccount());
        log.info("financeCompany======" + JSONObject.toJSONString(financeCompany));
        paymentRequestDto.getPaymentRequestHead().setCorporationCode(financeCompany.getCompanyCode());
        paymentRequestDto.getPaymentRequestHead().setCurrencyCode(SrmConstant.DEAULT_CURRENCY_CODE);
        paymentRequestDto.getPaymentRequestHead().setCurrencyName(SrmConstant.DEAULT_CURRENCY_NAME);
        paymentRequestDto.getPaymentRequestHead().setBudgetDepartmentCode(financeCompany.getSkDept());
        paymentRequestDto.getPaymentRequestHead().setBudgetDepartmentName(financeCompany.getSkDeptName());

        paymentRequestDto.getPaymentRequestHead().setPaymentDate(toDateStr(new Date()));

        paymentRequestDto.getPaymentRequestHead().setFileFlag(true);
        paymentRequestDto.getPaymentRequestHead().setPaymentType(ApiPaymentTypeEnum.BATCH_IMPORT_PAYMNET.name());

        LambdaQueryWrapper<ExtSouMarginRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouMarginRecord::getProjectId, souProject.getProjectId());
        queryWrapper.eq(ExtSouMarginRecord::getType, SouMarginRecordTypeEnum.REFUND.getCode());
        queryWrapper.orderByDesc(ExtSouMarginRecord::getCreationDate);
        List<ExtSouMarginRecord> marginRecordList = marginRecordService.list(queryWrapper);
        String description = "";
        if (CollectionUtils.isNotEmpty(marginRecordList)) {
            description = "，" + marginRecordList.get(0).getDescription();
        }
        paymentRequestDto.getPaymentRequestHead().setRequestNote("供应商保证金退款" + description);

        /** 组装头 结束*/
    }

    private String toDateStr(Date date) {
        if(Objects.isNull(date)) {
            return null;
        }
        return DateUtil.format(date, DateUtil.DATE_FORMAT_19);
    }

    private String toHrOrganizationCode(String organizationCode) {
        String[] organizationCodes = StringUtils.defaultString(organizationCode, "").split(SrmConstant.UNDER_LINE);
        if(Integer.compare(organizationCodes.length, SrmConstant.NUM_TWO) == 0) {
            return organizationCodes[1];
        }
        return "";
    }

    @Override
    public void callApiPaymentWithDepositRefund(List<Record> souIntDepositRefundList) {

        if(CollectionUtils.isEmpty(souIntDepositRefundList)) {
            return;
        }

        List<SouReqHead> souReqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER)
                .in(SouReqHead::getReqHeadId, souIntDepositRefundList.stream().map(r -> r.get(SouIntDepositRefund::getReqHeadId)).distinct().collect(Collectors.toList())), SouReqHead.class);

        SouReqHead souReqHead = souReqHeadList.get(0);

        Map<String, Object> cache = new HashMap<>(16);
        /** 供应商负责人 */
        if(ObjectUtils.allNotNull(souReqHead.getResponsibilityUserId())) {
            User user = rbacClient.getUserByIdAnon(souReqHead.getResponsibilityUserId());
            cache.put(LambdaUtil.getFieldName(SouReqHead::getResponsibilityUserId), user);
        }

        /** 收款账号 */
        FinanceCompany financeCompany = financeService.queryFinanceCompany(souReqHead.getBankAccount());
        cache.put(LambdaUtil.getFieldName(FinanceCompany::getFcId), financeCompany);

        /** 报名供应商 */
        List<SouReqApply> souReqApplyList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY)
                .in(SouReqApply::getApplyId, souIntDepositRefundList.stream().map(r -> r.get(SouIntDepositRefund::getApplyId)).distinct().collect(Collectors.toList())), SouReqApply.class);
        Map<Long, SouReqApply> souReqApplyMap = souReqApplyList.stream().collect(Collectors.toMap(k -> k.getApplyId(), Function.identity(), (k1, k2)->k2));
        cache.put(LambdaUtil.getFieldName(SouReqApply::getApplyId), souReqApplyMap);

        /** 销售结算记账接口，完成会计记账 */
        callApiPaymentWithDepositRefundAccounting(souIntDepositRefundList, souReqHead, cache);

        /** 批量付款及自动提交审批 */
        callApiPaymentWithDepositRefundSaveOutSourceOneVo(souIntDepositRefundList, souReqHead, cache);

    }

    /**
     * 批量付款及自动提交审批
     * @param souIntDepositRefundList
     */
    private void callApiPaymentWithDepositRefundSaveOutSourceOneVo(List<Record> souIntDepositRefundList, SouReqHead souReqHead, Map<String, Object> cache) {
        ApiPaymentRequestDto paymentRequestDto = new ApiPaymentRequestDto();
        paymentRequestDto.setPaymentRequestHead(new ApiPaymentRequestHeadDto());
        paymentRequestDto.setBatchImportPaymentRequestItems(new ArrayList<>(16));

        /** 组装头 */
        callApiPaymentWithDepositRefundSaveOutSourceOneVoHead(paymentRequestDto.getPaymentRequestHead(), souReqHead, cache);
        /** 组装明细 */
        callApiPaymentWithDepositRefundSaveOutSourceOneVoItems(paymentRequestDto.getBatchImportPaymentRequestItems(), souIntDepositRefundList, souReqHead, cache);

        ApiPaymentResponseDto responseDto = pjProjectExtClient.saveOutSourceOneVo(paymentRequestDto);
        if(SrmConstant.SUCCESS.equals(responseDto.getStatus())) {
//            saveOutSourceOneVoAsSuccess(souReqHead.getReqHeadId(), souIntDepositRefundList.stream().map(m -> Objects.toString(m.get(SouIntDepositRefund::getApplyId), "")).distinct().collect(Collectors.joining(SrmConstant.SIG_3)), SrmConstant.CC_API_PAYMENT_REQ, responseDto.getData().stream().map(d -> d.getRequestItemId()).collect(Collectors.joining(SrmConstant.SIG_3)));
            depositRefundSaveOutSourceOneVoAsSuccess(souReqHead.getReqHeadId(), cache, responseDto);

        } else {
            throw new BaseException(responseDto.getMessage());
        }
    }

    private void sendDingTalkReq(SouReqHead souReqHead, List<SouReqApply> souIntDepositRefundList, String payResult, Map<String, Object> cache) {
        Map<String, String> var = new HashMap<>(16);

        User user = (User) cache.get(LambdaUtil.getFieldName(SouReqHead::getResponsibilityUserId));

        if(Objects.isNull(user)) {
            return;
        }

        var.put("${reqHeadNum}", souReqHead.getReqHeadNo());
        var.put("${projectName}", souReqHead.getProjectName());
        var.put("${vendorName}", souIntDepositRefundList.stream().map(m -> m.getVendorName()).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
        var.put("${payResult}", payResult);
        DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(Arrays.asList(user.getUsername()), DingTalkConstant.PAY_SUCESS_REQ, var);
    }

    private List<SccSouPaymentApiRelDto> depositRefundSaveOutSourceOneVoAsSuccess(Long reqHeadId, Map<String, Object> cache, ApiPaymentResponseDto responseDto) {
        Map<Integer, Long> itemXhApplyIdMap = (Map<Integer, Long>) cache.getOrDefault("itemXhApplyIdMap", new HashMap<>(16));

        List<SccSouPaymentApiRelDto> apiRelDtoList = new ArrayList<>(16);

        if(CollectionUtils.isNotEmpty(responseDto.getData())) {
            responseDto.getData().stream().forEach(data -> {
                SccSouPaymentApiRelDto sccSouPaymentApiRelDto = new SccSouPaymentApiRelDto();
                sccSouPaymentApiRelDto.setPaymentApiId(IdGenrator.generate());
                sccSouPaymentApiRelDto.setBusinessId(reqHeadId);
                sccSouPaymentApiRelDto.setBusinessCode(Objects.toString(itemXhApplyIdMap.get(data.getItemNum())));
                sccSouPaymentApiRelDto.setBusinessType(SrmConstant.CC_API_PAYMENT_REQ);
                sccSouPaymentApiRelDto.setRequestItemId(data.getRequestItemId());
                apiRelDtoList.add(sccSouPaymentApiRelDto);
            });
        }
        if(CollectionUtils.isNotEmpty(apiRelDtoList)) {
            qlService.create(MqlType.SCC_SOU_PAYMENT_API_REL, apiRelDtoList);
        }
        return apiRelDtoList;
    }

    private void callApiPaymentWithDepositRefundSaveOutSourceOneVoItems(List<ApiPaymentRequestItemDto> batchImportPaymentRequestItems, List<Record> souIntDepositRefundList, SouReqHead souReqHead, Map<String, Object> cache) {
        AtomicReference<Integer> index = new AtomicReference<>(0);
        FinanceCompany financeCompany = (FinanceCompany) cache.getOrDefault(LambdaUtil.getFieldName(FinanceCompany::getFcId), new FinanceCompany());
        Map<Long, SouReqApply> souReqApplyMap = (Map<Long, SouReqApply>) cache.getOrDefault(LambdaUtil.getFieldName(SouReqApply::getApplyId), new HashMap<>(16));

        Map<Integer, Long> itemXhApplyIdMap = new HashMap<>(16);

        souIntDepositRefundList.stream().forEach(item -> {
            index.getAndSet(index.get() + 1);
            ApiPaymentRequestItemDto itemDto = new ApiPaymentRequestItemDto();
            batchImportPaymentRequestItems.add(itemDto);
            itemDto.setProfitCenterCode(financeCompany.getProfitCenterCode());
            itemDto.setProfitCenterName(financeCompany.getProfitCenterName());
            itemDto.setCostCenterCode(financeCompany.getCenterCode());
            itemDto.setCostCenterName(financeCompany.getCenterName());

            SouReqApply apply = souReqApplyMap.getOrDefault(item.get(SouIntDepositRefund::getApplyId), new SouReqApply());
            itemDto.setSupplyCode(apply.getVendorCode());
            itemDto.setSupplyName(apply.getVendorName());
            itemDto.setPaymentNatureName(SrmConstant.API_PAYMENT_NATURE_NAME_INT_DEPOSIT_REFUND);
            itemDto.setAccountsPayableAmount(item.get(SouIntDepositRefund::getRefundAmount));
            itemDto.setCustomerSupplyEnum(ApiCustomerSupplyEnum.SUPPLY.getCode());
            itemDto.setReceiveTypeStatus(ApiReceiveTypeStatusEnum.SUPPLY_UNIT.getCode());
            itemDto.setPaymentDate(toDateStr(item.get(SouIntDepositRefund::getExpectRefundTime)));
            itemDto.setPaymentMethod(ApiPaymentMethodEnum.TELEGRAPHIC.getCode());
            itemDto.setTotalAmount(item.get(SouIntDepositRefund::getRefundAmount));
            itemDto.setPaymentMoney(item.get(SouIntDepositRefund::getRefundAmount));
            itemDto.setReceivingBankName(item.get(SouIntDepositRefund::getRefundBankName));
            itemDto.setBankLinkNumber(item.get(SouIntDepositRefund::getRefundBankNumber));
            itemDto.setReceivingBankAccount(item.get(SouIntDepositRefund::getRefundBankAccount));
            itemDto.setArrivedTicketAmount(BigDecimal.ZERO);
            itemDto.setReceiverCode(apply.getVendorCode());
            itemDto.setReceiverName(apply.getVendorBankAccountName());
            itemDto.setPaymentNote(souReqHead.getReqHeadNo());
            itemDto.setOnlyKey(UUIDUtils.getUUID());
            itemDto.setXh(index.get());

            itemXhApplyIdMap.put(itemDto.getXh(), apply.getApplyId());
        });

        cache.put("itemXhApplyIdMap", itemXhApplyIdMap);
    }

    private void callApiPaymentWithDepositRefundSaveOutSourceOneVoHead(ApiPaymentRequestHeadDto paymentRequestHead, SouReqHead souReqHead, Map<String, Object> cache) {
        /** 供应商负责人 */
        User vendorUser = (User) cache.getOrDefault(LambdaUtil.getFieldName(SouReqHead::getResponsibilityUserId), new User());

        SccPjUser pjUser = pjProjectExtClient.getSccUserByPersonnelNo(vendorUser.getUsername());

        paymentRequestHead.setCreator(vendorUser.getUsername());
        paymentRequestHead.setSysFlag(SrmConstant.SYS_SRM);
        paymentRequestHead.setOrganizationCode(Objects.toString(pjUser.getGroupId(), ""));
        paymentRequestHead.setCurrencyCode(SrmConstant.DEAULT_CURRENCY_CODE);
        paymentRequestHead.setCurrencyName(SrmConstant.DEAULT_CURRENCY_NAME);
        paymentRequestHead.setPaymentDate(toDateStr(new Date()));
        FinanceCompany financeCompany = (FinanceCompany) cache.getOrDefault(LambdaUtil.getFieldName(FinanceCompany::getFcId), new FinanceCompany());
        log.info("financeCompany======" + JSONObject.toJSONString(financeCompany));
        paymentRequestHead.setCorporationCode(financeCompany.getCompanyCode());
        paymentRequestHead.setFileFlag(true);
        paymentRequestHead.setPaymentType(ApiPaymentTypeEnum.BATCH_IMPORT_PAYMNET.name());
        paymentRequestHead.setBudgetDepartmentCode(financeCompany.getSkDept());
        paymentRequestHead.setBudgetDepartmentName(financeCompany.getSkDeptName());
    }


    /**
     * 销售结算记账接口，完成会计记账
     * @param souIntDepositRefundList
     */
    private void callApiPaymentWithDepositRefundAccounting(List<Record> souIntDepositRefundList, SouReqHead souReqHead, Map<String, Object> cache) {

        List<SccSouPaymentApiRelDto> relDtos = qlService.queryByWrapper(QlWrappers.query(MqlType.SCC_SOU_PAYMENT_API_REL)
                .in(SccSouPaymentApiRelDto::getBusinessId, souIntDepositRefundList.stream().map(s -> s.get(SouIntDepositRefund::getReqHeadId)).distinct().collect(Collectors.toList()))
                .in(SccSouPaymentApiRelDto::getBusinessCode, souIntDepositRefundList.stream().map(s -> Objects.toString(s.get(SouIntDepositRefund::getApplyId))).distinct().collect(Collectors.toList()))
                .eq(SccSouPaymentApiRelDto::getBusinessType, SrmConstant.CC_API_PAYMENT_REQ_ACOUNT), SccSouPaymentApiRelDto.class);
        //过滤已成功的数据
        List<String> hasSuccessList = relDtos.stream().map(s -> StringUtils.joinWith(SrmConstant.UNDER_LINE, s.getBusinessId(), s.getBusinessCode())).collect(Collectors.toList());

        List<Record> savSouIntDepositRefundList = souIntDepositRefundList.stream().filter(s -> !hasSuccessList.contains(StringUtils.joinWith(SrmConstant.UNDER_LINE, s.get(SouIntDepositRefund::getReqHeadId), s.get(SouIntDepositRefund::getApplyId)))).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(savSouIntDepositRefundList)) {
            return;
        }

        ApiSettleAcountingRequestDto requestDto = new ApiSettleAcountingRequestDto();

        requestDto.setHeader(new ApiSettleAcountingRequestHead());
        requestDto.setItems(new ArrayList<>());

        /** 组装头 */
        callApiPaymentWithDepositRefundAccountingHead(requestDto.getHeader(), souReqHead, savSouIntDepositRefundList.get(0), cache);

        /** 组装明细 */
        callApiPaymentWithDepositRefundAccountingItems(requestDto.getItems(), souReqHead, savSouIntDepositRefundList, cache);

        ApiSettleAcountingResponseDto responseDto = pjProjectExtClient.accounting(requestDto);

        if(SrmConstant.SUCCESS_CODE.equals(responseDto.getCode())) {
            List<SccSouPaymentApiRelDto> sccSouPaymentApiRelDtos = new ArrayList<>(16);
            savSouIntDepositRefundList.stream().forEach(s -> {
                sccSouPaymentApiRelDtos.add(saveAcountingAsSuccess(s.get(SouIntDepositRefund::getReqHeadId), Objects.toString(s.get(SouIntDepositRefund::getApplyId)), SrmConstant.CC_API_PAYMENT_REQ_ACOUNT, Objects.toString(s.get(SouIntDepositRefund::getRefundId))));
            });
            //feign方式保存，隔离事务
            qlOpenClient.save(ContextPath.SOU, MqlType.SCC_SOU_PAYMENT_API_REL, sccSouPaymentApiRelDtos);
        } else {
            throw new BaseException(responseDto.getMsg());
        }

    }

    private SccSouPaymentApiRelDto saveAcountingAsSuccess(Long businessId, String businessCode, String businessType, String requestItemId) {
        SccSouPaymentApiRelDto sccSouPaymentApiRelDto = new SccSouPaymentApiRelDto();
        sccSouPaymentApiRelDto.setBusinessId(businessId);
        sccSouPaymentApiRelDto.setBusinessCode(businessCode);
        sccSouPaymentApiRelDto.setBusinessType(businessType);
        sccSouPaymentApiRelDto.setRequestItemId(requestItemId);

        return sccSouPaymentApiRelDto;
    }

    private void callApiPaymentWithDepositRefundAccountingItems(List<ApiSettleAcountingRequestItems> items, SouReqHead souReqHead, List<Record> souIntDepositRefundList, Map<String, Object> cache){
        AtomicReference<Integer> index = new AtomicReference<>(0);
        souIntDepositRefundList.stream().forEach(record -> {
            index.getAndSet(index.get() + 1);
            ApiSettleAcountingRequestItems item = new ApiSettleAcountingRequestItems();
            items.add(item);

            item.setBaseInfo(new ApiSettleAcountingRequestItemsBase());
            callApiPaymentWithDepositRefundAccountingItemsBase(item.getBaseInfo(), souReqHead, record, cache, index.get());

            item.setSettleDetailList(new ArrayList<>());
            callApiPaymentWithDepositRefundAccountingItemsSettle(item.getSettleDetailList(), souReqHead, record, cache, index.get());
        });
    }

    private void callApiPaymentWithDepositRefundAccountingItemsSettle(List<ApiSettleAcountingRequestItemsSettle> settleDetailList, SouReqHead souReqHead, Record record, Map<String, Object> cache, Integer index){

        ApiSettleAcountingRequestItemsSettle settle = new ApiSettleAcountingRequestItemsSettle();
        settleDetailList.add(settle);

        //默认：FW0907/意向金冲销FW9901/保证金扣款FW9902
        settle.setServiceCode(CaApiAcountServiceEnum.EARNEST_AGINST.getServiceCode());
        //默认：3049900000000000000/意向金冲销、保证金扣款不传
        settle.setTaxClassifyCode(SrmConstant.API_SETTLE_TAX_CLASSIFY_CODE);
        settle.setTaxRate(SrmConstant.NUM_SIX.toString());
        settle.setSettleInfoList(new ArrayList<>());
        ApiSettleAcountingRequestItemsSettleInfo info = new ApiSettleAcountingRequestItemsSettleInfo();
        settle.getSettleInfoList().add(info);

        //默认：FW0907/意向金冲销FW9901/保证金扣款FW9902
        info.setBusinessCode(CaApiAcountServiceEnum.EARNEST_AGINST.getBusinessCode());
        info.setSettleNumber(BigDecimal.ONE);
        info.setPrice(record.get(SouIntDepositRefund::getRefundAmount));
        info.setUnit("项");
        info.setDiscountFlag(false);
    }

    private void callApiPaymentWithDepositRefundAccountingItemsBase(ApiSettleAcountingRequestItemsBase base, SouReqHead souReqHead, Record record, Map<String, Object> cache, Integer index){
        FinanceCompany financeCompany = (FinanceCompany) cache.getOrDefault(LambdaUtil.getFieldName(FinanceCompany::getFcId), new FinanceCompany());
        Map<Long, SouReqApply> souReqApplyMap = (Map<Long, SouReqApply>) cache.getOrDefault(LambdaUtil.getFieldName(SouReqApply::getApplyId), new HashMap<>(16));

        SouReqApply apply = souReqApplyMap.getOrDefault(record.get(SouIntDepositRefund::getApplyId), new SouReqApply());
        base.setItemNo(index);
        base.setPartnerType(ApiSettleAcountingPartnerTypeEnum.VENDOR.getCode());
        base.setPartnerCode(apply.getVendorCode());
        base.setPartnerName(apply.getVendorName());
        base.setProfitCenterCode(financeCompany.getProfitCenterCode());
        base.setProfitCenterName(financeCompany.getProfitCenterName());
        base.setDocumentExplain(StringUtils.join(apply.getVendorName(), "意向金开票"));
        base.setContainTax(true);
        base.setCostCenterCode(financeCompany.getCenterCode());
        base.setCostCenterName(financeCompany.getCenterName());
    }

    private void callApiPaymentWithDepositRefundAccountingHead(ApiSettleAcountingRequestHead header, SouReqHead souReqHead, Record record, Map<String, Object> cache){
        FinanceCompany financeCompany = (FinanceCompany) cache.getOrDefault(LambdaUtil.getFieldName(FinanceCompany::getFcId), new FinanceCompany());

        header.setSystemCode(SrmConstant.SYS_SRM);
        header.setBusinessNo(StringUtils.joinWith(SrmConstant.UNDER_LINE, souReqHead.getReqHeadNo(), record.get(SouIntDepositRefund::getApplyId)));
        header.setReqSn(UUIDUtils.getUUID());
        header.setSaleType(ApiSettleAcountingSaleTypeEnum.SERVICE.getCode());
        header.setDeptName(SrmConstant.API_SETTLE_DEAULT_DEPART_NAME);
        header.setCreateUserNo(AppUserUtil.getUserName());
        header.setCreateUserName(AppUserUtil.getLoginAppUser().getNickname());
        header.setAccountUserNo(AppUserUtil.getUserName());
        header.setAccountUserName(AppUserUtil.getLoginAppUser().getNickname());
        header.setCreateTime(toDateStr(new Date()));
        header.setOrgCode(financeCompany.getCompanyCode());
        header.setNeedApprove(false);
        header.setSettleExplain("意向金收入冲销");
    }

    @Override
    public void callBackAsApiPayment(List<SccPjCmsCallbackTemp> sccPjCmsCallbackTempList) {
        if(CollectionUtils.isEmpty(sccPjCmsCallbackTempList)) {
            return;
        }
        List<SccSouPaymentApiRelDto> sccSouPaymentApiRelDtoList = qlService.queryByWrapper(QlWrappers.query(MqlType.SCC_SOU_PAYMENT_API_REL).in(SccSouPaymentApiRelDto::getRequestItemId, sccPjCmsCallbackTempList.stream().map(SccPjCmsCallbackTemp::getRequestItemId).collect(Collectors.toList())), SccSouPaymentApiRelDto.class);

        Map<String, SccPjCmsCallbackTemp> callbackMap = sccPjCmsCallbackTempList.stream().collect(Collectors.toMap(k -> k.getRequestItemId(), v -> v, (k1, k2)->k2));
        if(CollectionUtils.isNotEmpty(sccSouPaymentApiRelDtoList)) {
            Map<String, List<SccSouPaymentApiRelDto>> relMap = sccSouPaymentApiRelDtoList.stream().collect(Collectors.groupingBy(SccSouPaymentApiRelDto::getBusinessType));
            for(String key : relMap.keySet()) {
                callBackAsApiPaymentWithBusinessType(key, relMap.get(key), callbackMap);
            }
        }
    }

    public void callBackAsApiPaymentWithBusinessType(String businessType, List<SccSouPaymentApiRelDto> sccSouPaymentApiRelDtoList, Map<String, SccPjCmsCallbackTemp> callbackMap) {
        switch (businessType) {
            case SrmConstant.CC_API_PAYMENT_BID:
                callBackAsApiPaymentWithBusinessTypeFroBid(sccSouPaymentApiRelDtoList, callbackMap);
                break;
            case SrmConstant.CC_API_PAYMENT_REQ:
                callBackAsApiPaymentWithBusinessTypeFroReq(sccSouPaymentApiRelDtoList, callbackMap);
                break;
            default:
        }
    }

    private void callBackAsApiPaymentWithBusinessTypeFroBid(List<SccSouPaymentApiRelDto> sccSouPaymentApiRelDtoList, Map<String, SccPjCmsCallbackTemp> callbackMap) {
        List<ExtSouMarginRecord> marginRecordList = marginRecordService.listByIds(sccSouPaymentApiRelDtoList.stream().map(r -> strToLong(r.getBusinessCode())).distinct().collect(Collectors.toList()));
        if(CollectionUtils.isEmpty(marginRecordList)) {
            return;
        }

        Map<Long, ExtSouMarginRecord> recordMap = marginRecordList.stream().collect(Collectors.toMap(k -> k.getRecordId(), v -> v, (k1, k2) -> k2));

        List<ExtSouMargin> marginList = marginService.lambdaQuery().in(ExtSouMargin::getMarginId, marginRecordList.stream().map(r -> r.getMarginId()).distinct().collect(Collectors.toList())).list();
        if(CollectionUtils.isEmpty(marginList)) {
            return;
        }
        Map<Long, ExtSouMargin> marginMap = marginList.stream().collect(Collectors.toMap(k -> k.getMarginId(), v -> v, (k1, k2) -> k2));
        //年度保证金
        List<Long> yearMarginIdList = marginList.stream().filter(m -> YesOrNo.YES.getValue().equals(m.getYearFlag()) && Long.compare(SrmConstant.LONG_MINUS_ONE, m.getProjectId()) != 0).map(m -> m.getRelYearMarginId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(yearMarginIdList)) {
            List<ExtSouMargin> yearMarginList = marginService.lambdaQuery().in(ExtSouMargin::getMarginId, yearMarginIdList).list();
            if(CollectionUtils.isNotEmpty(yearMarginList)) {
                yearMarginList.stream().forEach(m -> marginMap.put(m.getMarginId(), m));
            }
        }

        sccSouPaymentApiRelDtoList.stream().forEach(rel -> {
            ExtSouMarginRecord record = recordMap.get(strToLong(rel.getBusinessCode()));
            if(Objects.isNull(record)) {
                return;
            }
            ExtSouMargin margin = marginMap.get(record.getMarginId());
            if(Objects.isNull(margin)) {
                return;
            }
            SccPjCmsCallbackTemp callbackTemp = callbackMap.getOrDefault(rel.getRequestItemId(), new SccPjCmsCallbackTemp());
            if(SrmConstant.SUCCESS_NAME.equals(callbackTemp.getStatus())) {
                refundSuccessMargin(margin, strToDate(callbackTemp.getPaymentDate()));
                if(YesOrNo.YES.getValue().equals(margin.getYearFlag()) && marginMap.containsKey(margin.getRelYearMarginId())) {
                    refundSuccessMargin(marginMap.get(margin.getRelYearMarginId()), strToDate(callbackTemp.getPaymentDate()));
                }
            } else {
                refundFailMargin(margin, callbackTemp.getPaymentDesc());
                if(YesOrNo.YES.getValue().equals(margin.getYearFlag()) && marginMap.containsKey(margin.getRelYearMarginId())) {
                    refundFailMargin(marginMap.get(margin.getRelYearMarginId()), callbackTemp.getPaymentDesc());
                }
            }
        });

        List<ExtSouMargin> updateMarginList = new ArrayList<>(marginMap.values());
        marginService.updateBatchById(updateMarginList);

        List<Long> projectIdList = updateMarginList.stream().map(m -> m.getSourceProjectId()).distinct().collect(Collectors.toList());
        List<ExtSouProject> extSouProjectList = projectService.listByIds(projectIdList);
        Map<Long, ExtSouProject> extSouProjectMap = extSouProjectList.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(),(k1 , k2) -> k2));

        Map<Long, List<ExtSouMargin>> projectMarginGroup = updateMarginList.stream().collect(Collectors.groupingBy(ExtSouMargin::getSourceProjectId));

        for(Long projectId : projectMarginGroup.keySet()) {
            ExtSouProject souProject = extSouProjectMap.get(projectId);
            if(Objects.isNull(souProject)) {
                continue;
            }
            List<ExtSouMargin> subMarginList = projectMarginGroup.get(projectId);
            Map<String, List<ExtSouMargin>> refundStatusGroup = subMarginList.stream().collect(Collectors.groupingBy(k -> k.getRefundStatus()));
            for(String refundStatus : refundStatusGroup.keySet()) {
                if(IntDepositRefundStatusEnum.REFUNDED.getCode().equals(refundStatus)) {
                    sendDingTalkBid(souProject, refundStatusGroup.get(refundStatus), "成功");
                } else {
                    sendDingTalkBid(souProject, refundStatusGroup.get(refundStatus), "失败");
                }
            }
        }

    }

    /**
     * 更新成功
     * @param margin
     * @param paymentDate
     */
    private void refundSuccessMargin(ExtSouMargin margin, Date paymentDate) {
        margin.setRefundStatus(IntDepositRefundStatusEnum.REFUNDED.getCode());
        margin.setRefundPaymentDate(paymentDate);
        margin.setRefundFailCause("");
    }

    /**
     * 更新失败
     * @param margin
     * @param refundFailCause
     */
    private void refundFailMargin(ExtSouMargin margin, String refundFailCause) {
        margin.setRefundStatus(IntDepositRefundStatusEnum.REFUND_FAILED.getCode());
        margin.setRefundFailCause(refundFailCause);
    }

    private Date strToDate(String dateStr) {
        if(StringUtils.isNotBlank(dateStr)) {
            try {
                return DateUtil.parseDate(dateStr);
            } catch (Exception e) {
                log.error("strToDate Exception", e);
            }
        }
        return null;
    }

    private void callBackAsApiPaymentWithBusinessTypeFroReq(List<SccSouPaymentApiRelDto> sccSouPaymentApiRelDtoList, Map<String, SccPjCmsCallbackTemp> callbackMap) {

        List<SouReqApply> souReqApplyList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY)
                .in(SouReqApply::getApplyId, sccSouPaymentApiRelDtoList.stream().map(s -> strToLong(s.getBusinessCode())).distinct().collect(Collectors.toList())), SouReqApply.class);
        if(CollectionUtils.isEmpty(souReqApplyList)) {
            return;
        }

        Map<Long, SouReqApply> applyMap = souReqApplyList.stream().collect(Collectors.toMap(k -> k.getApplyId(), v -> v, (k1, k2) -> k2));

        List<Long> reqHeadIdList = new ArrayList<>(15);
        sccSouPaymentApiRelDtoList.stream().forEach(rel -> {
            SouReqApply apply = applyMap.get(strToLong(rel.getBusinessCode()));
            if(Objects.isNull(apply)) {
                return;
            }

            SccPjCmsCallbackTemp callbackTemp = callbackMap.getOrDefault(rel.getRequestItemId(), new SccPjCmsCallbackTemp());
            if(SrmConstant.SUCCESS_NAME.equals(callbackTemp.getStatus())) {
                apply.setDepositRefundStatus(IntDepositRefundStatusEnum.REFUNDED.getCode());
                apply.setRefundPaymentDate(strToDate(callbackTemp.getPaymentDate()));
                apply.setRefundFailCause("");
            } else {
                apply.setDepositRefundStatus(IntDepositRefundStatusEnum.REFUND_FAILED.getCode());
                apply.setRefundFailCause(callbackTemp.getPaymentDesc());
            }
            reqHeadIdList.add(apply.getReqHeadId());
        });

        List<SouReqApply> updateReqApplyList = new ArrayList<>(applyMap.values());

        qlService.save(MqlType.SOU_REQ_APPLY, updateReqApplyList);

        List<SouReqHead> souReqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).in(SouReqHead::getReqHeadId, reqHeadIdList), SouReqHead.class);
        Map<Long, SouReqHead> souReqHeadMap = souReqHeadList.stream().collect(Collectors.toMap(k -> k.getReqHeadId(), Function.identity(), (k1, k2) -> k2));

        Map<Long, List<SouReqApply>> headApplyGroup = updateReqApplyList.stream().collect(Collectors.groupingBy(SouReqApply::getReqHeadId));

        for(Long reqHeadId : headApplyGroup.keySet()) {
            SouReqHead souReqHead = souReqHeadMap.get(reqHeadId);
            if(Objects.isNull(souReqHead)) {
                continue;
            }
            Map<String, Object> cache = new HashMap<>(16);
            /** 供应商负责人 */
            if(ObjectUtils.allNotNull(souReqHead.getResponsibilityUserId())) {
                User user = rbacClient.getUserByIdAnon(souReqHead.getResponsibilityUserId());
                cache.put(LambdaUtil.getFieldName(SouReqHead::getResponsibilityUserId), user);
            }

            List<SouReqApply> subApplyList = headApplyGroup.get(reqHeadId);

            Map<String, List<SouReqApply>> refundStatusApplyGroup = subApplyList.stream().collect(Collectors.groupingBy(SouReqApply::getDepositRefundStatus));

            for(String refundStatus : refundStatusApplyGroup.keySet()) {
                if(IntDepositRefundStatusEnum.REFUNDED.getCode().equals(refundStatus)) {
                    sendDingTalkReq(souReqHead, refundStatusApplyGroup.get(refundStatus), "成功", cache);
                } else {
                    sendDingTalkReq(souReqHead, refundStatusApplyGroup.get(refundStatus), "失败", cache);
                }
            }
        }
    }

    private Long strToLong(String value) {
        if(StringUtils.isBlank(value)) {
            return SrmConstant.LONG_MINUS_ONE;
        }
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            log.error("callBackAsApiPaymentWithBusinessTypeFroBid Exception", e);
        }
        return SrmConstant.LONG_MINUS_ONE;
    }
}
