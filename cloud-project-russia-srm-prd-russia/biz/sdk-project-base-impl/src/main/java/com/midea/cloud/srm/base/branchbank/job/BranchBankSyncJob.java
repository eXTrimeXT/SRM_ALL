package com.midea.cloud.srm.base.branchbank.job;


import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.base.organization.entity.ErpBranchBank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author GW00311146
 */
@Job("BranchBankSyncJob")
@Slf4j
public class BranchBankSyncJob implements ExecuteableJob {

    @Autowired
    PjProjectExtClient pjProjectExtClient;
    @Autowired
    private QlService qlService;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {

            List<ErpBranchBank> erpBranchBankList = pjProjectExtClient.bankSync();
            if (erpBranchBankList.isEmpty()) {
                return BaseResult.buildSuccess("同步银行支行信息定时任务-执行成功！");
            }
            List<String> branchBankNumList = erpBranchBankList.stream().map(ErpBranchBank::getBranchBankNum).collect(Collectors.toList());

            List<ErpBranchBank> queryExistList = qlService.query("BranchBank", MeiQl.newCondition()
                    .in(ErpBranchBank::getBranchBankNum, branchBankNumList), ErpBranchBank.class);

            if (queryExistList.isEmpty()) {
                qlService.save("BranchBank", erpBranchBankList);
                return BaseResult.buildSuccess("同步银行支行信息定时任务-执行成功！");
            }


            Map<String, ErpBranchBank> queryExistMap = queryExistList.stream()
                    .collect(Collectors.toMap(ErpBranchBank::getBranchBankNum, e -> e));
            // 准备保存和更新列表
            List<ErpBranchBank> updateBankList = new ArrayList<>();
            List<ErpBranchBank> newBankList = new ArrayList<>();
            // 遍历新数据并进行分类处理
            for (ErpBranchBank erpBranchBank : erpBranchBankList) {
                String branchBankNum = erpBranchBank.getBranchBankNum();
                if (queryExistMap.containsKey(branchBankNum)) {
                    // 更新已有记录
                    ErpBranchBank queryExist = queryExistMap.get(branchBankNum);
                    ErpBranchBank updateBank = new ErpBranchBank();
                    updateBank.setBranchBankId(queryExist.getBranchBankId());
                    updateBank.setBankName(erpBranchBank.getBankName());
                    updateBank.setBankNum(queryExist.getBankNum());
                    updateBank.setBranchBankName(erpBranchBank.getBranchBankName());
                    updateBank.setBranchBankNum(queryExist.getBranchBankNum());

                    updateBankList.add(updateBank);
                } else {
                    // 添加新的记录
                    newBankList.add(erpBranchBank);
                }
            }
            // 执行保存和更新操作
            if (!newBankList.isEmpty()) {
                qlService.save("BranchBank", newBankList);
            }
            if (!updateBankList.isEmpty()) {
                qlService.update("BranchBank", updateBankList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.buildSuccess("同步银行支行信息定时任务-执行失败！");
        }
        return BaseResult.buildSuccess("同步银行支行信息定时任务-执行成功！");
    }
}
