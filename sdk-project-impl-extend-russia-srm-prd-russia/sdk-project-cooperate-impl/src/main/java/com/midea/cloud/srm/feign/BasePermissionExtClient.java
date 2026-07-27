package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

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
@FeignClient(value = "cloud-biz-base", contextId = "basePermissionExtClient", path = "/api-base")
public interface BasePermissionExtClient extends FlowBusinessCallbackClient {

    /**
     * 获取集团列表
     * @return 返回
     */
    @GetMapping(value = "/base-anon/permission/getGroupList")
    public String getGroupList();

    /**
     * 获取板块列表
     * @return 返回
     */
    @GetMapping(value = "/base-anon/permission/getBuList")
    public String getBuList();

    /**
     * 获取公司列表
     * @return 返回
     */
    @GetMapping(value = "/base-anon/permission/getOuList")
    public String getOuList();

    /**
     * 获取部门列表
     * @return 返回
     */
    @GetMapping(value = "/base-anon/permission/getDepList")
    public String getDepList();
}
