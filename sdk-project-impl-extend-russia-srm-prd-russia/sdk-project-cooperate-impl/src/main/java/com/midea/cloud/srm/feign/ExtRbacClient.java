package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.rbac.ExtUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@FeignClient(value = "${cloud.scc.feign-name-mapping.rbac-center:rbac-center}", path = "${cloud.scc.feign-name-mapping.rbac-center-path:/api-rbac}", contextId = "ExtRbacClient")
public interface ExtRbacClient extends FlowBusinessCallbackClient {
    /**
     * 备注
     * @param id 参数
     * @return 返回
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @GetMapping("/extUser/getByUserId")
    ExtUser getByUserId(@RequestParam("id") Long id);
}
