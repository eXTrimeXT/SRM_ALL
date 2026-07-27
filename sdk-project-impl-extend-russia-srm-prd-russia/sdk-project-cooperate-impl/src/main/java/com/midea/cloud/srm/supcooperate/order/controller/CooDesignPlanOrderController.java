package com.midea.cloud.srm.supcooperate.order.controller;

import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.supcooperate.order.mapper.CooDesignPlanOrderMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Api(value = "CooDesignPlanOrderController", tags = {"订单"})
@Slf4j
@RestController
@RequestMapping("/sc/design/plan/order")
public class CooDesignPlanOrderController {

    @Resource
    private CooDesignPlanOrderMapper designPlanOrderMapper;

    @ApiOperation(value = "订单", notes = "订单", httpMethod = "POST")
    @PostMapping("/getOrderLineHeadList")
    public List<SccSouChDemandYearData> getOrderLineHeadList(@RequestBody PullQueryDto pullQueryDto) {
        return designPlanOrderMapper.getOrderByParam(pullQueryDto);
    }

}
