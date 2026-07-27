package com.midea.cloud.srm.file.archivist.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.file.archivist.service.BidArchivistService;
import com.midea.cloud.srm.model.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/15
 */
@RestController
@Slf4j
@RequestMapping("/bid")
public class BidArchivistController extends BaseController {

    @Autowired
    private BidArchivistService bidArchivistService;

    @GetMapping("/archivist")
    public void archivist(@RequestParam("projectId") Long projectId, HttpServletResponse response) {
        bidArchivistService.archivist(projectId, response);
    }

    /**
     * 批量压缩文件
     * @param body
     * @param response
     */
    @PostMapping("/batchDownloadToZip")
    public void batchDownloadToZip(@RequestBody Map<String, Object> body, HttpServletResponse response) throws Exception {
        try {
            List<Long> fileIdList = (List<Long>) body.get("fileIdList");
            bidArchivistService.batchDownloadToZip(fileIdList, response);
        } catch (Exception e) {
            log.error("batchDownloadToZip Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
