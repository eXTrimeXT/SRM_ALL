package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm 寻源报名供应商联系人
 *
 * @author srm
 * @date 2024-05-18
 */
public class VendorRiskPreTreatmentReqApply extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        //寻源报名供应商联系人缓存对象，key-value key为供应商ID， value为寻源报名供应商信息
        Map<Long, SouReqApply> vendorReqApplyMap = new HashMap<>(50);
        //判断合并申请单号是否为空
        if(StringUtils.isNotBlank(riskRequest.getApplicantNo())) {
            //不为空时，解析出合并申请单号，放到list集合，元素为单个申请单号
            List<String> applicantNoList = Arrays.asList(riskRequest.getApplicantNo().split(SrmConstant.SIG_1));
            //根据申请单号查询寻源单据
            List<SouReqHead> souReqHeadList = RiskComponent.getInstance().getQlService().queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).in(SouReqHead::getRequirementHeadNo, applicantNoList), SouReqHead.class);
            //判断寻源单据是否为空
            if(CollectionUtils.isNotEmpty(souReqHeadList)) {
                //查询报名成功的供应商联系人
                List<SouReqApply> souReqApplyList = RiskComponent.getInstance().getQlService().queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY_BUYER).in(SouReqApply::getReqHeadId, souReqHeadList.stream().map(h -> h.getReqHeadId()).collect(Collectors.toList())).eq(SouReqApply::getApplyStatus, SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode()), SouReqApply.class);
                //判断是否存在报名成功的寻源供应商信息
                if(CollectionUtils.isNotEmpty(souReqApplyList)) {
                    //将寻源报名成功的供应商列表信息转换成缓存对象vendorReqApplyMap
                    vendorReqApplyMap = souReqApplyList.stream().collect(Collectors.toMap(c->c.getVendorId(), Function.identity(), (k1, k2)->k2));
                }
            }
        }
        return new RiskResponse(vendorReqApplyMap);
    }
}
