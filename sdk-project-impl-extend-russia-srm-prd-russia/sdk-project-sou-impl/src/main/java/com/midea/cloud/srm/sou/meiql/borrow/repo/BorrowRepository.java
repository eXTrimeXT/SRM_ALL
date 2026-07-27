package com.midea.cloud.srm.sou.meiql.borrow.repo;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertCategoryRelation;
import com.midea.cloud.srm.sou.meiql.borrow.dto.Borrow;
import com.midea.cloud.srm.sou.meiql.borrow.dto.BorrowAttach;
import com.midea.cloud.srm.sou.meiql.borrow.enums.BorrowStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class BorrowRepository extends CrudRepository {

    public BorrowRepository() {
        //注册action
        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存/提交");
        this.register("abandon", this::abandon, true, "废弃");
        this.register("saveOrUpdateBorrowAttach", this::saveOrUpdateBorrowAttach, true, "附件");
    }

    @Autowired
    private QlService qlService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private QlResult saveOrUpdate(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Assert.notEmpty(records, "参数缺失");
        Assert.isTrue(records.size() == 1, "仅支持1条数据");
        Record record = records.get(0);
        Assert.isTrue(BorrowStatusEnum.DRAFT.getCode().equals(record.get(Borrow::getStatus))
                ||BorrowStatusEnum.APPROVING.getCode().equals(record.get(Borrow::getStatus))
                ||BorrowStatusEnum.REJECTED.getCode().equals(record.get(Borrow::getStatus))
                ||BorrowStatusEnum.WITHDRAW.getCode().equals(record.get(Borrow::getStatus)), "不支持的状态参数");

        Long borrowId = record.get(Borrow::getBorrowId);
        if (borrowId != null) {
            Record borrow = qlService.readByKey("Borrow", borrowId, Record.class);
            Assert.notNull(borrow, "借阅申请ID不存在");
            Assert.isTrue(BorrowStatusEnum.DRAFT.getCode().equals(borrow.get(Borrow::getStatus))
                    ||BorrowStatusEnum.WITHDRAW.getCode().equals(borrow.get(Borrow::getStatus))
                    ||BorrowStatusEnum.REJECTED.getCode().equals(borrow.get(Borrow::getStatus)), "当前状态不能修改");
        } else {
            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
            HrUserOrgnizationDto hrUser = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
            Assert.notNull(hrUser, "获取hr用户失败");
            Organization orgBu = hrUser.getBuOrganization();
            if (orgBu != null) {
                record.set(Borrow::getOrgBuId, orgBu.getOrganizationId());
                record.set(Borrow::getOrgBuCode, orgBu.getOrganizationCode());
                record.set(Borrow::getOrgBuName, orgBu.getOrganizationName());
            }
            Organization orgOu = hrUser.getOuOrganization();
            if (orgOu != null) {
                record.set(Borrow::getOrgId, orgOu.getOrganizationId());
                record.set(Borrow::getOrgCode, orgOu.getOrganizationCode());
                record.set(Borrow::getOrgName, orgOu.getOrganizationName());
            }
            Organization department = hrUser.getDepartmentOrganization();
            if (department != null) {
                record.set(Borrow::getDepartmentId, department.getOrganizationId());
                record.set(Borrow::getDepartmentCode, department.getOrganizationCode());
                record.set(Borrow::getDepartmentName, department.getOrganizationName());
            }
        }

        return super.doSave(action, records);
    }


    private QlResult abandon(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Assert.notEmpty(records, "参数缺失");
        Assert.isTrue(records.size() == 1, "仅支持1条数据");
        Record record = records.get(0);
        Long borrowId = record.get(Borrow::getBorrowId);
        Record borrow = qlService.readByKey("Borrow",borrowId,Record.class);
        Assert.notNull(borrow, "借阅申请ID不存在");

        if (BorrowStatusEnum.APPROVED.getCode().equals(borrow.get(Borrow::getStatus))) {
            throw new BaseException("当前状态不能操作废弃");
        }

        Record r = new Record();
        r.put(Borrow::getBorrowId, borrowId);
        r.put(Borrow::getStatus, BorrowStatusEnum.ABANDON.getCode());
        qlService.update("Borrow", Arrays.asList(r));
        return QlResult.empty();
    }

    private QlResult saveOrUpdateBorrowAttach(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        qlService.deleteByWrapper(QlWrappers.update("BorrowAttach").eq(BorrowAttach::getBorrowId, records.get(0).get(Borrow::getBorrowId)));
        List<BorrowAttach> list = new ArrayList<>();
        log.info("借阅申请获取到的附件信息==={}", JSONObject.toJSONString(records));
        records.forEach(e -> {
            if (e.get(BorrowAttach::getAttachId) != null) {
                BorrowAttach attach = new BorrowAttach();
                attach.setBorrowId(e.get(BorrowAttach::getBorrowId));
                attach.setAttachId(e.get(BorrowAttach::getAttachId));
                attach.setAttachName(e.get(BorrowAttach::getAttachName));
                attach.setAttachPath(e.get(BorrowAttach::getAttachPath));
                list.add(attach);
            }
        });
        if (CollectionUtils.isNotEmpty(list)) {
            qlService.create("BorrowAttach", list);
        }
        return QlResult.empty();
    }
}
