
package com.midea.cloud.srm.sou.sourcing.vendor.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderDetailDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouOrderItemVendorQueryHandler implements ISouSpiBean {



    @ApiOperation("寻源分页查询的后置处理")
    public void doHandlerAfterQueryOrderItem(Long orderId, String souType, ApiExtSouOrderDetailDto detailDto) {

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
