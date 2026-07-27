package com.midea.cloud.srm.supcooperate.meiql.requirement.controller;

import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.supcooperate.meiql.requirement.processor.ExtPrSouRequirementVendorExportProcessor;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author: panmq
 * @Date: 2024/03/06/ $
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/requirement/vendor")
public class ExtPrSouRequirementVendorController extends BaseController {

    @Autowired
    private SiesClient siesExportClient;

    @Autowired
    private ExtPrSouRequirementVendorExportProcessor extPrSouRequirementVendorExportProcessor;

    @PostMapping("/exportdataAsSies")
    @ApiOperation(value = "生成数据", notes = "生成数据")
    public void exportdata(@RequestBody ExtPrSouRequirementVendor params, HttpServletRequest request) throws Exception {

        siesExportClient.exportExcel(this.getClass(), "exportdata", params, request);
    }

    @PostMapping("/exportdata")
    @ApiOperation(value = "生成数据", notes = "生成数据")
    public void exportdataAsEasyExcel(@RequestBody ExtPrSouRequirementVendor params, HttpServletResponse response) throws Exception {

        extPrSouRequirementVendorExportProcessor.doExportAsEasyExcel(params, response);
    }
}
