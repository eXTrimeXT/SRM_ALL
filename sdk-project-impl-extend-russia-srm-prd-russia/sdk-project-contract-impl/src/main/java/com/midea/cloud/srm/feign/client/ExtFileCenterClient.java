package com.midea.cloud.srm.feign.client;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 二开重定义文件中心接口
 * @author huangbf3
 */
@FeignClient(value = "${cloud.scc.feign-name-mapping.file-center:file-center}", path = "${cloud.scc.feign-name-mapping.file-center-path:/api-file}",contextId = "ext-file-center")
public interface ExtFileCenterClient {
    /**
     * 备注
     * @param fileupload 参数
     * @return 返回
     */
    @RequestMapping(
            value = {"/files-anon/file/fileupload/download"},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    Response downloadFileByParamForAnon(Fileupload fileupload);

    /**
     * 合同下载
     * @param fileuploadId
     * @return
     */
    @GetMapping("/extfileupload/queryById")
    Fileupload queryById(@RequestParam("fileuploadId") Long fileuploadId);
}
