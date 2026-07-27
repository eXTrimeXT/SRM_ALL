package com.midea.cloud.srm.sou.bid.earlywarnings.service.impl;

import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.bid.earlywarnings.mapper.SouBidEarlyWarningMapper;
import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm 上报-预警
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Service
public class SouBidEarlyWarningServiceSumreportImpl extends SouBidEarlyWarningServiceCommonImpl implements SouBidEarlyWarningService {
    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouProjectService souProjectService;

    @Resource
    private SouBidEarlyWarningMapper souBidEarlyWarningMapper;

    private static final Long LONG1 = 1L;
    private static final Long LONG3 = 3L;

    @Override
    public String doWarning() {
        log.info("SouBidEarlyWarningService 预警上报开始");
        Map<String, Object> params = super.getQueryParams();
        String sumReportTime = super.threeDayBefore();
        params.put("sumReportTime", sumReportTime);
        List<ExtSouPlan> dataList = souBidEarlyWarningMapper.listPlan(params);

        if(CollectionUtils.isNotEmpty(dataList)) {
            List<ExtSouPlan> actualList = planService.lambdaQuery().eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.ACTUAL.getCode()).in(ExtSouPlan::getProjectId, dataList.stream().map(p -> p.getProjectId()).collect(Collectors.toList())).list();
            Map<Long, ExtSouPlan> actualMap = actualList.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(), (k1 , k2) -> k2));

            List<ExtSouProject> souProjectList = souProjectService.lambdaQuery().in(ExtSouProject::getProjectId, dataList.stream().map(d -> d.getProjectId()).distinct().collect(Collectors.toList())).list();
            Map<Long, ExtSouProject> souProjectMap = souProjectList.stream().collect(Collectors.toMap(e -> e.getProjectId(), Function.identity(), (k1, k2)->k2));

            dataList.stream().forEach(data -> {

                ExtSouPlan actual = actualMap.get(data.getProjectId());
                if(Objects.isNull(actual)) {
                    return;
                }

                Long diffActual = diffDays(new Date(), Objects.isNull(actual.getTechEvaluationTime())?actual.getPriceOpenTime():actual.getTechEvaluationTime());
                Long diffPlan = diffDays(data.getSumReportTime(), Objects.isNull(data.getTechEvaluationTime())?data.getPriceOpenTime():data.getTechEvaluationTime());
                Long diff = diffActual - diffPlan;
                String remark = "";
                if(!Arrays.asList(LONG3, LONG1).contains(diff)) {
                    return;
                }

                if(Long.compare(diff, DIFF_THREE_DAYS) >= 0) {
                    remark = MessageFormat.format("（{0}天）", diff);
                }
                Map<String, String> var = new HashMap<>(50);
                /** ${souName}(${souNo})${processName}已延期${remark}，请关注并及时处理。 */

                ExtSouProject project = souProjectMap.getOrDefault(data.getProjectId(), new ExtSouProject());

                var.put("${souName}", project.getSouName());
                var.put("${souNo}", project.getExtProjectNo());
                var.put("${processName}", PROCESS_DATA_SUMREPORT);
                var.put("${remark}", remark);

                List<String> userNameList = StringUtils.isNotBlank(project.getCreatedBy()) ? Collections.singletonList(project.getCreatedBy()) : new ArrayList<>(50);
                super.dingTalk(userNameList, var);
            });
        }

        log.info("SouBidEarlyWarningService 预警上报结束");
        return super.doWarning();
    }
}
