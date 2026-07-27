package com.midea.cloud.srm.sou.meiql.inspect.repo;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.sou.meiql.inspect.dto.Inspect;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class InspectRepository extends CrudRepository {

    public InspectRepository() {
        //注册action
        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存/提交");
        this.register("abandon", this::abandon, true, "废弃");
        this.register("report", this::report, true, "提交考察报告");
    }

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private QlResult saveOrUpdate(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Record record = getRecord(records);
        if (!InspectStatusEnum.DRAFT.getCode().equals(record.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.APPLY_APPROVING.getCode().equals(record.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.APPLY_WITHDRAW.getCode().equals(record.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.APPLY_REJECTED.getCode().equals(record.get(Inspect::getInspectStatus))) {
            throw new BaseException("不支持的状态参数");
        }

        Long inspectId = record.get(Inspect::getInspectId);
        if (inspectId != null) {
            Record inspect = qlService.readByKey("Inspect", inspectId, Record.class);
            Assert.notNull(inspect, "考察申请ID不存在");
            if (!InspectStatusEnum.DRAFT.getCode().equals(inspect.get(Inspect::getInspectStatus))
                    && !InspectStatusEnum.APPLY_REJECTED.getCode().equals(inspect.get(Inspect::getInspectStatus))
                    && !InspectStatusEnum.APPLY_WITHDRAW.getCode().equals(inspect.get(Inspect::getInspectStatus))) {
                throw new BaseException("当前状态不能修改");
            }
        } else {
            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
            HrUserOrgnizationDto hrUser = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
            Assert.notNull(hrUser, "获取hr用户失败");
            Organization orgBu = hrUser.getBuOrganization();
            if (orgBu != null) {
                record.set(Inspect::getOrgBuId, orgBu.getOrganizationId());
                record.set(Inspect::getOrgBuCode, orgBu.getOrganizationCode());
                record.set(Inspect::getOrgBuName, orgBu.getOrganizationName());
            }
            Organization orgOu = hrUser.getOuOrganization();
            if (orgOu != null) {
                record.set(Inspect::getOrgId, orgOu.getOrganizationId());
                record.set(Inspect::getOrgCode, orgOu.getOrganizationCode());
                record.set(Inspect::getOrgName, orgOu.getOrganizationName());
            }
            Organization department = hrUser.getDepartmentOrganization();
            if (department != null) {
                record.set(Inspect::getDepartmentId, department.getOrganizationId());
                record.set(Inspect::getDepartmentCode, department.getOrganizationCode());
                record.set(Inspect::getDepartmentName, department.getOrganizationName());
            }
        }

        // 同一招标项目只能创建一个考察申请
        Long bidingId = record.get(Inspect::getBidingId);
        if (bidingId != null) {
            long count = qlService.countByWrapper(QlWrappers.query("Inspect")
                    .eq(Inspect::getBidingId, bidingId)
                    .notEq(Inspect::getInspectStatus, InspectStatusEnum.ABANDON.getCode())
                    .notEq(inspectId != null, Inspect::getInspectId, inspectId));
            Assert.isTrue(count == 0, "同一招标项目只能创建一个考察申请");
        }

        return super.doSave(action, records);
    }


    private QlResult abandon(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Record inspect = getByRecord(records);
        if (InspectStatusEnum.REPORT_APPROVED.getCode().equals(inspect.get(Inspect::getInspectStatus))) {
            throw new BaseException("当前状态不能操作废弃");
        }

        Record r = new Record();
        r.put(Inspect::getInspectId, inspect.get(Inspect::getInspectId));
        r.put(Inspect::getInspectStatus, InspectStatusEnum.ABANDON.getCode());
        qlService.update("Inspect", Arrays.asList(r));
        return QlResult.empty();
    }

    private QlResult report(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Record record = getRecord(records);
        if (!InspectStatusEnum.APPLY_APPROVED.getCode().equals(record.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.REPORT_APPROVING.getCode().equals(record.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.REPORT_WITHDRAW.getCode().equals(record.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.REPORT_REJECTED.getCode().equals(record.get(Inspect::getInspectStatus))) {
            throw new BaseException("不支持的状态参数");
        }

        Record inspect = getByRecord(records);
        if (!InspectStatusEnum.APPLY_APPROVED.getCode().equals(inspect.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.REPORT_WITHDRAW.getCode().equals(inspect.get(Inspect::getInspectStatus))
                && !InspectStatusEnum.REPORT_REJECTED.getCode().equals(inspect.get(Inspect::getInspectStatus))) {
            throw new BaseException("当前状态不能提交考察报告");
        }

        if (inspect.get(Inspect::getReportNum) == null) {
            record.put(Inspect::getReportNum, baseClient.seqGen("INSPECT_REPORT_NO"));
        }

        return super.doSave(action, records);
    }

    private Record getByRecord(List<Record> records) {
        Record record = getRecord(records);
        Long inspectId = record.get(Inspect::getInspectId);
        Assert.notNull(inspectId, "考察申请ID不能为空");
        Record inspect = qlService.readByKey("Inspect", inspectId, Record.class);
        Assert.notNull(inspect, "考察申请ID不存在");
        return inspect;
    }

    private Record getRecord(List<Record> records) {
        Assert.notEmpty(records, "参数缺失");
        Assert.isTrue(records.size() == 1, "仅支持1条数据");
        return records.get(0);
    }

}
