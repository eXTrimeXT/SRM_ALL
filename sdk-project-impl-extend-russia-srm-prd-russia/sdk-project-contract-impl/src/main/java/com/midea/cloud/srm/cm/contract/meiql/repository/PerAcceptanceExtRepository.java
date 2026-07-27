package com.midea.cloud.srm.cm.contract.meiql.repository;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptance;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlanMilestone;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.contract.dto.PerAcceptanceExt;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 100014336 ganyh19
 */
@Service
@Slf4j
public class PerAcceptanceExtRepository extends ProxyRepository {

    @Autowired
    private QlService qlService;

    @Autowired
    private ContractPjExtClient pjProjectExtClient;


    @Override
    protected void beforeUpdate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeUpdate(queryAction, records);
    }

    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> records) {
        handleBeforeCreate(records);
        super.beforeCreate(queryAction, records);
    }

    private void handleBeforeCreate(Collection<Record> records) {
        if(ObjectUtil.isNotEmpty(records)){
            for (Record record:records){
                handleBeforeCreate(record);
            }

        }
    }

    private void handleBeforeCreate(Record record) {
        setCompanyInfo(record);
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {
        List<Record> milestoneBeforeUpdate = handleMilestoneBeforeUpdate(queryAction);
        QlResult result = super.save(queryAction);
        qlService.update(ContractMqlSchemaType.PerPlanMilestone.getType(), milestoneBeforeUpdate);
        return result;
    }


    private Record getUpdateMilestone(Map record){
        Long milestoneId = (Long)record.get("perPlanMilestoneId");
        if(ObjectUtil.isNotNull(milestoneId)){
            Record updateRecord = new Record();
            Integer nodePlanNum = (Integer)record.get("nodePlanNum");
            String practicallyEndDateStr = (String) record.get("practicallyEndDate");
            Date practicallyEndDate = null;
            if(ObjectUtil.isNotEmpty(practicallyEndDateStr)){
                try {
                    practicallyEndDate = DateUtil.parseDate(practicallyEndDateStr);
                } catch (ParseException e) {
                    log.error("日期格式不对{}",e.getMessage());
                    throw new BaseException("日期格式不对");
                }
            }

            updateRecord.put(PerPlanMilestone::getPerPlanMilestoneId,milestoneId);
            updateRecord.put(PerPlanMilestone::getNodePlanNum,nodePlanNum);
            updateRecord.put(PerPlanMilestone::getPracticallyEndDate,practicallyEndDate);
            return updateRecord;
        }
        return null;
    }

    private List<Record> handleMilestoneBeforeUpdate(QlQueryAction queryAction){
        List<Record> planMilestones = new ArrayList<>();
        List<Record> playLoads= getPayloadListForType(queryAction,Record.class);
        for (Record record:playLoads){
            if("DRAFT".equals(record.get(PerAcceptance::getStatus))&&ObjectUtil.isNotNull(record.get(PerAcceptance::getPerAcceptanceId))){
                Map planMilestone = (Map)record.get("perPlanMilestoneId");
                if(ObjectUtil.isNotEmpty(planMilestone)){
                    Record updateRecord = getUpdateMilestone(planMilestone);
                    if(ObjectUtil.isNotNull(updateRecord)){
                        planMilestones.add(updateRecord);
                    }
                };
            }
        }
        return planMilestones;
    }

    private void setCompanyInfo(Record record){
        LoginAppUser appUser = AppUserUtil.getLoginAppUser();
        HrUserOrgnizationDto userOrganization = pjProjectExtClient.getHrUserOrgnizationByUsername(appUser.getUsername());
        if(ObjectUtil.isNotNull(userOrganization)){
            //公司
            Organization ouOrganization = userOrganization.getOuOrganization();
            //板块
            Organization buOrganization = userOrganization.getBuOrganization();
            //部门
            Organization departmentOrganization = userOrganization.getDepartmentOrganization();

            if(ObjectUtil.isNotNull(ouOrganization)){
                record.put(PerAcceptanceExt::getExtHrCompanyId,ouOrganization.getOrganizationId());
                record.put(PerAcceptanceExt::getExtHrCompanyCode,ouOrganization.getOrganizationCode());
                record.put(PerAcceptanceExt::getExtHrCompanyName,ouOrganization.getOrganizationName());
            }

            if(ObjectUtil.isNotNull(buOrganization)){
                record.put(PerAcceptanceExt::getExtHrSectorId,buOrganization.getOrganizationId());
                record.put(PerAcceptanceExt::getExtHrSectorName,buOrganization.getOrganizationName());
                record.put(PerAcceptanceExt::getExtHrSectorCode,buOrganization.getOrganizationCode());
            }

            if(ObjectUtil.isNotNull(departmentOrganization)){
                record.put(PerAcceptanceExt::getExtHrDeptId,departmentOrganization.getOrganizationId());
                record.put(PerAcceptanceExt::getExtHrDeptCode,departmentOrganization.getOrganizationCode());
                record.put(PerAcceptanceExt::getExtHrDeptName,departmentOrganization.getOrganizationName());
            }
        }


    }


}
