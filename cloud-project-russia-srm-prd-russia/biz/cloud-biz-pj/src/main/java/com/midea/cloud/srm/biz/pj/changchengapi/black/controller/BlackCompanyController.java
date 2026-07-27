package com.midea.cloud.srm.biz.pj.changchengapi.black.controller;

import com.midea.cloud.srm.biz.pj.changchengapi.black.service.IBlackCompanyService;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.BlackCompanyInfo;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 * bpm=>srm接口
 */
@RestController
@RequestMapping("/external/blackCompany")
public class BlackCompanyController {

    @Autowired
    private IBlackCompanyService iBlackCompanyService;

    /**
     * 阳光诚信系统供应商黑名单
     * @param taxCode 统一社会信用代码
     * @return
     */
    @GetMapping("/public/mdm/sun/blackcompany/info")
    public BlackCompanyInfo blackcompanyInfo(@RequestParam(value = "taxCode")String taxCode) {
        return iBlackCompanyService.blackcompanyInfo(taxCode);
    }
}
