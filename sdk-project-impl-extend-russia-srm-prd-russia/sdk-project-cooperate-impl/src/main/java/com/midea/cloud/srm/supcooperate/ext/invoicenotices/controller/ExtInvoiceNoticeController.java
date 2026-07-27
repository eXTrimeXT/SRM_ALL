package com.midea.cloud.srm.supcooperate.ext.invoicenotices.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.designplans.dto.AgreementExcelDto;
import com.midea.cloud.srm.model.sou.fixprice.vo.ExtFixPriceLineExportVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.excel.InvoiceNoticeDetailExcel;
import com.midea.cloud.srm.model.supcooperate.excel.InvoiceNoticeDetailVo;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.service.ExtInvoiceNoticeService;
import com.mideacloud.common.util.BeanUtil;
import freemarker.template.SimpleDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对账单导出
 * @author srm
 * @Description: for srm
 * @date 2024/6/6
 */
@RequestMapping("/InvoiceNotice")
@Controller
public class ExtInvoiceNoticeController {
    @Autowired
    ExtInvoiceNoticeService extInvoiceNoticeService;

    /**
     * 对账明细导出Excel
     * @param response
     * @param Id
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping("/getInvoiceNoticeUpload")
    public void upload(HttpServletResponse response,@RequestParam("invoiceNoticeId") Long Id) throws IOException, ParseException {
        List<InvoiceNoticeDetailVo> list= extInvoiceNoticeService.GetById(Id);
        InvoiceNoticeDetailExcel invoiceNoticeDetailExcel=new InvoiceNoticeDetailExcel();
        List<InvoiceNoticeDetailExcel> ExcelList =new ArrayList<>();
        //数据库查询对账信息到list集合中,然后遍历list copy到ExcelList集合中
        for(int i=0;i<list.size();i++) {
            invoiceNoticeDetailExcel = BeanUtil.copyProperties(list.get(i), InvoiceNoticeDetailExcel.class);
            //将入库类型转换为中文
            if("RETURN".equals(invoiceNoticeDetailExcel.getType())){
                invoiceNoticeDetailExcel.setType("退库");
            }
            else{
                invoiceNoticeDetailExcel.setType("入库");
            }
            //截取日期年月日
            SimpleDateFormat simpleDate=new SimpleDateFormat("yyyy-MM-dd");
            String newDate=simpleDate.format(list.get(i).getReceiveDate());
            invoiceNoticeDetailExcel.setReceiveNewDate(newDate);
            ExcelList.add(invoiceNoticeDetailExcel);
        }
        this.setExcelResponseProp(response, "入库或入库冲销明细");
        EasyExcel.write(response.getOutputStream())
                .head(InvoiceNoticeDetailExcel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("入库或入库冲销明细")
                .doWrite(SouObjectXUtil.convertList(ExcelList,InvoiceNoticeDetailExcel.class));
    }

    private void setExcelResponseProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(rawFileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
    }
}
