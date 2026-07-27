package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.sou.designplans.dto.OrganizationDto;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
@FeignClient(value = "cloud-biz-base", contextId = "baseSouClient", path = "/api-base")
public interface BaseExtClient extends FlowBusinessCallbackClient {
    /**
     * 根据流程模板类型查询模板配置
     * @param requestBody 参数
     * @return 返回
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @PostMapping("/flow/event/getIsEnableFlow")
    Boolean getIsEnableFlow(@RequestBody String requestBody) ;

    /**
     * 获取层级
     * @param pullQueryList 参数
     * @return 返回
     */
    @ApiOperation("获取层级")
    @PostMapping("/pj/category/getLevelInfo")
    List<Long> getLevelInfo(@RequestBody List<PullQueDto> pullQueryList);

    /**
     * 获取层级
     * @param ids 参数
     * @return 返回
     */
    @ApiOperation("获取层级")
    @PostMapping("/orgQuery/getAreaListByOrgIds")
    List<OrganizationDto> getAreaListByOrgIds(@RequestBody List<Long> ids);

    /**
     * listLastLevelCategoryByIds
     * @param categoryIds
     * @return
     */
    @ApiOperation("根据所选品类ID集合查询末级品类")
    @PostMapping("/pj/category/listLastLevelCategoryByIds")
    List<PurchaseCategory> listLastLevelCategoryByIds(@RequestBody Set<Long> categoryIds);

    /**
     * 根据字典编码获取字典明细
     * @param dictCode
     * @param dictItemCode
     * @return
     */
    @ApiOperation("根据字典编码获取字典明细")
    @GetMapping({"/pjDictItem/getDictItem"})
    DictItem getDictItem(@RequestParam("dictCode") String dictCode, @RequestParam("dictItemCode") String dictItemCode);

    /**
     * 获取组织
     *
     * @param orgCodes 参数
     * @return 返回
     */
    @ApiOperation("根据组织编码列表获取组织")
    @PostMapping("/organization/organization/ch/getOrganizationByOrgCodes")
    List<Organization> getOrganizationByOrgCodes(@RequestBody List<String> orgCodes);
}
