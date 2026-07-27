package com.midea.cloud.srm.sou.bid.init.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtBidSouInitEventWebService {

    /**
     * 导入报价模板
     * @param projectId 参数
     * @param file 参数
     * @param fileupload 参数
     * @param souType 参数
     * @return 返回
     * @throws Exception
     */
    public Map<String, Object> importPriceExcel(Long projectId, MultipartFile file, Fileupload fileupload, String souType) throws Exception;

    /**
     * 备注
     * @param fileupload 参数
     * @param file 参数
     * @param headList 参数
     * @param errorList 参数
     * @return 返回
     * @throws Exception
     */
    Fileupload uploadFile(Fileupload fileupload, MultipartFile file, List<List<String>> headList, List<List<Object>> errorList) throws Exception;
}
