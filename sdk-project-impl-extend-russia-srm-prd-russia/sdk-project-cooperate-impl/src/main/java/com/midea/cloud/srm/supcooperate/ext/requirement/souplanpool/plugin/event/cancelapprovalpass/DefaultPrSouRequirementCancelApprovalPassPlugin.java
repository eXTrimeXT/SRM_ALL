package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.cancelapprovalpass;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolQueryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalpass.IPrSouRequirementCancelApprovalPassPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalpass.PrSouRequirementCancelApprovalPassContext;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.ReuirementCancleCommonService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 招标计划 - 计划取消 - 审批通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCancelApprovalPassPlugin implements IPrSouRequirementCancelApprovalPassPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private PrSouRequirementPoolQueryService prSouRequirementPoolQueryService;

    @Autowired
    private ReuirementCancleCommonService reuirementCancleCommonService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCancelApprovalPassContext judgeApprovalPassAuth(PrSouRequirementCancelApprovalPassContext context) {
        ExtPrSouRequirementCancel reqCancel = qlService.readByKey(ExtPrSouRequirementCancel.class.getSimpleName(), context.getRequirementCancelId(), ExtPrSouRequirementCancel.class);
        switch (reqCancel.getCancelStatus()) {
            case "APPROVING":
                // 审批中
            case "APPROVED":
                // 已审批
                break;
            default:
                throw new IllegalArgumentException("当前单据状态，禁止访问该接口");
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCancelApprovalPassContext executeApprovalPass(PrSouRequirementCancelApprovalPassContext context) {
        ExtPrSouRequirementCancelVO cancelVO = prSouRequirementPoolQueryService.getCancelInfo(context.getRequirementCancelId());

        //采购需求提报ID
        List<Long> requirementHeadIdList = cancelVO.getCancelLineList().stream().map(l -> l.getRequirementHeadId()).distinct().collect(Collectors.toList());

        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementCancel.class)
                .set(ExtPrSouRequirementCancel::getCancelStatus, PrSouRequirementCancelStatusEnum.APPROVED.name())
                .eq(ExtPrSouRequirementCancel::getRequirementCancelId, context.getRequirementCancelId())
                .eq(ExtPrSouRequirementCancel::getCancelStatus, PrSouRequirementCancelStatusEnum.APPROVING.name()));

        //已取消
        qlService.updateByWrapper(QlWrappers.update(MqlType.PURCHASE_REQUIREMENT_HEAD)
                .set(RequirementHead::getAuditStatus, RequirementApproveStatus.ABANDONED.name())
                .in(RequirementHead::getRequirementHeadId, requirementHeadIdList));

        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.CANCEL)
                .set(ExtPrSouRequirementHead::getReqCancelReason, cancelVO.getCancelReason())
                .in(ExtPrSouRequirementHead::getRequirementHeadId, requirementHeadIdList));

        //执行后续业务流程取消逻辑
        List<Record> requirementHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(PurchaseRequirementHeadDTO::getRequirementHeadId, requirementHeadIdList), Record.class);
        if(CollectionUtils.isNotEmpty(requirementHeadList)) {
            List<Long> cancleRequirementHeadIdList = new ArrayList<>(16);
            Map<Long, String> cancleRequirementHeadNumMap = new HashMap<>(16);
            requirementHeadList.stream().forEach(h -> {
                cancleRequirementHeadIdList.add(h.get(RequirementHead::getRequirementHeadId));
                cancleRequirementHeadNumMap.put(h.get(RequirementHead::getRequirementHeadId), h.get(RequirementHead::getRequirementHeadNum));
            });
            reuirementCancleCommonService.cancleReuirement(cancleRequirementHeadIdList, cancleRequirementHeadNumMap);
        }
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
