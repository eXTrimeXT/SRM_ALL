package com.midea.cloud.srm.feign.pj.supplier;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationOpenApiDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
 *  修改日期: 2023/10/05 17:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "cloud-biz-supplier", contextId = "supplierExt",path = "/api-sup")
public interface SupplierExtClient extends FlowBusinessCallbackClient {

    /**
     * 备注
     * @param entryConfig
     * @return
     */
    @ApiOperation(value = "分页条件查询-供应商准入流程数据列表", notes = "分页条件查询-供应商准入流程数据列表", httpMethod = "POST")
    @PostMapping("/entry/entryConfig/listPageByParam")
    public PageInfo<EntryConfig> listEntryConfigPageByParam(@RequestBody EntryConfig entryConfig);
}
