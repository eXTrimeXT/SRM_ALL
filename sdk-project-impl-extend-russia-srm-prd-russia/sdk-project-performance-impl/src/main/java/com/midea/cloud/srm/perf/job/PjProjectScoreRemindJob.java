package com.midea.cloud.srm.perf.job;

/**
 * <pre>
 *  定时器执行，计划时间开始后，直到评价人评价完毕，每天通知评价人需完成评价打分工作。
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/01/23 17:21
 *  修改内容:
 * </pre>
 */

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreManStatusEnum;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Job("pjProjectScoreRemindJob")
@Slf4j
public class PjProjectScoreRemindJob implements ExecuteableJob {

    @Autowired
    private ProjectScoreManService projectScoreManService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        // 定时器执行，计划时间开始后，直到评价人评价完毕，每天通知评价人需完成评价打分工作。
        // 获取当前时间在计划时间后,结束时间前的项目
        LocalDate now = LocalDate.now();
        List<ProjectScoreMan> scoreManList = projectScoreManService.list(Wrappers.lambdaQuery(ProjectScoreMan.class)
                .in(ProjectScoreMan::getApproveStatus, ProjectScoreManStatusEnum.DRAFT.name(), ProjectScoreManStatusEnum.CHECK_REJECT.name())
                .le(ProjectScoreMan::getPerStartMonth, now)
                .ge(ProjectScoreMan::getPerEndMonth, now));
        if (CollectionUtils.isNotEmpty(scoreManList)) {
            for (ProjectScoreMan projectScoreMan : scoreManList) {
                try {
                    pjProjectExtClient.workNotices("您好，" + projectScoreMan.getContractName() + "项目，您还未完成履约评价，请登录SRM平台完成评分", Arrays.asList(projectScoreMan.getScoreManAccount()));
                } catch (Exception e) {
                    log.error("绩效评分人提醒异常:", e);
                    return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
                }
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
