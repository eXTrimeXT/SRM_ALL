package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
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
 *  修改日期: 2023/10/07 17:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-sou:cloud-biz-sou}", path = "${cloud.scc.feign-name-mapping.cloud-biz-sou-path:/api-sou}",contextId = "recommVendorExt")
public interface RecommVendorExtClient extends FlowBusinessCallbackClient {
    /**
     * 保存推荐供应商信息
     * @param param 参数
     * @return 返回
     */
    @ApiOperation(value = "保存推荐供应商信息")
    @PostMapping("/ext/buyer/recommvendor/editRecommVendorInfo")
    Long editRecommVendorInfo(@RequestBody ApiExtSouRecommVendorInfoDTO param);

    /**
     * 查询推荐供应商基本信息
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation(value = "查询推荐供应商基本信息")
    @GetMapping("/ext/buyer/recommvendor/getRecommVendorInfo")
    ApiExtSouRecommVendorInfoDTO getRecommVendorInfo(@RequestParam(value = "projectId") Long projectId);

}
