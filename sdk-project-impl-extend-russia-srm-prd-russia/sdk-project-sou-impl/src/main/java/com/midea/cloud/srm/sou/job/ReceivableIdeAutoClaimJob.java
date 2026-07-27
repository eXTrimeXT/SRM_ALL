package com.midea.cloud.srm.sou.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceUseRecord;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositStatusEnum;
import com.midea.cloud.srm.sou.deposit.dto.FinanceDto;
import com.midea.cloud.srm.sou.deposit.mapper.FinanceUseRecordMapper;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 意向金自动认领(应收单撤销查询接口)
 * @author ex_liuxy46
 */
@Slf4j
@Job("receivableIdeAutoClaimJob")
public class ReceivableIdeAutoClaimJob implements ExecuteableJob {

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private SouReqHeadService souReqHeadService;

    @Resource
    private SouReqApplyService reqApplyService;

    @Resource
    private FinanceUseRecordMapper financeUseRecordMapper;

    /**
     * 应收单撤销查询接口
     */
    private static final String RECEIVABLE = "RECEIVABLE";

    /**
     * 定时（20分钟）查询“应收单撤销查询接口”，如果查询到数据，把“供应商缴纳账户”“缴纳户名”“缴纳银行”“银行联行号”清空，意向金缴纳状态改成“待确认”
     * @param params 参数
     * @return 返回
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        LambdaQueryWrapper<FinanceUseRecord> cxQuery = new LambdaQueryWrapper<>();
        cxQuery.eq(FinanceUseRecord::getRecordType, "Y");
        cxQuery.eq(FinanceUseRecord::getClaimStatus, "Y");
        List<FinanceUseRecord> cxList = financeUseRecordMapper.selectList(cxQuery);
        if (CollectionUtils.isNotEmpty(cxList)) {
            //应收单撤销查询接口
            List<String> noList = cxList.stream().map(FinanceUseRecord::getSystemSourceNo).collect(Collectors.toList());
            String paramStr = FinanceDto.dealCxData(noList);
            log.info("应收单撤销查询接口==={}", JSONObject.toJSONString(paramStr));
            String str = pjSouClient.sendFinance(paramStr, RECEIVABLE);
            log.info("应收单撤销查询接口返回结果=={}" + JSONObject.toJSONString(str));
//            String res = str.replace("\\r", "").replace("\\n", "").replace("\\", "");
            //JSONObject object = JSONObject.parseObject(res);
            JSONObject object = JSONObject.parseObject(StringEscapeUtils.unescapeJava(str));
            JSONObject cmsCloud = object.getJSONObject("Cmscloud").getJSONObject("Body").getJSONObject("Data");
            if ("0000".equals(cmsCloud.get("ResultCode"))) {
                JSONArray jsonArray = cmsCloud.getJSONArray("ResultSet");
                if (jsonArray != null) {
                    for (Object o : jsonArray) {
                        JSONObject jo = (JSONObject)o;
                        String systemSourceNo = jo.getString("SystemSourceNo");
                        String[] arr = systemSourceNo.split("_");
                        List<SouReqHead> souReqHeadList = souReqHeadService.list(new LambdaQueryWrapper<SouReqHead>().eq(SouReqHead::getReqHeadNo, arr[0]));
                        if (CollectionUtils.isNotEmpty(souReqHeadList)) {
                            LambdaUpdateWrapper<SouReqApply> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                            lambdaUpdateWrapper.set(SouReqApply::getDepositStatus, IntDepositStatusEnum.TO_CONFIRM.getCode()).
                                    set(SouReqApply::getVendorBankAccount, "").
                                    set(SouReqApply::getVendorBankAccountName, "").
                                    set(SouReqApply::getVendorBankName, "").
                                    set(SouReqApply::getVendorBankNumber, "").
                                    eq(SouReqApply::getReqHeadId, souReqHeadList.get(0).getReqHeadId()).
                                    eq(SouReqApply::getVendorCode, arr[1]);
                            reqApplyService.update(lambdaUpdateWrapper);
                            LambdaUpdateWrapper<FinanceUseRecord> updateWrapper = new LambdaUpdateWrapper<>();
                            updateWrapper.set(FinanceUseRecord::getClaimStatus, "N");
                            updateWrapper.eq(FinanceUseRecord::getSystemSourceNo, systemSourceNo);
                            financeUseRecordMapper.update(null, updateWrapper);
                        }
                    }
                }
            } else {
                return BaseResult.build(ResultCode.UNKNOWN_ERROR, cmsCloud.get("ResultCode") + "---" + cmsCloud.get("ResultMsg"));
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }
}
