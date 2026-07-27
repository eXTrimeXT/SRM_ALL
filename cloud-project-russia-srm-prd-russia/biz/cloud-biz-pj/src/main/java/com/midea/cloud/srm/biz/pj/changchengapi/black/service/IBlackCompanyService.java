package com.midea.cloud.srm.biz.pj.changchengapi.black.service;

import com.midea.cloud.srm.model.pj.changchengapi.yangguan.BlackCompanyInfo;
import io.swagger.annotations.ApiOperation;

/**
 * @author huangbf3
 */
public interface IBlackCompanyService {
    /**
     * 备注
     * @param taxCode
     * @return
     */
    @ApiOperation("通过信用代码获取公司信息")
    BlackCompanyInfo blackcompanyInfo(String taxCode);
}
