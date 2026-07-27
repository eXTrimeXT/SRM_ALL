package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.sou.designplans.dto.OrganizationDto;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

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
@FeignClient(value = "cloud-biz-base", contextId = "baseRbacCenterClient", path = "/api-base")
public interface BaseExtClient {

    /**
     * 添加或更新组织用户
     * @param infoList 参数
     * @return 返回
     */
    @ApiOperation("添加或更新组织用户")
    @PostMapping("/orgUser/insertOrUpdateOrgUser")
    List<OrganizationUser> insertOrUpdateOrgUser(@RequestBody List<OrganizationUser> infoList);

}
