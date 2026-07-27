package com.midea.cloud.srm.file.bidtechfile.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/8/9
 */
public interface BidTechFileService {

    /**
     * 批量下载招标技术附件
     * @param projectId
     * @param response
     */
    void downloadTechFile(Long projectId, HttpServletResponse response);
}
