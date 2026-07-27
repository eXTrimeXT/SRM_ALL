package com.midea.cloud.srm.sou.bid.earlywarnings.service.impl;

import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.enums.ExtPrRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.bid.earlywarnings.mapper.SouBidEarlyWarningMapper;
import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningService;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
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
 * @Description: for srm 发标-预警
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Service
public class SouBidEarlyWarningServicePublishImpl extends SouBidEarlyWarningServiceCommonImpl implements SouBidEarlyWarningService {
    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouProjectService souProjectService;

    @Resource
    private SouBidEarlyWarningMapper souBidEarlyWarningMapper;

    @Override
    public String doWarning() {
        log.info("SouBidEarlyWarningService 预警发标开始");
        Map<String, Object> params = super.getQueryParams();
        String publishTime = super.threeDayBefore();
        params.put("publishTime", publishTime);
        List<ExtSouPlan> dataList = souBidEarlyWarningMapper.listPlan(params);
        if(CollectionUtils.isNotEmpty(dataList)) {
            List<String> dateList = (List<String>) params.get(WARNING_TIME_LIST);
            dataList = dataList.stream().filter(p -> dateList.contains(DateUtil.format(p.getPublishTime(), DateUtil.DATE_FORMAT_10))).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(dataList)) {
            log.info("SouBidEarlyWarningService 预警发标无延期数据");
        } else {
            /** 招标负责人 */


            List<ExtSouProject> souProjectList = souProjectService.lambdaQuery().in(ExtSouProject::getProjectId, dataList.stream().map(d -> d.getProjectId()).distinct().collect(Collectors.toList())).list();
            Map<Long, ExtSouProject> souProjectMap = souProjectList.stream().collect(Collectors.toMap(e -> e.getProjectId(), Function.identity(), (k1, k2)->k2));

            //发送预警
            dataList.stream().forEach(data -> {
                Map<String, String> var = new HashMap<>(50);
                /** ${souName}(${souNo})${processName}已延期${remark}，请关注并及时处理。 */

                String remark = "";
                Long diffDays = diffDays(data.getPublishTime());
                if(Long.compare(diffDays, DIFF_THREE_DAYS) >= 0) {
                    remark = MessageFormat.format("（{0}天）", diffDays);
                }

                ExtSouProject project = souProjectMap.getOrDefault(data.getProjectId(), new ExtSouProject());

                var.put("${souName}", project.getSouName());
                var.put("${souNo}", project.getExtProjectNo());
                var.put("${processName}", PROCESS_DATA_PUBLISH);
                var.put("${remark}", remark);

                List<String> userNameList = StringUtils.isNotBlank(project.getCreatedBy()) ? Collections.singletonList(project.getCreatedBy()) : new ArrayList<>(50);
                super.dingTalk(userNameList, var);
            });
        }
        log.info("SouBidEarlyWarningService 预警发标结束");
        return super.doWarning();
    }
}
