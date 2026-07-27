package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author xiaym13
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/25 11:36:28
 *  修改内容:
 * </pre>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-supplier-cooperate:cloud-biz-supplier-cooperate}", path = "${cloud.scc.feign-name-mapping.cloud-biz-supplier-cooperate-path:/api-sup-ce}",contextId = "cloud-biz-supplier-sc")
public interface SCExtClient extends FlowBusinessCallbackClient {

    /**
     * 通过用户ID获取品类分工的品类ID
     * @param personInChargeUserId
     * @return
     */
    @ApiOperation("通过用户ID获取品类分工的品类ID")
    @GetMapping("/divisionCategory/ext/getUserDivisionCategoryId")
    List<Long> getUserDivisionCategoryId(@RequestParam("personInChargeUserId")Long personInChargeUserId);
}
