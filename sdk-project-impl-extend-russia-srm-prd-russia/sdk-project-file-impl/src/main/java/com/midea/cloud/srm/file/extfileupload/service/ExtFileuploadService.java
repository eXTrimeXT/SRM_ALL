package com.midea.cloud.srm.file.extfileupload.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/15
 */
public interface ExtFileuploadService {

    /**
     * 根据ID查询
     * @param fileuploadId
     * @return
     */
    Fileupload queryById(Long fileuploadId);
}
