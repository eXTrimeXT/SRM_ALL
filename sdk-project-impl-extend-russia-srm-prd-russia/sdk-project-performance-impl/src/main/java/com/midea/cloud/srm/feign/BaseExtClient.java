package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <pre>
 *
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/29 11:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "cloud-biz-base", contextId = "basePerfClient", path = "/api-base")
public interface BaseExtClient extends FlowBusinessCallbackClient {
    /**
     * 根据流程模板类型查询模板配置
     * @param requestBody
     * @return
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @PostMapping("/flow/event/getIsEnableFlow")
    Boolean getIsEnableFlow(@RequestBody String requestBody) ;

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
