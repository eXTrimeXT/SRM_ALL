package com.midea.cloud.srm.perf.job;

/**
 * <pre>
 *  定时器，分数生成后十五天和三十天提示一次
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
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.perf.projectscore.service.ProjectScoreHeaderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Job("pjProjectScoreLowScoreRemindJob")
@Slf4j
public class PjProjectScoreLowScoreRemindJob implements ExecuteableJob {

    @Autowired
    private ProjectScoreHeaderService projectScoreHeaderService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private BaseClient baseClient;

    private final String PERF_REMIND_LIST = "PERF_REMIND_LIST";

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        // 定时器，分数生成后十五天和三十天提示一次
        LocalDate now = LocalDate.now();
        LocalDate fifthDateLater = now.minusDays(15);
        LocalDate thirtyDateLater = now.minusDays(30);

        List<ProjectScoreHeader> scoreHeaderList = projectScoreHeaderService.list(Wrappers.lambdaQuery(ProjectScoreHeader.class)
                .lt(ProjectScoreHeader::getScore, BigDecimal.valueOf(60))
                .in(ProjectScoreHeader::getCalcDate, fifthDateLater, thirtyDateLater)
                .isNotNull(ProjectScoreHeader::getPerformanceCode)
                .isNull(ProjectScoreHeader::getSupplierRemark));

        if (CollectionUtils.isNotEmpty(scoreHeaderList)) {
            // 获取字典,获取配置的账户信息
            Map<String, String> dictItemMap = baseClient.getDictItmeMapByDictCode("MILESTONE_SCHEDULE");
            SystemConfigureDTO systemConfigure = baseClient.getSystemConfigure(PERF_REMIND_LIST);
            String paramValue = systemConfigure.getParamValue();
            List<String> noticeManList = Arrays.asList(paramValue.split(","));
            try {
                for (ProjectScoreHeader item : scoreHeaderList) {
                    pjProjectExtClient.workNotices("您好，" + item.getContractName() + "项目，" + item.getCompanyName() + "供应商" + dictItemMap.get(item.getPerformanceCode()) + "节点得分小于60分，请跟踪确认", noticeManList);
                }
            } catch (Exception e) {
                log.error("处理结果未填写进行报警提醒至履约管理员提醒异常:", e);
                return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
