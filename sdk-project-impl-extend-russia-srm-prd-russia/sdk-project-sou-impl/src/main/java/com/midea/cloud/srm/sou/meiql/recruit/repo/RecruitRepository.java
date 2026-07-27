package com.midea.cloud.srm.sou.meiql.recruit.repo;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.sou.meiql.recruit.dto.Recruit;
import com.midea.cloud.srm.sou.meiql.recruit.enums.RecruitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class RecruitRepository extends CrudRepository {

    public RecruitRepository() {
        //注册action
        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存/提交");
        this.register("abandon", this::abandon, true, "废弃");
        this.register("visitList", this::visitList, false, "访问列表");
        this.register("visitDetail", this::visitDetail, false, "访问详情");
    }

    @Autowired
    private QlService qlService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private QlResult saveOrUpdate(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Record record = getRecord(records);
        Assert.isTrue(RecruitStatusEnum.DRAFT.getCode().equals(record.get(Recruit::getStatus))
                || RecruitStatusEnum.APPROVING.getCode().equals(record.get(Recruit::getStatus))
                || RecruitStatusEnum.WITHDRAW.getCode().equals(record.get(Recruit::getStatus))
                || RecruitStatusEnum.REJECTED.getCode().equals(record.get(Recruit::getStatus)), "不支持的状态参数");

        Long recruitId = record.get(Recruit::getRecruitId);
        if (recruitId != null) {
            Record recruit = qlService.readByKey("Recruit", recruitId, Record.class);
            Assert.notNull(recruit, "招募申请ID不存在");
            Assert.isTrue(RecruitStatusEnum.DRAFT.getCode().equals(recruit.get(Recruit::getStatus))
                    || RecruitStatusEnum.WITHDRAW.getCode().equals(recruit.get(Recruit::getStatus))
                    || RecruitStatusEnum.REJECTED.getCode().equals(recruit.get(Recruit::getStatus)), "当前状态不能修改");
        } else {
            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
            HrUserOrgnizationDto hrUser = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
            Assert.notNull(hrUser, "获取hr用户失败");
            Organization orgBu = hrUser.getBuOrganization();
            if (orgBu != null) {
                record.set(Recruit::getOrgBuId, orgBu.getOrganizationId());
                record.set(Recruit::getOrgBuCode, orgBu.getOrganizationCode());
                record.set(Recruit::getOrgBuName, orgBu.getOrganizationName());
            }
            Organization orgOu = hrUser.getOuOrganization();
            if (orgOu != null) {
                record.set(Recruit::getOrgId, orgOu.getOrganizationId());
                record.set(Recruit::getOrgCode, orgOu.getOrganizationCode());
                record.set(Recruit::getOrgName, orgOu.getOrganizationName());
            }
            Organization department = hrUser.getDepartmentOrganization();
            if (department != null) {
                record.set(Recruit::getDepartmentId, department.getOrganizationId());
                record.set(Recruit::getDepartmentCode, department.getOrganizationCode());
                record.set(Recruit::getDepartmentName, department.getOrganizationName());
            }
        }

        return super.doSave(action, records);
    }


    private QlResult abandon(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Record recruit = getByRecord(records);
        if (RecruitStatusEnum.APPROVED.getCode().equals(recruit.get(Recruit::getStatus))) {
            throw new BaseException("当前状态不能操作废弃");
        }

        Record r = new Record();
        r.put(Recruit::getRecruitId, recruit.get(Recruit::getRecruitId));
        r.put(Recruit::getStatus, RecruitStatusEnum.ABANDON.getCode());
        qlService.update("Recruit", Arrays.asList(r));
        return QlResult.empty();
    }

    private QlResult visitList(QlQueryAction queryAction) {
        QueryParam payload = MeiQl.toValue(queryAction.getPayload(), QueryParam.class);
        payload.getFilter().setValue("status", "eq", RecruitStatusEnum.APPROVED.name());
        payload.getFilter().setValue("deadlineTime", "ge", LocalDateTime.now());
        queryAction.setPayload(payload);
        return super.query(queryAction);
    }


    private QlResult visitDetail(QlQueryAction queryAction) {
        return super.read(queryAction);
    }


    private Record getByRecord(List<Record> records) {
        Record record = getRecord(records);
        Long recruitId = record.get(Recruit::getRecruitId);
        Assert.notNull(recruitId, "招募申请ID不能为空");
        Record inspect = qlService.readByKey("Recruit", recruitId, Record.class);
        Assert.notNull(inspect, "招募申请ID不存在");
        return inspect;
    }

    private Record getRecord(List<Record> records){
        Assert.notEmpty(records, "参数缺失");
        Assert.isTrue(records.size() == 1, "仅支持1条数据");
        return records.get(0);
    }

}
