package com.midea.cloud.srm.sou.meiql.ca.controller;

import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaHistoryPriceDto;
import com.midea.cloud.srm.sou.meiql.ca.service.CaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/sou/api/v1/ca")
@Api(value = "定标申请", tags = "定标申请")
public class CaController {
    @Autowired
    private CaService caService;

    @GetMapping("/add")
    @ApiOperation("新增")
    public CaDTO add(@ApiParam(value = "寻源单ID") @RequestParam("projectId") Long projectId) throws Exception {
        return caService.add(projectId);
    }
    @PostMapping("/addHistoryBidPrice")
    @ApiOperation("新增")
    public void add(@ApiParam(value = "添加招标历史价格记录") @RequestBody List<CaHistoryPriceDto> caHistoryPriceList) throws Exception {
        caService.saveBidHistoryPrice(caHistoryPriceList);
    }
}
