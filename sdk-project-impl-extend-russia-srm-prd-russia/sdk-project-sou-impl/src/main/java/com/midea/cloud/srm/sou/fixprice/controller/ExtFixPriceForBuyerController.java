package com.midea.cloud.srm.sou.fixprice.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.model.sou.designplans.dto.AgreementExcelDto;
import com.midea.cloud.srm.model.sou.fixprice.dto.*;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.vo.*;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceEventService;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceQueryService;
import com.mideacloud.common.util.BeanUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/npm/fix-price/buyer")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtFixPriceForBuyerController {

    @Autowired
    private ExtFixPriceQueryService extFixPriceQueryService;
    @Autowired
    private ExtFixPriceEventService extFixPriceEventService;

    @PostMapping("/pageFixPrices")
    @ApiOperation("定价单列表查询")
    public PageInfo<ExtFixPriceHead> listFixPrices(@RequestBody ExtFixPriceQueryDTO queryParam) {
        return new PageInfo<>(extFixPriceQueryService.listFixPrices(queryParam));
    }
    @GetMapping("/getFixPrice/{fixPriceHeadId}")
    @ApiOperation("查看定价单详情")
    public ExtFixPriceHeadVO getFixPrice(@PathVariable("fixPriceHeadId") Long fixPriceHeadId) {
        return extFixPriceQueryService.getFixPrice(fixPriceHeadId);
    }

    @PostMapping("/editFixPrice")
    @ApiOperation("编辑定价单")
    public ExtFixPriceHeadDTO editFixPrice(@RequestBody ExtFixPriceHeadDTO param) {
        return extFixPriceEventService.editFixPrice(param);
    }

    @PostMapping("/deleteFixPrice/{fixPriceHeadId}")
    @ApiOperation("删除定价单")
    public void deleteFixPrice(@PathVariable("fixPriceHeadId") Long fixPriceHeadId) {
        extFixPriceEventService.deleteFixPrice(fixPriceHeadId);
    }

    @PostMapping("/listSouInqOrderItems")
    @ApiOperation("询比价中标信息列表查询")
    public PageInfo<ExtFixPriceInqOrderItemsQueryVO> listSouInqOrderItems(@RequestBody ExtFixPriceInqOrderItemsQueryDTO queryParam) {
        return new PageInfo<>(extFixPriceQueryService.listSouInqOrderItems(queryParam));
    }

    @PostMapping("/listReqLines")
    @ApiOperation("近期采购列表查询")
    public PageInfo<ExtFixPriceReqLinesQueryVO> listReqLines(@RequestBody ExtFixPriceReqLinesQueryDTO queryParam) {
        return new PageInfo<>(extFixPriceQueryService.listReqLines(queryParam));
    }

    @GetMapping("/listSouInqOrderItemsForPriceLine")
    @ApiOperation("查询供应商报价明细")
    public List<ExtFixPriceInqOrderItemVO> listSouInqOrderItemsForPriceLine(Long orderItemId) {
        AssertUtils.notNull(orderItemId, "缺少orderItemId参数");

        return extFixPriceQueryService.listSouInqOrderItemsForPriceLine(orderItemId);
    }

    /**
     * 关闭
     */
    @ApiOperation("关闭定价明细")
    @PostMapping("/closeFixPriceLine")
    public void closeFixPriceLine(@RequestBody ExtFixPriceLine priceLine) {
        extFixPriceEventService.closeFixPriceLine(priceLine);
    }

    @ApiOperation("取消定价明细")
    @PostMapping("/cancelFixPriceLine")
    public void cancelFixPriceLine(ExtFixPriceLineCancelDTO param) {
        extFixPriceEventService.cancelFixPriceLine(param);
    }

    @ApiOperation(value = "定价单明细导出", notes = "定价单明细导出")
    @PostMapping(path = "/exportFixPrice")
    public void exportFixPrice(HttpServletResponse response,@RequestBody ExtFixPriceQueryDTO queryParam) throws IOException {
        if (CollectionUtils.isEmpty(queryParam.getFixPriceIds())) {
            throw new BaseException("请至少选择一条数据");
        }
        List<ExtFixPriceExportVO> extFixPriceExportVOS = extFixPriceQueryService.exportFixPrices(queryParam);
        EasyExcel.write(EasyExcelUtil.getServletOutputStream(response,"定价单明细"), ExtFixPriceExportVO.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet().doWrite(extFixPriceExportVOS);
    }

    @ApiOperation(value = "单个定价单明细导出", notes = "定价单明细导出")
    @GetMapping(path = "/exportFixPriceDetail")
    public void exportFixPriceDetail(HttpServletResponse response,@RequestParam Long fixPriceHeadId) throws IOException {
        List<ExtFixPriceExportVO> extFixPriceExportVOS = extFixPriceQueryService.exportFixPriceLine(fixPriceHeadId);
        List<ExtFixPriceLineExportVO> voList = new ArrayList<>();
        if(CollUtil.isNotEmpty(extFixPriceExportVOS)){
            voList = extFixPriceExportVOS.stream().map(e->BeanUtil.copyProperties(e,ExtFixPriceLineExportVO.class)).collect(Collectors.toList());
        }
        EasyExcel.write(EasyExcelUtil.getServletOutputStream(response,"单个定价单明细"), ExtFixPriceLineExportVO.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet().doWrite(voList);
    }

    @ApiOperation(value = "判断定价单明细是否存在", notes = "判断定价单明细是否存在")
    @PostMapping(path = "/isFixPriceLineExist")
    public boolean isFixPriceLineExist(@RequestBody ExtFixPriceLine extFixPriceLine){
        List<ExtFixPriceLine> priceLineList = extFixPriceQueryService.queryLines(extFixPriceLine.getItemCode(),extFixPriceLine.getSourceFromNo());
        return priceLineList.size()>0;
    }



}
