package com.midea.cloud.srm.sou.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceUseRecord;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.deposit.dto.FinanceDto;
import com.midea.cloud.srm.sou.deposit.mapper.FinanceUseRecordMapper;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import com.mideacloud.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 意向金自动认领(认领结果查询接口)
 * @author ex_liuxy46
 */
@Slf4j
@Job("claimIdeAutoClaimJob")
public class ClaimIdeAutoClaimJob implements ExecuteableJob {

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private SouReqHeadService souReqHeadService;

    @Resource
    private SouReqApplyService reqApplyService;

    @Resource
    private FinanceUseRecordMapper financeUseRecordMapper;

    @Resource
    private QlOpenClient qlOpenClient;

    /**
     * 认领结果查询接口
     */
    private static final String CLAIM = "CLAIM";

    /**
     * 定时（20分钟）扫描意向金状态为“待确认”，调用“认领结果查询接口”，查询成功，回写“供应商缴纳账户”“缴纳户名”“缴纳银行”“银行联行号”，并把状态改成已缴纳
     * @param params 参数
     * @return 返回
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        LambdaQueryWrapper<FinanceUseRecord> cxQuery = new LambdaQueryWrapper<>();
        cxQuery.eq(FinanceUseRecord::getRecordType, "Y");
        cxQuery.eq(FinanceUseRecord::getClaimStatus, "N");
        List<FinanceUseRecord> rlList = financeUseRecordMapper.selectList(cxQuery);
        if (CollectionUtils.isNotEmpty(rlList)) {
            //认领结果查询接口
            List<String> noList = rlList.stream().map(FinanceUseRecord::getSystemSourceNo).collect(Collectors.toList());
            String reQu = FinanceDto.dealRlData(noList);
            log.info("认领结果查询接口==={}", JSONObject.toJSONString(reQu));
            log.info(JSONObject.toJSONString(reQu));
            String reQuStr = pjSouClient.sendFinance(reQu, CLAIM);
            log.info("认领结果查询接口返回结果==={}", JSONObject.toJSONString(reQuStr));
            JSONObject reQuObject = JSONObject.parseObject(StringEscapeUtils.unescapeJava(reQuStr));
            JSONObject reQuCmsCloud = reQuObject.getJSONObject("Cmscloud").getJSONObject("Body").getJSONObject("Data");
            if ("0000".equals(reQuCmsCloud.get("ResultCode"))) {
                JSONArray jsonArray = reQuCmsCloud.getJSONArray("ResultSet");
                if (jsonArray != null) {
                    for (Object o : jsonArray) {
                        JSONObject jo = (JSONObject)o;
                        if (SrmConstant.NUM_THREE.toString().equals(jo.getString("WriteOffStatus"))) {
                            String systemSourceNo = jo.getString("SystemSourceNo");
                            String[] arr = systemSourceNo.split("_");
                            JSONArray bankFlowSubBOList = jo.getJSONArray("BankFlowSubBOList");
                            log.info("bankFlowSubBOList===" + JSONObject.toJSONString(bankFlowSubBOList));
                            if (CollectionUtils.isNotEmpty(bankFlowSubBOList)) {
                                List<SouReqHead> souReqHeadList = souReqHeadService.list(new LambdaQueryWrapper<SouReqHead>().eq(SouReqHead::getReqHeadNo, arr[0]));
                                log.info("souReqHeadList===" + JSONObject.toJSONString(souReqHeadList));
                                if (CollectionUtils.isNotEmpty(souReqHeadList)) {
                                    JSONObject bankFlowSubBO = (JSONObject)bankFlowSubBOList.get(0);
                                    log.info("bankFlowSubBO===" + JSONObject.toJSONString(bankFlowSubBO));
                                    List<Record> bankList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("BranchBank")
                                            .eq("branchBankName", bankFlowSubBO.getString("OppBankName")), Record.class);
                                    log.info("bankList===" + JSONObject.toJSONString(bankList));
                                    LambdaUpdateWrapper<SouReqApply> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                                    lambdaUpdateWrapper.set(SouReqApply::getDepositStatus, IntDepositStatusEnum.PAID.getCode()).
                                            set(SouReqApply::getVendorBankAccount, bankFlowSubBO.getString("OppAccountNo")).
                                            set(SouReqApply::getVendorBankAccountName, bankFlowSubBO.getString("OppAccountName")).
                                            set(SouReqApply::getVendorBankName, bankFlowSubBO.getString("OppBankName")).
                                            set(SouReqApply::getTransTime, strToDate(bankFlowSubBO.getString("TransTime"))).
                                            set(SouReqApply::getVendorBankNumber, CollectionUtils.isEmpty(bankList) ? "" : bankList.get(0).getString("branchBankNum")).
                                            eq(SouReqApply::getReqHeadId, souReqHeadList.get(0).getReqHeadId()).
                                            eq(SouReqApply::getVendorCode, arr[1]);
                                    reqApplyService.update(lambdaUpdateWrapper);
                                    LambdaUpdateWrapper<FinanceUseRecord> updateWrapper = new LambdaUpdateWrapper<>();
                                    updateWrapper.set(FinanceUseRecord::getClaimStatus, "Y");
                                    updateWrapper.eq(FinanceUseRecord::getSystemSourceNo, systemSourceNo);
                                    financeUseRecordMapper.update(null, updateWrapper);
                                }
                            }
                        }
                    }
                }
            } else {
                return BaseResult.build(ResultCode.UNKNOWN_ERROR, reQuCmsCloud.get("ResultCode") + "---" + reQuCmsCloud.get("ResultMsg"));
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

    private Date strToDate(String value) {
        if(StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return DateUtil.parseDate(value);
        } catch (Exception e) {
            log.error("strToDate Exception", e);
        }
        return null;
    }
}
