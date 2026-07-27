package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouOrderDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@FeignClient(value = "cloud-biz-sou", contextId = "SouExtSupClient", path = "/api-sou")
public interface SouExtClient {

    /**
     * 查询投标控制明细
     * @param extProjectNo 参数
     * @return 返回
     */
    @ApiOperation(value = "查询投标控制明细", notes = "查询投标控制明细", httpMethod = "POST")
    @GetMapping("/ext/buyer/bid/init/getExtSouOrderInfo")
    List<ApiExtSouOrderDto> getExtSouOrderInfo(@RequestParam(value = "extProjectNo") String extProjectNo);

}
