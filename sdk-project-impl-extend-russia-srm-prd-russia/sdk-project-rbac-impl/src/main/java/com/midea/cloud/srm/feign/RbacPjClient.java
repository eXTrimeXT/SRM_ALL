package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.pj.siss.dto.SunHonestyReturnDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestySupDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-pj:cloud-biz-pj}", path = "${cloud.scc.feign-name-mapping.cloud-biz-pj-path:/api-pj}",
        contextId = "rbacPjClient"
)
public interface RbacPjClient {

    /**
     * 根据组织ID获取组织
     * @param sunHonestySupDtos
     * @return
     */
    @ApiOperation(value = "推送供应商信息给阳光诚信自助平台")
    @GetMapping("/sun-honesty/pushCompanyUser")
    List<SunHonestyReturnDto> pushCompanyUser(@RequestBody List<SunHonestySupDto> sunHonestySupDtos);

}
