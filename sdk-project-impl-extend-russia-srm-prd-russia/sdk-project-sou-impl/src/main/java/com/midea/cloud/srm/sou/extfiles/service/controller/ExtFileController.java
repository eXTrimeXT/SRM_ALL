package com.midea.cloud.srm.sou.extfiles.service.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.sou.extfiles.service.ExtFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/extfile")
public class ExtFileController extends BaseController {

    @Autowired
    private ExtFileService extFileService;

    /**
     * 批量压缩文件
     * @param body
     * @param response
     */
    @PostMapping("/batchDownloadToZip")
    public void batchDownloadToZip(@RequestBody Map<String, Object> body, HttpServletResponse response) throws Exception {
        try {
            List<Long> fileIdList = (List<Long>) body.get("fileIdList");
            extFileService.batchDownloadToZip(fileIdList, response);
        } catch (Exception e) {
            log.error("batchDownloadToZip Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

}
