package com.midea.cloud.srm.feign.pj.base;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationOpenApiDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.flow.process.dto.OaRequestDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.flow.process.dto.TemplateHeaderDTO;
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
@FeignClient(value = "cloud-biz-base", contextId = "baseExt",path = "/api-base")
public interface BaseExtClient extends FlowBusinessCallbackClient {


    /**
     * 备注
     * @param orgTypeCode
     * @param organizationList
     * @return
     */
    @ApiOperation(value = "批量创建组织", notes = "1.组织校验，根据组织编码新增更新组织  \n" +
            "2.同步组织至IAM  \n" +
            "3.目前只支持5000条批量操作  \n")
    @PostMapping({"/organization/api/v1.0/saveOrganizationBatch/{orgTypeCode}"})
    List<OrganizationOpenApiDTO> saveOrganizationBatch(@PathVariable("orgTypeCode") String orgTypeCode, @RequestBody List<OrganizationOpenApiDTO> organizationList);

    /**
     * 备注
     * @param purchaseCategories
     */
    @ApiOperation(value = "批量添加采购分类", notes = "批量添加采购分类")
    @PostMapping({"/purchase/purchaseCategory/batchSaveOrUpdate"})
    void batchSaveOrUpdate(@RequestBody List<PurchaseCategory> purchaseCategories);

    /**
     * 备注
     * @param wfOaParam
     * @throws Exception
     */
    @ApiOperation(value = "统一接收回调", notes = "统一接收回调")
    @PostMapping({"/flow/api/v1/callbackExt"})
    void callback(@RequestBody OaRequestDTO wfOaParam) throws Exception;

    /**
     * 备注
     * @param organization
     * @return
     */
    @PostMapping("/organization/organization/listAllOrganization")
    @ApiOperation(value = "分页查询组织", notes = "分页查询组织")
    PageInfo<Organization> listAllOrganization(@RequestBody Organization organization);

    /**
     * 备注
     * @param requestBody
     * @return
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @PostMapping("/flow/event/getIsEnableFlow")
    Boolean getIsEnableFlow(@RequestBody String requestBody) ;

    /**
     * 提交
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @ApiOperation("根据流程模板类型查询模板配置")
    @PostMapping("/flow/event/submitEngine")
    String submitEngine(@RequestBody JSONObject jsonObject) throws Exception;

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
     * queryProcessTemplateByCode
     * @param templateHeadCode
     * @return
     */
    @ApiOperation(value = "根据流程头ID获取流程头、行相关信息", notes = "根据流程头ID获取流程头、行相关信息", httpMethod = "GET")
    @GetMapping("/flow/processTemplent/queryProcessTemplateByCode")
    TemplateHeaderDTO queryProcessTemplateByCode(@RequestParam("templateHeadCode") String templateHeadCode);
}
