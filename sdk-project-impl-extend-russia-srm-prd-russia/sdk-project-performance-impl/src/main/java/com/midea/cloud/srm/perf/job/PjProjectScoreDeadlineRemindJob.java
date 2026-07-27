package com.midea.cloud.srm.perf.job;

/**
 * <pre>
 *  定时器，评分完成时间截止前3天，通知合同经办人
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/01/24 17:21
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Job("pjProjectScoreDeadlineRemindJob")
@Slf4j
public class PjProjectScoreDeadlineRemindJob implements ExecuteableJob {

    @Autowired
    private ProjectScoreManService projectScoreManService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        // 定时器，评分完成时间截止前3天，通知合同经办人
        // 获取当前时间在计划时间后,结束时间前的项目
        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);
        List<ProjectScoreMan> scoreManList = projectScoreManService.list(Wrappers.lambdaQuery(ProjectScoreMan.class)
                .in(ProjectScoreMan::getApproveStatus, ProjectScoreManStatusEnum.DRAFT.name(), ProjectScoreManStatusEnum.CHECK_REJECT.name())
                .ge(ProjectScoreMan::getPerEndMonth, now)
                .le(ProjectScoreMan::getPerEndMonth, threeDaysLater));

        Pattern pattern = Pattern.compile("\\((.*?)\\)");

        if (CollectionUtils.isNotEmpty(scoreManList)) {
            Map<Long, List<ProjectScoreMan>> projectMap = scoreManList.stream().collect(Collectors.groupingBy(ProjectScoreMan::getProjectScoreItemsId));
            for (Long key : projectMap.keySet()) {
                try {
                    List<ProjectScoreMan> tempList = projectMap.get(key);
                    ProjectScoreMan projectScoreMan = tempList.get(0);
                    // 获取所有评分人账号-xxx(GW***)
                    // 合同经办人
                    String contractManager = projectScoreMan.getContractManager();
                    Matcher matcher = pattern.matcher(contractManager);
                    String contractManagerStr = "";
                    if (matcher.find()) {
                        contractManagerStr = matcher.group(1);
                    } else {
                        return BaseResult.build(ResultCode.UNKNOWN_ERROR, "合同经办人异常");
                    }
                    List<String> collect = tempList.stream().map(ProjectScoreMan::getScoreManAccount).collect(Collectors.toList());
                    String accountStr = String.join(",", collect);
                    pjProjectExtClient.workNotices("您好，" + projectScoreMan.getContractName() + "项目，(" + accountStr + ")评分人还未完成履约评价评分，请跟踪确认，并于" + projectScoreMan.getPerEndMonth() + "日前完成", Arrays.asList(contractManagerStr));
                } catch (Exception e) {
                    log.error("合同经办人提醒异常:", e);
                    return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
                }
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
