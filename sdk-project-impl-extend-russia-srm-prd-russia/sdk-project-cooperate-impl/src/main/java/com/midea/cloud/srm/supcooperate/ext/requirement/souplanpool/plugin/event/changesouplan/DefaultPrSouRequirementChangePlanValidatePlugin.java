package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.changesouplan;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.IPrSouRequirementChangePlanValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.PrSouRequirementChangePlanContext;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 招标计划池 - 计划变更校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Component
public class DefaultPrSouRequirementChangePlanValidatePlugin implements IPrSouRequirementChangePlanValidatePlugin {

    @Override
    @ApiModelProperty("校验及转化采购申请/招标计划")
    public PrSouRequirementChangePlanContext validateAndConvertReqHead(PrSouRequirementChangePlanContext context) {
        AssertUtils.notNull(context.getParam().getSouReqHead().getAfterTotalAmountByTenKilo(), "请填写变更后概算金额(万元)");
        AssertUtils.isTrue(context.getParam().getSouReqHead().getAfterTotalAmountByTenKilo().compareTo(BigDecimal.ZERO) > 0, "变更后概算金额(万元)必须大于0");
        AssertUtils.notNull(context.getParam().getSouReqHead().getChangeReason(), "请填写变更原因");


        ExtPrSouRequirementHeadDTO result = SouObjectXUtil.convertTargetObj(context.getExistSouPrHead(), ExtPrSouRequirementHeadDTO.class);
        result.getSouReqHead().setChangeRequirementHeadId(result.getRequirementHeadId());
        result.getSouReqHead().setChangeRequirementHeadNum(result.getRequirementHeadNum());

        result.setRequirementHeadId(null);
        result.getSouReqHead().setRequirementHeadId(null);
        result.getSouReqHead().setChangeRequirementHeadId(context.getParam().getRequirementHeadId());
        result.getSouReqHead().setAfterTotalAmountByTenKilo(context.getParam().getSouReqHead().getAfterTotalAmountByTenKilo());
        result.getSouReqHead().setTotalAmountByTenKilo(context.getParam().getSouReqHead().getAfterTotalAmountByTenKilo());
        result.getSouReqHead().setChangeReason(context.getParam().getSouReqHead().getChangeReason());
        result.getSouReqHead().setHasSendSouProfile(Enable.N);
        result.getSouReqHead().setHasCreateVendorRecommend(Enable.N);
        result.getSouReqHead().setHasCreateSouReq(Enable.N);
        result.getSouReqHead().setHasCreateSou(Enable.N);
        result.getSouReqHead().setHasAssigned(Enable.N);
        result.getSouReqHead().setHasSubmit(Enable.N);
        result.setTempSave(context.getParam().isTempSave());

        context.setResult(result);
        return context;
    }

    @Override
    @ApiModelProperty("校验及转化工作成员")
    public PrSouRequirementChangePlanContext validateAndConvertReqGroups(PrSouRequirementChangePlanContext context) {
        if (CollectionUtils.isNotEmpty(context.getResult().getSouGroupList())) {
            context.getResult().getSouGroupList().forEach(group -> {
                group.setRequirementGroupId(null);
                group.setRequirementHeadId(null);
            });
        }

        return context;
    }

    @Override
    @ApiModelProperty("校验及转化推荐供应商")
    public PrSouRequirementChangePlanContext validateAndConvertReqVendors(PrSouRequirementChangePlanContext context) {
        if (CollectionUtils.isNotEmpty(context.getResult().getSouVendorList())) {
            context.getResult().getSouVendorList().forEach(vendor -> {
                vendor.setRequirementVendorId(null);
                vendor.setRequirementHeadId(null);
            });
        }

        return context;
    }

    @Override
    @ApiModelProperty("校验及转化附件")
    public PrSouRequirementChangePlanContext validateAndConvertReqAttaches(PrSouRequirementChangePlanContext context) {
        if (CollectionUtils.isNotEmpty(context.getResult().getSouAttachList())) {
            context.getResult().getSouAttachList().forEach(attach -> {
                attach.setRequirementAttachId(null);
                attach.setRequirementHeadId(null);
            });
        }

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
