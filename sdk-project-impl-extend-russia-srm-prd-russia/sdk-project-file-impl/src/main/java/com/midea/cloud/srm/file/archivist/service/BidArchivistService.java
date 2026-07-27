package com.midea.cloud.srm.file.archivist.service;

import com.midea.cloud.srm.model.sou.file.upload.entity.Fileupload;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/13
 */
public interface BidArchivistService {

    /**
     * 招标文件归档
     * @param projectId
     * @param response
     */
    public void archivist(Long projectId, HttpServletResponse response);

    /**
     * 查询附件
     * @param fileIdList
     * @return
     */
    Map<Long, Fileupload> queryFileuploadByIds(List<Long> fileIdList);

    /**
     * 批量附件下载
     * @param fileIdList
     * @param response
     * @throws Exception
     */
    public void batchDownloadToZip(List<Long> fileIdList, HttpServletResponse response) throws Exception;
}
