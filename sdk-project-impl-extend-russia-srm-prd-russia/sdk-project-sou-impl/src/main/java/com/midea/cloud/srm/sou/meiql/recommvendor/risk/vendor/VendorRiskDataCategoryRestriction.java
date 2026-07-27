package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sup.orgcategory.enums.PjSupplierControlType;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatForm;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatFormCategory;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 供应商品类受限具体内容
 * @author GW00311146
 */
@Slf4j
public class VendorRiskDataCategoryRestriction extends AbstractRiskPretreatment {

    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskDataCategoryRestriction start...");

        //请求参数，供应商ID列表集合
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("OrgCatForm")
                .in(OrgCatForm::getVendorId,riskRequest.getVendorIdList())
                .eq(OrgCatForm::getSupplierControlType, PjSupplierControlType.CATEGORY_LIMIT_FLAG)
                .eq(OrgCatForm::getApproveStatus, SouApprovalStatusEnum.APPROVED);
        List<OrgCatForm> orgCatFormList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP,wrapper,OrgCatForm.class);


        orgCatFormList.forEach(orgCatForm ->{
            log.info("------------------------------------------>"+orgCatForm.toString());
            QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query("OrgCatFormCategoryDetail")
                    .eq(OrgCatForm::getOrgCatFormId,orgCatForm.getOrgCatFormId())
                    .eq(OrgCatFormCategory::getSelected, Enable.Y)
                    .eq(OrgCatFormCategory::getControlFlag, '1');
            List<OrgCatFormCategory> orgCatFormCategoryList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP,qlOpenQueryWrapper,OrgCatFormCategory.class);

            //子表-控制范围
            orgCatForm.setRangeList(orgCatFormCategoryList);
        } );

        log.info("riskService VendorRiskDataCategoryRestriction end...");

        return new RiskResponse(orgCatFormList);
    }
}
