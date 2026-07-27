package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.pool.dto.SouBidRequirementPoolDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
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
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-sou:cloud-biz-sou}", path = "${cloud.scc.feign-name-mapping.cloud-biz-sou-path:/api-sou}", contextId = "inviteTendersExt")
public interface InviteTendersExtClient extends FlowBusinessCallbackClient {
    /**
     * 获取需求申请单号
     * @param appNo 参数
     * @return 返回
     */
    @ApiOperation(value = "获取需求申请单号", notes = "获取需求申请单号")
    @GetMapping("/ext/buyer/recommvendor/getDemandByAppNo")
    Long getDemandByAppNo(@RequestParam(value = "appNo") String appNo);

    /**
     * 根据projectId查询推荐供应商信息
     * @param projectId 参数
     * @return 返回
     */
    @GetMapping("/ext/buyer/recommvendor/getRecommVendorInfoByProjectId")
    @ApiOperation(value = "根据projectId查询推荐供应商信息", notes = "推荐供应商信息")
    public List<ExtSouVendor> getRecommVendorInfoByProjectId(@RequestParam(value = "projectId") Long projectId);

    /**
     * 保存招标基本信息
     * @param param 参数
     * @return 返回
     */
    @ApiOperation(value = "保存招标基本信息")
    @PostMapping("/ext/buyer/bid/init/editProjectInfo")
    Long editProjectInfo(@RequestBody ApiExtSouProjectInfoDTO param);

    /**
     * 查询招标基本信息
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation(value = "查询招标基本信息")
    @GetMapping("/ext/buyer/bid/init/getProjectInfo")
    ApiExtSouProjectInfoDTO getProjectInfo(@RequestParam(value = "projectId") Long projectId);

    /**
     * 招标需求池校验供应商推荐
     * @param param 参数
     * @return 返回
     */
    @ApiOperation("招标需求池校验供应商推荐、标书、寻源需求申请单号-查询")
    @PostMapping("/bid/requirementpool/getRequirementPoolInfo")
    SouBidRequirementPoolDto getRequirementPoolInfo(@RequestBody SouBidRequirementPoolDto param);

}
