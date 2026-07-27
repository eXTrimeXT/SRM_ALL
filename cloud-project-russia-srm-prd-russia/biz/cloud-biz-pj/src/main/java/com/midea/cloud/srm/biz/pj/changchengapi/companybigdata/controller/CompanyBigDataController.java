package com.midea.cloud.srm.biz.pj.changchengapi.companybigdata.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.changchengapi.companybigdata.CompanyBigDataService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangbf3
 * bpm=>srm接口
 */
@RestController
@RequestMapping("/external/companyBigData")
public class CompanyBigDataController {

    @Autowired
    private CompanyBigDataService companyBigDataService;

    @ApiOperation(value = "通过供应商名称去爱企查查询数据")
    @PostMapping("/findAQCByNames")
    public List<CompanyAQCApiDTO> findAqcByNames(@RequestBody List<String> companyNames) {
        return companyBigDataService.findAqcByNames(companyNames);
    }
}
