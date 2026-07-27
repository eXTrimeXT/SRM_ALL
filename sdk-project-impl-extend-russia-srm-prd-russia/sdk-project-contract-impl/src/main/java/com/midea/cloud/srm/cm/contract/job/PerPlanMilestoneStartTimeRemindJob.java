package com.midea.cloud.srm.cm.contract.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.contract.ContractPerformPlanStatus;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.cm.contract.mapper.ExtPerPlanMapper;
import com.midea.cloud.srm.cm.contract.utils.DingTalkSender;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.contract.constant.DingTalkTempConstant;
import com.midea.cloud.srm.model.contract.dto.ContractPerPlanStartTimeDto;
import com.midea.cloud.srm.model.contract.enums.ContractHeadPlanStatusEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 【长城SRM】【合同履约埋点】
 * BUG2024011700024
 * @author 100014336 ganyh19
 * 定时器执行，查找合同履约计划里程碑的计划开始时间前两天，触发
 */
@Job("perPlanMilestoneStartTimeRemindJob")
@Slf4j
public class PerPlanMilestoneStartTimeRemindJob implements ExecuteableJob {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ContractPjExtClient contractPjExtClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private ExtPerPlanMapper extPerPlanMapper;



    @Override
    public BaseResult executeJob(Map<String, String> params) {
        Date now = new Date();
        Date remindDate = DateUtil.addAndSubtractDaysByCalendar(now,2);

        List<ContractPerPlanStartTimeDto> startTimeDtoList = extPerPlanMapper.queryContractPerPlanByPlanStartTime(Arrays.asList(ContractPerformPlanStatus.APPROVED.getKey(),ContractPerformPlanStatus.IN_PERFORMANCE.getKey()),DateUtil.format(remindDate,"yyyy-MM-dd"));
        for (ContractPerPlanStartTimeDto contractPerPlanStartTimeDto:startTimeDtoList){
            /*您好，${contractName}项目（合同名称），${planStartTime}日（计划开始时间）开展履约评价，请指定评价人*/
            Map<String, String> tempParams = getPerPlanMilestonePlanStartTime(contractPerPlanStartTimeDto);
            DingTalkSender.create(baseClient,contractPjExtClient).sendDingTalk(Collections.singletonList(contractPerPlanStartTimeDto.getExtContractHandlerAccount()), DingTalkTempConstant.PER_PLAN_MILESTONE_PLAN_START_TIME,tempParams,null);
        }
        return BaseResult.buildSuccess("调用成功");
    }

    private Map<String, String> getPerPlanMilestonePlanStartTime(ContractPerPlanStartTimeDto contractPerPlanStartTimeDto) {
        Map<String, String> params = new HashMap<>(16);
        params.put("${contractName}",contractPerPlanStartTimeDto.getContractName());
        params.put("${planStartTime}",DateUtil.format(contractPerPlanStartTimeDto.getPlanStartDate(),"yyyy-MM-dd"));
        params.put("${milestoneType}",getMileStoneName(contractPerPlanStartTimeDto.getMilestoneType()));
        return params;
    }

    private String getMileStoneName(String milestoneType){
        List<DictItemDTO> itemDTOS = baseClient.listAllByDictCode("MILESTONE_SCHEDULE");
        String milestoneName =  null;
        if(ObjectUtil.isEmpty(milestoneType)){
            if(CollUtil.isNotEmpty(itemDTOS)){
                milestoneName = itemDTOS.stream().filter(e->e.getDictItemCode().equalsIgnoreCase(milestoneType)).collect(Collectors.toList()).get(0).getDictItemName();
            }
        }
        return milestoneName;
    }
}
