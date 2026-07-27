package com.midea.cloud.srm.biz.pj.base.ocr.controller;

import com.midea.cloud.srm.biz.pj.base.ocr.service.OcrService;
import com.midea.cloud.srm.model.pj.base.ocr.dto.CompanyResponseDTO;
import com.midea.cloud.srm.model.pj.base.ocr.dto.IdCardBackDTO;
import com.midea.cloud.srm.model.pj.base.ocr.dto.PersonalInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 */
@RestController
@RequestMapping("/ocr")
@Api(value = "PjOcrController", tags = "OCR识别")
public class PjOcrController {

    @Autowired
    private OcrService ocrService;

    @GetMapping("/recognizeLcImage")
    @ApiOperation(value = "营业执照Ocr识别", notes = "营业执照Ocr识别")
    public CompanyResponseDTO recognizeLcImage(@RequestParam("fileuploadId") Long fileuploadId) {
        Assert.notNull(fileuploadId, "fileuploadId不能为空");
        return ocrService.recognizeLicence(fileuploadId);
    }

    @GetMapping("/recognizeIDCardFront")
    @ApiOperation(value = "身份证正面Ocr识别", notes = "身份证正面Ocr识别")
    public PersonalInfoDTO recognizeIdCardFront(@RequestParam("fileuploadId") Long fileuploadId) {
        Assert.notNull(fileuploadId, "fileuploadId不能为空");
        return ocrService.recognizeIdCardFront(fileuploadId);
    }

    @GetMapping("/recognizeIDCardBack")
    @ApiOperation(value = "身份证反面Ocr识别", notes = "身份证反面Ocr识别")
    public IdCardBackDTO recognizeIdCardBack(@RequestParam("fileuploadId") Long fileuploadId) {
        Assert.notNull(fileuploadId, "fileuploadId不能为空");
        return ocrService.recognizeIdCardBack(fileuploadId);
    }
}
