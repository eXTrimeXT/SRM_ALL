package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.select;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.order.MqlInqSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.select.MqlInqSouOrderItemWebVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/14
 */
@Data
public class MqlInqSouPriceComparePdfWebVO {

    /** @see SouOrderItem#getItemCode */
    @ApiModelProperty("物料编码")
    private String itemCode;
    /** @see SouOrderItem#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;
    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商ID")
    private String vendorName;
    /** @see InqSouOrderItem#getFormulaId */
    @ApiModelProperty("公式ID")
    private Long formulaId;
    /** @see SouOrderItem#getStandardNotaxPrice */
    private BigDecimal notaxPrice;
    @ApiModelProperty("公式属性集合")
    private List<EssentialFactor> factorList;
/**    factorId */
    @ApiModelProperty("公式报价元素值")
    private Map<Long, Object> userAttrMap;

    /**
     * 转换方法
     */
    public static List<MqlInqSouPriceComparePdfWebVO> convert(List<MqlInqSouOrderItemVO> orderItemList, List<SouVendor> vendorList,
                                                              Map<Long/* formulaId */, List<EssentialFactor>> factorMap) {
        if (orderItemList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* vendorId */, String/* vendorName */> vendorMap = vendorList.stream()
                .collect(Collectors.toMap(SouVendor::getVendorId, SouVendor::getVendorName));

        List<MqlInqSouPriceComparePdfWebVO> voList = new ArrayList<>(orderItemList.size());
        MqlInqSouPriceComparePdfWebVO vo;
        for (MqlInqSouOrderItemVO orderItem : orderItemList) {
            vo = new MqlInqSouPriceComparePdfWebVO();
            BeanUtils.copyProperties(orderItem, vo);
            vo.notaxPrice = orderItem.getStandardNotaxPrice();
            vo.vendorName = vendorMap.get(orderItem.getVendorId());

            vo.factorList = factorMap.get(orderItem.getFormulaId());
            if (vo.factorList == null) {
                vo.factorList = Collections.emptyList();
            }
            vo.userAttrMap = new HashMap<>(16);
            if (StringUtils.isNotBlank(orderItem.getInqSouOrderItem().getFormulaAttrValues())) {
                Map<Long/* factorId */, String/* price */> map = MqlInqSouOrderItemWebVO.deserializationUserPrices(orderItem.getInqSouOrderItem().getFormulaAttrValues());
                for (Map.Entry<Long, String> entry : map.entrySet()) {
                    vo.userAttrMap.put(entry.getKey(), entry.getValue());
                }
            }

            voList.add(vo);
        }
        return voList;
    }

    /**
     * 导出Excel
     */
    public static void exportPdf(HttpServletResponse response, String fileName, List<MqlInqSouPriceComparePdfWebVO> voList) {
        try {
            Document document = new Document(PageSize.A4.rotate(),40,40,50,0);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            if (!voList.isEmpty()) {
                buildPdf(document, voList);
            }
            response.setContentType("application/pdf;charset=UTF-8");
            String pdfText = ".pdf";
            if (! fileName.endsWith(pdfText)) {
                fileName = fileName.concat(pdfText);
            }
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("gb2312"), StandardCharsets.ISO_8859_1));
            document.close();
            writer.close();
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static void buildPdf(Document document, List<MqlInqSouPriceComparePdfWebVO> voList) throws DocumentException, IOException {
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontCn10 = new Font(bfChinese, 10, Font.NORMAL);
        Font fontCn15 = new Font(bfChinese, 15, Font.NORMAL);
        Font fontCn20 = new Font(bfChinese, 20, Font.NORMAL);

        // 总列数(4 + 公式元素数量)
        int totalColumns = 4 + voList.get(0).getFactorList().size();
        // 标题信息
        buildTitle(document, "比价表", fontCn20);
        // 增加空行
        addBlankLine(document);
        // 增加表信息
        Map<String/* itemCode或者itemName(无编码物料) */, List<MqlInqSouPriceComparePdfWebVO>> voMap = new HashMap<>(16);
        voList.forEach(vo -> {
            boolean isVirtual = vo.getItemCode().contains("VIRTUAL_INQ_");
            voMap
                    .computeIfAbsent(isVirtual ? vo.getItemDesc() : vo.getItemCode(), k -> new ArrayList<>(10))
                    .add(vo);
        });
        for (Map.Entry<String, List<MqlInqSouPriceComparePdfWebVO>> entry : voMap.entrySet()) {
            buildTable(document, fontCn10, totalColumns, entry.getKey(), entry.getValue());
            addBlankLine(document);
        }
        // 增加末尾：审核签字
        addBlankLine(document);
        buildEnd(document, "审核签字：", fontCn15);
        document.newPage();
    }

    /** 构建表格信息 */
    private static void buildTable(Document document, Font font, int totalColumns, String itemCode,
                                   List<MqlInqSouPriceComparePdfWebVO> voList) throws DocumentException {
        // 1. 构建表格对象
        PdfPTable pdfTable = new PdfPTable(totalColumns);
        // 2. 构造表格
        // 行：物料编码(单行，合并单元格)
        PdfPCell cell = new PdfPCell(new Paragraph("物料编码：" + itemCode, font));
        cell.setColspan(totalColumns);
        pdfTable.addCell(cell);
        // 列：物料描述
        cell = new PdfPCell(new Paragraph("物料描述", font));
        pdfTable.addCell(cell);
        // 列：供应商名称
        cell = new PdfPCell(new Paragraph("供应商名称", font));
        pdfTable.addCell(cell);
        // 列：公式元素名称
        for (EssentialFactor factor : voList.get(0).factorList) {
            cell = new PdfPCell(new Paragraph(factor.getEssentialFactorName(), font));
            pdfTable.addCell(cell);
        }
        // 列：合计
        cell = new PdfPCell(new Paragraph("合计", font));
        pdfTable.addCell(cell);
        // 列：是否中标
        cell = new PdfPCell(new Paragraph("是否中标", font));
        pdfTable.addCell(cell);
        // 3. 添加实际数据
        int index = -1;
        for (MqlInqSouPriceComparePdfWebVO vo : voList) {
            index++;
            // 列：物料描述(合并行)
            if (index == 0) {
                cell = new PdfPCell(new Paragraph(vo.itemDesc, font));
                cell.setRowspan(voList.size());
                pdfTable.addCell(cell);
            }
            // 列：供应商名称
            cell = new PdfPCell(new Paragraph(vo.vendorName, font));
            pdfTable.addCell(cell);
            // 列：公式元素值
            for (EssentialFactor factor : vo.factorList) {
                Object value = vo.userAttrMap.get(factor.getEssentialFactorId());
                cell = new PdfPCell(new Paragraph(value != null ? value.toString() : " ", font));
                pdfTable.addCell(cell);
            }
            // 列：合计
            cell = new PdfPCell(new Paragraph(vo.notaxPrice.toString(), font));
            pdfTable.addCell(cell);
            // 列：是否中标(略)
            cell = new PdfPCell(new Paragraph(" ", font));
            pdfTable.addCell(cell);
        }

        document.add(pdfTable);
    }

    /**
     * 构建pdf标题信息
     */
    public static void buildTitle(Document document, String titleName, Font font) throws DocumentException {
        Paragraph title = new Paragraph(titleName, font);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    /**
     * 构建pdf结尾信息
     */
    public static void buildEnd(Document document, String name, Font font) throws DocumentException {
        Paragraph title = new Paragraph(name, font);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    /** 增加空行 */
    public static void addBlankLine(Document document) throws DocumentException {
        document.add(new Paragraph("   "));
    }

}
