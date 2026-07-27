package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 100014336 ganyh19
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-base:cloud-biz-base}", path = "${cloud.scc.feign-name-mapping.cloud-biz-base-path:/api-base}",
        contextId = "cloud-biz-base-contract-ext"
)
public interface ContractBaseExtClient {

    /**
     * 根据流程模板类型查询模板配置
     * @param requestBody
     * @return
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @PostMapping({"/flow/event/getIsEnableFlow"})
    Boolean getIsEnableFlow(@RequestBody String requestBody);

    /**
     * 根据字典编码获取字典明细
     * @param dictCode
     * @param dictItemCode
     * @return
     */
    @ApiOperation("根据字典编码获取字典明细")
    @GetMapping({"/pjDictItem/getDictItem"})
    DictItem getDictItem(@RequestParam("dictCode") String dictCode, @RequestParam("dictItemCode") String dictItemCode);
}
