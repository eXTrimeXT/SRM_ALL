package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.supplier.info.dto.InfoDTO;
import com.midea.cloud.srm.model.supplier.info.entity.BankInfo;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.model.supplier.info.entity.SiteInfo;
import io.swagger.annotations.ApiOperation;
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
        contextId = "cloud-biz-supplier-contract-ext"
)
public interface ContractSupExtClient {

    /**
     * 获取供应商信息
     * @param companyId
     * @return
     */
    @GetMapping({"/sup-anon/internal/info/companyInfo/getInfoByParam"})
    InfoDTO getInfoByParam(@RequestParam("companyId") Long companyId);


    /**
     * 获取供应商清单
     * @param companyId
     * @return
     */
    @GetMapping({"/sup-anon/internal/info/companyInfo/get"})
    CompanyInfo  getCompanyInfoById(@RequestParam("companyId") Long companyId);

    /**
     * 根据条件获取银行信息
     * @param bankInfo
     * @return
     */
    @PostMapping({"/sup-anon/internal/info/bankInfo/getBankInfosByParam"})
    List<BankInfo> getBankInfosByParam(@RequestBody BankInfo bankInfo);

    /**
     * 获取收货地点
     * @param siteInfo
     * @return
     */
    @PostMapping({"/sup-anon/internal/info/siteInfo/getSiteInfosByParam"})
    List<SiteInfo> getSiteInfosByParam(@RequestBody SiteInfo siteInfo);

    /**
     * 获取供应商合同信息
     * @param companyId
     * @return
     */
    @PostMapping({"/sup-anon/internal/info/contactInfo/getContactInfoByCompanyId"})
    ContactInfo getContactInfoByCompanyId(@RequestParam("companyId") Long companyId);

    /**
     * getBankInfoByParmForAnon
     * @param bankInfo
     * @return
     */
    @PostMapping({"/sup-anon/internal/info/bankInfo/getBankInfoByParm"})
    BankInfo getBankInfoByParmForAnon(@RequestBody BankInfo bankInfo);
}
