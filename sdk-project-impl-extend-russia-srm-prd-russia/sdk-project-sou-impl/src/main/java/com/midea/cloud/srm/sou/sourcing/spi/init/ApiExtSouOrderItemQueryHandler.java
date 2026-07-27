package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemQueryDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouOrderItemQueryHandler implements ISouSpiBean {

    @ApiOperation("报价详情查询的后置处理")
    public List<ApiExtSouOrderItemDto> doHandlerAfterOrderItem(ApiExtSouOrderItemQueryDto query, String souType, List<ApiExtSouOrderItemDto> orderItemDtoList) {
        return orderItemDtoList;
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
