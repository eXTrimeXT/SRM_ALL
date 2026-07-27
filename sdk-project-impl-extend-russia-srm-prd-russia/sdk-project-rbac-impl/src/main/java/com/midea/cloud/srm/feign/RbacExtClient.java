package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.rbac.user.dto.UserPermissionDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
 *  修改日期: 2023/10/25 11:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "rbac-center", contextId = "baseRbacClient", path = "/api-rbac")
public interface RbacExtClient extends FlowBusinessCallbackClient {
    /**
     * 增加供应商
     * @param userPermissionDTO 参数
     */
    @ApiOperation("增加供应商")
    @PostMapping("/user/addVendor")
    void addVendor(@RequestBody UserPermissionDTO userPermissionDTO);

    /**
     * 修改供应商
     * @param userPermissionDTO 参数
     */
    @ApiOperation("修改供应商")
    @PostMapping("/user/modifyVendor")
    void modifyVendor(@RequestBody UserPermissionDTO userPermissionDTO);

}
