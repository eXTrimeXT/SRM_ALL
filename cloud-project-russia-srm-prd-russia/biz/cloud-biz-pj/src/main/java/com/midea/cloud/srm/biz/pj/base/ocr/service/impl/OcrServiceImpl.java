package com.midea.cloud.srm.biz.pj.base.ocr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.Byte2InputStream;
import com.midea.cloud.common.utils.redis.RSALockUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.base.ocr.service.OcrService;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.base.ocr.dto.CompanyResponseDTO;
import com.midea.cloud.srm.model.pj.base.ocr.dto.IdCardBackDTO;
import com.midea.cloud.srm.model.pj.base.ocr.dto.PersonalInfoDTO;
import com.mideacloud.common.enums.YesOrNo;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Base64;


/**
 * @author huangbf3
 */
@Slf4j
@Service
public class OcrServiceImpl implements OcrService {

    @Autowired
    private FileCenterClient fileCenterClient;
    @Autowired
    private RSALockUtil rsaLockUtil;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @Value("${gwm.prdAppkey:N}")
    private String prdAppkey;

    @Value("${gwm.prdSecret:N}")
    private String prdSecret;
    /**
     * 营业执照OCR识别URL
     */
    @Value("${gwm.recognizeLicenceOcrUrl:https://gwapi.gwm.cn/rest/public/pubqc/api/tripartite/ocr/ticket/BizLicenseOCR}")
    private String recognizeLicenceOcrUrl;
    /**
     * 身份证正面OCR识别URL
     */
    @Value("${gwm.recognizeIDCardFrontOcrUrl:https://gwapi.gwm.cn/rest/public/pubqc/api/tripartite/ocr/ticket/idCard}")
    private String recognizeIdCardFrontOcrUrl;
    /**
     * 身份证反面OCR识别URL
     */
    @Value("${gwm.recognizeIDCardBackOcrUrl:https://gwapi.gwm.cn/rest/public/pubqc/api/tripartite/ocr/ticket/idcardne}")
    private String recognizeIdCardBackOcrUrl;

    @Value("${gwm.preappkey}")
    private String preappKey;
    @Value("${gwm.presecret}")
    private String presecret;
    @Resource
    private IInterfaceLogService interfaceLogService;


    private static final String SUCCESS_VALUE = "200";

    @Override
    public CompanyResponseDTO recognizeLicence(Long fileuploadId) {
        CompanyResponseDTO companyResponseDTO=null;
        try {
            long startMills = System.currentTimeMillis();
            log.info("营业执照开始OCR识别startMills={}", startMills);
            byte[] bytes=this.getFile(fileuploadId);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("image_base64", Base64.getEncoder().encodeToString(bytes));
            OpenClient openClient = new OpenClient(this.getPropertyAppkey(),getPropertySecret());
            log.info("营业执照OCR识别调用入参:{}",jsonObject.toString());

            ApiInfoEnum apiInfoEnum = ApiInfoEnum.RECOGNIZE_LICENCE;
            InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,jsonObject);
            String resultJsonStr = null;
            try{
                log.info(apiInfoEnum.getServiceName()+" 参数："+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
                resultJsonStr = openClient.sendHttpPost(recognizeLicenceOcrUrl,jsonObject.toString(),"application/json");
            }catch (Exception e){
                log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
                interfaceLog.setStatus(ResultStatus.FAIL.toString());
                interfaceLog.setErrorInfo(e.getMessage());
            }finally {
                interfaceLog.setReturnInfo(resultJsonStr);
                interfaceLogService.createInterfaceLog(interfaceLog);
            }


            if(StringUtils.isBlank(resultJsonStr)){
                throw new BaseException(LocaleHandler.getLocaleMsg("营业执照ocr识别无信息返回"));
            }
            log.info("营业执照OCR识别调用成功，返回信息{}",resultJsonStr);
            JSONObject resultJsonObject=JSONObject.parseObject(resultJsonStr);
            JSONObject result=resultJsonObject.getJSONObject("result");
            String code=resultJsonObject.getString("code");
            if(!SUCCESS_VALUE.equals(code)){
                throw new BaseException(LocaleHandler.getLocaleMsg("营业执照ocr识别失败"));
            }
            companyResponseDTO = JSONObject.parseObject(result.toJSONString(),CompanyResponseDTO.class);
            CompanyResponseDTO.convertbusinessDate(companyResponseDTO);
            long endMills = System.currentTimeMillis();
            long costMills = endMills - startMills;
            log.info("营业执照结束OCR识别endMills={} 耗时={}ms", endMills, costMills);
        } catch (Exception e) {
            log.error("营业执照OCR识别异常", e);
            throw new BaseException(e.getMessage());
        }
        return companyResponseDTO;
    }




    @Override
    public PersonalInfoDTO recognizeIdCardFront(Long fileuploadId) {
        PersonalInfoDTO personalInfoDTO=null;
        try {
            long startMills = System.currentTimeMillis();
            log.info("身份证正面开始OCR识别startMills={}", startMills);
            byte[] bytes=this.getFile(fileuploadId);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("image_base64", Base64.getEncoder().encodeToString(bytes));
            log.info("身份证正面OCR识别调用入参:{}",jsonObject.toString());
            OpenClient openClient = new OpenClient(this.getPropertyAppkey(),getPropertySecret());
            ApiInfoEnum apiInfoEnum = ApiInfoEnum.RECOGNIZE_ID_CARD_FRONT;
            InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,jsonObject);
            String resultJsonStr = null;
            try{
                resultJsonStr = openClient.sendHttpPost(recognizeIdCardFrontOcrUrl,jsonObject.toString(),"application/json");
            }catch (Exception e){
                log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
                interfaceLog.setStatus(ResultStatus.FAIL.toString());
                interfaceLog.setErrorInfo(e.getMessage());
            }finally {
                interfaceLog.setReturnInfo(resultJsonStr);
                interfaceLogService.createInterfaceLog(interfaceLog);
            }
            if(StringUtils.isBlank(resultJsonStr)){
                throw new BaseException(LocaleHandler.getLocaleMsg("身份证正面ocr识别无信息返回"));
            }
            log.info("身份证正面OCR识别调用成功，返回信息{}",resultJsonStr);
            JSONObject resultJsonObject=JSONObject.parseObject(resultJsonStr);
            JSONObject result=resultJsonObject.getJSONObject("result");
            String code=resultJsonObject.getString("code");
            if(!SUCCESS_VALUE.equals(code)){
                throw new BaseException(LocaleHandler.getLocaleMsg("身份证正面ocr识别失败"));
            }
            personalInfoDTO=JSONObject.parseObject(result.toJSONString(),PersonalInfoDTO.class);
            long endMills = System.currentTimeMillis();
            long costMills = endMills - startMills;
            log.info("身份证正面结束OCR识别endMills={} 耗时={}ms", endMills, costMills);
        } catch (Exception e) {
            log.error("身份证正面OCR识别异常", e);
            throw new BaseException(e.getMessage());
        }
        return personalInfoDTO;
    }

    @Override
    public IdCardBackDTO recognizeIdCardBack(Long fileuploadId) {
        IdCardBackDTO idCardBackDTO=null;
        try {

            long startMills = System.currentTimeMillis();
            log.info("身份证反面开始OCR识别startMills={}", startMills);
            byte[] bytes=this.getFile(fileuploadId);
            JSONObject paramJson=new JSONObject();
            paramJson.put("image_base64", Base64.getEncoder().encodeToString(bytes));
            log.info("身份证反面OCR识别调用入参:{}",paramJson.toString());
            OpenClient openClient = new OpenClient(this.getPropertyAppkey(),getPropertySecret());
            ApiInfoEnum apiInfoEnum = ApiInfoEnum.RECOGNIZE_ID_CARD_BACK;
            InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,paramJson);
            String resultJsonStr = null;
            try{
                resultJsonStr = openClient.sendHttpPost(recognizeIdCardBackOcrUrl,paramJson.toString(),"application/json");
            }catch (Exception e){
                log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
                interfaceLog.setStatus(ResultStatus.FAIL.toString());
                interfaceLog.setErrorInfo(e.getMessage());
            }finally {
                interfaceLog.setReturnInfo(resultJsonStr);
                interfaceLogService.createInterfaceLog(interfaceLog);
            }
            if(StringUtils.isBlank(resultJsonStr)){
                throw new BaseException(LocaleHandler.getLocaleMsg("身份证反面ocr识别无信息返回"));
            }
            log.info("身份证反面OCR识别调用成功，返回信息{}",resultJsonStr);
            JSONObject resultJsonObject=JSONObject.parseObject(resultJsonStr);
            JSONObject result=resultJsonObject.getJSONObject("result");
            String code=resultJsonObject.getString("code");
            if(!SUCCESS_VALUE.equals(code)){
                throw new BaseException(LocaleHandler.getLocaleMsg("身份证反面ocr识别失败"));
            }
            idCardBackDTO=JSONObject.toJavaObject(result,IdCardBackDTO.class);
            IdCardBackDTO.convertvalidDateToBusinessDate(idCardBackDTO);
            long endMills = System.currentTimeMillis();
            long costMills = endMills - startMills;
            log.info("身份证反面结束OCR识别endMills={} 耗时={}ms", endMills, costMills);
        } catch (Exception e) {
            log.error("身份证正面OCR识别异常", e);
            throw new BaseException(e.getMessage());
        }
        return idCardBackDTO;
    }

    private byte[] getFile(Long fileuploadId) throws Exception {
        Fileupload fileupload = new Fileupload();
        String fileKey = rsaLockUtil.encrypt(fileuploadId.toString());
        fileupload.setFileKey(fileKey);
        Response download = fileCenterClient.downloadFileByParam(fileupload);
        Assert.notNull(download.body(), "附件下载失败");
        InputStream inputStream = download.body().asInputStream();
        byte[] bytes = Byte2InputStream.inputStream2byte(inputStream);
        return bytes;
    }

//    private byte[] localTest(){
//        String filePath = "D:\\copy\\营业执照.jpeg"; // 替换为你的本地文件路径
//        //String filePath = "D:\\copy\\身份证\\正面.jpg";
//        //String filePath = "D:\\copy\\身份证\\反面.jpg";
//
//        try {
//            FileInputStream fileInputStream = new FileInputStream(filePath);
//            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//
//            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                byteOutputStream.write(buffer, 0, bytesRead);
//            }
//
//            byte[] fileBytes = byteOutputStream.toByteArray();
//
//            // 现在，fileBytes 包含了本地文件的字节流数据
//
//            fileInputStream.close();
//            byteOutputStream.close();
//            return fileBytes;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public String transferFileExtend2OcrFileType(String fileExtend) {
        String pdfText = "pdf";
        String ocrFileType = "image";
        if (pdfText.equalsIgnoreCase(fileExtend)) {
            ocrFileType = "pdf";
        }
        return ocrFileType;
    }


    public String getPropertyAppkey() {
        return YesOrNo.NO.getName().equals(prdAppkey) ? appKey : prdAppkey;
    }

    public String getPropertySecret() {
        return YesOrNo.NO.getName().equals(prdSecret) ? secret : prdSecret;
    }
}
