package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.supplier.info.dto.InfoDTO;
import com.midea.cloud.srm.model.supplier.info.entity.BankInfo;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.model.supplier.info.entity.SiteInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-supplier:cloud-biz-supplier}",
        path = "${cloud.scc.feign-name-mapping.cloud-biz-supplier-path:/api-sup}",
        contextId = "rbacSupClient"
)
public interface RbacSupClient {

    /**
     * 获取供应商清单
     * @param companyId
     * @return
     */
    @GetMapping({"/sup-anon/internal/info/companyInfo/get"})
    CompanyInfo  getCompanyInfoById(@RequestParam("companyId") Long companyId);

}
