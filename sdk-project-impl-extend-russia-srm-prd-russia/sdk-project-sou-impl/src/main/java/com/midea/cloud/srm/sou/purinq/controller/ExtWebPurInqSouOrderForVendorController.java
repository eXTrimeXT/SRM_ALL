package com.midea.cloud.srm.sou.purinq.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqSouOrderItemImportDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ExtPurInqOrderItemHisQueryVO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.sou.inq.ext.excelhandler.conf.ExtInqEnumsCellWriteHandler;
import com.midea.cloud.srm.sou.inq.ext.excelhandler.conf.ExtInqSpinnerWriteHandler;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouOrderEventService;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouOrderQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 集采询比价 - 报价
 * @author 100014337
 */
@RestController
@RequestMapping("/npm/vendor/ext_pur_inq/order")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebPurInqSouOrderForVendorController {

    @Autowired
    private ExtPurInqSouOrderQueryService extPurInqSouOrderQueryService;
    @Autowired
    private ExtPurInqSouOrderEventService extPurInqSouOrderEventService;

    @ApiOperation("获取上一轮报价")
    @GetMapping("/getLastOrderItems")
    public List<ApiPurInqSouOrderItemVO> getLastOrderItems(Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");

        return extPurInqSouOrderQueryService.getLastOrderItems(projectId, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @ApiOperation("查询指定轮次报价信息")
    @GetMapping("/getSouOrderInfo")
    public ApiPurInqSouOrderDetailVO getSouOrderInfo(Long projectId, Integer round) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");

        return extPurInqSouOrderQueryService.getSouOrderInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), round);
    }

    @ApiOperation("供应商历史报价列表查询")
    @PostMapping("/listVendorOrderHis")
    public PageInfo<ExtPurInqOrderItemHisQueryVO> listVendorOrderHis(@RequestBody ExtPurInqOrderItemHisQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsVendor();
        queryParam.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        queryParam.setForBuyer(false);

        return new PageInfo<>(extPurInqSouOrderQueryService.listVendorOrderHis(queryParam));
    }

    @ApiOperation("下载报价导入模板")
    @GetMapping("/getLastOrderItems/download")
    public void getSouOrderInfoForDownload(Long projectId, Integer round) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");
        // 1: 查询报价信息
        ApiPurInqSouOrderDetailVO vo = extPurInqSouOrderQueryService.getSouOrderInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), round);
        // 2: 数据转换
        List<ExtPurInqSouOrderItemImportDTO> dataList = new ArrayList<>(vo.getItemList().size()); {
            int index = 0;
            for (ApiPurInqSouOrderItemVO item : vo.getItemList()) {
                index++;
                ExtPurInqSouOrderItemImportDTO data = new ExtPurInqSouOrderItemImportDTO();
                dataList.add(data);

                data.setSouItemId(String.valueOf(item.getSouItemId()));
                data.setIndex(String.valueOf(index));
                data.setArea(item.getArea());
                data.setItemCode(item.getItemCode());
                data.setItemDesc(item.getItemDesc());
                data.setRequireQuantity(item.getRequireQuantity() != null ? item.getRequireQuantity().stripTrailingZeros().toPlainString() : null);
                data.setUnit(item.getUnit());
                data.setTaxKey(item.getTaxKey());
                data.setInvoiceType(item.getInvoiceType());
                data.setOrderNotaxPrice(item.getOrderNotaxPrice() != null ? item.getOrderNotaxPrice().stripTrailingZeros().toPlainString() : null);
                data.setOrderTaxPrice(item.getOrderTaxPrice() != null ? item.getOrderTaxPrice().stripTrailingZeros().toPlainString() : null);
                data.setPriceTaxTotal(item.getPriceTaxTotal() != null ? item.getPriceTaxTotal().stripTrailingZeros().toPlainString() : null);
                data.setIsLadder(item.getIsLadder() != null ? item.getIsLadder().name() : Enable.N.name());
                data.setExtLeadTime(item.getExtLeadTime() != null ? String.valueOf(item.getExtLeadTime()) : null);
                data.setExtWarrantyPeriod(item.getExtWarrantyPeriod() != null ? String.valueOf(item.getExtWarrantyPeriod()) : null);
                data.setOrderRemark(item.getOrderRemark());
            }
        }
        // 3: 导出文件
        String fileName = LocaleHandler.getLocaleMsg("集采询比价报价明细") + ".xlsx";
        ExcelWriter excelWriter = null;
        try {
            ServletOutputStream outputStream = EasyExcelUtil.getServletOutputStream(HttpServletHolder.getResponse(), fileName);
            excelWriter = EasyExcel.write(outputStream).build();
            WriteSheet mainSheet = EasyExcel
                    .writerSheet(0, "sheet1")
                    .head(ExtPurInqSouOrderItemImportDTO.class)
                    // 处理字典值转换
                    .registerWriteHandler(ExtInqEnumsCellWriteHandler.HANDLER)
                    .registerWriteHandler(ExtInqSpinnerWriteHandler.HANDLER)
                    .build();
            excelWriter.write(dataList, mainSheet);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    @ApiOperation("导入报价信息")
    @PostMapping("/getLastOrderItems/import")
    public Map<String, Object> importSouOrder(@RequestParam(value = "projectId", required = false) Long projectId,
                                              @RequestParam("file") MultipartFile file,
                                              Fileupload fileupload) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");

        return extPurInqSouOrderEventService.importOrderItems(projectId, null, AppUserUtil.getLoginAppUser().getCompanyId(), false, file, fileupload);
    }

    @ApiOperation("/下载集采报价须知附件")
    @GetMapping("/getPurOrderNoticeFile")
    public void getPurOrderNoticeFile() throws IOException {
        extPurInqSouOrderQueryService.getPurOrderNoticeFile();
    }

}
