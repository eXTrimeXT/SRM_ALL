package com.midea.cloud.srm.sou.meiql.bidnotices.util;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.MyXWPFDocument;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.midea.cloud.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import com.aspose.words.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
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

    public MockMultipartFile getFile(String fileName, String templatePath, Map<String, Object> params) {
        try {
            //获取证书
            this.getLicense();
            //获取模板
            InputStream is = this.getClass().getResourceAsStream(templatePath);
            log.info("getFileInputStream is null? answer is " + Objects.isNull(is) + " path: " + templatePath);
            XWPFDocument xwpfDocument = new MyXWPFDocument(is);
            //变量替换
            WordExportUtil.exportWord07(xwpfDocument, params);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            xwpfDocument.write(byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            Document doc = new Document(byteArrayInputStream);
            //转换PDF
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            doc.save(pdfOutputStream, SaveFormat.PDF);
            return new MockMultipartFile(fileName, fileName, "application/pdf", pdfOutputStream.toByteArray());
        } catch (Exception e) {
            log.error("docx转换pdf异常：", e);
            throw new BaseException("docx转换pdf异常: " + e.getMessage());
        }
    }

    public byte[] getWordByte(String fileName, String templatePath, Map<String, Object> params){
        byte[] array = null;
        try {
            //获取证书
            this.getLicense();
            //获取模板
            InputStream is = this.getClass().getResourceAsStream(templatePath);
            log.info("getFileInputStream is null? answer is " + Objects.isNull(is) + " path: " + templatePath);
            XWPFDocument xwpfDocument = new MyXWPFDocument(is);
            //变量替换
            WordExportUtil.exportWord07(xwpfDocument, params);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            xwpfDocument.write(byteArrayOutputStream);
            array  = byteArrayOutputStream.toByteArray();
        } catch (Exception e){
            log.error("docx 模板替换异常", e);
            throw new BaseException("docx 模板替换异常:"+e.getMessage());
        }
        return array;
    }

    public MockMultipartFile covertFile(String fileName,InputStream inputStream, Long sourceFileupload) {
        try {
            //获取证书
            this.getLicense();
//            XWPFDocument xwpfDocument = new MyXWPFDocument(inputStream);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            xwpfDocument.write(byteArrayOutputStream);
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//            Document doc = new Document(byteArrayInputStream);
            Document doc = new Document(inputStream);

            //转换PDF
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            doc.save(pdfOutputStream, SaveFormat.PDF);
            return new MockMultipartFile(fileName, fileName, "application/pdf", pdfOutputStream.toByteArray());
        } catch (Exception e) {
            log.error("源文件 {},docx转换pdf异常：", sourceFileupload,e);
            throw new BaseException("docx转换pdf异常: " + e.getMessage());
        }
    }



    public static void main(String[] args) throws Exception {
        String filePath = "D:\\srm\\sdk\\sdk-project-impl-extend-master\\sdk-project-sou-impl\\target\\classes\\template\\notWinBidnotice-template.docx";
        String filePath2 = "D:\\export\\落标通知模板-copynew.pdf";
        Map<String, Object> param = new HashMap<>(15);
        param.put("vendorName", "1213143242");
        param.put("extProjectNo", "fjlkajlfah");
        param.put("souName", "中华人民共和国广西壮族自治区广东省佛山市顺德区北滘镇街道112号兼中华人民共和国福建省福州市北部湾建设局股份有限公司就海洋生态建设投标项目安得物流中国区域大中华总部基地建设项目部物资物流项目");

        String templatePath = "\\template\\notWinBidnotice-template.docx";
        InputStream is = XwpfdUtils.class.getClassLoader().getResourceAsStream(templatePath);
        XwpfdUtils.instance().getLicense();
        XWPFDocument xwpfDocument = new MyXWPFDocument(is);
        WordExportUtil.exportWord07(xwpfDocument, param);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        xwpfDocument.write(byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        Document doc = new Document(byteArrayInputStream);
        doc.save(filePath2, SaveFormat.PDF);
        //TODO 转换失败word
        String fPath = "D:\\export\\极电光能P4激光预处理机_20241011V1.docx";
        String fPath2 = "D:\\export\\极电光能P4激光预处理机_20241011V1-srm.pdf";
        XwpfdUtils.instance().getLicense();
        Document doc1 = new Document(new FileInputStream(fPath));
        //转换PDF
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        doc1.save(fPath2, SaveFormat.PDF);

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
