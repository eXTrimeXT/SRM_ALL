package com.midea.cloud.srm.sou.inq.ext.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
public interface ExtInqSouOrderEventService {

    /**
     * 报价导入
     * @param projectId 参数
     * @param round 参数
     * @param vendorId 参数
     * @param isBuyer 参数
     * @param file 参数
     * @param fileupload 参数
     * @return 返回
     */
    Map<String, Object> importOrderItems(long projectId, Integer round, long vendorId, boolean isBuyer, MultipartFile file, Fileupload fileupload);

}
