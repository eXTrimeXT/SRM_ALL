package com.midea.cloud.srm.sou.meiql.bidnotices.util;

import com.midea.cloud.common.exception.BaseException;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.*;
import java.io.*;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
public class SpirePdfUtil {

    public static MockMultipartFile getFile (String extProjectNo,String vendorName,String souName,String fileName,InputStream inputStream) {
        byte [] bytes = getPdfBytes( extProjectNo, vendorName, souName,inputStream);
        return new MockMultipartFile(fileName, fileName, "application/pdf", bytes);
    }


    public static MockMultipartFile getFile (String fileName,byte [] bytes) {
        return new MockMultipartFile(fileName, fileName, "application/pdf", bytes);
    }


    public static byte [] getPdfBytes(String extProjectNo,String vendorName,String souName,InputStream inputStream) {
        PdfDocument doc = new PdfDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte [] data = null;
        try {
            log.info("字符流:"+inputStream.toString());
            // 创建 PdfDocument 类的对象
            doc.loadFromStream(inputStream);
            // 载入PDF文档
            // 创建 PdfTrueTypeFont 类的对象以设置字体
            PdfTrueTypeFont font = new PdfTrueTypeFont(new Font("微软雅黑", Font.BOLD, 14));
            writeText(doc.getPages().get(0),vendorName,font,PdfBrushes.getRed(),-16F,163F,100F,14F);
            font = new PdfTrueTypeFont(new Font("微软雅黑", Font.ITALIC, 14));
            writeText(doc.getPages().get(0),extProjectNo,font,PdfBrushes.getRed(),51F,196F,100F,14F);
            font = new PdfTrueTypeFont(new Font("微软雅黑", Font.ITALIC, 14));
            writeText(doc.getPages().get(0),souName,font,PdfBrushes.getRed(),51F,229F,100F,14F);
            doc.saveToStream(outputStream);
            data = outputStream.toByteArray();
        } catch (Exception e) {
            log.error("PDF写入异常",e);
            throw new BaseException("PDF写入异常"+e.getMessage());
        } finally {
            doc.close();
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return data;
    }

    public static void writeText(PdfPageBase page, String text, PdfTrueTypeFont font, PdfBrush pdfBrush, float x, float y,float w,float h)  {
        // 保存画布状态
        PdfGraphicsState state = page.getCanvas().save();
        page.getCanvas().translateTransform(x, y);
        page.getCanvas().drawString(text, font, pdfBrush, w, h);
        // 再次保存画布状态
        page.getCanvas().restore(state);
    }

    public static void writeImage(PdfPageBase page, PdfImage image , float x, float y, float w, float h) {
        // 保存画布状态
        PdfGraphicsState state = page.getCanvas().save();
        page.getCanvas().drawImage(image, x, y, w, h);
        // 再次保存画布状态
        page.getCanvas().restore(state);
    }
    
}