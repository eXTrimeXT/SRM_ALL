package com.midea.cloud.srm.perf.job;

/**
 * <pre>
 *  定时器执行，评价完成时间截止后，仍未完成评价的，通知招标负责人
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
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreItemCheckStatusEnum;
import com.midea.cloud.srm.model.perf.projectscoreman.enums.ProjectScoreManStatusEnum;
import com.midea.cloud.srm.perf.projectscoreitem.service.ProjectScoreItemsService;
import com.midea.cloud.srm.perf.projectscoreman.service.ProjectScoreManService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Job("pjProjectScoreDelayRemindJob")
@Slf4j
public class PjProjectScoreDelayRemindJob implements ExecuteableJob {

    @Autowired
    private ProjectScoreManService projectScoreManService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private ProjectScoreItemsService projectScoreItemsService;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        LocalDate now = LocalDate.now();
        // 状态不是已计算得分，都算没完成
        List<ProjectScoreItems> projectScoreItemsList = projectScoreItemsService.list(Wrappers.lambdaQuery(ProjectScoreItems.class)
                .ne(ProjectScoreItems::getCheckStatus, ProjectScoreItemCheckStatusEnum.CALCULATED_SCORE.name())
                .lt(ProjectScoreItems::getPerEndMonth, now));
        List<ProjectScoreMan> scoreManList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(projectScoreItemsList)) {
            List<Long> scoreItemIdList = projectScoreItemsList.stream().map(ProjectScoreItems::getProjectScoreItemsId).collect(Collectors.toList());
            scoreManList = projectScoreManService.list(Wrappers.lambdaQuery(ProjectScoreMan.class)
                    .in(ProjectScoreMan::getProjectScoreItemsId, scoreItemIdList));
        }
        Pattern pattern = Pattern.compile("\\((.*?)\\)");

        if (CollectionUtils.isNotEmpty(scoreManList)) {
            Map<Long, List<ProjectScoreMan>> projectMap = scoreManList.stream().collect(Collectors.groupingBy(ProjectScoreMan::getProjectScoreItemsId));
            for (Long key : projectMap.keySet()) {
                try {
                    List<ProjectScoreMan> tempList = projectMap.get(key);
                    ProjectScoreMan projectScoreMan = tempList.get(0);
                    LocalDate perEndMonth = projectScoreMan.getPerEndMonth();
                    BigDecimal days = new BigDecimal(perEndMonth.until(now, ChronoUnit.DAYS));
                    // 获取所有评分人账号-xxx(GW***)
                    // 合同经办人
                    String manager = projectScoreMan.getBidManager();
                    Matcher matcher = pattern.matcher(manager);
                    String managerStr = "";
                    if (matcher.find()) {
                        managerStr = matcher.group(1);
                    } else {
                        return BaseResult.build(ResultCode.UNKNOWN_ERROR, "招标负责人延期获取异常");
                    }
                    pjProjectExtClient.workNotices("您好，" + projectScoreMan.getContractName() + "项目，履约评价评分已延期" + days + "天，请跟踪确认并督促评分人完成评价", Arrays.asList(managerStr));
                } catch (Exception e) {
                    log.error("招标负责人延期提醒异常:", e);
                    return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
                }
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
