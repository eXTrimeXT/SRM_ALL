package com.midea.cloud.srm.sou.purfixprice.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouProjectVO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceEditDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceInqQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceLineGroupQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceLineGroupVO;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceVO;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceEventService;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@RestController
@RequestMapping("/npm/pur_fix_price/buyer")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurFixPriceForBuyerController {

    @Autowired
    private ExtPurFixPriceQueryService extPurFixPriceQueryService;
    @Autowired
    private ExtPurFixPriceEventService extPurFixPriceEventService;

    @ApiOperation("定价列表查询")
    @PostMapping("/page")
    public PageInfo<ExtPurFixPriceHead> listFixPrices(@RequestBody ExtPurFixPriceQueryDTO queryParam) {
        return new PageInfo<>(extPurFixPriceQueryService.listFixPrices(queryParam));
    }

    @ApiOperation("查询可用的集采询比价")
    @PostMapping("/queryPurInq")
    public PageInfo<ApiPurInqSouProjectVO> queryPurInq(@RequestBody ExtPurFixPriceInqQueryDTO queryParam) {
        return new PageInfo<>(extPurFixPriceQueryService.queryPurInq(queryParam));
    }

    @ApiOperation("查询集采询比价中标明细")
    @PostMapping("/queryPurInq/orderItemList")
    public ExtPurFixPriceLineGroupVO getPurInqOrderItems(@RequestBody ExtPurFixPriceLineGroupQueryDTO queryParam) {
        return extPurFixPriceQueryService.getPurInqOrderItems(queryParam);
    }

    @ApiOperation("编辑定价单")
    @PostMapping("/edit")
    public ExtPurFixPriceEditDTO editFixPrice(@RequestBody ExtPurFixPriceEditDTO param) {
        extPurFixPriceEventService.editFixPrice(param);

        return param;
    }

    @ApiOperation("查询定价单详情")
    @GetMapping("/getFixPrice/{purFixPriceHeadId}")
    public ExtPurFixPriceVO getFixPrice(@PathVariable("purFixPriceHeadId") Long purFixPriceHeadId) {
        return extPurFixPriceQueryService.getFixPrice(purFixPriceHeadId);
    }

    @ApiOperation("删除定价单")
    @PostMapping("/remove/{purFixPriceHeadId}")
    public void removeFixPrice(@PathVariable("purFixPriceHeadId") Long purFixPriceHeadId) {
        extPurFixPriceEventService.removeFixPrice(purFixPriceHeadId);
    }

    @ApiOperation("导出明细")
    @GetMapping("downloadExcel/{purFixPriceHeadId}")
    public void downloadExcel(@PathVariable("purFixPriceHeadId")Long purFixPriceHeadId,HttpServletResponse response) throws IOException {
        try {
            extPurFixPriceQueryService.downloadExcel(purFixPriceHeadId,response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
