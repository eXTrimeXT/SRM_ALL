
package com.midea.cloud.srm.sou.sourcing.vendor.spi;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderDetailDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryCompent;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouMarginVendorQueryHandler implements ISouSpiBean {

    @Autowired
    private IpMonitoryCompent ipMonitoryCompent;

    @Autowired
    private IExtSouVendorService vendorService;

    @ApiOperation("寻源分页查询的后置处理")
    public void doHandlerAfterQueryMargin(Long projectId, Long vendorId, String souType, ExtSouMarginDto dto) {
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId)
                .eq(ExtSouVendor::getVendorId, vendorId).list();
        if(CollectionUtils.isNotEmpty(vendorList)) {
            ExtSouVendor vendor = vendorList.get(0);
            ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(projectId, vendor.getVendorId(), vendor.getVendorCode(), vendor.getVendorName(), "查看保证金"));
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
