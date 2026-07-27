package com.midea.cloud.srm.sou.meiql.bidnotices.util;

import com.midea.cloud.common.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dromara.pdf.pdfbox.core.base.Document;
import org.dromara.pdf.pdfbox.core.base.Page;
import org.dromara.pdf.pdfbox.core.component.Image;
import org.dromara.pdf.pdfbox.core.component.Textarea;
import org.dromara.pdf.pdfbox.core.enums.FontStyle;
import org.dromara.pdf.pdfbox.handler.PdfHandler;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;

/**
 * <pre>
 *  x-easypdf工具类
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/7/15 17:23
 *  修改内容:
 * </pre>
 */
@SuppressWarnings("ALL")
@Slf4j
public class XEasypdfUtil {
    /**
     *
     * @param page Page
     * @param text 追加的文本
     * @param fontName 字体名称
     * @param fontColor 字体颜色
     * @param fontStyle 字体风格
     * @param fontSize 字体大小
     * @param x x坐标
     * @param y y坐标
     */
    public static void writeText(Page page, String text,String fontName,Color fontColor,FontStyle fontStyle,float fontSize,Float x,Float y)  {
        // 创建文本域
        Textarea textarea = new Textarea(page);
        // 设置X轴起始坐标
        textarea.setBeginX(x);
        // 设置Y轴起始坐标
        textarea.setBeginY(y);
        textarea.setFontName(fontName);
        textarea.setFontColor(fontColor);
        textarea.setFontStyle(fontStyle);
        textarea.setFontSize(fontSize);
        textarea.setText(text);
        // 渲染文本
        textarea.render();
    }

    /**
     *
     * @param page Page对象
     * @param inputStream 图片流
     * @param x x坐标
     * @param y y坐标
     * @param w 宽
     * @param h 高
     */
    public static void writeImage(Page page, InputStream inputStream, float x, float y, int w, int h) {
        Image image=new Image(page);
        image.setImage(inputStream);
        image.setBeginX(x);
        image.setBeginY(y);
        image.setWidth(w);
        image.setHeight(h);
        //渲染
        image.render();
    }

    /**
     *
     * @param page Page对象
     * @param inputStream 图片流
     * @param x x相对坐标
     * @param y y相对坐标
     * @param w 宽
     * @param h 高
     */
    public static void writeImageByRelative(Page page, InputStream inputStream, float x, float y, int w, int h) {
        Image image=new Image(page);
        image.setImage(inputStream);
        image.setRelativeBeginX(x);
        image.setRelativeBeginY(y);
        image.setWidth(w);
        image.setHeight(h);
        //渲染
        image.render();
    }


    public static MockMultipartFile getPageFile(Integer pageNum, String fileName, InputStream inputStream, HttpServletResponse response) throws Exception {
        Document document = PdfHandler.getDocumentHandler().load(inputStream);
        AssertUtils.isTrue(pageNum>0,"页码有误");
        AssertUtils.isTrue(document.getTotalPageNumber() >= pageNum,"页码有误");

        Page documentPage = document.getPage(pageNum-1);

        Document newDocument = PdfHandler.getDocumentHandler().create();
        newDocument.appendPage(documentPage);

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        newDocument.save(pdfOutputStream);

        if(response !=null){
            response.setContentType("application/pdf;charset=UTF-8");
            response.setHeader("total", document.getTotalPageNumber()+ "");
            response.setHeader("width", documentPage.getWidth()+ "");
            response.setHeader("height", documentPage.getHeight() + "");
            response.setHeader("deleteStatus", "0");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName);
            IOUtils.write(pdfOutputStream.toByteArray(),response.getOutputStream());
        }


        return new MockMultipartFile(fileName, fileName, "application/pdf", pdfOutputStream.toByteArray());
    }


    public static void main(String[] args) throws Exception {
        String filePath = "D:\\work\\srm\\开发\\02684104965BB4C8D6-59AB-4468-8E8C-807A32109921.pdf";
        String filePath2 = "D:\\work\\srm\\开发\\vv.pdf";

        Document document = PdfHandler.getDocumentHandler().load(filePath);
        Page documentPage = document.getPage(3);
        Document newDocument = PdfHandler.getDocumentHandler().create();
        newDocument.appendPage(documentPage);
        newDocument.save(filePath2);

    }
}
