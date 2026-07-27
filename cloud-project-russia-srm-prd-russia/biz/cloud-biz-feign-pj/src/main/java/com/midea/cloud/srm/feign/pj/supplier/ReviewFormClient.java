package com.midea.cloud.srm.feign.pj.supplier;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.pj.supplier.rev.entity.ReviewForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author huangbf3
 * 供应商受限
 */
@FeignClient(value = "cloud-biz-supplier", contextId = "ReviewForm", path = "/api-sup")
public interface ReviewFormClient extends FlowBusinessCallbackClient {
    /**
     * 备注
     * @param businessId
     * @return
     */
    @ApiOperation(value = "获取资质审查信息", notes = "获取资质审查信息", httpMethod = "GET")
    @GetMapping("/pj/supplier/review/form/info")
    ReviewForm getReviewFormInfo(@RequestParam("businessId") Long businessId);
}
