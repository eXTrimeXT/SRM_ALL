package com.midea.cloud.srm.sou.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceUseRecord;
import com.midea.cloud.srm.model.sou.enums.MarginHanderModeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositRefundStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.deposit.dto.FinanceDto;
import com.midea.cloud.srm.sou.deposit.mapper.FinanceUseRecordMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.mideacloud.common.util.DateUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 保证金自动认领(认领结果查询接口)
 * @author ex_liuxy46
 */
@Slf4j
@Job("claimMarginAutoClaimJob")
public class ClaimMarginAutoClaimJob implements ExecuteableJob {

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private IExtSouMarginService marginService;

    @Resource
    private FinanceUseRecordMapper financeUseRecordMapper;

    @Resource
    private IExtSouProjectService projectService;

    @Resource
    private QlOpenClient qlOpenClient;

    /**
     * 认领结果查询接口
     */
    private static final String CLAIM = "CLAIM";

    /**
     * 定时（20分钟）扫描缴纳状态为“待确认”，调用“认领结果查询接口”，查询成功，回写“供应商缴纳账户”“缴纳户名”“缴纳银行”，并把状态改成已缴纳
     * @param params 参数
     * @return 返回
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        LambdaQueryWrapper<FinanceUseRecord> cxQuery = new LambdaQueryWrapper<>();
        cxQuery.eq(FinanceUseRecord::getRecordType, "B");
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
                            if (CollectionUtils.isNotEmpty(bankFlowSubBOList)) {
                                List<ExtSouProject> souProjectList = projectService.list(new LambdaQueryWrapper<ExtSouProject>().eq(ExtSouProject::getSouNo, arr[0]));
                                if (CollectionUtils.isEmpty(souProjectList)) {
                                    break;
                                }

                                List<ExtSouMargin> extSouMarginList = marginService.lambdaQuery()
                                        .eq(ExtSouMargin::getProjectId, souProjectList.get(0).getProjectId())
                                        .eq(ExtSouMargin::getVendorCode, arr[1])
                                        .list();

                                if(CollectionUtils.isEmpty(extSouMarginList)) {
                                    break;
                                }

                                List<Long> marginIdList = new ArrayList<>(16);
                                extSouMarginList.stream().forEach(margin -> {
                                    if(YesOrNo.YES.getValue().equals(margin.getYearFlag()) && SrmConstant.LONG_MINUS_ONE.compareTo(margin.getProjectId()) != 0) {
                                        marginIdList.add(margin.getRelYearMarginId());
                                    } else {
                                        marginIdList.add(margin.getMarginId());
                                    }
                                });

                                JSONObject bankFlowSubBO = (JSONObject)bankFlowSubBOList.get(0);
                                List<Record> bankList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("BranchBank")
                                        .eq("branchBankName", bankFlowSubBO.getString("OppBankName")), Record.class);
                                LambdaUpdateWrapper<ExtSouMargin> souMarginUpdateWrapper = new LambdaUpdateWrapper<>();
                                souMarginUpdateWrapper.set(ExtSouMargin::getMarginStatus, SouBidMarginStatusEnum.PAY.getCode());
                                souMarginUpdateWrapper.set(ExtSouMargin::getHanderMode, MarginHanderModeEnum.ON_LINE.getCode());
                                souMarginUpdateWrapper.set(ExtSouMargin::getRefundStatus, IntDepositRefundStatusEnum.NOT_REFUNDED.getCode());
                                souMarginUpdateWrapper.set(ExtSouMargin::getChargeAmount, new BigDecimal("0"));
                                souMarginUpdateWrapper.set(ExtSouMargin::getRefundAmount, jo.getBigDecimal("WriteOffAmount").divide(new BigDecimal(10000)));
                                souMarginUpdateWrapper.set(ExtSouMargin::getPayBank, bankFlowSubBO.getString("OppBankName"));
                                souMarginUpdateWrapper.set(ExtSouMargin::getPayAccountName, bankFlowSubBO.getString("OppAccountName"));
                                souMarginUpdateWrapper.set(ExtSouMargin::getPayAccount, bankFlowSubBO.getString("OppAccountNo"));
                                souMarginUpdateWrapper.set(ExtSouMargin::getTransTime, strToDate(bankFlowSubBO.getString("TransTime")));
                                souMarginUpdateWrapper.set(ExtSouMargin::getBankLine, CollectionUtils.isEmpty(bankList) ? "" : bankList.get(0).getString("branchBankNum"));
                                souMarginUpdateWrapper.set(ExtSouMargin::getPayAmount, jo.getBigDecimal("WriteOffAmount").divide(new BigDecimal(10000)));
                                souMarginUpdateWrapper.in(ExtSouMargin::getMarginId, marginIdList);
                                marginService.update(souMarginUpdateWrapper);
                                LambdaUpdateWrapper<FinanceUseRecord> updateWrapper = new LambdaUpdateWrapper<>();
                                updateWrapper.set(FinanceUseRecord::getClaimStatus, "Y");
                                updateWrapper.eq(FinanceUseRecord::getSystemSourceNo, systemSourceNo);
                                financeUseRecordMapper.update(null, updateWrapper);
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
