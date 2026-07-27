package com.midea.cloud.srm.feign.pj.rbac;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationOpenApiDTO;
import com.midea.cloud.srm.model.rbac.user.dto.UserPermissionDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.rbac.user.entity.UserThird;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/11/19 17:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "rbac-center", contextId = "baseRbacClient", path = "/api-rbac")
public interface RbacExtClient extends FlowBusinessCallbackClient {
    /**
     * 查询第三方账号
     *
     * @param userThird 参数
     * @return
     */
    @PostMapping("/extUser/selectUserThird")
    List<UserThird> selectUserThird(UserThird userThird);
}
