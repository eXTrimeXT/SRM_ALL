package com.midea.cloud.srm.biz.pj.changchengapi.companybigdata;

import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author huangbf3
 */
public interface CompanyBigDataService {

    /**
     * 备注
     * @param companyNames
     * @return
     */
    @ApiOperation("通过供应商名称去爱企查查询数据")
    List<CompanyAQCApiDTO> findAqcByNames(List<String> companyNames);
}
