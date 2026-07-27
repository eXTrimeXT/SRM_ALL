package com.midea.cloud.srm.biz.pj.mdmcompanyintf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmCompanyDto;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.mdm.entity.SccPjMdmCompanyIntf;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapCreateSupViewDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapCreateSupViewListDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapResponseDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;

import java.util.List;

/**
 * @author huangbf3
 */
public interface ISccPjMdmCompanyIntfService extends IService<SccPjMdmCompanyIntf> {

    /**
     * 备注
     * @param mdmCompanyDtoList
     * @param
     * @return
     */
    MdmResponseDto createSupplierToMdm(List<MdmCompanyDto> mdmCompanyDtoList);
    /**
     * 备注
     * @param sapCreateSupViewListDto
     * @param
     * @return
     */
    SapResponseDto createSupplierToSap(SapCreateSupViewListDto sapCreateSupViewListDto);


    /**
     * 备注
     * @param mdmCompanyDto
     * @param
     * @return
     */
    MdmResponseDto editSupplierToMdm(MdmCompanyDto mdmCompanyDto);

    /**
     * 备注
     * @param companyInfo
     * @return
     */
    MdmResponseDto sendCompanyInfoToMdm(CompanyInfo companyInfo);
}
