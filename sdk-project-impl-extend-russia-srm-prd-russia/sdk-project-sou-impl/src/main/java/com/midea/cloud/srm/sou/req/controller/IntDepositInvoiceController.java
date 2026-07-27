package com.midea.cloud.srm.sou.req.controller;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.sou.req.service.SouIntDepositInvoiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/2 14:16
 *  修改内容:
 * </pre>
 */
@Api(value = "IntDepositInvoiceController", tags = {"意向金开票"})
@RestController
@Slf4j
@RequestMapping("/intDepositInvoice")
public class IntDepositInvoiceController {
    @Autowired
    SouIntDepositInvoiceService souIntDepositInvoiceService;
    @Autowired
    QlService qlService;

    @ApiOperation(value = "下载代付凭证模板", notes = "下载代付凭证模板")
    @RequestMapping("/importModelDownload")
    public void importModelDownload(HttpServletResponse response) throws Exception {
        try (
                InputStream inputStream = this.getClass().getResourceAsStream("/template/股份开票信息模板.xlsx");
        ) {
            assert inputStream != null;
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                 OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "股份开票信息模板");
            ) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }
    @ApiOperation(value = "发票开具创建", notes = "发票开具创建")
    @RequestMapping("/createInvoice")
    public void createInvoice(@RequestBody SouIntDepositInvoice invoiceParam) {
        SouIntDepositInvoice invoice = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_DEPOSIT_INVOICE_BUYER)
                .eq(SouIntDepositInvoice::getReqHeadId,invoiceParam.getReqHeadId())
                .eq(SouIntDepositInvoice::getInvoiceType,invoiceParam.getInvoiceType())
                ,SouIntDepositInvoice.class).get(0);
        JSONObject result = souIntDepositInvoiceService.createInvoice(invoice);
        log.info("result:{}",result.toJSONString());
//        if(StringUtils.equals("200",result.getString("code"))&&result.getJSONObject("data")!=null
//                &&result.getJSONObject("data").containsKey("settleDocumentCode")){
//            souIntDepositInvoiceService.update(Wrappers.lambdaUpdate(SouIntDepositInvoice.class)
//                    .set(SouIntDepositInvoice::getSettleDocumentCode,result.getJSONObject("data").containsKey("settleDocumentCode"))
//                    .eq(SouIntDepositInvoice::getInvoiceId,invoice.getInvoiceId()));
//        }
    }

}
