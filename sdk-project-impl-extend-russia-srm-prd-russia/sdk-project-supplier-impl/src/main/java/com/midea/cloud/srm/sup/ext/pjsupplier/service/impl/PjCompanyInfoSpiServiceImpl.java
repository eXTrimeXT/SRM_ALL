package com.midea.cloud.srm.sup.ext.pjsupplier.service.impl;

import com.midea.cloud.srm.model.supplier.info.dto.InfoDTO;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.supauth.company.service.ICompanyInfoSPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
/**
 * @author luxc18
 */
@Slf4j
@Primary
@Service
public class PjCompanyInfoSpiServiceImpl implements ICompanyInfoSPIService {

    /**
     * 获取供应商编码
     *
     * @param companyInfo
     * @return
     */
    @Override
    public String getCompanyCode(CompanyInfo companyInfo) {
        return "";
    }

    @Override
    public void checkBankInfos(InfoDTO infoDTO) {

    }
}

