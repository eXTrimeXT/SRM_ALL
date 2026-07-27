package com.midea.cloud.srm.feign;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-supplier-cooperate:cloud-biz-supplier-cooperate}", path = "${cloud.scc.feign-name-mapping.cloud-biz-supplier-cooperate-path:/api-sup-ce}",contextId = "cloud-biz-supplier-ce-sou")
public interface SupplierCooperateClient extends FlowBusinessCallbackClient {

    /**
     * 订单
     * @param pullQueryDto 查询条件
     * @return 行+头
     */
    @ApiOperation("订单")
    @PostMapping("/sc/design/plan/order/getOrderLineHeadList")
    List<SccSouChDemandYearData> getOrderLineHeadList(@RequestBody PullQueryDto pullQueryDto);


    @ApiOperation("创建定价订单")
    @PostMapping("/purchaseRequirement/createOrderByFixPrice")
    Object createOrderByFixPrice(@RequestBody ExtFixPriceHeadDTO extFixPriceHeadDTO);

    @ApiOperation("新增采购目录上下架商品")
    @PostMapping("/catalogOnShelves/ext/create")
    List<Serializable> create(@RequestBody List<Record> records);

    @ApiOperation("修改采购目录上下架商品")
    @PostMapping("/catalogOnShelves/ext/update")
    List<Serializable> update(@RequestBody List<Record> records);

}
