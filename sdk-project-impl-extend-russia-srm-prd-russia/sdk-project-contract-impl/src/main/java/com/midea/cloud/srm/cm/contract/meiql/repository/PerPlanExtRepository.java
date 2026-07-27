package com.midea.cloud.srm.cm.contract.meiql.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.contract.ContractPerformPlanStatus;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.result.RepoRecMap;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.CollectionUtil;
import com.midea.cloud.meiql.core.util.OpenApiDispatcherUtil;
import com.midea.cloud.srm.cm.contract.service.IContractPerPlanExtService;
import com.midea.cloud.srm.cm.repo.PerPayPlanRepository;
import com.midea.cloud.srm.cm.util.PerPlanMilestoneContext;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.cm.perform.entity.PerTemplHead;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.contract.dto.PerPlanExt;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 100014336 ganyh19
 */
@Slf4j
@Component
public class PerPlanExtRepository extends CrudRepository {

    @Resource
    private BaseClient baseClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private IContractPerPlanExtService contractPerPlanExtService;

    @Autowired
    private ContractPjExtClient pjProjectExtClient;

    public PerPlanExtRepository() {
        this.register("deleteOne", this::deleteOne, true, "删除");
    }

    private QlResult deleteOne(QlQueryAction queryAction) throws Exception {
        QlResult qlResult = new QlResult();
        String type = queryAction.getType();
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Long perPlanId = (Long)((Record)records.get(records.size() - 1)).get("perPlanId");
        QlQueryWrapper qw = (QlQueryWrapper)MeiQl.newQueryWrapper("PerPlanDetail").eq("perPlanId", perPlanId);
        List<Record> perPlanDetailRecord = this.qlService.queryByWrapper(qw, Record.class);
        if (!CollectionUtil.isEmpty(perPlanDetailRecord)) {
            List<Long> perPlanDetailIds = new ArrayList();
            perPlanDetailRecord.forEach((r) -> {
                Long perPlanDetailId = r.getLong("perPlanDetailId");
                perPlanDetailIds.add(perPlanDetailId);
            });
            OpenApiDispatcherUtil.apply("PerPlanDetail", "delete", perPlanDetailIds);
        }

        QlQueryWrapper qw2 = (QlQueryWrapper)MeiQl.newQueryWrapper("PerPlanMilestone").eq("perPlanId", perPlanId);
        List<Record> perPlanMilestoneRecord = this.qlService.queryByWrapper(qw2, Record.class);
        if (!CollectionUtil.isEmpty(perPlanMilestoneRecord)) {
            List<Long> perPlanMilestoneIds = new ArrayList();
            perPlanMilestoneRecord.forEach((r) -> {
                Long perPlanMilestoneId = r.getLong("perPlanMilestoneId");
                perPlanMilestoneIds.add(perPlanMilestoneId);
            });
            OpenApiDispatcherUtil.apply("PerPlanMilestone", "delete", perPlanMilestoneIds);
        }

        QlQueryWrapper qw3 = (QlQueryWrapper)MeiQl.newQueryWrapper("PerPayPlan").eq("perPlanId", perPlanId);
        List<Record> perPayPlanRecord = this.qlService.queryByWrapper(qw3, Record.class);
        if (!CollectionUtil.isEmpty(perPayPlanRecord)) {
            List<Long> perPayPlanIds = new ArrayList();
            perPayPlanRecord.forEach((r) -> {
                Long perPayPlanId = r.getLong("perPayPlanId");
                perPayPlanIds.add(perPayPlanId);
            });
            OpenApiDispatcherUtil.apply("PerPayPlan", "delete", perPayPlanIds);
        }

        OpenApiDispatcherUtil.apply("PerPlan", "delete", Arrays.asList(perPlanId));
        return qlResult;
    }

    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> collection) {
        super.beforeCreate(queryAction, collection);
        this.checkPerOrder(collection);
        handleBeforeCreate(collection);
        collection.forEach((r) -> {
            r.put("status", ContractPerformPlanStatus.DRAFT.getKey());
        });
    }

    @Override
    protected QlResult doSave(QlQueryAction queryAction, List<Record> records) {
        QlResult var3;
        try {
            PerPlanMilestoneContext.init();
            var3 = super.doSave(queryAction, records);
        } finally {
            PerPlanMilestoneContext.destory();
        }

        return var3;
    }

    @Override
    protected void beforeUpdate(QlQueryAction queryAction, Collection<Record> collection) {
        super.beforeUpdate(queryAction, collection);
        this.checkPerOrder(collection);
    }

    public void checkPerOrder(Collection<Record> collection) {
        Record rec = null;
        Iterator var3 = collection.iterator();

        while(var3.hasNext()) {
            Record r = (Record)var3.next();
            rec = r;
            if (r != null) {
                break;
            }
        }

        PerPlan perPlan = (PerPlan)MeiQl.toValue(rec, PerPlan.class);
        if (perPlan.getPerTemplHeadId() != null && StringUtils.isNotBlank(perPlan.getContractClass())) {
            PerTemplHead perTemplHead = (PerTemplHead)this.qlService.readByKey("PerTemplHead", perPlan.getPerTemplHeadId(), PerTemplHead.class);
            if (!perTemplHead.getContractType().equals(perPlan.getContractClass())) {
                throw new BaseException("合同类型与里程碑模板配置的合同类型不一致");
            }
        }

        Assert.notNull(perPlan.getContractHeadId(), "合同ID不能为空");
        QlCondition condition = MeiQl.newCondition();
        condition.eq("ceeaContractOldId", perPlan.getContractHeadId());
        condition.notIn("contractStatus", new String[]{ContractStatus.ARCHIVED.name(), ContractStatus.ABANDONED.name()});
        List<Record> heads = this.qlService.query("ContractHead", condition, Record.class);
        if (heads != null && heads.size() > 0) {
            throw new BaseException("该合同存在变更或终止的单据，操作失败");
        }
    }

    @Override
    protected QlCondition beforeRead(QlQueryAction queryAction, Collection keys) {
        super.beforeRead(queryAction, keys);
        return this.getListCondition();
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        super.beforeQuery(queryAction, payload);
        return this.getListCondition();
    }

    @Override
    public QlResult read(QlQueryAction queryAction) {
        QlResult result =  super.read(queryAction);
        RepoRecMap repoRecMap = result.getRef().get(ContractMqlSchemaType.PerPlanMilestone.getType());
        if(CollUtil.isNotEmpty(repoRecMap)){
            for(Map.Entry recordEntry:repoRecMap.entrySet()){
                Record  record = (Record)recordEntry.getValue();
                record.put("evalFlag",contractPerPlanExtService.isNeedPerfEvalByMilestoneId((Long)recordEntry.getKey())? Enable.Y.name():Enable.N.name());
            }
        }
        return result;
    }

    private QlCondition getListCondition() {
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        if (!UserType.BUYER.name().equals(user.getUserType())) {
            List<String> statusList = new ArrayList();
            statusList.add(ContractPerformPlanStatus.COMPLETE_PERFORMANCE.getKey());
            statusList.add(ContractPerformPlanStatus.APPROVED.getKey());
            statusList.add(ContractPerformPlanStatus.IN_PERFORMANCE.getKey());
            QlCondition cond = MeiQl.newCondition();
            cond.eq("vendorId", user.getCompanyId());
            cond.in("status", new String[]{ContractPerformPlanStatus.COMPLETE_PERFORMANCE.getKey(), ContractPerformPlanStatus.APPROVED.getKey(), ContractPerformPlanStatus.IN_PERFORMANCE.getKey()});
            return cond;
        } else {
            return null;
        }
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
                record.put(PerPlanExt::getExtHrCompanyId,ouOrganization.getOrganizationId());
                record.put(PerPlanExt::getExtHrCompanyCode,ouOrganization.getOrganizationCode());
                record.put(PerPlanExt::getExtHrCompanyName,ouOrganization.getOrganizationName());
            }

            if(ObjectUtil.isNotNull(buOrganization)){
                record.put(PerPlanExt::getExtHrSectorId,buOrganization.getOrganizationId());
                record.put(PerPlanExt::getExtHrSectorName,buOrganization.getOrganizationName());
                record.put(PerPlanExt::getExtHrSectorCode,buOrganization.getOrganizationCode());
            }

            if(ObjectUtil.isNotNull(departmentOrganization)){
                record.put(PerPlanExt::getExtHrDeptId,departmentOrganization.getOrganizationId());
                record.put(PerPlanExt::getExtHrDeptCode,departmentOrganization.getOrganizationCode());
                record.put(PerPlanExt::getExtHrDeptName,departmentOrganization.getOrganizationName());
            }
        }



    }
}
