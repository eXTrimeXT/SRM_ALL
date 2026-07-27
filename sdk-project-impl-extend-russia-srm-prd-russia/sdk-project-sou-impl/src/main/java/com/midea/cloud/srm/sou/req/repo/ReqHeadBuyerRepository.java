package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQuery;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.result.RepoRecMap;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.model.base.scene.enums.SceneModuleCodeEnum;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositInvoiceStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.InvoiceTypeEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import com.midea.cloud.srm.sou.req.utils.SouReqCommonUtil;
import com.mideacloud.partner.enums.Enable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class ReqHeadBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouReqHeadService souReqHeadService;
    @Autowired
    private SouReqCommonUtil souReqCommonUtil;

    public ReqHeadBuyerRepository() {
        //注册action
        this.register("submit", this::submit, true, "提交");
        this.register("updatePublicEndTime", this::updatePublicEndTime, true, "调整公示时间");
        this.register("cancelPublic", this::cancelPublic, true, "关闭公示");
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Map<String, Object> params = new HashMap<>(50);
        params.put("depositAmount", records.get(0).get(SouReqHead::getDepositAmount));
        params.put("requirementHeadIdList", records.get(0).get(SouReqHead::getRequirementHeadIdList));
        souReqHeadService.updateExtPr(params);
        //删除附件
        souReqCommonUtil.deleteFiles(queryAction, SceneModuleCodeEnum.SCENE_SOU_REQ_ATTACHMENT.toString(), "reqHeadId");
        return super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
    }

    private QlResult cancelPublic(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Date nowDate = new Date();
        records.forEach(record -> {
            record.put(SouReqHead::getPublicEndTime, nowDate);
        });
       List<SouReqHead> list = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER)
                .select(SouReqHead::getRequirementHeadId)
                .eq(SouReqHead::getReqHeadId, records.get(0).get(SouReqHead::getReqHeadId)), SouReqHead.class
        );

        Map<String, Object> params = new HashMap<>(50);
        params.put("publicEndTime", records.get(0).get(SouReqHead::getPublicEndTime));
        params.put("requirementHeadId", list.get(0).getRequirementHeadId());
        souReqHeadService.updateRequirementHeadExtPr(params);
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), DefaultAction.UPDATE.value(), records));
    }

    private QlResult updatePublicEndTime(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();

        List<SouReqHead> list = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER)
                .select(SouReqHead::getRequirementHeadId)
                .eq(SouReqHead::getReqHeadId, records.get(0).get(SouReqHead::getReqHeadId)), SouReqHead.class
        );

        Map<String, Object> params = new HashMap<>(50);
        params.put("publicEndTime", records.get(0).get(SouReqHead::getPublicEndTime));
        params.put("requirementHeadId", list.get(0).getRequirementHeadId());
        souReqHeadService.updateRequirementHeadExtPr(params);
        return super.update(OpenApiUtil.convertSaveRequest(queryAction.getType(), DefaultAction.UPDATE.value(), records));
    }

    @Override
    protected void beforeDelete(QlQueryAction queryAction, Collection<Record> records) {
        for (Record record : records) {
            //
            Record reqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, record.get(SouReqApply::getReqHeadId), Record.class);
            record.put("requirementHeadNoList", reqHead.get(SouReqHead::getRequirementHeadNoList));
            //回写申请单状态
            QlOpenUpdateWrapper up = QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                    .set("hasCreateSou", Enable.N.name())
                    .eq("requirementHeadId", reqHead.get(SouReqHead::getRequirementHeadId));
            qlOpenClient.update(ContextPath.SUP_CE, up);
        }
        super.afterDelete(queryAction, records);
    }

    @Override
    protected void afterDelete(QlQueryAction queryAction, Collection<Record> records) {
        for (Record record : records) {
            if (ObjectUtil.isNotEmpty(record.get(SouReqHead::getRequirementHeadNoList))) {
                QlOpenQueryWrapper wrapper = QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in("requirementHeadNum", Arrays.asList(record.get(SouReqHead::getRequirementHeadNoList).split(",")));
                List<Record> purchaseRequirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, wrapper, Record.class);
                List<Long> ids = purchaseRequirementHeadList.stream().map(record1 -> record1.get(SouReqHead::getRequirementHeadId)).collect(Collectors.toList());
                //回写申请单状态
                QlOpenUpdateWrapper up = QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                        .set("hasCreateSouReq", Enable.N.name())
                        .set("souReqId", null)
                        .set("souReqNo", null)
                        .in("requirementHeadId", ids);
                qlOpenClient.update(ContextPath.SUP_CE, up);
            }
        }
        super.afterDelete(queryAction, records);
    }

    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        souReqHeadService.handleSignupDone();
        return MeiQl.newCondition();
    }

    @Override
    public QlCondition beforeRead(QlQueryAction queryAction, Collection keys) {
        souReqHeadService.handleSignupDoneSync();
        //非供应商负责人角色不允许查询报名列表
        List<Role> roleList = AppUserUtil.getLoginAppUser().getRolePermissions();
        boolean hasRoleCodeAbc = roleList.stream().anyMatch(role -> "SL".equals(role.getRoleCode())
                || "Company_supplier_leader".equals(role.getRoleCode())
                || "Head_of_the_plate_supplier".equals(role.getRoleCode())
                || "Group_supplier_leader".equals(role.getRoleCode()));
        if (!hasRoleCodeAbc) {
            QlQuery query = queryAction.getQuery();
            query.remove("souReqApplyList");
            queryAction.setQuery(query);
        }
        return null;
    }

    @Override
    public QlResult read(QlQueryAction queryAction) {
        QlResult qlResult = super.read(queryAction);
        if(MqlType.SOU_REQ_HEAD_BUYER.equals(queryAction.getType())) {
            extendInvoiceInfo(qlResult);
        }
        return qlResult;
    }

    private void extendInvoiceInfo(QlResult qlResult) {
        if(Objects.isNull(qlResult) || Objects.isNull(qlResult.getRef()) || Objects.isNull(qlResult.getRef().get(MqlType.SOU_REQ_APPLY_BUYER))) {
            return;
        }
        RepoRecMap repoRecMap = qlResult.getRef().get(MqlType.SOU_REQ_APPLY_BUYER);
        if(MapUtils.isEmpty(repoRecMap)) {
            return;
        }
        List<Long> applyIdList = new ArrayList<>(16);
        repoRecMap.values().forEach(r -> applyIdList.add(r.get(SouReqApply::getApplyId)));

        List<Record> invoiceList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_DEPOSIT_INVOICE_BUYER)
                .in(SouIntDepositInvoice::getApplyId, applyIdList)
                .in(SouIntDepositInvoice::getStatus, Arrays.asList(IntDepositInvoiceStatusEnum.INVOICED.getCode(), IntDepositInvoiceStatusEnum.INVOICING.getCode()))
                .eq(SouIntDepositInvoice::getInvoiceType, InvoiceTypeEnum.INVOICE.getCode()), Record.class);
        List<Long> invoiceApplyIdList = invoiceList.stream().map(r -> r.get(SouIntDepositInvoice::getApplyId)).distinct().collect(Collectors.toList());
        for(Record apply : repoRecMap.values()) {
            apply.put("invoiceStatus", invoiceApplyIdList.contains(apply.get(SouReqApply::getApplyId)));
        }

    }

    @Override
    public QlResult save(QlQueryAction queryAction) {
        //移除附件
        souReqCommonUtil.deleteFiles(queryAction, SceneModuleCodeEnum.SCENE_SOU_REQ_ATTACHMENT.toString(), "reqHeadId");
        return super.save(queryAction);
    }
}
