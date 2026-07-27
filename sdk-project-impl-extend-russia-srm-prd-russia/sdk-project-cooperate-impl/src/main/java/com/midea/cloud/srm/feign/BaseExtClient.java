package com.midea.cloud.srm.feign;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.dto.OrgQueryDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.scene.entity.SceneTemplate;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
@FeignClient(value = "cloud-biz-base", contextId = "extBaseCooperateClient", path = "/api-base")
public interface BaseExtClient extends FlowBusinessCallbackClient {
    /**
     * 备注
     * @param requestBody 参数
     * @return 返回
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @PostMapping("/flow/event/getIsEnableFlow")
    Boolean getIsEnableFlow(@RequestBody String requestBody);

    /**
     * 获取组织关系
     * @param organizationIds 参数
     * @return 返回
     */
    @ApiOperation("获取组织关系")
    @PostMapping("/pjorganizationRelation/listOrganizationRelation")
    List<OrganizationRelation> listOrganizationRelation(@RequestBody List<Long> organizationIds);

    /**
     * listSceneTemplate
     * @param sceneTemplate
     * @return
     */
    @ApiOperation("分页查询附件模板")
    @PostMapping("/base/scene_template/listAll")
    List<SceneTemplate> listSceneTemplate(@RequestBody SceneTemplate sceneTemplate);

    /**
     * getOrgAddress
     * @param orgId
     * @return
     */
    @ApiOperation("查询组织地址")
    @GetMapping("/orgQuery/getOrgAddress")
    List<Record> getOrgAddress(@RequestParam Long orgId);

    /**
     * 查询组织地址-批量方法
     * @param orgIdList
     * @return
     */
    @ApiOperation("查询组织地址-批量方法")
    @PostMapping("/orgQuery/getOrgAddressBatch")
    public Map<Long, List<Record>> getOrgAddressBatch(@RequestBody List<Long> orgIdList);

    /**
     * getSubOrgs
     * @param orgQueryDTO
     * @return
     */
    @ApiOperation("查询所有下级组织列表")
    @PostMapping("/orgQuery/getSubOrgs")
    public List<Organization> getSubOrgs(@RequestBody OrgQueryDTO orgQueryDTO);

    /**
     * queryMaxLevelCategory
     * @param purchaseCategory
     * @return
     */
    @ApiOperation("根据物料小类查-二开")
    @PostMapping("/purchase/ext/purchaseCategory/queryMaxLevelCategory")
    PurchaseCategory queryMaxLevelCategory(@RequestBody PurchaseCategory purchaseCategory);

    /**
     * queryMaxLevelCategoryList
     * @param categoryIds
     * @return
     */
    @ApiOperation("根据物料小类查-二开(批量)")
    @PostMapping("/purchase/ext/purchaseCategory/queryMaxLevelCategoryList")
    List<PurchaseCategory> queryMaxLevelCategoryList(@RequestBody List<Long> categoryIds);

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
     * 根据id查询
     * @param organizationId id
     * @return 组织
     */
    @ApiOperation("查询所有下级组织列表")
    @PostMapping("/organization/organization/ch/getOrganizationByOrgId")
    com.midea.cloud.srm.model.base.entity.Organization getOrganizationByOrgId(@RequestParam("organizationId") Long organizationId);

    /**
     * 根据编码查询
     * @param organizationCodeList
     * @return
     */
    @ApiOperation(value = "根据编码查询", notes = "根据编码查询")
    @PostMapping("/organization/organization/ch/listOrganizationByOrgCodes")
    public List<com.midea.cloud.srm.model.base.entity.Organization> listOrganizationByOrgCodes(@RequestBody List<String> organizationCodeList);

}
