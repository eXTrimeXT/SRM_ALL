package com.midea.cloud.srm.biz.pj.purveyor.controller;

import com.midea.cloud.srm.biz.pj.purveyor.PurveyorService;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorRootDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @author huangbf3
 * 根据社会信用代码查询供应商信息controller
 * **/
@RestController
@RequestMapping("/external/supplier")
public class PurveyorController {

    @Autowired
    private PurveyorService purveyorService;

    /**
     * 根据社会信用代码查询供应商信息接口
     * 20230928
     * **/
    @ApiOperation(value = "根据社会信用代码查询供应商信息接口")
    @PostMapping("/searchListByTaxCodes")
    public PurveyorRootDTO searchListByTaxCodes(@RequestBody List<String> taxCode, @RequestParam("region") String region)  {
        return  purveyorService.searchListByTaxCodes(taxCode,region);
    }

}
