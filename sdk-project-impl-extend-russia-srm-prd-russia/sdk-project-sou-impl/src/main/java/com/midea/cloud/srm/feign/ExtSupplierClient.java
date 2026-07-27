package com.midea.cloud.srm.feign;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.vendororgcategory.dto.VendorAiRecommendDTO;
import com.midea.cloud.srm.model.supplier.vendororgcategory.vo.VendorAiRecommendVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-supplier:cloud-biz-supplier}", path = "${cloud.scc.feign-name-mapping.cloud-biz-supplier-path:/api-sup}",contextId = "cloud-biz-supplier-ext1")
public interface ExtSupplierClient extends FlowBusinessCallbackClient {

    /**
     * 智能推荐
     * @param queryParams 参数
     * @return 返回
     */
    @PostMapping("/vendorOrgCategory/aiRecommend")
    List<VendorAiRecommendVO> aiRecommend(@RequestBody VendorAiRecommendDTO queryParams);

    /**
     * 查询供应商风险-黑名单
     * @param vendorIdList
     * @return
     */
    @PostMapping("/pj/companyInfo/querySupplierRiskBlacklist")
    @ApiOperation("查询供应商风险-黑名单")
    List<Record> querySupplierRiskBlacklist(@RequestBody List<Long> vendorIdList);

    /**
     * 通过公司ID获取公司信息
     *
     * @param companyId
     */
    @GetMapping("/sup-anon/internal/info/companyInfo/get")
    CompanyInfo getCompanyInfo(@RequestParam("companyId") Long companyId);

}
