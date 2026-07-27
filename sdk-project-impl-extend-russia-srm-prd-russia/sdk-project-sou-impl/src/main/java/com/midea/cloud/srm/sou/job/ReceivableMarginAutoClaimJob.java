package com.midea.cloud.srm.sou.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceUseRecord;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.deposit.dto.FinanceDto;
import com.midea.cloud.srm.sou.deposit.mapper.FinanceUseRecordMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 保证金自动认领(应收单撤销查询接口)
 * @author ex_liuxy46
 */
@Slf4j
@Job("receivableMarginAutoClaimJob")
public class ReceivableMarginAutoClaimJob implements ExecuteableJob {

    @Resource
    private FinanceUseRecordMapper financeUseRecordMapper;

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private IExtSouProjectService projectService;

    @Resource
    private IExtSouMarginService marginService;

    /**
     * 应收单撤销查询接口
     */
    private static final String RECEIVABLE = "RECEIVABLE";

    /**
     * 定时（20分钟）查询“应收单撤销查询接口”，如果查询到数据，如果是保证金，把“供应商缴纳账户”“缴纳户名”“缴纳银行”清空，缴纳状态改成“待确认”
     * @param params 参数
     * @return 返回
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        LambdaQueryWrapper<FinanceUseRecord> cxQuery = new LambdaQueryWrapper<>();
        cxQuery.eq(FinanceUseRecord::getRecordType, "B");
        cxQuery.eq(FinanceUseRecord::getClaimStatus, "Y");
        List<FinanceUseRecord> cxList = financeUseRecordMapper.selectList(cxQuery);
        if (CollectionUtils.isNotEmpty(cxList)) {
            //应收单撤销查询接口
            List<String> noList = cxList.stream().map(FinanceUseRecord::getSystemSourceNo).collect(Collectors.toList());
            String paramStr = FinanceDto.dealCxData(noList);
            log.info("应收单撤销查询接口==={}", JSONObject.toJSONString(paramStr));
            String str = pjSouClient.sendFinance(paramStr, RECEIVABLE);
            log.info("应收单撤销查询接口返回结果=={}" + JSONObject.toJSONString(str));
            /*String res = str.replace("\\r", "").replace("\\n", "").replace("\\", "");
            JSONObject object = JSONObject.parseObject(res);*/
            JSONObject object = JSONObject.parseObject(StringEscapeUtils.unescapeJava(str));
            JSONObject cmsCloud = object.getJSONObject("Cmscloud").getJSONObject("Body").getJSONObject("Data");
            if ("0000".equals(cmsCloud.get("ResultCode"))) {
                JSONArray jsonArray = cmsCloud.getJSONArray("ResultSet");
                if (jsonArray != null) {
                    for (Object o : jsonArray) {
                        JSONObject jo = (JSONObject)o;
                        String systemSourceNo = jo.getString("SystemSourceNo");
                        String[] arr = systemSourceNo.split("_");
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


                        LambdaUpdateWrapper<ExtSouMargin> souMarginUpdateWrapper = new LambdaUpdateWrapper<>();
                        souMarginUpdateWrapper.set(ExtSouMargin::getMarginStatus, SouBidMarginStatusEnum.CONFIRM_TODO.getCode());
                        souMarginUpdateWrapper.set(ExtSouMargin::getHanderMode, "");
                        souMarginUpdateWrapper.set(ExtSouMargin::getPayBank, "");
                        souMarginUpdateWrapper.set(ExtSouMargin::getPayAccountName, "");
                        souMarginUpdateWrapper.set(ExtSouMargin::getPayAccount, "");
                        souMarginUpdateWrapper.set(ExtSouMargin::getBankLine, "");
                        souMarginUpdateWrapper.in(ExtSouMargin::getMarginId, marginIdList);

                        marginService.update(souMarginUpdateWrapper);
                        LambdaUpdateWrapper<FinanceUseRecord> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.set(FinanceUseRecord::getClaimStatus, "N");
                        updateWrapper.eq(FinanceUseRecord::getSystemSourceNo, systemSourceNo);
                        financeUseRecordMapper.update(null, updateWrapper);
                    }
                }
            } else {
                return BaseResult.build(ResultCode.UNKNOWN_ERROR, cmsCloud.get("ResultCode") + "---" + cmsCloud.get("ResultMsg"));
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }
}
