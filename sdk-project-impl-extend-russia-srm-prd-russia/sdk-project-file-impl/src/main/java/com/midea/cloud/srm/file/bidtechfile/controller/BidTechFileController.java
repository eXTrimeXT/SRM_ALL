package com.midea.cloud.srm.file.bidtechfile.controller;

import com.midea.cloud.common.utils.redis.RSALockUtil;
import com.midea.cloud.srm.file.bidtechfile.service.BidTechFileService;
import com.midea.cloud.srm.file.util.AbstractFileLockUtil;
import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import com.midea.cloud.srm.file.largerfile.service.LargerFileService;
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
@RequestMapping("/bid/techfile")
public class BidTechFileController extends BaseController {

    @Autowired
    private BidTechFileService businessFileService;
    @Autowired
    private LargerFileService largerFileService;
    @Autowired
    private RSALockUtil rsaLockUtil;
    /**
     * 批量下载招标技术附件
     * @param projectId
     * @param response
     */
    @GetMapping("/downloadTechFile")
    public void downloadTechFile(@RequestParam("projectId") Long projectId, HttpServletResponse response) {
        businessFileService.downloadTechFile(projectId, response);
    }

    /**
     * 获取招标技术附件 oss路径
     * @param fileuploadId
     */
    @GetMapping("/downloadTechFileSign")
    public DownLoadResultDto downloadTechFile(@RequestParam("fileuploadId") Long fileuploadId) throws Exception {
        String encryptKey = rsaLockUtil.encrypt(fileuploadId.toString());
        return largerFileService.downloadSign(encryptKey,false);
    }

}
