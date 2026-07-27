package com.midea.cloud.srm.file.bidbusinessfile.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/8/9
 */
public interface BidBusinessFileService {

    /**
     * 批量下载招标商务附件
     * @param projectId
     * @param response
     */
    void downloadBusinessFile(Long projectId, HttpServletResponse response);
}
