package com.midea.cloud.srm.sou.deposit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.feign.ExtSupplierClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceCompany;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceUseRecord;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.sou.deposit.dto.FinanceDto;
import com.midea.cloud.srm.sou.deposit.mapper.FinanceCompanyMapper;
import com.midea.cloud.srm.sou.deposit.mapper.FinanceUseRecordMapper;
import com.midea.cloud.srm.sou.deposit.service.FinanceService;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Slf4j
@Service
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private FinanceCompanyMapper financeCompanyMapper;

    @Resource
    private FinanceUseRecordMapper financeUseRecordMapper;

    @Resource
    protected QlService qlService;

    @Resource
    private IExtSouProjectService projectService;

    @Resource
    private ExtSupplierClient extSupplierClient;

    @Resource
    private SouReqApplyService reqApplyService;

    @Resource
    private IExtSouMarginService marginService;

    /**
     * 接收应收单信息接口
     */
    private static final String RECEIVE = "RECEIVE";

    private final static String NUM_0000 = "0000";
    private final static String RESULT_CODE = "ResultCode";
    private final static String LETTER_B = "B";

    /**
     * 意向金缴款
     * @param reqHeadId 寻源id
     * @param payAccountName 意向金/保证金付款方名称，付款账号名称(付款方名称)
     * @param vendorId 供应商id
     */
    @Override
    public void dealIntentionalDepositPayment(Long reqHeadId, String payAccountName, Long vendorId) {
        //来源系统单据号
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, reqHeadId, SouReqHead.class);
        LambdaUpdateWrapper<SouReqApply> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SouReqApply::getReqHeadId, reqHeadId);
        lambdaUpdateWrapper.eq(SouReqApply::getVendorId, vendorId);
        if (YesOrNo.NO.getValue().equals(souReqHead.getIsNeedDeposit())) {
            lambdaUpdateWrapper.set(SouReqApply::getDepositStatus, IntDepositStatusEnum.NOT_APPLICABLE.getCode());
            reqApplyService.update(lambdaUpdateWrapper);
            return;
        }
        lambdaUpdateWrapper.set(SouReqApply::getDepositStatus, IntDepositStatusEnum.TO_CONFIRM.getCode());
        reqApplyService.update(lambdaUpdateWrapper);
        List<FinanceCompany> fcList = financeCompanyMapper.selectList(new LambdaQueryWrapper<FinanceCompany>().eq(FinanceCompany::getAccount, souReqHead.getBankAccount().replace(" ", "")));
        if (CollectionUtils.isEmpty(fcList)) {
//            throw new BaseException("收款账户未维护财务相关信息，请联系长城对接人");
            //收款账户未维护财务相关信息 增加线下流程
            return;
        }
        CompanyInfo companyInfo = extSupplierClient.getCompanyInfo(vendorId);
//        String systemSourceNo = String.format("%s_%s_%s", souReqHead.getReqHeadNo() + ThreadLocalRandom.current().nextInt(100, 1000), companyInfo.getCompanyCode(), "Y");
        String systemSourceNo = String.format("%s_%s_%s", souReqHead.getReqHeadNo(), companyInfo.getCompanyCode(), "Y");
        String paramStr = FinanceDto.dealData(fcList.get(0), souReqHead, companyInfo, payAccountName, systemSourceNo);
        log.info("意向金接收应收单信息接口===" + paramStr);
        String str = pjSouClient.sendFinance(paramStr, RECEIVE);
        /*log.info("---" + str);
        String res = str.replace("\\r", "").replace("\\n", "").replace("\\t", "").replace("\\", "");
        log.info(res);
        JSONObject jo = JSONObject.parseObject(res);*/
        JSONObject jo = JSONObject.parseObject(StringEscapeUtils.unescapeJava(str));
        log.info("---" + jo);
        log.info(JSON.toJSONString(jo));
        JSONObject cmsCloud = jo.getJSONObject("Cmscloud").getJSONObject("Body").getJSONObject("Data");
        if (!NUM_0000.equals(cmsCloud.get(RESULT_CODE))) {
            String msg = cmsCloud.getString("ResultMsg");
            throw new BaseException(msg);
        } else {
            financeUseRecordMapper.insert(dealFinanceUseRecord("Y", systemSourceNo));
        }
    }

    /**
     * 保证金缴款
     * @param extSouMargin 保证金
     * @param payAccountName 付款方账号
     * @param vendorId 供应商
     */
    @Override
    public void dealEarnestMoneyDepositPayment(ExtSouMargin extSouMargin, String payAccountName, Long vendorId) {
        //查询项目信息
        ExtSouProject souProject = projectService.getById(extSouMargin.getProjectId());
        if (YesOrNo.NO.getValue().equals(souProject.getExtEarnestFlag())) {
            return;
        }
        if (souProject.getExtEarnestAmount() == null || souProject.getExtEarnestAmount().compareTo(new BigDecimal(0)) == 0) {
            throw new BaseException("保证金金额异常，请联系招标负责人");
        }
        CompanyInfo companyInfo = extSupplierClient.getCompanyInfo(vendorId);
        String systemSourceNo = String.format("%s_%s_%s", souProject.getSouNo(), companyInfo.getCompanyCode(), "B");

        if(checkExistsFinanceUseRecord(systemSourceNo, LETTER_B)) {
            log.info(MessageFormat.format("缴纳保证金存在调用接口成功记录，不重复处理: {0}", systemSourceNo));
            //存在成功记录时不调用接口
            return;
        }

        LambdaUpdateWrapper<ExtSouMargin> souMarginUpdateWrapper = new LambdaUpdateWrapper<>();
        souMarginUpdateWrapper.eq(ExtSouMargin::getMarginId, extSouMargin.getMarginId());
        souMarginUpdateWrapper.set(ExtSouMargin::getMarginStatus, SouBidMarginStatusEnum.CONFIRM_TODO.getCode());
        marginService.update(souMarginUpdateWrapper);
        List<FinanceCompany> fcList = financeCompanyMapper.selectList(new LambdaQueryWrapper<FinanceCompany>().eq(FinanceCompany::getAccount, souProject.getExtBankAccount().replace(" ", "")));
        if (CollectionUtils.isEmpty(fcList)) {
            //throw new BaseException("收款账户未维护财务相关信息，请联系长城对接人");
            //收款账户未维护财务相关信息 增加线下流程
            return;
        }

        String paramStr = FinanceDto.dealData(souProject, companyInfo, fcList.get(0), extSouMargin, payAccountName, systemSourceNo);
        log.info("保证金接收应收单信息接口===" + paramStr);
        String str = pjSouClient.sendFinance(paramStr, RECEIVE);
        /*String res = str.replace("\\r", "").replace("\\n", "").replace("\\", "");
        log.info(res);
        JSONObject jo = JSONObject.parseObject(res);*/
        JSONObject jo = JSONObject.parseObject(StringEscapeUtils.unescapeJava(str));
        log.info(JSON.toJSONString(jo));
        JSONObject cmsCloud = jo.getJSONObject("Cmscloud").getJSONObject("Body").getJSONObject("Data");
        if (!NUM_0000.equals(cmsCloud.get(RESULT_CODE))) {
            String msg = cmsCloud.getString("ResultMsg");
            throw new BaseException(msg);
        } else {
            financeUseRecordMapper.insert(dealFinanceUseRecord("B", systemSourceNo));
        }
    }

    /**
     * 判断记录是否存在
     * @param systemSourceNo
     * @param recordType
     * @return
     */
    private boolean checkExistsFinanceUseRecord(String systemSourceNo, String recordType) {
        LambdaQueryWrapper<FinanceUseRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FinanceUseRecord::getSystemSourceNo,systemSourceNo);
        wrapper.eq(FinanceUseRecord::getRecordType, recordType);
        return financeUseRecordMapper.exists(wrapper);
    }

    @Override
    public boolean existsFinanceUseRecord(String reqHeadNo, String companyCode) {
        String systemSourceNo = String.format("%s_%s_%s", reqHeadNo, companyCode, "Y");
        LambdaQueryWrapper<FinanceUseRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FinanceUseRecord::getSystemSourceNo,systemSourceNo);
        wrapper.eq(FinanceUseRecord::getRecordType, "Y");
        return financeUseRecordMapper.exists(wrapper);
    }

    public FinanceUseRecord dealFinanceUseRecord(String rt, String ssn) {
        FinanceUseRecord fur = new FinanceUseRecord();
        fur.setSystemSourceNo(ssn);
        fur.setRecordType(rt);
        fur.setClaimStatus("N");
        return fur;
    }

    @Override
    public FinanceCompany queryFinanceCompany(String account) {

        if(StringUtils.isBlank(account)) {
            return new FinanceCompany();
        }

        account = account.replaceAll(" ", "");

        LambdaQueryWrapper<FinanceCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinanceCompany::getAccount, account);

        List<FinanceCompany> financeCompanyList = financeCompanyMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(financeCompanyList)) {
            return new FinanceCompany();
        }
        FinanceCompany financeCompany = financeCompanyList.get(0);
        //去掉空格
        financeCompany.setProfitCenterCode(ObjectUtils.defaultIfNull(financeCompany.getProfitCenterCode(), "").replaceAll(" ", ""));
        financeCompany.setProfitCenterName(ObjectUtils.defaultIfNull(financeCompany.getProfitCenterName(), "").trim());
        financeCompany.setCenterCode(ObjectUtils.defaultIfNull(financeCompany.getCenterCode(), "").replaceAll(" ", ""));
        financeCompany.setCenterName(ObjectUtils.defaultIfNull(financeCompany.getCenterName(), "").trim());
        return financeCompany;
    }
}
