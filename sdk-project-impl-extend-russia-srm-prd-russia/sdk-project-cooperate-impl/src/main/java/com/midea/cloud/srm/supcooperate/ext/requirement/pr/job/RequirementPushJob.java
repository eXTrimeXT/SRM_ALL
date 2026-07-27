package com.midea.cloud.srm.supcooperate.ext.requirement.pr.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PrPushConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author zenghx2
 */
@Job("requirementPushJob")
@Slf4j
public class RequirementPushJob implements ExecuteableJob {

    @Autowired
    private PrPushConfigService prPushConfigService;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            prPushConfigService.autoPushPoolTask();
        } catch (Exception e) {
            log.error("执行定时任务【采购申请自动进入需求池】异常", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e.getMessage());
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }
}
