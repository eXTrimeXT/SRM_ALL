package com.midea.cloud.srm.sou.req.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/2 15:32
 *  修改内容:
 * </pre>
 */
@Job("scheduledSouReqApplyJob")
public class ScheduledSouReqApplyJob implements ExecuteableJob {
    @Autowired
    private SouReqHeadService reqHeadService;
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        reqHeadService.handleSignupDone();
        return BaseResult.buildSuccess("执行成功！");
    }
}
