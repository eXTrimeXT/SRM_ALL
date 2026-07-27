package com.midea.cloud.srm.feign;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@FeignClient(value = "cloud-biz-contract", contextId = "sdkPjClient", path = "/api-cm")
public interface SdkPjClient {

    /**
     * save
     * @param list
     */
    @PostMapping("/contractHead/ext/save")
    @ApiOperation("合同创建")
    void save(@RequestBody List<Record> list);
}
