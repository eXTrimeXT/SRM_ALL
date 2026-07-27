package com.midea.cloud.srm.supcooperate.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjProjectBidExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouRequirementForDataSubmit;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplan.service.PrSouRequirementQueryService;
import com.midea.cloud.srm.supcooperate.utils.DingTalkSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 需求提交截止24小时前钉钉提醒任务
 * 【二批次】招标项目计划（计划中递交招标资料时间 但未提交）
 * BUG2024010400042
 * @author 100014336 ganyh19
 */
@Job("requirementSubmitDataRemindJob")
@Slf4j
public class RequirementSubmitDataRemindJob implements ExecuteableJob {

    @Autowired
    private PrSouRequirementQueryService prSouRequirementQueryService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjProjectBidExtClient pjProjectBidExtClient;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        List<ExtPrSouRequirementForDataSubmit> extPrSouRequirementForDataSubmits = prSouRequirementQueryService.findSouRequirementBySendProfileEndDateFromHour(24,48);
        DingTalkSender dingTalkSender = DingTalkSender.create(baseClient,pjProjectBidExtClient);
        for (ExtPrSouRequirementForDataSubmit extPrSouRequirementForDataSubmit:extPrSouRequirementForDataSubmits){
            String template = String.format("【%S】于【%S】到达递交申请资料时间，目前未递交申请资料，请知悉！",extPrSouRequirementForDataSubmit.getProjectName(), extPrSouRequirementForDataSubmit.getSendSouProfileEndDate()
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            dingTalkSender.sendDingTalk(Collections.singletonList(extPrSouRequirementForDataSubmit.getUsername()),null,null,template);
        }
        return BaseResult.buildSuccess("调用成功");
    }
}
