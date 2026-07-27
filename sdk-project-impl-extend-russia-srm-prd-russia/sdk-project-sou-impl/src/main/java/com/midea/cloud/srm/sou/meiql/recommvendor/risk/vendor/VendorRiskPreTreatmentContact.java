package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: for srm查询供应商默认联系人
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentContact extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentContact start...");
        //通过RiskComponent组件获取注入依赖Bean实例QlOpenClient，跨微服务查询供应商联系人表
        List<ContactInfoDto> vendorContactList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.CONTACTINFO).in(ContactInfoDto::getCompanyId, riskRequest.getVendorIdList()), ContactInfoDto.class);
        //定义供应商联系人缓存HashMap对象，key-value，key为供应商ID，value为供应商对应的联系人列表
        Map<Long, List<ContactInfoDto>> vendorContactMap = new HashMap<>(50);
        //判断是否查询到供应商联系人信息
        if(CollectionUtils.isNotEmpty(vendorContactList)) {
            //查询到供应商联系人信息时，将列表转换成HashMap缓存对象，key-value，key为供应商ID，value为供应商对应的联系人列表
            vendorContactMap = vendorContactList.stream().collect(Collectors.groupingBy(c->c.getCompanyId()));
        }
        log.info("riskService VendorRiskPreTreatmentContact end...");
        //响应，Data为供应商联系人缓存HashMap对象
        return new RiskResponse(vendorContactMap);
    }
}
