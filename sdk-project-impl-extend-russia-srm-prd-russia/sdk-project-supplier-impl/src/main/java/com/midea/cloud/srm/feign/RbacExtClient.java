package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.rbac.ExtUserPermissionDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
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
@FeignClient(value = "rbac-center", contextId = "baseRbacClient-1", path = "/api-rbac")
public interface RbacExtClient extends FlowBusinessCallbackClient {
    /**
     * 通过用户ID获取采购商用户相关信息
     * @param id
     * @return
     */
    @ApiOperation("通过用户ID获取采购商用户相关信息")
    @GetMapping("/extUser/getByBuyer")
    ExtUserPermissionDTO getByBuyer(@RequestParam(value = "id") Long id);


    /**
     * 供应商用户推送阳光诚信
     * @param uList
     * @return
     */
    @ApiOperation("供应商用户推送阳光诚信")
    @PostMapping("/extUser/pushVendorSiss")
    void pushVendorSiss(@RequestBody List<User> uList);
}
