package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorStatusEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;
import com.midea.cloud.srm.model.sou.req.SouIntDepositRefund;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositInvoiceStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositRefundStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.InvoiceTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouRecommendedVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.sou.ccapipayments.service.CcApiPaymentWithBusinessService;
import com.midea.cloud.srm.sou.req.service.SouIntDepositInvoiceService;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.mideacloud.partner.enums.Enable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 *  寻源单意向金退款
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/7 14:06
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class IntDepositRefundBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private SouReqApplyService souReqApplyService;
    @Autowired
    private SouIntDepositInvoiceService souIntDepositInvoiceService;

    @Autowired
    private CcApiPaymentWithBusinessService ccApiPaymentWithBusinessService;

    @Autowired
    private QlOpenClient qlOpenClient;
    public IntDepositRefundBuyerRepository() {
        //注册action
        this.register("submit", this::submit, this::beforeSubmit, this::afterSubmit, true, "提交");
    }

    /**
     * 校验供应商是否被推荐
     * @param records
     * @param souReqHead
     * @return
     */
    private String checkIsRecomm(List<Record> records, SouReqHead souReqHead) {
        if(StringUtils.isBlank(souReqHead.getRequirementHeadNoList()) || CollectionUtils.isEmpty(records)) {
            return null;
        }
        List<String> requirementHeadNumList = new ArrayList<>(Arrays.asList(souReqHead.getRequirementHeadNoList().split(SrmConstant.SIG_3)));
        List<ExtSouDemand> souDemandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).in(ExtSouDemand::getApplicantNo, requirementHeadNumList), ExtSouDemand.class);

        if(CollectionUtils.isEmpty(souDemandList)) {
            return null;
        }

        List<Long> projectIdList = souDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList());

        List<RecommvendorProjectDto> projectList = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProject.name())
                .eq(ExtSouProject::getSouType, SouTypeEnum.recomm.name())
                .in(ExtSouProject::getProjectId, projectIdList)
                .in(ExtSouProject::getProjectStatus, Arrays.asList(SouRecommvendorStatusEnum.APPROVED.getCode(), SouRecommvendorStatusEnum.APPROVING.getCode())), RecommvendorProjectDto.class);

        if(CollectionUtils.isEmpty(projectList)) {
            return null;
        }


        List<Long> vendorIdList = records.stream().map(v -> v.get(SouReqApply::getVendorId)).distinct().collect(Collectors.toList());

        QlQueryWrapper queryWrapper = QlWrappers.query(RecommType.Recommvendor.name());
        queryWrapper.in(ExtSouVendor::getProjectId, projectList.stream().map(RecommvendorProjectDto::getProjectId).collect(Collectors.toList()));
        queryWrapper.in(ExtSouVendor::getVendorId, vendorIdList);

        queryWrapper.select(ExtSouVendor::getVendorId, ExtSouVendor::getVendorCode, ExtSouVendor::getVendorName);

        List<Record> recommvendorList = qlService.queryByWrapper(queryWrapper, Record.class);
        if(CollectionUtils.isNotEmpty(recommvendorList)) {
            return recommvendorList.stream().map(v -> v.get(ExtSouVendor::getVendorName)).distinct().collect(Collectors.joining(SrmConstant.SIG_3));
        }

        return null;
    }

    private void beforeSubmit(QlQueryAction queryAction, Payload payload) {
        List<Record> records = payload.asRecords();
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, records.get(0).get(SouIntDepositRefund::getReqHeadId), SouReqHead.class);
        Record requirementHead = qlOpenClient.read(ContextPath.SUP_CE,MqlType.PURCHASE_REQUIREMENT_HEAD, souReqHead.getRequirementHeadId(), Record.class);
        //需求申请单未取消，未作为推荐供应商，且供应商推荐未审批通过，可以退款。
        if(!Arrays.asList(RequirementApproveStatus.ABANDONED, RequirementApproveStatus.WITHDRAW).contains(requirementHead.get(RequirementHead::getAuditStatus))) {
            String checkMsg = checkIsRecomm(records, souReqHead);
            if (StringUtils.isNotBlank(checkMsg)) {
                throw new RuntimeException(MessageFormat.format("退款失败：供应商[{0}]已被推荐，不可退款。", checkMsg));
            }
        }
        //查询最新的一条开票记录
        SouIntDepositInvoice params = SouIntDepositInvoice.builder()
                .reqHeadId(records.get(0).get(SouIntDepositRefund::getReqHeadId))
                .applyId(records.get(0).get(SouIntDepositRefund::getApplyId))
                .vendorId(records.get(0).get(SouIntDepositRefund::getVendorId))
                .invoiceType(InvoiceTypeEnum.INVOICE.getCode())
                .build();
        SouIntDepositInvoice souIntDepositInvoice = souIntDepositInvoiceService.getLatestData(params);
        //检查最新记录是否已经开红票对冲，且红票状态为已开具
        if (ObjectUtil.isNotEmpty(souIntDepositInvoice)) {
            if(IntDepositInvoiceStatusEnum.INVOICING.getCode().equals(souIntDepositInvoice.getStatus())) {
                throw new BaseException("退款失败：供应商开票中，不可退款");
            }
            if (IntDepositInvoiceStatusEnum.INVOICED.getCode().equals(souIntDepositInvoice.getStatus())) {
                //如果已经开票，则要检查是否开了红字发票对冲，如果没有则不允许退款
                params.setFromDepositInvoiceId(souIntDepositInvoice.getInvoiceId());
                params.setInvoiceType(InvoiceTypeEnum.RED_INVOICE.getCode());
                SouIntDepositInvoice souIntDepositInvoice1 = souIntDepositInvoiceService.getLatestData(params);
                if (ObjectUtil.isEmpty(souIntDepositInvoice1) || !IntDepositInvoiceStatusEnum.INVOICED.getCode().equals(souIntDepositInvoice1.getStatus())) {
                    throw new RuntimeException("退款失败：供应商已开票，不可退款，需开红字发票之后退款。");
                }
            }
        }
    }

    private void afterSubmit(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {

    }


    /**
     * 退款逻辑：
     * 1、项目取消：仅对应的需求申请单取消时，允许发起退款申请。
     * 2、如果该供应商。该供应商在报名之后，未作为推荐供应商，不参与投标，可以退款。（供应商推荐未审批通过，不可退款）（以上：若供应商已开票，不可退款，需开红冲发票之后退款。）
     * 3、仅已成功缴纳的供应商可以退款。
     * 4、允许直接报名的供应商，报名后默认不涉及。
     * 5、已退款、不涉及不可退款。
     * 6、已取消的状态大于推荐逻辑，只要项目取消了，已推荐也可以退款。
     *
     * @param queryAction
     * @return
     */
    private QlResult submit(QlQueryAction queryAction) {
        List<Record> recordList = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();

        QlResult qlResult = super.save(ProxyQlQueryAction.proxy(queryAction, "save"));
        Collection<Record> saveList = qlResult.getRefValues(queryAction.getType());
        //更新申请单状态
        List<SouReqApply> updateList = new ArrayList<>(recordList.size());
        recordList.stream().forEach(record -> {
            SouReqApply reqApply = new SouReqApply();
            reqApply.setApplyId(record.get(SouIntDepositRefund::getApplyId));
            reqApply.setDepositRefundStatus(IntDepositRefundStatusEnum.REFUNDING.getCode());
            updateList.add(reqApply);
        });

        souReqApplyService.updateBatchById(updateList);
        //触发接口
        ccApiPaymentWithBusinessService.callApiPaymentWithDepositRefund(new ArrayList<>(saveList));
        return qlResult;
    }
}
