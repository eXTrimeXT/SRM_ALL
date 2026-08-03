package com.midea.cloud.srm.biz.pj.utils;

import com.aspose.words.*;
import com.midea.cloud.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
public class XwpfdUtils {

    private Boolean license = false;

    private static XwpfdUtils xwpfdUtils;

    private XwpfdUtils() {

    }

    public static XwpfdUtils instance() {
        if(Objects.isNull(xwpfdUtils)) {
            xwpfdUtils = new XwpfdUtils();
        }
        return xwpfdUtils;
    }

    public Boolean getLicense() {
        if(!license) {
            license = license();
        }
        return license;
    }


    public MockMultipartFile covertFile(String fileName,InputStream inputStream, Long sourceFileupload) {
        try {
            //获取证书
            this.getLicense();
            Document doc = new Document(inputStream);
            //转换PDF
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setJpegQuality(50);
            pdfSaveOptions.setImageCompression(PdfImageCompression.JPEG);
            pdfSaveOptions.createSaveOptions(SaveFormat.PDF);

            doc.save(pdfOutputStream, pdfSaveOptions);
            return new MockMultipartFile(fileName, fileName, "application/pdf", pdfOutputStream.toByteArray());
        } catch (Exception e) {
            log.error("源文件 {},docx转换pdf异常：", sourceFileupload,e);
            throw new BaseException("docx转换pdf异常: " + e.getMessage());
        }
    }



    public static void main(String[] args) throws Exception {
        //TODO 转换失败word
        String fPath = "D:\\Downloads\\技术文件-长城汽车气动声学风洞试验室项目装修及公用安装工程投标文件（技术文件）-盛鸿.docx";
        String fPath2 = "D:\\Downloads\\技术方案文件-srm-null.pdf";
        XwpfdUtils.instance().getLicense();
        Document doc1 = new Document(new FileInputStream(fPath));

        PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
        pdfSaveOptions.setJpegQuality(50);
        pdfSaveOptions.setImageCompression(PdfImageCompression.JPEG);
        pdfSaveOptions.createSaveOptions(SaveFormat.PDF);
        //转换PDF
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        doc1.save(fPath2, pdfSaveOptions);

    }


    private boolean license() {
        boolean result = false;
        try {
            // license.xml应放在资源路径下
            InputStream is = this.getClass().getResourceAsStream("/xml/license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
