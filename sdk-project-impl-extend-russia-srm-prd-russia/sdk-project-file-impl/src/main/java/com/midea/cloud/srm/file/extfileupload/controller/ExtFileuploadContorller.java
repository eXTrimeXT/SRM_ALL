package com.midea.cloud.srm.file.extfileupload.controller;

import com.midea.cloud.srm.file.extfileupload.service.ExtFileuploadService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/15
 */
@RestController
@Slf4j
@RequestMapping("/extfileupload")
public class ExtFileuploadContorller extends BaseController {
    @Autowired
    private ExtFileuploadService extFileuploadService;

    @GetMapping("/queryById")
    Fileupload queryById(@RequestParam("fileuploadId") Long fileuploadId) {
        return extFileuploadService.queryById(fileuploadId);
    }
}
