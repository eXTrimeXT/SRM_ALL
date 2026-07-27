package com.midea.cloud.srm.biz.pj.mdmcompanyintf.controller;

import com.midea.cloud.srm.biz.pj.mdmcompanyintf.service.ISccPjMdmCompanyIntfService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmCompanyDto;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/mdmcompany")
public class SccPjMdmCompanyIntfController extends BaseController {
    @Autowired
    private ISccPjMdmCompanyIntfService iSccPjMdmCompanyIntfService;

    @PostMapping("/createSupplierToMdm")
    MdmResponseDto createSupplierToMdm(@RequestBody List<MdmCompanyDto> mdmCompanyDtoList) {
        return iSccPjMdmCompanyIntfService.createSupplierToMdm(mdmCompanyDtoList);
    }

    /**
     * 调用MDM供应商主数据接口：自动识别申请 OR 更新
     * @param companyInfo
     * @return
     */
    @PostMapping("/sendCompanyInfoToMdm")
    MdmResponseDto sendCompanyInfoToMdm(@RequestBody CompanyInfo companyInfo) {
        return iSccPjMdmCompanyIntfService.sendCompanyInfoToMdm(companyInfo);
    }

}
