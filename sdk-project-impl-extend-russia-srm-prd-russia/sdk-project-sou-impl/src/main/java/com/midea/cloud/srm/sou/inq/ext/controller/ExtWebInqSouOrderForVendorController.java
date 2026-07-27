package com.midea.cloud.srm.sou.inq.ext.controller;

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
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouOrderItemImportDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqOrderItemHisQueryVO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderDetailVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.sou.inq.ext.excelhandler.conf.ExtInqEnumsCellWriteHandler;
import com.midea.cloud.srm.sou.inq.ext.excelhandler.conf.ExtInqSpinnerWriteHandler;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouOrderEventService;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouOrderQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 长城 - 询比价 - 报价
 * @author huangbf3
 */
@RestController
@RequestMapping("/npm/vendor/inq/order")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebInqSouOrderForVendorController {

    @Autowired
    private ExtInqSouOrderQueryService extInqSouOrderQueryService;
    @Autowired
    private ExtInqSouOrderEventService extInqSouOrderEventService;

    @ApiOperation("获取上一轮报价")
    @GetMapping("/getLastOrderItems")
    public List<ApiInqSouOrderItemVO> getLastOrderItems(Long projectId) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");

        return extInqSouOrderQueryService.getLastOrderItems(projectId, AppUserUtil.getLoginAppUser().getCompanyId());
    }

    @ApiOperation("查询指定轮次报价信息")
    @GetMapping("/getSouOrderInfo")
    public ApiInqSouOrderDetailVO getSouOrderInfo(Long projectId, Integer round) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");

        return extInqSouOrderQueryService.getSouOrderInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), round);
    }

    @ApiOperation("供应商历史报价列表查询")
    @PostMapping("/listVendorOrderHis")
    public PageInfo<ExtInqOrderItemHisQueryVO> listVendorOrderHis(@RequestBody ExtInqOrderItemHisQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsVendor();
        queryParam.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        queryParam.setForBuyer(false);

        return new PageInfo<>(extInqSouOrderQueryService.listVendorOrderHis(queryParam));
    }

    @ApiOperation("下载报价导入模板")
    @GetMapping("/getLastOrderItems/download")
    public void getSouOrderInfoForDownload(Long projectId, Integer round, HttpServletResponse response) {
        SouUserTypeCheckUtils.checkIsVendor();
        AssertUtils.notNull(projectId, "缺少projectId参数");
        // 1: 查询报价信息
        ApiInqSouOrderDetailVO vo = extInqSouOrderQueryService.getSouOrderInfo(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), round);
        // 2: 数据转换
        List<ExtInqSouOrderItemImportDTO> dataList = new ArrayList<>(vo.getItemList().size()); {
            int index = 0;
            for (ApiInqSouOrderItemVO item : vo.getItemList()) {
                index++;
                ExtInqSouOrderItemImportDTO data = new ExtInqSouOrderItemImportDTO();
                dataList.add(data);

                data.setSouItemId(String.valueOf(item.getSouItemId()));
                data.setIndex(String.valueOf(index));
                data.setRound(String.valueOf(item.getRound()));
                data.setOrgOuName(item.getOrgOuName());
                data.setExtAreaName(item.getExtAreaName());
                data.setNoCodeItem(item.getNoCodeItem().name());
                data.setItemCode(item.getItemCode());
                data.setItemDesc(item.getItemDesc());
                data.setCategoryName(item.getCategoryName());
                data.setExtMaterialModel(item.getExtMaterialModel());
                data.setRequireQuantity(item.getRequireQuantity() != null ? item.getRequireQuantity().stripTrailingZeros().toPlainString() : null);
                data.setUnit(item.getUnit());
                data.setExtBrand(item.getExtBrand());
                data.setTaxKey(item.getTaxKey());
                data.setInvoiceType(item.getInvoiceType());
                data.setOrderNotaxPrice(item.getOrderNotaxPrice() != null ? item.getOrderNotaxPrice().stripTrailingZeros().toPlainString() : null);
                data.setOrderTaxPrice(item.getOrderTaxPrice() != null ? item.getOrderTaxPrice().stripTrailingZeros().toPlainString() : null);
                data.setPriceTaxTotal(item.getPriceTaxTotal() != null ? item.getPriceTaxTotal().stripTrailingZeros().toPlainString() : null);
                data.setAdvancePaymentRemark(item.getAdvancePaymentRemark() != null ? item.getAdvancePaymentRemark().name() : Enable.N.name());
                data.setIsLadder(item.getIsLadder() != null ? item.getIsLadder().name() : Enable.N.name());
                data.setExtLeadTime(item.getExtLeadTime() != null ? String.valueOf(item.getExtLeadTime()) : null);
                data.setExtWarrantyPeriod(item.getExtWarrantyPeriod() != null ? String.valueOf(item.getExtWarrantyPeriod()) : null);
                data.setOrderRemark(item.getOrderRemark());
                data.setRequirementRemark(item.getRemark());
            }
        }
        // 3: 导出文件
        String fileName = LocaleHandler.getLocaleMsg("询比价报价明细") + ".xlsx";
        ExcelWriter excelWriter = null;
        try {
            ServletOutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, fileName);
            excelWriter = EasyExcel.write(outputStream).build();
            WriteSheet mainSheet = EasyExcel
                    .writerSheet(0, "sheet1")
                    .head(ExtInqSouOrderItemImportDTO.class)
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

        return extInqSouOrderEventService.importOrderItems(projectId, null, AppUserUtil.getLoginAppUser().getCompanyId(), false, file, fileupload);
    }

}
