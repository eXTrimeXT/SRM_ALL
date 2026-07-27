package com.midea.cloud.srm.biz.pj.file.anon.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.stereotype.Service;
/**
 * @author bs
 */

public interface FileAnonService {
    /**
     * 备注
     * @param fileId fileId
     * @param fileName fileName
     * @return Fileupload
     */
    Fileupload wordToPdf(Long fileId, String fileName) throws Exception;
}
