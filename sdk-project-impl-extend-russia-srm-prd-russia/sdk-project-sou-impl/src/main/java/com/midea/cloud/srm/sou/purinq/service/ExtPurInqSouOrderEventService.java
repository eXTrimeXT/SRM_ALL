package com.midea.cloud.srm.sou.purinq.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouOrderEventService {

    /**
     * 报价导入
     * @param projectId
     * @param round
     * @param vendorId
     * @param isBuyer
     * @param file
     * @param fileupload
     * @return
     */
    Map<String, Object> importOrderItems(long projectId, Integer round, long vendorId, boolean isBuyer, MultipartFile file, Fileupload fileupload);

}
