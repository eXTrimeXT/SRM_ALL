package com.midea.cloud.srm.cm.contract.job;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.cm.contract.mapper.ExtPartnerMapper;
import com.midea.cloud.srm.cm.contract.utils.DingTalkSender;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.ContractSouExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.contract.dto.ContractHeadSourceDto;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 【长城SRM】【合同到期提醒】
 * @author GW00086630
 * 定时器执行，每天上午九点执行，当合同中状态为“已归档”，是否周期合同=是时，合同有效期前，三个月提醒一次，到期前两个月提醒一次，到期前一个月提醒一次
 */
@Job("contractExpirationReminderJob")
@Slf4j
public class ContractExpirationReminderJob implements ExecuteableJob {

    @Resource
    private BaseClient baseClient;
    @Resource
    private RbacClient rbacClient;
    @Resource
    private ContractSouExtClient contractSouExtClient;
    @Resource
    private ContractPjExtClient contractPjExtClient;

    @Resource
    private ExtPartnerMapper extPartnerMapper;

    public static final String CONTRACT_WARNING = "CONTRACT_WARNING";
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        //查询已归档状态的，是否周期合同=是，三个月内的所有单据
        List<ContractHeadSourceDto>resultList=new ArrayList<>();
        //查询已归档状态的，是否周期合同=是，三个月内的所有单据
        List<ContractHeadSourceDto> queryFirst=extPartnerMapper.queryContractList("1", YesOrNo.YES.getValue(), ContractStatus.ARCHIVED.name());
        List<ContractHeadSourceDto> querySecond=extPartnerMapper.queryContractList("2",YesOrNo.YES.getValue(), ContractStatus.ARCHIVED.name());
        List<ContractHeadSourceDto> queryThird=extPartnerMapper.queryContractList("3",YesOrNo.YES.getValue(), ContractStatus.ARCHIVED.name());
        resultList.addAll(queryFirst);
        resultList.addAll(querySecond);
        resultList.addAll(queryThird);
        //循环每个单据，获取用户名和项目名称
        User user=new User();
        for(ContractHeadSourceDto contractHeadSourceDto:resultList){

            if(contractHeadSourceDto.getExtInviteHeadAccount()!=null){
                user.setUserId(Long.parseLong(contractHeadSourceDto.getExtInviteHeadAccount()));
                User userList=rbacClient.getUser(user);
                contractHeadSourceDto.setExtInviteHeadAccount(userList.getUsername());
            }
            //获取项目名称
            ExtSouProject extSouProject=new ExtSouProject();
            extSouProject.setExtProjectNo(contractHeadSourceDto.getSourceNumber());
            List<ExtSouProject>souProjectList= contractSouExtClient.queryByProjectNo(extSouProject);
            if(souProjectList.size()>0){
                contractHeadSourceDto.setProjectName(souProjectList.get(0).getSouName());
            }

        }
        //调用钉钉接口

        for (ContractHeadSourceDto contractHeadSourceDto:resultList){
            List<String>userList=new ArrayList<>();
            if(contractHeadSourceDto.getExtContractHandlerAccount()!=null){
                userList.add(contractHeadSourceDto.getExtContractHandlerAccount());
            }
            if(contractHeadSourceDto.getExtInviteHeadAccount()!=null){
                userList.add(contractHeadSourceDto.getExtInviteHeadAccount());
            }
            Map<String, String> tempParams = getPerPlanMilestonePlanStartTime(contractHeadSourceDto);
            DingTalkSender.create(baseClient,contractPjExtClient).sendDingTalk(userList, CONTRACT_WARNING,tempParams,null);
        }
        return BaseResult.buildSuccess("调用成功");
    }

    private Map<String, String> getPerPlanMilestonePlanStartTime(ContractHeadSourceDto contractHeadSourceDto) {
        Map<String, String> params = new HashMap<>(16);
        params.put("${sourceNumber}",contractHeadSourceDto.getSourceNumber());
        params.put("${projectName}",contractHeadSourceDto.getProjectName());
        params.put("${effectiveDateTo}",contractHeadSourceDto.getEffectiveDateTo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return params;
    }
}
