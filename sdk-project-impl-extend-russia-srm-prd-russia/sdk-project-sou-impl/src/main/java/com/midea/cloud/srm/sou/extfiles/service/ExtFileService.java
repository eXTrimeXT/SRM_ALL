package com.midea.cloud.srm.sou.extfiles.service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
public interface ExtFileService {

    /**
     * 批量压缩文件
     * @param fileIdList
     * @param response
     * @throws Exception
     */
    public void batchDownloadToZip(List<Long> fileIdList, HttpServletResponse response) throws Exception;
}
