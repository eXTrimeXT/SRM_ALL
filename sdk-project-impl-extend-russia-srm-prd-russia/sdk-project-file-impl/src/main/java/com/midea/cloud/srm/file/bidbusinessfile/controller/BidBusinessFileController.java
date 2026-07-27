package com.midea.cloud.srm.file.bidbusinessfile.controller;

import com.midea.cloud.srm.file.bidbusinessfile.service.BidBusinessFileService;
import com.midea.cloud.srm.model.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/8/9
 */

@RestController
@Slf4j
@RequestMapping("/bid/businessfile")
public class BidBusinessFileController extends BaseController {

    @Autowired
    private BidBusinessFileService businessFileService;

    /**
     * 批量下载招标商务附件
     * @param projectId
     * @param response
     */
    @GetMapping("/downloadBusinessFile")
    public void downloadBusinessFile(@RequestParam("projectId") Long projectId, HttpServletResponse response) {
        businessFileService.downloadBusinessFile(projectId, response);
    }
}
